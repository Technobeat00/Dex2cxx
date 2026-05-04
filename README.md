# Dex2cxx

Dex2cxx is a powerful tool for converting Android DEX files into C++ code. It automates the process of JNI wrapper generation APK rebuilding and method filtering



<div style="display:flex; gap:10px;">
    <img src="image/image1.jpg" style="width:50%; height:auto; border-radius:10px;">
    <img src="image/image2.jpg" style="width:50%; height:auto; border-radius:10px;">
</div>

### Using Dex2cxx with Termux

Dex2cxx can be used on Termux for Android devices. Follow the steps below to set up and use the tool

#### Prerequisites
Ensure the following are installed on your Termux environment:

1. **Java Runtime Environment JRE**:
   ```bash
   pkg install openjdk-17
   ```

2. **Git** (optional, for cloning repositories):
   ```bash
   pkg install git
   ```

3. **Storage Permissions** (if accessing files outside Termux):
   ```bash
   termux-setup-storage
   ```

### filter.txt
The `filter.txt` file specifies class filters. Each line represents a method to include or exclude.

---

## Project Structure

- `java/src` -> Contains the Java source code.
- `project/jni` -> Contains the C++ source code.
- `keystore` -> Contains keystore files for signing APKs.

---


## Contributing
We welcome contributions! Please follow these steps:
Fork the repository Create a new branch for your feature or bug fix Submit a pull request with a detailed description of your changes.

---

## Support
If you encounter any issues, please open an issue on the [GitHub repository](https://github.com/your-repo/dex2cxx/issues).

---

## Authors
- **Your Name** - @aantik_mods

---


## Quick Commands

### Windows

1. Run Dex2cxx with filters and report generation:
   ```powershell
   java -jar dex2cxx.jar -a .\aa.apk -o .\aa_p.apk --filter .\filter.txt --report .\RuruData_methods-report.txt
   ```

2. Auto-setup configuration:
   ```powershell
   java -jar dex2cxx.jar --auto
   ```

### Termux

1. Run Dex2cxx with filters and report generation:
   ```bash
   java -jar dex2cxx.jar -a ./aa.apk -o ./aa_p.apk --filter ./filter.txt --report ./RuruData_methods-report.txt
   ```

2. Auto-setup configuration:
   ```bash
   java -jar dex2cxx.jar --auto
   ```
