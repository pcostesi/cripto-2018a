#!/bin/bash

set -e 

cd stegobmp && \
    docker build -t stego:latest . && \
    docker run --rm -it stego:latest "$@"
