## Build & Install V8

http://code.google.com/apis/v8/build.html

If you want to build v8 with GCC 4.x at x64 platform, you should compile v8 with PIC (Position-Independent Code) mode, and set the arch to x64 for scons.

    export CCFLAGS=-fPIC
    scons arch=x64

## Install Ant

http://ant.apache.org/bindownload.cgi

## Build Jav8

    $ export V8_HOME=<v8_path>
    $ ant
