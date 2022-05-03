# oneML-bootcamp

Introduction and sample apps to showcase `oneML` functionalities and possible use cases.

Currently supported HW architectures:
- `x86_64`
- `msvc-x64`
- `aarch64-linux-gnu`
- `arm-linux-gnueabihf`
- `aarch64-linux-android`
- `arm-linux-android`

Currently supported HW devices:
- CPU
- GPU, for `android` targets only

Currently supported OSs:
- Ubuntu 20.04, 64 bit
- Windows 10, 64 bit
- Android, API level > 23

Coming soon:
- support for GPU on `x86_64`
- support for `iOS`

## What is `oneML`?
TBD.

## What can you do with this repository?
TBD.

# Setup and build
Depending on your OS and on which programming language you would like to use, the
setup and build process is going to be slightly different. Please refer to the
respective section below.

## Linux
On Linux machines, it is always advised to used `docker` to create a reproducible
workspace and not have to worry about dependencies. If you don't want to use `docker`,
feel free to setup your local environment to match the one provided in our
[Dockerfiles](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/docker).

### Docker build
There are multiple Dockerfiles available based on the HW to be targeted for the
deployment as well as if the device requires specific build tools or dependencies.

There are two different types of docker images:
- `base` docker images are supposed to provide all the common tools and libraries
shared by all the other, more specific, images built on top of them
- device/OS-specific, e.g. `cpu`, `gpu`, `android`


For a normal CPU environment:
```bash
docker build -t oneml-bootcamp:cpu-base -f docker/Dockerfile --build-arg base_image=ubuntu:20.04 .
docker build -t oneml-bootcamp:cpu -f docker/Dockerfile.cpu --build-arg base_image=oneml-bootcamp:cpu-base .
```

For a GPU environment:
```bash
docker build -t oneml-bootcamp:gpu-base -f docker/Dockerfile --build-arg base_image=nvidia/cuda:11.5.1-cudnn8-runtime-ubuntu20.04 .
docker build -t oneml-bootcamp:gpu -f docker/Dockerfile.gpu --build-arg base_image=oneml-bootcamp:gpu-base .
```

For an Android environment:
```bash
docker build -t oneml-bootcamp:cpu-base -f docker/Dockerfile --build-arg base_image=ubuntu:20.04 .
docker build -t oneml-bootcamp:android -f docker/Dockerfile.android --build-arg base_image=oneml-bootcamp:cpu-base .
```

### Apps build
We provide a `bash` script that will setup the project, prepare the environment
and build all the artifacts necessary to run the sample applications.

```bash
./build.sh --help
```
will provide with the description of the build script. Here are some examples:

To build `C++` apps for `x86_64` target:
```bash
./build.sh -t x86_64 -cc --clean
```

To build `Python` apps for `aarch64-linux-gnu` target:
```bash
./build.sh -t aarch64-linux-gnu -py --clean
```

To build `Java` apps for `x86_64` target:
```bash
./build.sh -t x86_64 -jni --clean
```

To build `Android` apps for `aarch64-linux-android` target:
```bash
./build.sh -t aarch64-linux-android -jni --clean
```

If `--clean` is not specified, the existing `oneML` artifacts will be used and
the old build files will not be deleted.

## Windows
We provide support for our library and sample applications for Windows OS as well.

Unfortunately, there is no easy way to use our Dockerfiles in this environment, thus the
user has to rely on their own local environment and make sure all the dependencies are installed
before proceeding with the build process and run the applications.

### Requirements
Only Windows 10 64 bit is currently supported.

### Dependencies
The following dependencies must be installed in order for the project to build and run
successfully:
- Microsoft Visual Studio 16 2019 or newer
- `curl` to pull some data from the internet
- `tar` to unpack some archives
- CMake 3.17 or newer (for `C++` build only)
- Python 3.6 or newer and `pip` (for `Python` build only)
- JDK 1.8 (for `Java` build only)
- Android Studio xxx (for `Android` build only)
- Golang 1.15 (for `Golang` build only)
- Powershell, a recent version (for any build, but `C++`)

### Apps build
We provide a `batch` script that will setup the project, prepare the environment
and build all the artifacts necessary to run the sample applications.

```batch
build.bat --help
```
will provide with the description of the build script. Here are some examples:

To build `C++` apps for `msvc-x64` target:
```batch
build.bat -t msvc-x64 -cc --clean
```

To build `Python` apps for `msvc-x64` target:
```batch
build.bat -t msvc-x64 -py --clean
```

To build `Java` apps for `msvc-x64` target:
```batch
build.bat -t msvc-x64 -jni --clean
```

To build `Golang` apps for `x86_64` target:
```batch
build.bat -t x86_64 -go --clean
```

If `--clean` is not specified, the existing `oneML` artifacts will be used and
the old build files will not be deleted.

# Usage
It is possible to find more details about the usage in each programming language
specific folder in `apps` folder. You can click on the following links:
- [C++ apps](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/apps/cpp)
- [Python apps](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/apps/python)
- [Java apps](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/apps/java)
- [Golang apps](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/apps/golang)

For all the applications, it is possible to set the LOG level of `oneML` by settings
`ONEML_CPP_MIN_LOG_LEVEL` environment variable to one of the following values:
- `INFO`, to log all the information
- `WARNING`, to log only warnings and more critical information
- `ERROR`, to log only errors and fatal crashes
- `FATAL`, to only log fatal crashes
