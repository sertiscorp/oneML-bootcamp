cmake_minimum_required(VERSION 3.17 FATAL_ERROR)
message("CMAKE_VERSION: ${CMAKE_VERSION}")
project(oneML-bootcamp CXX)

# Configure build type
set(CMAKE_BUILD_TYPE Release)

# Configure to put executable in top level bin directory
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/bin)

# Detect if we're compiling for Android.
set(TEST_FOR_ANDROID_CXX
    "#ifndef __ANDROID__"
    "#error"
    "#endif"
    "int main() {}")
set(TEST_FOR_ANDROID_DIR "${CMAKE_BINARY_DIR}${CMAKE_FILES_DIRECTORY}/CMakeTmp")
set(TEST_FOR_ANDROID_FILE "${TEST_FOR_ANDROID_DIR}/test_for_android.cc")
string(REPLACE ";" "\n" TEST_FOR_ANDROID_CXX_TEXT "${TEST_FOR_ANDROID_CXX}")
file(WRITE "${TEST_FOR_ANDROID_FILE}" "${TEST_FOR_ANDROID_CXX_TEXT}")
try_compile(BUILD_FOR_ANDROID "${CMAKE_BINARY_DIR}${CMAKE_FILES_DIRECTORY}" "${TEST_FOR_ANDROID_FILE}")
file(REMOVE "${TEST_FOR_ANDROID_FILE}")
if(BUILD_FOR_ANDROID)
    message(STATUS "Building for Android")
endif()

# Import oneML
message(STATUS "Building for ${TARGET_ARCH}")
include_directories(${PROJECT_SOURCE_DIR}/assets/binaries/${TARGET_ARCH}/include)
if(MSVC)
    add_library(oneml STATIC IMPORTED)
    set_target_properties(oneml PROPERTIES IMPORTED_LOCATION  ${PROJECT_SOURCE_DIR}/assets/binaries/${TARGET_ARCH}/lib/oneml.lib)
else()
    add_library(oneml SHARED IMPORTED)
    set_target_properties(oneml PROPERTIES IMPORTED_LOCATION  ${PROJECT_SOURCE_DIR}/assets/binaries/${TARGET_ARCH}/lib/liboneml.so)
endif()

# Build apps
add_subdirectory(apps)
