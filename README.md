# oneML-bootcamp

Introduction and sample apps to showcase `oneML` functionalities and possible use cases.

<!-- toc -->

- [oneML-bootcamp](#oneml-bootcamp)
  - [Overview](#overview)
    - [Supported platforms](#supported-platforms)
    - [What is `oneML`?](#what-is-oneml)
    - [What can you do with this repository?](#what-can-you-do-with-this-repository)
  - [Features](#features)
    - [Face applications](#face-applications)
    - [Vehicle applications](#vehicle-applications)
  - [Getting Started](#getting-started)
    - [For Linux users](#for-linux-users)
      - [Step 1: Build a docker image](#step-1-build-a-docker-image)
      - [Step 2: Build sample applications](#step-2-build-sample-applications)
      - [Step 3: Run sample applications](#step-3-run-sample-applications)
    - [For Windows users](#for-windows-users)
      - [Step 0: Install required tools](#step-0-install-required-tools)
      - [Step 1: Build sample applications](#step-1-build-sample-applications)
      - [Step 2: Run sample applications](#step-2-run-sample-applications)
  - [Setup and Build](#setup-and-build)
    - [Linux](#linux)
      - [Docker build](#docker-build)
      - [Apps build](#apps-build)
    - [Windows](#windows)
      - [Requirements](#requirements)
      - [Dependencies](#dependencies)
      - [Apps build](#apps-build-1)
    - [Cuda](#cuda)
  - [API Reference](#api-reference)
  - [Usage](#usage)
  - [Roadmap](#roadmap)

<!-- tocstop -->
## Overview

### Supported platforms

|                  | Supported                                                                                                              |
|------------------|------------------------------------------------------------------------------------------------------------------------|
| HW architectures | x86_64 <br> msvc-x64 <br> aarch64-linux-gnu <br> arm-linux-gnueabihf <br> aarch64-linux-android <br> arm-linux-android |
| HW devices       | CPU <br> GPU, for `cuda` and `android` targets                                                                         |
| OSes             | Ubuntu 20.04, 64 bit <br> Windows 10, 64 bit <br> Android, API level > 23                                              |
| Coming soon      | `iOS`                                                                                                                  |

### What is `oneML`?
`oneML` is a fully-fledged C++ SDK providing APIs for a number of different AI/ML applications. It can be deployed on any target (CPU, GPU, CUDA) and platform (Android, iOS, embedded Linux, Linux, Windows). Morveover, `oneML` library provides API bindings in other programming languages such as Java, Python, and Golang.

### What can you do with this repository?
This repository provides `oneML` library (community edition) and its example applications in 4 programming languages: C++, Java, Python, and Golang (for Linux only). In case of Android development, you can use `oneML`'s Java bindings.
Please feel free to open an issue on GitHub if you found any issue.

## Features
In this community edition, `oneML` library provides APIs for 2 main applications: face and vehicle AI applications.

### Face applications
- Face detector
- Face embedding
- Face 1-1 verification
- Face identification

### Vehicle applications
- Vehicle detector

## Getting Started
This section describes steps for quickly testing `oneML` library. We will download `oneML` library and models. Then, we build and run sample C++ applications on `x86_64` architecture. There are 2 separated guidelines for Linux and Windows users.

**NOTE**: For other languages and architectures, please refer to [Setup and Build](#setup-and-build) section.

### For Linux users

#### Step 1: Build a docker image
We recommend to set up an environment for `oneML` with a docker container. A docker image can be built from `Dockerfile` in `docker` folder. Here, we are going to build a base image and another image for CPU runtime. Run the below command to build the image.

```
docker build -t oneml-bootcamp:cpu-base -f docker/Dockerfile --build-arg base_image=ubuntu:20.04 .
docker build -t oneml-bootcamp:cpu -f docker/Dockerfile.cpu --build-arg base_image=oneml-bootcamp:cpu-base .
```

#### Step 2: Build sample applications
We will run a docker container with `oneml-bootcamp:cpu` image that we built in the previous step, and mount this repository folder to the container at `/workspace` path. We execute `bash` inside the container to run the next following steps.
```
docker run -it --name oneml-bootcamp -v $PWD:/workspace oneml-bootcamp:cpu /bin/bash -c "cd /workspace && /bin/bash"   
```

Then, inside the container, we download `oneML` library and models, and build sample C++ applications with `build.sh` script.
```
./build.sh -t x86_64 -cc --clean
```

The `oneML` library and models are downloaded to `assets/binaries/x86_64`. 
```
assets/binaries/x86_64
├── bindings
├── config.yaml
├── include
├── lib
├── oneml-bootcamp-x86_64.tar.gz
├── README.md
└── share
```

The compiled C++ applications are in `bin` folder.
```
bin
├── face_detector
├── face_embedder
├── face_id
├── face_verification
└── vehicle_detector
```

#### Step 3: Run sample applications
We will run face detector and vehicle detector application. 
Our sample applications run `oneML` models with images in `assets/images` folder.
Change the directory to `bin` folder.
```
cd bin
```

To run the face detector application, simply run this command.
```
$ ./face_detector 
Faces: 1
Face 0
Score: 0.997635
Pose: Front
BBox: [(43.740112, 85.169624), (170.153168, 173.094971)]
Landmark 0: (109.936066, 95.690453)
Landmark 1: (149.844360, 94.360649)
Landmark 2: (130.055618, 117.397400)
Landmark 3: (112.128708, 137.498520)
Landmark 4: (146.398651, 136.513870)
```

To run the vehicle detector application, simply run this command.
```
$ ./vehicle_detector 
Vehicles: 8
Vehicle 0
Score: 0.907127
BBox[top=302.584076,left=404.385437,bottom=486.317841,right=599.864502]
Vehicle 1
Score: 0.899688
BBox[top=301.987000,left=651.770996,bottom=432.352997,right=920.845947]
Vehicle 2
Score: 0.875428
BBox[top=314.573608,left=143.590546,bottom=445.937286,right=367.556488]
Vehicle 3
Score: 0.873904
BBox[top=237.489685,left=100.689484,bottom=303.421906,right=279.191864]
Vehicle 4
Score: 0.842179
BBox[top=243.125473,left=328.062103,bottom=307.927338,right=473.017853]
Vehicle 5
Score: 0.822146
BBox[top=238.680069,left=563.760925,bottom=308.430756,right=705.746033]
Vehicle 6
Score: 0.653955
BBox[top=215.012100,left=477.994904,bottom=252.589249,right=547.705505]
Vehicle 7
Score: 0.620528
BBox[top=213.333237,left=641.528503,bottom=249.301697,right=742.615601]
```

### For Windows users

#### Step 0: Install required tools
To build `oneML` C++ applications on Windows, we need to install these tools as follows:
- Microsoft Visual Studio 16 2019 or newer
- CMake 3.17 or newer

#### Step 1: Build sample applications
We will download `oneML` library and models, and build sample C++ applications with `build.bat` script.
```
.\build.bat -t msvc-x64 -cc --clean
```

The `oneML` library and models are downloaded to `assets\binaries\msvc-x64`. 
```
assets\binaries\msvc-x64
├── bindings
├── config.yaml
├── include
├── lib
├── oneml-bootcamp-msvc-x64.tar.gz
├── README.md
└── share
```

The compiled C++ applications are in `bin\Release` folder.
```
bin\Release
├── face_detector.exe
├── face_embedder.exe
├── face_id.exe
├── face_verification.exe
└── vehicle_detector.exe
```

#### Step 2: Run sample applications
We will run face detector and vehicle detector application. 
Our sample applications run `oneML` models with images in `assets\images` folder.
Change the directory to `bin\Release` folder.
```
cd bin\Release
```

To run the face detector application, simply run this command.
```
.\face_detector.exe 
```
Result
```
Faces: 1
Face 0
Score: 0.997635
Pose: Front
BBox: [(43.740120, 85.169617), (170.153168, 173.094971)]
Landmark 0: (109.936081, 95.690453)
Landmark 1: (149.844376, 94.360649)
Landmark 2: (130.055634, 117.397392)
Landmark 3: (112.128716, 137.498520)
Landmark 4: (146.398636, 136.513870)
```

To run the vehicle detector application, simply run this command.
```
.\vehicle_detector.exe
```
Output
```
Vehicles: 8
Vehicle 0
Score: 0.907128
BBox[top=302.583893,left=404.385590,bottom=486.317871,right=599.864380]
Vehicle 1
Score: 0.899688
BBox[top=301.986755,left=651.771118,bottom=432.352966,right=920.846069]
Vehicle 2
Score: 0.875427
BBox[top=314.573425,left=143.590775,bottom=445.937408,right=367.556946]
Vehicle 3
Score: 0.873901
BBox[top=237.489792,left=100.689354,bottom=303.421936,right=279.191772]
Vehicle 4
Score: 0.842178
BBox[top=243.125641,left=328.062012,bottom=307.927002,right=473.017883]
Vehicle 5
Score: 0.822146
BBox[top=238.680267,left=563.760864,bottom=308.430756,right=705.746338]
Vehicle 6
Score: 0.653957
BBox[top=215.012009,left=477.994873,bottom=252.589355,right=547.705811]
Vehicle 7
Score: 0.620529
BBox[top=213.333160,left=641.528442,bottom=249.301712,right=742.615662]
```

## Setup and Build
Depending on your OS and on which programming language you would like to use, the
setup and build process is going to be slightly different. Please refer to the
respective section below.

### Linux
On Linux machines, it is always advised to used `docker` to create a reproducible
workspace and not have to worry about dependencies. If you don't want to use `docker`,
feel free to setup your local environment to match the one provided in our
[Dockerfiles](https://github.com/sertiscorp/oneML-bootcamp/tree/develop/docker).

#### Docker build
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

#### Apps build
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

On `x86_64`, Cuda-enabled GPU is also supported. To build `C++` apps for `x86-64-cuda` target:
```bash
./build.sh -t x86_64-cuda -cc --clean
```

If `--clean` is not specified, the existing `oneML` artifacts will be used and
the old build files will not be deleted.

### Windows
We provide support for our library and sample applications for Windows OS as well.

Unfortunately, there is no easy way to use our Dockerfiles in this environment, thus the
user has to rely on their own local environment and make sure all the dependencies are installed
before proceeding with the build process and run the applications.

#### Requirements
Only Windows 10 64 bit is currently supported.

#### Dependencies
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

#### Apps build
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

If `--clean` is not specified, the existing `oneML` artifacts will be used and
the old build files will not be deleted.

### Cuda
Currently `x86-64-cuda` target supports only Cuda 11.x runtime on Linux only. We also plan to support Cuda 10.2 in the future. Moreover, it is built with backward compatibility in mind. `x86-64-cuda` supports the following Cuda compute capabilities 
  * 7.5
  * 7.0
  * 6.1
  * 6.0
  * 5.2
  * 5.0

For compute capabilities later than `7.5`, it might not work.

## API Reference
- [C++/Java/Python APIs](https://sertiscorp.github.io/oneML-bootcamp)
- [Golang APIs](https://sertiscorp.github.io/oneML-bootcamp/godoc)

## Usage
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

## Roadmap
- Support `iOS`
- Support Cuda 10.2
- Support Golang on Windows