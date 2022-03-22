ARG base_image
FROM $base_image

ENV TZ=Asia/Bangkok

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
        build-essential \
        curl \
        openssl \
        libssl-dev && \
    apt-get clean > /dev/null && \
    apt-get autoremove -y --purge && \
    apt-get update -qq > /dev/null && \
    rm -rf /var/lib/apt/lists/ && \
    rm -rf /tmp/* /var/tmp/*

# CMake
RUN apt-get --assume-yes remove --purge cmake && \
    hash -r
RUN curl -L https://github.com/Kitware/CMake/releases/download/v3.22.1/cmake-3.22.1.tar.gz > cmake.tar.gz && \
    tar -zxf cmake.tar.gz && \
    rm cmake.tar.gz
RUN cd cmake-* && \
    ./bootstrap && \
    make -j$(nproc) && \
    make install && \
    cd .. && \
    rm -rf cmake-*