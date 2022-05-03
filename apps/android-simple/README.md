# oneml-android-simple-app

The simple Android application for testing oneML library.

| Information            |                            |
|------------------------|----------------------------|
| Supported Architecture | `arm64-v8a`, `armeabi-v7a` |

## 1 Getting Started

### 1.1 oneML Java Package

- Download a oneML Java package archive for `aarch64-linux-android` or `arm-linux-android` from [the release page](https://github.com/sertiscorp/oneML-bootcamp/releases)
- Extract the archive and place files at `oneml-bootcamp/assets/binaries/<target_arch>`. `<target_arch>` is `aarch64-linux-android` or `arm-linux-android` which you downloaded previously.

### 1.2 NDK Version
Please make sure that you use the same NDK version as one that built the oneML Java package.
The NDK version used for building oneML package is described in oneML's `config.yaml`. For an example,
```
  ndk_path: '/root/ndk/21.0.6113669'     # NDK Path, only applicable when build with Android
```

After that, update the NDK version in the project at `app/build.gradle`.
```
android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"
    // NDK version should be the same as one in model config
    ndkVersion "21.0.6113669"
    
    ...
}
```

### 1.3 Build
For Android development environment, You can run a Docker container with an image at `oneml-bootcamp/docker/Dockerfile.android`. Then, run the below command to build an Android app.
```
$ ./gradlw build
```
The Android app package will be available at `./app/build/outputs/apk/debug/app-debug.apk`. 

Please note that you can specify the target ABI by `ABI` env (the default is `arm64-v8a`). For example,
```
$ export ABI=armeabi-v7a
$ ./gradlw build
```

## 2 APIs

### 2.1 Supported APIs
- [x] FaceDetector
- [x] FaceEmbedder
- [x] FaceId
- [x] Utils
