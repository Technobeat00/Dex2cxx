/*
 * Created by aantik
 * 2/23/2022 11:03 AM
 *
 *   ⋆    ႔ ႔
 *     ᠸ^ ^ ⸝⸝
 *       |、˜〵
 *      じしˍ,)⁐̤ᐷ
 *
 * Fox Mode 🍺
 */
package com.dex2c.pipeline;

import com.dex2c.model.MethodInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class SmaliPatcher {
    private SmaliPatcher() {
    }

    static List<MethodInfo> patch(Path decompiledDir, List<MethodInfo> methods) throws IOException {
        List<MethodInfo> patched = new ArrayList<>();
        for (MethodInfo method : methods.stream().sorted(Comparator.comparing(MethodInfo::toDexSignature)).toList()) {
            Path smali = findSmali(decompiledDir, method);
            if (smali == null) {
                continue;
            }
            if (patchMethod(smali, method)) {
                patched.add(method);
            }
        }
        return patched;
    }

    private static Path findSmali(Path decompiledDir, MethodInfo method) throws IOException {
        String relative = method.className().substring(1, method.className().length() - 1) + ".smali";
        try (var stream = Files.walk(decompiledDir)) {
            return stream
                    .filter(path -> path.toString().replace('\\', '/').endsWith(relative))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static boolean patchMethod(Path smali, MethodInfo method) throws IOException {
        List<String> lines = Files.readAllLines(smali, StandardCharsets.UTF_8);
        String suffix = method.methodName() + method.descriptor();
        for (int i = 0; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            if (!trimmed.startsWith(".method ") || !trimmed.endsWith(suffix)) {
                continue;
            }

            int end = findEnd(lines, i + 1);
            if (end < 0 || trimmed.contains(" native ") || trimmed.contains(" abstract ")) {
                return false;
            }

            List<String> replacement = new ArrayList<>();
            replacement.add(makeNativeMethodLine(lines.get(i), method));
            replacement.add(".end method");
            replacement.add("");
            replacement.add(makeRenamedMethodLine(lines.get(i), method));
            replacement.addAll(lines.subList(i + 1, end + 1));

            lines.subList(i, end + 1).clear();
            lines.addAll(i, replacement);
            ensureLoadLibrary(lines);
            Files.write(smali, lines, StandardCharsets.UTF_8);
            return true;
        }
        return false;
    }

    private static int findEnd(List<String> lines, int start) {
        for (int i = start; i < lines.size(); i++) {
            if (lines.get(i).trim().equals(".end method")) {
                return i;
            }
        }
        return -1;
    }

    private static String makeNativeMethodLine(String line, MethodInfo method) {
        String indent = line.substring(0, line.indexOf(".method"));
        String body = line.trim();
        Set<String> tokens = new HashSet<>(List.of(body.split("\\s+")));
        String signature = method.methodName() + method.descriptor();
        String prefix = body.substring(0, body.length() - signature.length()).trim();
        if (!tokens.contains("native")) {
            prefix += " native";
        }
        return indent + prefix + " " + signature;
    }

    private static String makeRenamedMethodLine(String line, MethodInfo method) {
        String renamed = JniNames.renamedMethod(method) + method.descriptor();
        return line.substring(0, line.length() - (method.methodName() + method.descriptor()).length()) + renamed;
    }

    private static void ensureLoadLibrary(List<String> lines) {
        for (String line : lines) {
            if (line.contains("loadLibrary(Ljava/lang/String;)V") && line.contains(NativeNames.MODULE)) {
                return;
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().startsWith(".method ") && lines.get(i).trim().endsWith("<clinit>()V")) {
                int locals = findDirective(lines, i + 1, ".locals", ".registers");
                if (locals > 0) {
                    lines.set(locals, bumpLocalDirective(lines.get(locals)));
                    lines.add(locals + 1, "    const-string v0, \"" + NativeNames.MODULE + "\"");
                    lines.add(locals + 2, "    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V");
                }
                return;
            }
        }

        int insertAt = lines.size();
        lines.add(insertAt, "");
        lines.add(insertAt + 1, ".method static constructor <clinit>()V");
        lines.add(insertAt + 2, "    .locals 1");
        lines.add(insertAt + 3, "    const-string v0, \"" + NativeNames.MODULE + "\"");
        lines.add(insertAt + 4, "    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V");
        lines.add(insertAt + 5, "    return-void");
        lines.add(insertAt + 6, ".end method");
    }

    private static int findDirective(List<String> lines, int start, String... directives) {
        for (int i = start; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            for (String directive : directives) {
                if (trimmed.startsWith(directive + " ")) {
                    return i;
                }
            }
            if (trimmed.equals(".end method")) {
                return -1;
            }
        }
        return -1;
    }

    private static String bumpLocalDirective(String line) {
        String trimmed = line.trim();
        String directive = trimmed.startsWith(".registers") ? ".registers" : ".locals";
        String indent = line.substring(0, line.indexOf(directive));
        String[] parts = trimmed.split("\\s+");
        int count = Integer.parseInt(parts[1]);
        return indent + directive + " " + Math.max(count, 1);
    }
}
