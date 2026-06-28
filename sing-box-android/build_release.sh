#!/bin/bash

# Release build script for sing-box Android

set -e

echo "=========================================="
echo "sing-box Android Release Build"
echo "=========================================="

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Run tests
echo "Running tests..."
./gradlew test

# Build release APK
echo "Building release APK..."
./gradlew assembleRelease

# Build release AAB (for Play Store)
echo "Building release AAB..."
./gradlew bundleRelease

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "Build Successful!"
    echo "=========================================="
    echo ""
    echo "Output files:"
    echo "  APK: app/build/outputs/apk/release/app-release.apk"
    echo "  AAB: app/build/outputs/bundle/release/app-release.aab"
    echo ""
    echo "File sizes:"
    ls -lh app/build/outputs/apk/release/*.apk 2>/dev/null || echo "  APK not found"
    ls -lh app/build/outputs/bundle/release/*.aab 2>/dev/null || echo "  AAB not found"
    echo ""
    echo "Next steps:"
    echo "  1. Test the APK on a device"
    echo "  2. Sign the AAB for Play Store upload"
    echo "  3. Create a GitHub release"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "Build Failed!"
    echo "=========================================="
    echo "Check the error messages above."
    exit 1
fi
