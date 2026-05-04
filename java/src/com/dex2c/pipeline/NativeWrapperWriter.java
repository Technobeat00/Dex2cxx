/*
 * Created by aantik
 * 2/22/2022 4:22 PM
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
import java.util.List;
import java.util.HashSet;
import java.util.Set;

final class NativeWrapperWriter {
    private NativeWrapperWriter() {
    }

    static void write(Path cppFile, List<MethodInfo> methods) throws IOException {
        StringBuilder out = new StringBuilder();
        out.append("#include <jni.h>\n");
        out.append("#include <android/log.h>\n\n");
        out.append("static jclass d2c_find_class(JNIEnv *env, const char *name) {\n");
        out.append("    return env->FindClass(name);\n");
        out.append("}\n\n");

        Set<String> emitted = new HashSet<>();
        for (MethodInfo method : methods) {
            if (!emitted.add(JniNames.longName(method))) {
                continue;
            }
            writeMethod(out, method);
        }

        Files.createDirectories(cppFile.getParent());
        Files.writeString(cppFile, out.toString(), StandardCharsets.UTF_8);
    }

    private static void writeMethod(StringBuilder out, MethodInfo method) {
        List<String> params = Descriptor.params(method.descriptor());
        String ret = Descriptor.returnType(method.descriptor());
        String retType = Descriptor.jniType(ret);

        out.append("extern \"C\" JNIEXPORT ").append(retType).append(" JNICALL\n");
        out.append(JniNames.longName(method)).append("(JNIEnv *env, ");
        out.append(method.isStatic() ? "jclass clazz" : "jobject thiz");
        for (int i = 0; i < params.size(); i++) {
            out.append(", ").append(Descriptor.jniType(params.get(i))).append(" p").append(i);
        }
        out.append(") {\n");

        if (method.isStatic()) {
            out.append("    jclass target = d2c_find_class(env, \"")
                    .append(method.className(), 1, method.className().length() - 1)
                    .append("\");\n");
        } else {
            out.append("    jclass target = env->GetObjectClass(thiz);\n");
        }
        out.append("    if (target == nullptr) { ");
        if (!ret.equals("V")) {
            out.append("return 0; ");
        } else {
            out.append("return; ");
        }
        out.append("}\n");
        out.append("    jmethodID mid = env->")
                .append(method.isStatic() ? "GetStaticMethodID" : "GetMethodID")
                .append("(target, \"")
                .append(JniNames.renamedMethod(method))
                .append("\", \"")
                .append(method.descriptor())
                .append("\");\n");
        out.append("    if (mid == nullptr) { ");
        if (!ret.equals("V")) {
            out.append("return 0; ");
        } else {
            out.append("return; ");
        }
        out.append("}\n");

        if (!params.isEmpty()) {
            out.append("    jvalue args[").append(params.size()).append("];\n");
            for (int i = 0; i < params.size(); i++) {
                out.append("    args[").append(i).append("].")
                        .append(Descriptor.jvalueField(params.get(i)))
                        .append(" = p").append(i).append(";\n");
            }
        }

        String call = "Call" + (method.isStatic() ? "Static" : "") + Descriptor.callSuffix(ret) + "MethodA";
        out.append("    ");
        if (!ret.equals("V")) {
            out.append("return (").append(retType).append(") ");
        }
        out.append("env->").append(call).append("(")
                .append(method.isStatic() ? "target" : "thiz")
                .append(", mid, ")
                .append(params.isEmpty() ? "nullptr" : "args")
                .append(");\n");
        if (ret.equals("V")) {
            out.append("    return;\n");
        }
        out.append("}\n\n");
    }
}
