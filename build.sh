#!/bin/bash

set -e -u -o pipefail

# root path of the project
ROOT_DIR=$(pwd)

print_usage() {
  echo "Usage: ./build.sh \\"
  echo "        -t target_arch \\"
  echo "        [-cc cpp_apps] \\"
  echo "        [-py python_apps] \\"
  echo "        [-cs csharp] \\"
  echo "        [-jni java_apps] \\"
  echo "        [-go go_apps] \\"
  echo "        [--clean clean_build] \\"
  echo ""
  echo "Example: ./build.sh \\"
  echo "          -t x86_64 \\"
  echo "          [-cc] \\"
  echo "          [-py] \\"
  echo "          [-cs] \\"
  echo "          [-jni] \\"
  echo "          [-go] \\"
  echo "          [--clean] \\"
  echo ""
}

POSITIONAL=()

if [[ ($# -gt 0 && ("$1" == "--help" || "$1" == "-h")) || $# == 0 ]];
then
  print_usage
  exit 1
fi

TARGET_ARCH=

while [[ $# -gt 0 ]]
do
  key="$1"
  case ${key} in
    -t)
      TARGET_ARCH="$2"
      shift # past argument
      shift # past value
      ;;
    -cc)
      CPP_BUILD=true
      shift # past argument
      ;;
    -py)
      PYTHON_BUILD=true
      shift # past argument
      ;;
    -cs)
      CSHARP_BUILD=true
      shift # past argument
      ;;
    -jni)
      JAVA_BUILD=true
      shift # past argument
      ;;
    -go)
      GO_BUILD=true
      shift # past argument
      ;;
    --clean)
	  CLEAN=true
	  shift # past argument
	  ;;
    *) # unknown option
      print_usage
      exit 1
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

SUPPORTED_TARGETS=( x86_64 x86_64-cuda aarch64-linux-gnu arm-linux-gnueabihf aarch64-linux-android arm-linux-android )
if [[ ! -n ${TARGET_ARCH} ]];
then
    echo "ERROR: Unspecified target architecture. Please use -t from CLI to specify an architecture."
    exit 1
elif [[ ! " ${SUPPORTED_TARGETS[*]} " =~ " ${TARGET_ARCH} " ]]; # check if architecture is supported
then
  echo "ERROR: target ${TARGET_ARCH} is not supported yet. Please use one of (${SUPPORTED_TARGETS[@]})."
  exit 1
fi

# clean build
BINARY_PATH=${ROOT_DIR}/assets/binaries/${TARGET_ARCH}
clean_build() {
  echo "Cleanup build in progress..."
  rm -rf build \
         bin \
         apps/java/classes
  find . -name "*.tar.gz" -type f -delete
  find . -name "*.zip" -type f -delete
  find . -name "*.json" -type f -delete
  find ${BINARY_PATH}/ -type f -not -name '*.md' -delete
  find ${BINARY_PATH}/ -maxdepth 1 -mindepth 1 -type d -exec rm -rf '{}' \;
}

if [[ -v CLEAN ]];
then
  clean_build
fi

# artifacts
TAG=v0.11.0-test1
BASE_URL=https://github.com/sertiscorp/oneML-bootcamp/releases/download/${TAG}/oneml-bootcamp-${TARGET_ARCH}.tar.gz
if [ ! -f "$BINARY_PATH/oneml-bootcamp-${TARGET_ARCH}.tar.gz" ];
then
    echo "Downloading artifacts to ${BINARY_PATH}... "
    curl -L ${BASE_URL} > ${BINARY_PATH}/oneml-bootcamp-${TARGET_ARCH}.tar.gz
    tar xzf ${BINARY_PATH}/oneml-bootcamp-${TARGET_ARCH}.tar.gz -C ${BINARY_PATH}/ --strip-components=1
fi

# toolchian
EXTRA_FLAGS=
if [[ ${TARGET_ARCH} == "aarch64-linux-android" || ${TARGET_ARCH} == "arm-linux-android" ]];
then
  ANDROID_BUILD=true
  if [[ ${TARGET_ARCH} == "arm-linux-android" ]];
  then
    export ABI=armeabi-v7a
    EXTRA_FLAGS="-DANDROID_ARM_NEON=ON"
  else
    export ABI=arm64-v8a
  fi
  
  EXTRA_FLAGS="-DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK_HOME}/build/cmake/android.toolchain.cmake \
              -DANDROID_ABI=${ABI} \
              -DANDROID_NATIVE_API_LEVEL=23 \
              -DANDROID_STL=c++_shared \
              ${EXTRA_FLAGS}"
