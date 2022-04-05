# Java sample applications

Source code for the applications in Java programming language.

## How to run
If the setup and build process have already been completed successfully, then
you can simply run these applications by:

### Linux
```bash
TARGET_ARCH=<arch_name>
CLASSPATH=$(pwd)/apps/java/classes:$(pwd)/assets/binaries/${TARGET_ARCH}/bindings/java/face/oneml/oneml-face-api.jar:$(pwd)/assets/binaries/${TARGET_ARCH}/bindings/java/alpr/oneml/oneml-alpr-api.jar:$(pwd)/apps/java
export LD_LIBRARY_PATH=$(pwd)/assets/binaries/${TARGET_ARCH}/bindings/java/face/build:$(pwd)/assets/binaries/${TARGET_ARCH}/bindings/java/alpr/build:$LD_LIBRARY_PATH
cd apps/java
java -cp ${CLASSPATH} <app_name>
```

### Windows
```batch
SET TARGET_ARCH=<arch_name>
SET CLASSPATH=%CD%/apps/java/classes;%CD%/assets/binaries/%TARGET_ARCH%/bindings/java/face/oneml/oneml-face-api.jar;%CD%/assets/binaries/%TARGET_ARCH%/bindings/java/alpr/oneml/oneml-alpr-api.jar;%CD%/apps/java
CD apps/java
java -cp %CLASSPATH% <app_name>
```

in both operating systems:
- `arch_name` is one of the supported architectures listed in the [README](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/README.md) file and must be the same that was specified during the build process
- `app_name` is the name of the compiled Java application. E.g. use `FaceDetectorApp` to run `FaceDetectorApp.java`