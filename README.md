# Dex2cxx

Dex2cxx is a powerful tool for converting Android DEX files into C++ code. It automates the process of JNI wrapper generation, APK rebuilding, and method filtering.

## Features
- **DEX to C++ Conversion**: Transform Java methods into native C++ code.
- **Automated Pipeline**: Disassemble, patch, reassemble, and sign APKs.
- **Multi-ABI Support**: Build for all Android architectures.
- **Auto-Setup Mode**: Zero-config environment setup.
- **Method Filtering**: Apply regex-based filters to target specific methods.
- **JNI Code Generation**: Create native stubs for seamless integration.
- **OLLVM Obfuscation**: Optional code obfuscation for enhanced security.
- **Comprehensive Reports**: Generate TSV reports for method analysis.

---

## Prerequisites
Before using Dex2cxx, ensure you have the following installed:

- **Java Development Kit (JDK)** (version 8 or higher)
- **Android NDK**
- **Python** (for scripting, optional)
- **Git** (for cloning the repository)
- **Windows PowerShell** or **Bash** (for running build scripts)

---

## Installation

1. Download the pre-built JAR file from the [releases page](https://github.com/your-repo/dex2cxx/releases).
2. Ensure Java is installed on your system.

---

## Usage

### Command-Line Interface

1. Convert a DEX file to C++:
   ```bash
   java -cp java/src com.dex2c.cli.Main --dex <path-to-dex-file> --output <output-directory>
   ```

2. Apply filters to methods:
   ```bash
   java -cp java/src com.dex2c.cli.Main --dex <path-to-dex-file> --filter filter.txt --output <output-directory>
   ```

3. Rebuild APKs:
   ```bash
   java -cp java/src com.dex2c.pipeline.RawApkRebuilder --apk <path-to-apk-file> --output <output-directory>
   ```

---

## Using the JAR File

If you have the pre-built JAR file, you can use it directly without building the project. Follow these steps:

### Using Dex2cxx with Termux

Dex2cxx can be used on Termux for Android devices. Follow the steps below to set up and use the tool:

#### Prerequisites
Ensure the following are installed on your Termux environment:

1. **Java Runtime Environment (JRE)**:
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

#### Using the JAR File

1. Download the `dex2cxx.jar` file to your Termux environment.
2. Run the JAR file with the following command:
   ```bash
   java -jar dex2cxx.jar --dex <path-to-dex-file> --output <output-directory>
   ```

### Additional Options

- To apply filters:
  ```bash
  java -jar dex2cxx.jar --dex <path-to-dex-file> --filter filter.txt --output <output-directory>
  ```

- To rebuild APKs:
  ```bash
  java -jar dex2cxx.jar --apk <path-to-apk-file> --output <output-directory>
  ```

---

## Configuration

### dxx.cfg
The `dxx.cfg` file allows you to customize the behavior of Dex2cxx. Below is an example configuration:
```ini
[General]
output_dir=output
log_level=INFO

[Pipeline]
use_jni=true
apply_filters=true
```

### filter.txt
The `filter.txt` file specifies method filters. Each line represents a method to include or exclude.

---

## Project Structure

- `java/src`: Contains the Java source code.
- `project/jni`: Contains the C++ source code.
- `keystore`: Contains keystore files for signing APKs.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contributing
We welcome contributions! Please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Submit a pull request with a detailed description of your changes.

---

## Support
If you encounter any issues, please open an issue on the [GitHub repository](https://github.com/your-repo/dex2cxx/issues).

---

## Authors
- **Your Name** - Initial work

---

## Acknowledgments
- Inspired by the Android development community.
- Special thanks to contributors and testers.

---
