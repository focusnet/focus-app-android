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
-keepclassmembers class eu.focusnet.app.ui.fragment.widget.WebAppInterface {
   public *;
}

# MPAndroidChart depends on io.realm.* but does not include it.
# We don't use the features of this dependency, so let's ignore it.
-dontwarn io.realm.**
