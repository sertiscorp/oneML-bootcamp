#!/bin/bash

set -e -u -o pipefail

# root path of the project
ROOT_DIR=$(pwd)

print_usage() {
  echo "Usage: ./build.sh \\"
  echo "        -t target_arch \\"
  echo "        [-cc cpp_apps] \\"
  echo "        [-py python_apps] \\"
  echo "        [-jni java_apps] \\"
  echo "        [-go go_apps] \\"
  echo "        [--clean clean_build] \\"
  echo ""
  echo "Example: ./build.sh \\"
  echo "          -t x86_64 \\"
  echo "          [-cc] \\"
  echo "          [-py] \\"
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

if [[ ! -n ${TARGET_ARCH} ]];
then
    echo "ERROR: Unspecified target architecture. Please use -t from CLI to specify an architecture."
    exit 1
else # check if architecture is supported
    #TODO: implement this
    :
fi

# clean build
clean_build() {
  echo "Cleanup build in progress..."
  rm -rf build \
         bin
  find . -name "*.tar.gz" -type f -delete
  find . -name "*.zip" -type f -delete
  find . -name "*.json" -type f -delete
  find assets/binaries/${TARGET_ARCH}/ -type f -not -name '*.md' -delete
  find assets/binaries/${TARGET_ARCH}/ -maxdepth 1 -mindepth 1 -type d -exec rm -rf '{}' \;
}

if [[ -v CLEAN ]];
then
  clean_build
fi

# artifacts
TAG=v0.1.0
BASE_URL=https://github.com/sertiscorp/oneML-bootcamp/releases/download/${TAG}/oneml-bootcamp-${TARGET_ARCH}.tar.gz
BINARY_PATH=${ROOT_DIR}/assets/binaries/${TARGET_ARCH}/oneml-bootcamp-${TARGET_ARCH}.tar.gz
if [ ! -f "$BINARY_PATH" ];
then
    echo "Downloading artifacts to ${BINARY_PATH}... "
    curl -L ${BASE_URL} > ${BINARY_PATH}
    tar xzf ${BINARY_PATH} -C ${ROOT_DIR}/assets/binaries/${TARGET_ARCH}/ --strip-components=1
fi

# toolchian
EXTRA_FLAGS=
if [[ ${TARGET_ARCH} == "aarch64-linux-android" || ${TARGET_ARCH} == "arm-linux-android" ]];
then
  if [[ ${TARGET_ARCH} == "arm-linux-android" ]];
  then
    ABI=armeabi-v7a
    EXTRA_FLAGS="-DANDROID_ARM_NEON=ON"
  else
    ABI=arm64-v8a
  fi
  
  EXTRA_FLAGS="-DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK_HOME}/build/cmake/android.toolchain.cmake \
              -DANDROID_ABI=${ABI} \
              -DANDROID_NATIVE_API_LEVEL=23 \
              -DANDROID_STL=c++_shared \
              ${EXTRA_FLAGS}"
elif [[ ${TARGET_ARCH} != "x86_64" ]];
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
  #TODO: implement this
  :
fi

if [[ -v JAVA_BUILD ]];
then
  #TODO: implement this
  :
fi

if [[ -v GO_BUILD ]];
then
  #TODO: implement this
  :
fi