elif [[ ${TARGET_ARCH} != "x86_64" && ${TARGET_ARCH} != "x86_64-cuda" ]];
then
  EXTRA_FLAGS="-DCMAKE_TOOLCHAIN_FILE=cmake/toolchains/$TARGET_ARCH.cmake"
fi

# apps build
if [[ -v CPP_BUILD ]];
then
  mkdir -p build && cd build
  cmake -DTARGET_ARCH=${TARGET_ARCH} ${EXTRA_FLAGS} .. && make -j$(nproc)
fi

if [[ -v PYTHON_BUILD ]];
then
  if [[ -v CLEAN || ! $(pip list | grep oneML) ]];
  then
    pip3 install -U pip setuptools wheel
    cd ${BINARY_PATH}/bindings/python && ./install_oneml.sh
  else
    echo "Using existing oneML installation. Use --clean to reinstall."
  fi
fi

if [[ -v CSHARP_BUILD ]];
then
  cd ${BINARY_PATH}/bindings/csharp/face && ./build.sh
  cd ${BINARY_PATH}/bindings/csharp/alpr && ./build.sh
  if [[ -v LD_LIBRARY_PATH ]];
  then
    export LD_LIBRARY_PATH=${BINARY_PATH}/bindings/csharp/face/build:${BINARY_PATH}/bindings/csharp/alpr/build:$LD_LIBRARY_PATH
  else
    export LD_LIBRARY_PATH=${BINARY_PATH}/bindings/csharp/face/build:${BINARY_PATH}/bindings/csharp/alpr/build
  fi

  APPS=FaceEmbedderApp,FaceIdApp,FaceDetectorApp,FaceVerificationApp,FacePadApp,VehicleDetectorApp,EKYCApp
  for APP in ${APPS//,/ };
  do
    dotnet new console -o ${APP}
    cd ${APP}
    dotnet build
    cd ..
  done
fi

if [[ -v JAVA_BUILD && ! -v ANDROID_BUILD ]];
then
  cd ${BINARY_PATH}/bindings/java/face && ./build.sh
  cd ${BINARY_PATH}/bindings/java/alpr && ./build.sh
  if [[ -v LD_LIBRARY_PATH ]];
  then
    export LD_LIBRARY_PATH=${BINARY_PATH}/bindings/java/face/build:${BINARY_PATH}/bindings/java/alpr/build:$LD_LIBRARY_PATH
  else
    export LD_LIBRARY_PATH=${BINARY_PATH}/bindings/java/face/build:${BINARY_PATH}/bindings/java/alpr/build
  fi

  cd ${ROOT_DIR}
  mkdir -p apps/java/classes && cd apps/java
  CLASSPATH=${ROOT_DIR}/apps/java/classes:${BINARY_PATH}/bindings/java/face/oneml/oneml-face-api.jar:${BINARY_PATH}/bindings/java/alpr/oneml/oneml-alpr-api.jar:${ROOT_DIR}/apps/java
  APPS=FaceEmbedderApp,FaceIdApp,FaceDetectorApp,FaceVerificationApp,FacePadApp,VehicleDetectorApp,EKYCApp
  for APP in ${APPS//,/ };
  do
    javac -cp ${CLASSPATH} -d classes ${APP}.java
  done
elif [[ -v JAVA_BUILD && -v ANDROID_BUILD ]];
then
  # A list of all Android apps to be built
  ANDROID_APPS=android-simple,android-camera
  for ANDROID_APP in ${ANDROID_APPS//,/ };
  do
    echo "Build Android app: ${ANDROID_APP}"
    cd ${ROOT_DIR}/apps/${ANDROID_APP}
    touch local.properties
    ./gradlew build
  done
fi

if [[ -v GO_BUILD ]];
then
  LIB_NAMES=("face" "alpr")
  BINARY_ABS_PATH=$(realpath ${BINARY_PATH})
  # Update paths to oneML lib and headers
  for LIB_NAME in ${LIB_NAMES[@]}
  do
    cd ${BINARY_PATH}/bindings/go
    sed -i "s~/path/to/oneml/lib~${BINARY_ABS_PATH}/lib~g" ${LIB_NAME}/lib.go
    sed -i "s~/path/to/oneml/include~${BINARY_ABS_PATH}/include~g" ${LIB_NAME}/lib.go
  done

  # Build all apps
  cd ${ROOT_DIR}/apps/golang
  go mod edit -replace oneml=../../assets/binaries/${TARGET_ARCH}/bindings/go
  APPS=face_detector,face_embedder,face_id,face_verification,face_pad,vehicle_detector,utils_app,ekyc
  for APP in ${APPS//,/ };
  do
    go build ${APP}.go
  done
fi
