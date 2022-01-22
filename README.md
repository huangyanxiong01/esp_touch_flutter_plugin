# esp_touch_flutter_plugin

Client-side (mobile) Android Flutter implementation for ESP-Touch protocol

## Usage
To use this plugin, add esp_touch_flutter_plugin as a [dependency in your pubspec.yaml file](https://pub.dev/packages/esp_touch_flutter_plugin).
### build.gradle for project
Change kotlin version of build.gradle build script to 1.5.30 or newer and add `maven { url 'https://jitpack.io' }` to repositories of rootProject.
```
buildscript {
  ...
  ext.kotlin_version = '1.5.30'
  ...
}
rootProject.allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Add permission to AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
## Example
The example You can find [here](https://pub.dev/packages/arp_scanner/example).

## Fix `Duplicate class com.google.gson`
```
android {
  configurations {
    all {
      exclude module:'gson'
    }
  }
}
```

