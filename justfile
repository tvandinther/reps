default:
    @just --list

# Build debug APK
build:
    ./gradlew assembleDebug

# Build release APK
build-release:
    ./gradlew assembleRelease

# Install debug APK on connected device
install:
    ./gradlew installDebug

# Build and install debug APK
run: build install

# Run unit tests
test:
    ./gradlew test

# Run instrumented tests on connected device
test-instrumented:
    ./gradlew connectedAndroidTest

# Clean build outputs
clean:
    ./gradlew clean

# Show connected ADB devices
devices:
    adb devices

# Stream logcat output filtered to this app
logcat:
    adb logcat --pid=$(adb shell pidof -s com.tvandinther.reps)

# Launch the app on connected device
launch:
    adb shell am start -n com.tvandinther.reps/.MainActivity

# Force stop the app on connected device
stop:
    adb shell am force-stop com.tvandinther.reps

# Uninstall the app from connected device
uninstall:
    adb uninstall com.tvandinther.reps

# Clear app data on connected device
clear-data:
    adb shell pm clear com.tvandinther.reps

# Pull the Room database from connected device (requires root or debuggable build)
pull-db:
    adb shell run-as com.tvandinther.reps cp /data/data/com.tvandinther.reps/databases/reps_database.db /sdcard/reps_database.db
    adb pull /sdcard/reps_database.db ./reps_database.db
    adb shell rm /sdcard/reps_database.db
    @echo "Database saved to ./reps_database.db"
