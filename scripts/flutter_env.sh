#!/usr/bin/env bash
# Helper to expose the preinstalled Flutter and Android SDK toolchains in this container.
export FLUTTER_HOME=${FLUTTER_HOME:-/opt/flutter/flutter}
export ANDROID_SDK_ROOT=${ANDROID_SDK_ROOT:-/opt/android-sdk}
export PATH="$FLUTTER_HOME/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$PATH"
