@ECHO off
SETLOCAL

SET TARGET_ARCH=

:: argument parser
:parse
IF "%~1"=="" (
  GOTO endparse
)
IF "%~1"=="-t" (
  SET TARGET_ARCH=%~2
)
IF "%~1"=="-cc" (
  SET CPP_BUILD=true
)
IF "%~1"=="-py" (
  SET PYTHON_BUILD=true
)
IF "%~1"=="-cs" (
  SET CSHARP_BUILD=true
)
IF "%~1"=="-jni" (
  SET JAVA_BUILD=true
)
IF "%~1"=="-go" (
  SET GO_BUILD=true
)
IF "%~1"=="--clean" (
  SET CLEAN=true
)
IF "%~1"=="--help" (
  CALL :print_usage
  EXIT /B 0
)
IF "%~1"=="-h" (
  CALL :print_usage
  EXIT /B 0
)

SHIFT
GOTO parse
:endparse

:: check TARGET_ARCH value
IF "%TARGET_ARCH%"=="" (
  ECHO ERROR: Unspecified target architecture. Please use -t from CLI to specify an architecture.
  EXIT /B 1
)

::check if architecture is supported
SET SUPPORTED_TARGETS=msvc-x64;aarch64-linux-android;arm-linux-android
:: easy-to-undestand checking if TARGET_ARCH is in SUPPORTED_TARGETS list
FOR %%A IN (%SUPPORTED_TARGETS%) DO (
  IF "%TARGET_ARCH%"=="%%A" (
    SET VALID_TARGET_ARCH=1
  )
)

IF "%VALID_TARGET_ARCH%"=="" (
  ECHO ERROR: target %TARGET_ARCH% is not supported yet. Please use (%SUPPORTED_TARGETS%^).
  EXIT /B 1
)

:: check if need to clean
SET BINARY_PATH=%CD%\assets\binaries\%TARGET_ARCH%
IF NOT "%CLEAN%"=="" (
  CALL :clean_build %BINARY_PATH%
)

:: artifacts
SET TAG=v0.8.0
SET ARCHIVE_NAME=oneml-bootcamp-%TARGET_ARCH%.tar.gz
SET BASE_URL=https://github.com/sertiscorp/oneML-bootcamp/releases/download/%TAG%/%ARCHIVE_NAME%
IF NOT EXIST "%BINARY_PATH%\%ARCHIVE_NAME%" (
    ECHO Downloading artifacts to %BINARY_PATH%... 
    curl -L %BASE_URL% > %BINARY_PATH%\%ARCHIVE_NAME%
    tar xzf %BINARY_PATH%\%ARCHIVE_NAME% -C %BINARY_PATH%\ --strip-components=1
)

:: toolchian
SET EXTRA_FLAGS=
IF "%TARGET_ARCH%"=="aarch64-linux-android" GOTO android
IF "%TARGET_ARCH%"=="arm-linux-android" GOTO android
IF "%TARGET_ARCH%"=="msvc-x64" GOTO end_toolchain

SET EXTRA_FLAGS=-DCMAKE_TOOLCHAIN_FILE=cmake\toolchains\%TARGET_ARCH%.cmake
GOTO end_toolchain

:android
SET ANDROID_BUILD=true
IF "%TARGET_ARCH%"=="arm-linux-android" (
  SET ABI=armeabi-v7a
  SET EXTRA_FLAGS=-DANDROID_ARM_NEON=ON
) ELSE (
  SET ABI=arm64-v8a
)

SET EXTRA_FLAGS=-DCMAKE_TOOLCHAIN_FILE=%ANDROID_NDK_HOME%\build\cmake\android.toolchain.cmake^
                -DANDROID_ABI=%ABI%^
                -DANDROID_NATIVE_API_LEVEL=23^
                -DANDROID_STL=c++_shared^
                %EXTRA_FLAGS%

:end_toolchain

:: apps build
IF NOT "%CPP_BUILD%"=="" (
  IF NOT EXIST "build" MKDIR build
  CD build
  cmake -G "Visual Studio 16 2019" -A x64 -DTARGET_ARCH=%TARGET_ARCH% %EXTRA_FLAGS% ..
  cmake --build . --config Release
  CD ..
  COPY %BINARY_PATH%\bin\oneml.dll bin\Release\
)

IF NOT "%PYTHON_BUILD%"=="" (
  IF NOT "%CLEAN%"=="" (
    GOTO python_build
  )

  pip list | findstr oneML
  IF NOT %ERRORLEVEL%==0 (
    GOTO python_build
  )
  
  ECHO Using existing oneML installation. Use --clean to reinstall.
  GOTO end_python_build
) ELSE (
  GOTO end_python_build
)

:python_build
pip3 install -U pip setuptools wheel
CD %BINARY_PATH%\bindings\python
POWERSHELL -command "& .\install_oneml.ps1"

:end_python_build

