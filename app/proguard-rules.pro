# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\lwg\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# For FingerPush SDK
-dontwarn com.fingerpush.**
-ignorewarnings
-keep class com.fingerpush.** { *; }
-keep class android.** { *; }
-keep class javax.** { *; }
-keep class org.** { *; }
# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

# Add when compile with JDK 1.7
-keepattributes EnclosingMethod

# Firebase Authentication
-keepattributes *Annotation*

# Firebase Realtime database
-keepattributes Signature

-dontwarn okhttp3.**
-dontwarn okio.**

-dontnote okhttp3.**

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keep class com.google.android.gms.common.util.**