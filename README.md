# Android SDK

Currently, SDK supports Banners and Interstitials (including MRAID 2.0 API).

## Step 1: Installation ##

### Option 1: Via Gradle: ###

Add the following Maven configuration to your build.gradle file that contains your application's or module's 'repositories' configuration block:
```
/** 
 * Make sure you are configuring your project or module's 'repositories' configuration
 * block and not your buildscript's 'repositories' configuration block
 */
repositories {
    maven {
      url  "https://dl.bintray.com/adserver-online/maven"      
    }
}
```
Add the following lines to your 'dependencies' configuration within your module's build.gradle:
```
dependencies {
    /** 
     * Any other dependencies your module has are placed in this dependency configuration
     */
    implementation 'adserver-online:android-sdk:1.0.1'
}
```

### Option 2: Manually: ###

1. Download our SDK distribution from [here](https://github.com/adserver-online/android-sdk/tree/master/libs). <br>
2. Place adserver-sdk.jar into your project's "libs" folder.
3. Add the following to your module's build.gradle under 'dependencies':
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    /** Any other dependencies here */
}
```
## Step 2: Edit Manifest ##
1. In order to take full advantage of our platform on Android Pie devices with the targetSdkVersion set to 28 or higher in your build.gradle, you should point to a network security config file where the usesCleartextTraffic attribute is set to true. This is to ensure those devices continue receiving ads with http asset URLs ([Opt out of cleartext traffic](https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted) mentions that cleartext support is disabled by default starting with Android Pie):
```xml
<application
    ...
    android:networkSecurityConfig="@xml/network_security_config">
    ...
</application>
```
2. Set the following permissions in your project's "AndroidManifest.xml":<br>
* INTERNET<br>
* ACCESS_NETWORK_STATE<br>

If you plan using MRAID ads, you may want to include permissions for Photos, Calendar events, SMS, or any other MRAID functionality you wish to support.
   
You can do this by pasting the following lines before the \<application...> tag:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
```

## Step 3: Add Google Play Services Ads Library ##

In order to comply with [Google's developer program policies](https://play.google.com/about/developer-content-policy.html), we need to have access to advertising classes within the [Google Play Services SDK](https://developers.google.com/android/guides/setup) for advertising ID retrieval.


## Step 4: SDK initialization

Initialize SDK as early as possible in Main Activity

``` java
Adserver.initialize(this); // 'this' being the context
```

## Step 5: Gather Information From Your Adserver.Online Account ##

You have to create a zone and enable Server Tag according to this article https://adserver.online/article/server-tags

# Demo

Please check out the demo application in ```example``` directory