IF NOT "%CSHARP_BUILD%"=="" (
  IF NOT "%CLEAN%"=="" (
    GOTO csharp_build
  )
  IF NOT EXIST "%BINARY_PATH%\bindings\csharp\face\build" (
    GOTO java_build
  )
  IF NOT EXIST "%BINARY_PATH%\bindings\csharp\alpr\build" (
    GOTO java_build
  )
  
  ECHO Using existing C# binaries. Use --clean to rebuild.
  GOTO end_csharp_build
) ELSE (
  GOTO end_csharp_build
)

:csharp_build
:: Build Java application
CD %BINARY_PATH%\bindings\csharp\face
POWERSHELL -command "& .\build.ps1"
CD ..\alpr
POWERSHELL -command "& .\build.ps1"
CD ..\..\..\..\..\..\apps\csharp

SET APPS=FaceEmbedderApp FaceIdApp FaceDetectorApp FaceVerificationApp VehicleDetectorApp
FOR %%A IN (%APPS%) DO (
  dotnet new console -o %%A
  CD %%A
  dotnet build
  CD ..
)

COPY %BINARY_PATH%\bin\oneml.dll .
COPY %BINARY_PATH%\bindings\csharp\face\build\Release\oneMLfaceCSharp.dll .
COPY %BINARY_PATH%\bindings\csharp\alpr\build\Release\oneMLalprCSharp.dll .
:end_csharp_build

IF NOT "%JAVA_BUILD%"=="" (
  IF NOT "%CLEAN%"=="" (
    GOTO java_build
  )
  IF NOT EXIST "%BINARY_PATH%\bindings\java\face\build" (
    GOTO java_build
  )
  IF NOT EXIST "%BINARY_PATH%\bindings\java\alpr\build" (
    GOTO java_build
  )
  
  ECHO Using existing Java classes for oneML. Use --clean to compile again.
  GOTO end_java_build
) ELSE (
  GOTO end_java_build
)

:java_build
IF NOT "%ANDROID_BUILD%"=="" (

  SET ANDROID_APPS=android-simple;android-camera

  FOR %%A IN (%ANDROID_APPS%) DO (
    ECHO Build Android app: %%A

    CD .\apps\"%%A"
    :: Alternative `touch` command for Windows
    IF NOT EXIST .\local.properties (
      TYPE nul > local.properties
    )
    
    IF NOT "%CLEAN%"=="" (
      ECHO Clean a project by Gradle wrapper
      :: Clean flag is set. Clean a project before building
      CALL .\gradlew.bat clean
    )
    
    ECHO Build by Gradle wrapper
    CALL .\gradlew.bat build

    CD ..\..
  )

) else (
  :: Build Java application
  CD %BINARY_PATH%\bindings\java\face
  POWERSHELL -command "& .\build.ps1"
  CD ..\alpr
  POWERSHELL -command "& .\build.ps1"
  CD ..\..\..\..\..\..\apps\java
  IF NOT EXIST "classes" MKDIR classes
  SET CLASSPATH=classes;%BINARY_PATH%\bindings\java\face\oneml\oneml-face-api.jar;%BINARY_PATH%\bindings\java\alpr\oneml\oneml-alpr-api.jar;%CD%
  SET APPS=FaceEmbedderApp FaceIdApp FaceDetectorApp FaceVerificationApp VehicleDetectorApp
  FOR %%A IN (%APPS%) DO (
    javac -cp %CLASSPATH% -d classes %%A.java
  )

  COPY %BINARY_PATH%\bin\oneml.dll .
  COPY %BINARY_PATH%\bindings\java\face\build\Release\oneMLfaceJava.dll .
  COPY %BINARY_PATH%\bindings\java\alpr\build\Release\oneMLalprJava.dll .
)
:end_java_build

IF NOT "%GO_BUILD%"=="" (
  ECHO implement this
)

EXIT /B %ERRORLEVEL%

:: functions
:print_usage
ECHO Usage: build.bat
ECHO         -t target_arch
ECHO         [-cc cpp_apps]
ECHO         [-py python_apps]
ECHO         [-cs csharp_apps]
ECHO         [-jni java_apps]
ECHO         [-go go_apps]
ECHO         [--clean clean_build]
ECHO.
ECHO Example: build.bat
ECHO           -t x86_64
ECHO           [-cc]
ECHO           [-py]
ECHO           [-cs]
ECHO           [-jni]
ECHO           [-go]
ECHO           [--clean]
ECHO.
EXIT /B 0

:clean_build
ECHO Cleanup build in progress...
RMDIR /S /Q build bin apps\java\classes 2>NUL
DEL /S /Q *.tar.gz 2>NUL
DEL /S /Q *.zip 2>NUL
DEL /S /Q *.json 2>NUL
DEL /S /Q *.yaml 2>NUL
DEL /S /Q *.dll 2>NUL
RMDIR /S /Q %~1\bin 2>NUL
RMDIR /S /Q %~1\bindings 2>NUL
RMDIR /S /Q %~1\include 2>NUL
RMDIR /S /Q %~1\lib 2>NUL
RMDIR /S /Q %~1\share 2>NUL
EXIT /B 0
