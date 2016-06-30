# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\admin\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# FIXME check class path after cleaning
-keepclassmembers class eu.focusnet.app.ui.fragment.widget.Html5WidgetFragment.WebAppInterface {
   public *;
}

# MPAndroidChart depends on io.realm.* but does not include it.
# We don't use the features of this dependency, so let's ignore it.
-dontwarn io.realm.**

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
# -keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class eu.focusnet.app.model.gson.** { *; }
##---------------End: proguard configuration for Gson  ----------
