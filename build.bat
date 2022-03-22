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
) ELSE ( ::check if architecture is supported
  ECHO implement this
)

:: check if need to clean
IF NOT "%CLEAN%"=="" (
  CALL :clean_build
)

:: artifacts
SET TAG=v0.1.0
SET ARCHIVE_NAME=oneml-bootcamp-%TARGET_ARCH%.tar.gz
SET BASE_URL=https://github.com/sertiscorp/oneML-bootcamp/releases/download/%TAG%/%ARCHIVE_NAME%
SET BINARY_PATH=assets/binaries/%TARGET_ARCH%
::SET BINARY_PATH=test
IF NOT EXIST "%BINARY_PATH%/%ARCHIVE_NAME%" (
    ECHO Downloading artifacts to %BINARY_PATH%... 
    curl -L %BASE_URL% > %BINARY_PATH%/%ARCHIVE_NAME%
    tar xzf %BINARY_PATH%/%ARCHIVE_NAME% -C %BINARY_PATH%/ --strip-components=1
)

:: toolchian
SET EXTRA_FLAGS=
IF "%TARGET_ARCH%"=="aarch64-linux-android" GOTO android
IF "%TARGET_ARCH%"=="arm-linux-android" GOTO android
IF "%TARGET_ARCH%"=="msvc-x64" GOTO endtoolchain

SET EXTRA_FLAGS=-DCMAKE_TOOLCHAIN_FILE=cmake/toolchains/%TARGET_ARCH%.cmake
GOTO endtoolchain

:android
IF "%TARGET_ARCH%"=="arm-linux-android" (
  SET ABI=armeabi-v7a
  SET EXTRA_FLAGS=-DANDROID_ARM_NEON=ON
) ELSE (
  SET ABI=arm64-v8a
)

SET EXTRA_FLAGS=-DCMAKE_TOOLCHAIN_FILE=%ANDROID_NDK_HOME%/build/cmake/android.toolchain.cmake^
                -DANDROID_ABI=%ABI%^
                -DANDROID_NATIVE_API_LEVEL=23^
                -DANDROID_STL=c++_shared^
                %EXTRA_FLAGS%

:endtoolchain

:: apps build
IF NOT "%CPP_BUILD%"=="" (
  IF NOT EXIST "build" mkdir build
  cd build
  cmake -G "Visual Studio 16 2019" -A x64 -DTARGET_ARCH=%TARGET_ARCH% %EXTRA_FLAGS% ..
  cmake --build . --config Release
  cd ..
  copy %BINARY_PATH%/bin/oneml.dll bin/Release/
)

IF NOT "%PYTHON_BUILD%"=="" (
  ECHO implement this
)

IF NOT "%JAVA_BUILD%"=="" (
  ECHO implement this
)

IF NOT "%GO_BUILD%"=="" (
  ECHO implement this
)

:: functions
EXIT /B %ERRORLEVEL%

:print_usage
ECHO Usage: build.bat
ECHO         -t target_arch
ECHO         [-cc cpp_apps]
ECHO         [-py python_apps]
ECHO         [-jni java_apps]
ECHO         [-go go_apps]
ECHO         [--clean clean_build]
ECHO.
ECHO Example: build.bat
ECHO           -t x86_64
ECHO           [-cc]
ECHO           [-py]
ECHO           [-jni]
ECHO           [-go]
ECHO           [--clean]
ECHO.
EXIT /B 0

:clean_build
ECHO Cleanup build in progress...
RMDIR /S /Q build bin 2>NUL
DEL /S /Q *.tar.gz 2>NUL
DEL /S /Q *.zip 2>NUL
DEL /S /Q *.json 2>NUL
DEL /S /Q *.yaml 2>NUL
RMDIR /S /Q %BINARY_PATH%\bin 2>NUL
RMDIR /S /Q %BINARY_PATH%\bindings 2>NUL
RMDIR /S /Q %BINARY_PATH%\include 2>NUL
RMDIR /S /Q %BINARY_PATH%\lib 2>NUL
RMDIR /S /Q %BINARY_PATH%\share 2>NUL
EXIT /B 0
