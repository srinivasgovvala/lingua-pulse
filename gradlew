#!/bin/sh

# ----------------------------------------------------------------------------
# Gradle Wrapper script for LinguaPulse
# ----------------------------------------------------------------------------

APP_BASE_NAME=`basename "$0"`
APP_HOME="`pwd -P`"

# Fallback: if 'gradle' is installed in system/Termux PATH, invoke it directly
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

# Locate Java
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command was found in your PATH." >&2
    echo "Please set JAVA_HOME or install OpenJDK (e.g., pkg install openjdk-17 in Termux)." >&2
    exit 1
fi

WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
    exec "$JAVACMD" -jar "$WRAPPER_JAR" "$@"
else
    echo "gradle-wrapper.jar not found; invoking system gradle or bootstrapping..."
    exec gradle "$@"
fi
