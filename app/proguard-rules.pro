-keep class com.stripe.android.** { *; }
-keep class net.sf.scuba.smartcards.IsoDepCardService {*;}
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.ejbca.** {*;}
-dontwarn com.stripe.android.**
-dontwarn com.nimbusds.jose.**

-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn org.ejbca.**
-dontwarn org.bouncycastle.**
-dontwarn org.jmrtd.PassportService
-dontwarn net.sf.scuba.**

-dontwarn okhttp3.internal.platform.*


-keep class com.google.firebase.example.fireeats.model.** { *; }
 -keep class com.google.gson.examples.android.model.** { *; }
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer
#-keep class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}

# ViewModel's empty constructor is considered to be unused by proguard
#-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
#    <init>(...);
#}
#
#
#
## keep Lifecycle State and Event enums values
#-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
#-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
#-keepclassmembers class * {
#    @android.arch.lifecycle.OnLifecycleEvent *;
#}

#Google Classes
#-keepnames class com.google.android.gms.** {*;}
-keepclassmembers enum * { *; }



-keep class com.google.firebase.**{*;}

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**


# Application classes that will be serialized/deserialized over Gson
# -keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer
#Google Classes
#-keepnames class com.google.android.gms.** {*;}

#Youtube
# Needed to keep generic types and @Key annotations accessed via reflection

#-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

#-keepclassmembers class * {
#  @com.google.api.client.util.Key <fields>;
#}

# Needed by google-http-client-android when linking against an older platform version

#-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version

#-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version

#-dontwarn com.google.android.gms.**

# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path

# Suppress notes on LicensingServices
-dontnote **.ILicensingService

# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe


#FireBase
# Keep custom model classes
-keep class com.google.firebase.example.fireeats.java.model.** { *; }
-keep class com.google.firebase.example.fireeats.kotlin.model.** { *; }


#Amazon Aws
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class com.amazonaws.internal.**                   { *; }
-keep class org.codehaus.**                             { *; }
-keep class org.joda.time.tz.Provider                   { *; }
-keep class org.joda.time.tz.NameProvider               { *; }
#-keepattributes Signature,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class com.amazonaws.** { *; }

-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.joda.convert.**
-dontwarn com.amazonaws.org.joda.convert.**
#-dontwarn org.w3c.dom.bootstrap.**


#-dontnote com.amazonaws.services.sqs.QueueUrlHandler
#
# -keepattributes InnerClasses
#    -keep  class com.dnitinverma.amazons3library.AmazonS3**
#    -keepclassmembers  class com.dnitinverma.amazons3library.AmazonS3** {
#       *;
#    }
#
#    -dontwarn com.amazonaws.**


# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

#Facebook
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
# The Maps Android API uses custom parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}
#retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okio.**


-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Glide specific rules #
# https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


#google play services
##https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/proguard-rules.pro
## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#Facebook
-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity
-keep class com.facebook.login.Login




-keepclassmembers class com.taghawk.model.** {
  *;
}
#Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.Call
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class sun.misc.Unsafe { *; }
-keepattributes Signature
-keepattributes Exceptions

## GSON
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keepattributes *Annotation*
-dontwarn org.apache.http.annotation.**
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

### GSON
#-keep class com.google.gson.stream.** { *; }


