# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Dell\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
-keep class twitter4j.** { *; }

-dontwarn twitter4j.**

-dontwarn org.apache.commons.codec.binary.Base64

#-dontwarn com.demach.konotor
#-dontwarn com.demach.konotor.KonotorFeedbackActivity
#-dontwarn com.demach.konotor.service.model.GetAllConversationResponse
-dontwarn com.viewpagerindicator.LinePageIndicator
-dontwarn jp.wasabeef.recyclerview.animators.BaseItemAnimator

# Demach GSON files
-keep class com.google.gson.demach.** {
    <fields>;
    <methods>;
}

#-keep class com.demach.konotor.** { *; }

# Demach model
-keep class com.demach.** {
    <fields>;
    <methods>;
}

 -keep class **.R$* {
    <fields>;
    }

      -keep class com.facebook.** {
       *;
    }

-dontwarn com.github.mikephil.**

-keep class org.lucasr.twowayview.** { *; }
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn com.freshdesk.hotline.**
-dontwarn uk.co.chrishenx.calligraphy.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
-keep class com.webengage.sdk.android.**{*;}

-dontwarn com.webengage.sdk.android.**
-dontwarn rx.*
-keep class com.nowfloats.Store.Model.** { *; }
-keep class com.nowfloats.NavigationDrawer.model.** { *; }
-keep class com.nowfloats.NavigationDrawer.model.** { *; }
-keep class com.nowfloats.Login.Model.** { *; }
-keep class com.nowfloats.Business_Enquiries.Model.** { *; }
-keep class com.nowfloats.BusinessProfile.UI.Model.** { *; }
-keep class com.nowfloats.AccountDetails.Model.** { *; }
-keep class com.nowfloats.signup.UI.Model.** { *; }
-keep class com.nowfloats.Product_Gallery.Model.** { *; }
-keep class com.nowfloats.NotificationCenter.Model.** { *; }
-keep class com.nowfloats.CustomPage.Model.**{ *; }
-keep class sun.misc.Unsafe { *; }
-keepattributes Signature

-keep class com.google.gson.demach.** {
    <fields>;
    <methods>;
}

# Demach model
-keep class com.demach.** {
    <fields>;
    <methods>;
}


-keep class com.daimajia.androidanimations.** { *;}
-keep class com.daimajia.easing.** { *;}
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }



# common library
-keep class com.aviary.android.feather.sdk.AviaryIntent
-keep class com.aviary.android.feather.sdk.internal.tracking.AviaryTracker
-keep class com.aviary.android.feather.sdk.internal.tracking.AbstractTracker
-keep class com.aviary.android.feather.sdk.log.LoggerFactory
-keep class com.aviary.android.feather.sdk.internal.headless.gl.GLUtils
-keep class com.aviary.android.feather.sdk.internal.services.BaseContextService
-keep class com.aviary.android.feather.sdk.internal.tracking.TrackerFactory


# headless library
-keep interface com.aviary.android.feather.sdk.internal.headless.filters.IFilter
-keep class com.aviary.android.feather.sdk.internal.headless.AviaryEffect
-keep class com.aviary.android.feather.sdk.internal.headless.moa.Moa
-keep class com.aviary.android.feather.sdk.internal.headless.moa.MoaHD
-keep class com.aviary.android.feather.sdk.internal.headless.moa.MoaParameter
-keep class com.aviary.android.feather.sdk.internal.headless.utils.CameraUtils
-keep class com.aviary.android.feather.sdk.internal.headless.moa.MoaJavaUndo
-keep class com.aviary.android.feather.sdk.internal.headless.moa.MoaJavaUndo$MoaUndoBitmap

-keep class com.aviary.android.feather.sdk.BuildConfig
-keep class com.aviary.android.feather.cds.BuildConfig
-keep class com.aviary.android.feather.headless.BuildConfig
-keep class com.aviary.android.feather.common.BuildConfig

-keep class * extends com.aviary.android.feather.sdk.internal.headless.filters.IFilter
-keep class * extends com.aviary.android.feather.sdk.internal.headless.moa.MoaParameter

-keep class * extends com.aviary.android.feather.sdk.widget.AviaryStoreWrapperAbstract
-keep class * extends com.aviary.android.feather.sdk.widget.PackDetailLayout
-keep class * extends com.aviary.android.feather.sdk.internal.services.BaseContextService
-keep class * extends com.aviary.android.feather.sdk.internal.tracking.AbstractTracker
-keep class * extends android.app.Service
-keep class * extends android.os.AsyncTask
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class com.android.vending.licensing.ILicensingService
-keep public class com.android.vending.billing.IInAppBillingService
-keep class com.aviary.android.feather.sdk.internal.headless.moa.MoaResult
-keep class com.aviary.android.feather.sdk.internal.headless.filters.NativeFilterProxy
-keep class com.aviary.android.feather.sdk.utils.AviaryIntentConfigurationValidator
-keep class com.aviary.android.feather.sdk.internal.Constants
-keep class com.aviary.android.feather.sdk.AviaryIntent
-keep class com.aviary.android.feather.sdk.AviaryIntent$Builder
-keep class com.aviary.android.feather.sdk.AviaryVersion

