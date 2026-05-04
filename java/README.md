# Java dexlib2 build

This folder builds the Python-free Java dex2c jar. It uses dexlib2 to read
APK/DEX files, applies the same whitelist/blacklist filter style from
`filter.txt`, patches selected smali methods to native wrappers, builds the
JNI project with Android NDK, rebuilds the APK with apktool, then aligns and
signs the result.

## Build

```powershell
powershell -ExecutionPolicy Bypass -File java\build.ps1
```

Termux/Linux:

```sh
sh java/build.sh
```

Output:

```text
dex2cxx.jar
```

## Run

```powershell
java -jar dex2cxx.jar -a input.apk -o output.apk --filter filter.txt
```

Use `--list-only` to print matching methods to the console without rebuilding.
