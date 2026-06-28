#!/bin/bash

# Build Libbox for Android
# This script builds the sing-box core library for Android

set -e

echo "Building Libbox for Android..."

# Check if Go is installed
if ! command -v go &> /dev/null; then
    echo "Error: Go is not installed. Please install Go first."
    exit 1
fi

# Install gomobile if not installed
if ! command -v gomobile &> /dev/null; then
    echo "Installing gomobile..."
    go install golang.org/x/mobile/cmd/gomobile@latest
    go install golang.org/x/mobile/cmd/gobind@latest
fi

# Initialize gomobile
echo "Initializing gomobile..."
gomobile init

# Clone sing-box if not exists
if [ ! -d "sing-box" ]; then
    echo "Cloning sing-box repository..."
    git clone https://github.com/SagerNet/sing-box.git
fi

cd sing-box

# Checkout stable version
echo "Checking out stable version..."
git checkout v1.8.0

# Build for Android
echo "Building libbox for Android..."
gomobile bind -v -androidapi 26 \
    -javapkg=io.nekohasekai.sagernet \
    -o ../app/libs/libbox.aar \
    ./experimental/libbox

cd ..

echo "Build complete! libbox.aar is in app/libs/"
