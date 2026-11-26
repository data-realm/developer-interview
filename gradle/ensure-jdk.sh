#!/bin/sh
# Makes sure a JDK 17+ is available for the Gradle wrapper.
#
# If JAVA_HOME or the `java` on PATH is already JDK 17+, prints nothing and the wrapper carries on
# as normal. Otherwise it downloads a portable Eclipse Temurin JDK 21 into .jdk/ in this repo (once,
# about 200 MB, nothing installed system-wide) and prints its path for the wrapper to use as JAVA_HOME.
#
# Called from ./gradlew. Delete .jdk/ to remove the downloaded JDK.

set -e
MIN_VERSION=17
JDK_VERSION=21
ROOT=$(cd "$(dirname "$0")/.." && pwd)
LOCAL_JDK="$ROOT/.jdk/temurin-$JDK_VERSION"

major_version() {
    "$1" -version 2>&1 | awk -F'"' '/version/ { split($2, v, "."); print (v[1] == "1") ? v[2] : v[1]; exit }'
}

is_usable() {
    [ -x "$1" ] || return 1
    version=$(major_version "$1" 2>/dev/null) || return 1
    [ -n "$version" ] && [ "$version" -ge "$MIN_VERSION" ] 2>/dev/null
}

local_home() {
    if [ -d "$LOCAL_JDK/Contents/Home" ]; then echo "$LOCAL_JDK/Contents/Home"; else echo "$LOCAL_JDK"; fi
}

# 1. A previously downloaded JDK wins over nothing, but not over a good system JDK.
if [ -n "$JAVA_HOME" ] && is_usable "$JAVA_HOME/bin/java"; then exit 0; fi
if [ -z "$JAVA_HOME" ] && command -v java >/dev/null 2>&1 && is_usable "$(command -v java)"; then exit 0; fi
if is_usable "$(local_home)/bin/java"; then local_home; exit 0; fi

# 2. Download Temurin for this OS and CPU.
case "$(uname -s)" in
    Darwin) os=mac ;;
    Linux) os=linux ;;
    *) echo "No JDK $MIN_VERSION+ found and can't auto-install on $(uname -s). Please install one, e.g. from https://adoptium.net" >&2; exit 1 ;;
esac
case "$(uname -m)" in
    x86_64 | amd64) arch=x64 ;;
    arm64 | aarch64) arch=aarch64 ;;
    *) echo "No JDK $MIN_VERSION+ found and can't auto-install on $(uname -m). Please install one, e.g. from https://adoptium.net" >&2; exit 1 ;;
esac

url="https://api.adoptium.net/v3/binary/latest/$JDK_VERSION/ga/$os/$arch/jdk/hotspot/normal/eclipse"
echo "No JDK $MIN_VERSION+ found. Downloading Temurin $JDK_VERSION into .jdk/ (one-time, ~200 MB)..." >&2
mkdir -p "$ROOT/.jdk"
tmp="$ROOT/.jdk/download.$$"
rm -rf "$tmp" && mkdir -p "$tmp"
trap 'rm -rf "$tmp"' EXIT
curl -fL --progress-bar "$url" -o "$tmp/jdk.tar.gz" >&2
tar -xzf "$tmp/jdk.tar.gz" -C "$tmp"
rm -rf "$LOCAL_JDK"
mv "$tmp"/jdk-* "$LOCAL_JDK"
is_usable "$(local_home)/bin/java" || { echo "Downloaded JDK doesn't run; please install JDK $MIN_VERSION+ manually." >&2; exit 1; }
echo "Installed $("$(local_home)/bin/java" -version 2>&1 | head -1) in .jdk/" >&2
local_home