-keepclassmembers class com.aviary.android.feather.sdk.overlays.UndoRedoOverlay {
    void setAlpha1(int);
    void setAlpha2(int);
    void setAlpha3(int);
    int getAlpha1();
    int getAlpha2();
    int getAlpha3();
}

-keepclassmembers class * extends com.aviary.android.feather.sdk.internal.graphics.drawable.FeatherDrawable {
	float getScaleX();
	void setScaleX(float);
}

-keepclassmembers class com.aviary.android.feather.sdk.AviaryIntent {*;}
-keepclassmembers class com.aviary.android.feather.sdk.AviaryIntent$Builder {*;}
-keepclassmembers class com.aviary.android.feather.sdk.AviaryVersion {*;}
-keepclassmembers class com.aviary.android.feather.sdk.utils.AviaryIntentConfigurationValidator {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.graphics.drawable.FeatherDrawable {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.utils.SDKUtils {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.utils.SDKUtils$ApiKeyReader {*;}

# keep everything for native methods/fields
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.moa.Moa {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.moa.MoaHD {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.moa.MoaJavaUndo {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.utils.CameraUtils {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.moa.MoaResult {*;}
-keepclassmembers class com.aviary.android.feather.sdk.opengl.AviaryGLSurfaceView {*;}

-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.filters.MoaJavaToolStrokeResult {
  <methods>;
}

-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.gl.GLUtils {
  <methods>;
}

-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.filters.NativeToolFilter {*;}

-keepclassmembers class com.aviary.android.feather.sdk.AviaryIntent {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.os.AviaryIntentService {*;}
-keepclassmembers class com.aviary.android.feather.sdk.internal.os.AviaryAsyncTask {*;}

-keepclassmembers class com.aviary.android.feather.sdk.internal.tracking.AbstractTracker {
    <fields>;
}
-keepclassmembers class com.aviary.android.feather.sdk.internal.tracking.AviaryTracker {
    <fields>;
}

-keepclassmembers class com.aviary.android.feather.sdk.log.LoggerFactory {
    <fields>;
}

-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.moa.MoaJavaUndo$MoaUndoBitmap {
    <fields>;
}

-keepclassmembers class com.aviary.android.feather.sdk.BuildConfig {*;}
-keepclassmembers class com.aviary.android.feather.cds.BuildConfig {*;}
-keepclassmembers class com.aviary.android.feather.headless.BuildConfig {*;}
-keepclassmembers class com.aviary.android.feather.common.BuildConfig {*;}


# keep class members
-keepclassmembers class com.aviary.android.feather.sdk.internal.tracking.AbstractTracker { *; }
-keepclassmembers class com.aviary.android.feather.sdk.internal.tracking.TrackerFactory { *; }
-keepclassmembers class com.aviary.android.feather.sdk.internal.headless.gl.GLUtils { *; }
-keepclassmembers class com.aviary.android.feather.sdk.internal.services.BaseContextService { *; }
-keepclassmembers class com.aviary.android.feather.utils.SettingsUtils { *; }

-keepclassmembers class * extends com.aviary.android.feather.sdk.internal.services.BaseContextService {
   public <init>( com.aviary.android.feather.sdk.internal.services.IAviaryController );
}

-keepclasseswithmembers class * {
    public <init>( com.aviary.android.feather.sdk.internal.services.IAviaryController );
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep all the native methods
-keepclassmembers class * {
   private native <methods>;
   public native <methods>;
   protected native <methods>;
   public static native <methods>;
   private static native <methods>;
   static native <methods>;
   native <methods>;
}

-keepclasseswithmembers class * {
    public <init>( com.aviary.android.feather.sdk.internal.services.IAviaryController );
}

# EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

#
# facebook sdk specific entries
#

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    !static !transient <fields>;
}

-keepclassmembers class com.facebook.Session {*;}
-keepattributes Signature
-keep class com.facebook.model.** { *; }

-keep class com.google.android.gms.R$styleable
-keepclassmembers class com.google.android.gms.R$styleable {*;}
-dontwarn com.semusi.sdksample**
-dontwarn semusi.ruleengine.pushmanager**


# Twilio Client
-keep class com.twilio.** { *; }

# Apache HttpClient
-dontwarn org.apache.http.**


-dontskipnonpubliclibraryclassmembers

-keepattributes *Annotation*,EnclosingMethod

-keepnames class org.codehaus.jackson.** { *; }

-dontwarn javax.xml.**
-dontwarn org.slf4j.**
-dontwarn com.jayway.jsonpath.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**
