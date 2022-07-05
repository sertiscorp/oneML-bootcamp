ARG base_image
FROM $base_image

ENV TZ=Asia/Bangkok
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
        build-essential \
        curl \
        openssl \
        libssl-dev \
        python3 \
        python3-pip \
        openjdk-8-jdk && \
    apt-get clean > /dev/null && \
    apt-get autoremove -y --purge && \
    apt-get update -qq > /dev/null && \
    rm -rf /var/lib/apt/lists/ && \
    rm -rf /tmp/* /var/tmp/*

# Install CMake
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

# Install Golang
RUN curl -o go-linux-amd64.tar.gz -Lq https://golang.org/dl/go1.15.12.linux-amd64.tar.gz && \
    tar -zxf go-linux-amd64.tar.gz && \
    mv go /usr/local/go && \
    rm go-linux-amd64.tar.gz
ENV GOROOT=/usr/local/go
ENV GOBIN=$GOROOT/bin
ENV PATH=$PATH:$GOBIN

# Install dotnet
RUN curl https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb > packages-microsoft-prod.deb  && \
    dpkg -i packages-microsoft-prod.deb && \
    rm packages-microsoft-prod.deb && \
    apt-get update && \
    apt-get install -y \
        dotnet-sdk-6.0 \
        dotnet-runtime-6.0
