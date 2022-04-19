# Golang sample application
Source code for the applications in Golang programming language.

## How to run
1. Set up environment variables. Add a folder which contains `liboneml.so` to `LD_LIBRARY_PATH`. Replace `<target_arch>` with your target architecture. 
```
export LD_LIBRARY_PATH="/path/to/oneml-bootcamp/assets/binaries/<target_arch>/lib:$LD_LIBRARY_PATH"
```
For example,
```
export LD_LIBRARY_PATH="/workspace/oneml-bootcamp/assets/binaries/x86_64/lib:$LD_LIBRARY_PATH"
```

2. Run the app
```
go run <app_name>.go
```
For example,
```
go run face_id.go
```
