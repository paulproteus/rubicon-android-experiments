# Toolchain setup
export HOST_TAG="$(ls -1 $NDK/toolchains/llvm/prebuilt | head -n1)"
export TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_TAG
export TARGET=x86_64-linux-android
export ANDROID_SDK_VERSION=29
export AR=$TOOLCHAIN/bin/$TARGET-ar
export AS=$TOOLCHAIN/bin/$TARGET-as
export CC=$TOOLCHAIN/bin/${TARGET}${ANDROID_SDK_VERSION}-clang
export CXX=$TOOLCHAIN/bin/${TARGET}${ANDROID_SDK_VERSION}-clang++
export LD=$TOOLCHAIN/bin/$TARGET-ld
export RANLIB=$TOOLCHAIN/bin/$TARGET-ranlib
export STRIP=$TOOLCHAIN/bin/$TARGET-strip
export READELF=$TOOLCHAIN/bin/$TARGET-readelf
export CFLAGS="-fPIC -Wall -O0 -g"
export LDFLAGS='-landroid -llog'

# Build libffi, so that we can have ctypes :)
cd libffi-3.3
./configure --host "$TARGET" --build "$TARGET""$ANDROID_SDK_VERSION" --prefix=$PWD/built
make
make install
cd ..

# Copy it into the app so that `ctypes` can use it
cp libffi-3.3/built/lib/libffi*so ../HelloWorldApp/app/libs/x86_64/

# Build Python
cd Python-3.7.6
LDFLAGS=`PKG_CONFIG_PATH="$PWD/../libffi-3.3/built/lib/pkgconfig" pkg-config --libs-only-L libffi` PKG_CONFIG_PATH="$PWD/../libffi-3.3/built/lib/pkgconfig" LD_LIBRARY_PATH="$PWD/../libffi-3.3/built" ./configure --host "$TARGET" --build "$TARGET""$ANDROID_SDK_VERSION" --enable-shared \
  --enable-ipv6 ac_cv_file__dev_ptmx=yes \
  ac_cv_file__dev_ptc=no --without-ensurepip ac_cv_little_endian_double=yes \
  --prefix=$PWD/Python-3.7.6-built
make
make install
cd ..

# Copy the python .so into the app
chmod u+w ../HelloWorldApp/app/libs/x86_64/*.so || true
cp $PWD/Python-3.7.6/Python-3.7.6-built/lib/*.so ../HelloWorldApp/app/libs/x86_64

# Copy the compiled Python stdlib components into the app's libs/ directory,
# so that the app is allowed to dlopen() them.
mkdir -p ../HelloWorldApp/app/src/main/assets/pythonhome-bin/
cp -a $PWD/Python-3.7.6/Python-3.7.6-built/lib/python3.7/lib-dynload/ ../HelloWorldApp/app/libs/x86_64

# Copy the rubicon Python module in, so that rubicon-java can start.
rm -rf $PWD/Python-3.7.6/Python-3.7.6-built/lib/python3.7/rubicon
cp -a $PWD/../../../beeware/rubicon-java/rubicon $PWD/Python-3.7.6/Python-3.7.6-built/lib/python3.7/

# Add our hello-world thing
printf 'import cmath\nprint("hello, world, small number", cmath.sin(6.28))' > $PWD/Python-3.7.6/Python-3.7.6-built/lib/helloworld.py

# Zip up the architecture-independent parts of the Python stdlib, so the Android
# app can unpack it at startup.
pushd $PWD/Python-3.7.6/Python-3.7.6-built
ZIP_INDEP_OUTPUT="$(mktemp -t python-tarball -d)/pythonhome-arch-indep.zip"
zip -r "$ZIP_INDEP_OUTPUT" . -x '*.so' -x '*.so.*' -x '*.a'
popd

cp "$ZIP_INDEP_OUTPUT" ../HelloWorldApp/app/src/main/assets/pythonhome.zip
