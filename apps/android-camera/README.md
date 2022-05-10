# Simple camera app for real-time face identification with oneML

### Overview

This app is based on [TensorFlow Lite Object Detection Android Demo](https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection/android).

It is a camera app that continuously detects faces (with bounding boxes) in the frames seen by the device back camera and, if any ID has been registered, identifies the subjects in the frames as one of the registered IDs or as `unknown`.

All the model heavy-lifting is been carried by `oneML`, indeed this app mostly focus on the `app` implementation and on sending/receiving data through the `oneML` API.

To build and run this app successfully, it's necessary to get a release package of `oneML` separately (accessible on GCloud or through MLE team).

These instructions will walk you through building and running the demo on an Android device.

## Build the demo using Android Studio

### Prerequisites

*   If you don't have already, install
    **[Android Studio](https://developer.android.com/studio/index.html)**,
    following the instructions on the website.

*   You need an Android device and Android development environment with minimum
    API 23.

*   Android Studio 3.2 or later.

### Building

*   Open Android Studio, and from the Welcome screen, select Open an existing
    Android Studio project.

*   From the Open File or Project window that appears, navigate to and select
    the `oneml-android-camera-app` directory from wherever you cloned this repo from.
    Click OK.

*   If it asks you to do a Gradle Sync, click OK.

*   You may also need to install various platforms and tools, if you are asked to do
    so, please confirm and install everything required by the project.

*   If it asks you to use Instant Run, click Proceed Without Instant Run.

*   Also, you need to have an Android device plugged in with developer options
    enabled at this point. See
    **[here](https://developer.android.com/studio/run/device)** for more details
    on setting up developer devices.

### Download oneML Java Package
- Download a oneML Java package archive for `aarch64-linux-android` or `arm-linux-android` from 
  [the release page](https://github.com/sertiscorp/oneML-bootcamp/releases).
- Extract the archive and place files at `oneml-bootcamp/assets/binaries/<target_arch>` 
  where `<target_arch>` is `aarch64-linux-android` or `arm-linux-android` which you downloaded previously.

The models that are used in this application are:
- face detector (to detect faces)
- face embedder (to embed detected faces)

The identification process is facilitated by the `FaceID` API of `oneML`.

## Build with Gradle Wrapper
For Android development environment, You can run a Docker container with an image at `oneml-bootcamp/docker/Dockerfile.android`. 
Then, run the below command to build an Android app.
```
$ ./gradlw build
```
The Android app package will be available at `./app/build/outputs/apk/debug/app-debug.apk`.

Please note that you can specify the target ABI by `ABI` env (the default is `arm64-v8a`). For example,
```
$ export ABI=armeabi-v7a
$ ./gradlw build
```

## Usage

The application layout is very simple. There are three main elements:
- camera stream
- switch camera button (bottom left corner)
- register ID button (bottom right corner)

As soon as the app startups, the camera stream will be visible and bounding boxes of the
detected faces are shown overlayed to the camera stream automatically.

After registering one or more IDs (by using the register ID button), the bounding boxes of
faces detected in each frame will be decorated with additional information:
- ID name if face is identified, `unknown` otherwise
- bounding box color will be green if the face can be identified, red otherwise

## Additional Note

This app is just a demo. Once the app is closed or even when switching between front to back
camera or viceversa, all the registered IDs are wiped out and users must re-register.
