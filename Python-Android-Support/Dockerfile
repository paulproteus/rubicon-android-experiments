FROM ubuntu:18.04
RUN apt-get update && apt-get -y install wget xz-utils unzip

WORKDIR /opt/ndk
RUN wget -q https://dl.google.com/android/repository/android-ndk-r20b-linux-x86_64.zip && unzip -q android-ndk-r20b-linux-x86_64.zip && rm android-ndk-r20b-linux-x86_64.zip
ENV NDK /opt/ndk/android-ndk-r20b

WORKDIR /opt/python-build
# TODO(someday): Remove build-dep python, and figure out more specific dependencies.
RUN sed -i 's,[#] deb-src,deb-src,' /etc/apt/sources.list && apt-get update && apt-get -y build-dep python3 && apt-get -y install python3.7 pkg-config git zip
RUN git clone -q https://github.com/beeware/rubicon-java
RUN wget -q https://www.python.org/ftp/python/3.7.6/Python-3.7.6.tar.xz && tar xf Python-3.7.6.tar.xz && rm Python-3.7.6.tar.xz
RUN wget -q https://github.com/libffi/libffi/releases/download/v3.3/libffi-3.3.tar.gz && tar xf libffi-3.3.tar.gz && rm libffi-3.3.tar.gz
# Apply a C extensions linker hack; already fixed in Python 3.8+; see https://github.com/python/cpython/commit/254b309c801f82509597e3d7d4be56885ef94c11
RUN sed -i -e s,'libraries or \[\],\["python3.7m"] + libraries if libraries else \["python3.7m"\],' Python-3.7.6/Lib/distutils/extension.py
# Apply a hack to pylifecycle.c that injects a thread that send stdout/stderr to Android logging.
RUN wget -q https://gist.githubusercontent.com/paulproteus/61c7eac39da079f014f40f9a52ef87f8/raw/925468444357a0d56dc05e072a7ec3cb1049ed39/pylifecycle.c -O Python-3.7.6/Python/pylifecycle.c
ENV RUBICON_JAVA_PATH /opt/python-build/rubicon-java
COPY build.sh .
RUN bash -x build.sh

WORKDIR /opt/jdk
RUN wget -q https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.5%2B10/OpenJDK11U-jdk_x64_linux_hotspot_11.0.5_10.tar.gz && tar xf OpenJDK11U-jdk_x64_linux_hotspot_11.0.5_10.tar.gz && rm OpenJDK11U-jdk_x64_linux_hotspot_11.0.5_10.tar.gz
ENV JAVA_HOME /opt/jdk/jdk-11.0.5+10/
ENV PATH "/opt/jdk/jdk-11.0.5+10/bin:${PATH}"

WORKDIR /opt/python-build
COPY buildrubicon.sh .
RUN bash -x buildrubicon.sh