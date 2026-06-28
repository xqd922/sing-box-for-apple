#!/bin/bash

# sing-box Android - Complete Build and Release Script
# This script builds the app and prepares it for release

set -e

echo "=========================================="
echo "sing-box Android - Build & Release"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "sing-box-android/build.gradle.kts" ]; then
    print_error "Please run this script from the repository root directory"
    exit 1
fi

cd sing-box-android

# Step 1: Check dependencies
echo "Step 1: Checking dependencies..."
echo ""

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install JDK 17."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi
print_status "Java $JAVA_VERSION found"

# Check if Gradle wrapper exists
if [ ! -f "gradlew" ]; then
    print_warning "Gradle wrapper not found. Creating..."
    gradle wrapper --gradle-version 8.2
fi
print_status "Gradle wrapper ready"

echo ""

# Step 2: Check for Libbox
echo "Step 2: Checking for Libbox..."
echo ""

if [ ! -f "app/libs/libbox.aar" ]; then
    print_warning "libbox.aar not found in app/libs/"
    print_warning "You need to build or download Libbox first."
    echo ""
    echo "Options:"
    echo "  1. Build Libbox: ./build_libbox.sh"
    echo "  2. Download from GitHub releases"
    echo "  3. Continue anyway (app will crash without Libbox)"
    echo ""
    read -p "Continue without Libbox? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    print_status "libbox.aar found"
fi

echo ""

# Step 3: Clean previous builds
echo "Step 3: Cleaning previous builds..."
./gradlew clean
print_status "Clean completed"

echo ""

# Step 4: Run tests
echo "Step 4: Running tests..."
if ./gradlew test; then
    print_status "All tests passed"
else
    print_warning "Some tests failed (continuing anyway)"
fi

echo ""

# Step 5: Build Debug APK
echo "Step 5: Building Debug APK..."
./gradlew assembleDebug
print_status "Debug APK built"

echo ""

# Step 6: Build Release APK
echo "Step 6: Building Release APK..."
./gradlew assembleRelease
print_status "Release APK built"

echo ""

# Step 7: Build Release AAB
echo "Step 7: Building Release AAB..."
./gradlew bundleRelease
print_status "Release AAB built"

echo ""

# Step 8: Display results
echo "=========================================="
echo "Build Complete!"
echo "=========================================="
echo ""

echo "Output files:"
echo ""

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    DEBUG_SIZE=$(du -h "app/build/outputs/apk/debug/app-debug.apk" | cut -f1)
    echo "  Debug APK: app/build/outputs/apk/debug/app-debug.apk ($DEBUG_SIZE)"
fi

if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    RELEASE_SIZE=$(du -h "app/build/outputs/apk/release/app-release.apk" | cut -f1)
    echo "  Release APK: app/build/outputs/apk/release/app-release.apk ($RELEASE_SIZE)"
fi

if [ -f "app/build/outputs/bundle/release/app-release.aab" ]; then
    AAB_SIZE=$(du -h "app/build/outputs/bundle/release/app-release.aab" | cut -f1)
    echo "  Release AAB: app/build/outputs/bundle/release/app-release.aab ($AAB_SIZE)"
fi

echo ""

# Step 9: Create release package
echo "Step 9: Creating release package..."
RELEASE_DIR="release/v1.0.0"
mkdir -p "$RELEASE_DIR"

if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    cp "app/build/outputs/apk/release/app-release.apk" "$RELEASE_DIR/sing-box-android-v1.0.0.apk"
fi

if [ -f "app/build/outputs/bundle/release/app-release.aab" ]; then
    cp "app/build/outputs/bundle/release/app-release.aab" "$RELEASE_DIR/sing-box-android-v1.0.0.aab"
fi

# Copy documentation
cp ../README.md "$RELEASE_DIR/"
cp ../CHANGELOG.md "$RELEASE_DIR/"
cp BUILD_LIBBOX.md "$RELEASE_DIR/"
cp RELEASE_GUIDE.md "$RELEASE_DIR/"

print_status "Release package created in $RELEASE_DIR/"

echo ""

# Step 10: Display next steps
echo "=========================================="
echo "Next Steps"
echo "=========================================="
echo ""

echo "1. Test the APK:"
echo "   adb install $RELEASE_DIR/sing-box-android-v1.0.0.apk"
echo ""

echo "2. Create GitHub Release:"
echo "   - Go to: https://github.com/xqd922/sing-box-for-apple/releases"
echo "   - Click 'Draft a new release'"
echo "   - Tag: v1.0.0"
echo "   - Title: sing-box for Android v1.0.0"
echo "   - Upload the APK and AAB files"
echo "   - Copy release notes from CHANGELOG.md"
echo ""

echo "3. Or use GitHub CLI:"
echo "   gh release create v1.0.0 \\"
echo "     --title 'sing-box for Android v1.0.0' \\"
echo "     --notes-file ../CHANGELOG.md \\"
echo "     $RELEASE_DIR/*"
echo ""

echo "4. Submit to Play Store:"
echo "   - Upload the AAB file to Google Play Console"
echo "   - Follow the RELEASE_GUIDE.md instructions"
echo ""

print_status "Build and release preparation complete!"
