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
cd Python-3.7.6
./configure --host "$TARGET" --build "$TARGET""$ANDROID_SDK_VERSION" --enable-shared \
  --enable-ipv6 ac_cv_file__dev_ptmx=yes \
  ac_cv_file__dev_ptc=no --without-ensurepip ac_cv_little_endian_double=yes \
  --prefix=$PWD/Python-3.7.6-built
make
make install
