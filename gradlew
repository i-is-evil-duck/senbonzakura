#!/bin/sh
# Gradle wrapper script
set -e

APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P )
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
