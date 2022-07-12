# C# sample application
Source code for the applications in C# programming language.

## How to run
Set up environment variables. Add a folder which contains `liboneml.so` to `LD_LIBRARY_PATH`. Replace `<target_arch>` with your target architecture. 
```
export LD_LIBRARY_PATH="/path/to/oneml-bootcamp/assets/binaries/<target_arch>/lib:$LD_LIBRARY_PATH"
```
For example,
```
export LD_LIBRARY_PATH="/workspace/oneml-bootcamp/assets/binaries/x86_64/lib:$LD_LIBRARY_PATH"
```

Run the app:
```
dotnet new console -o MyApp
cd MyApp
cp ../<app_name>.cs Program.cs
dotnet run
```

Or:
```
dotnet new console -o MyApp
cd MyApp
cp ../<app_name>.cs Program.cs
dotnet build
bin/Debug/net6.0/MyApp
```
