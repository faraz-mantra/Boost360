-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.boost.upgrades.**$$serializer { *; }
-keepclassmembers class com.boost.upgrades.** {
    *** Companion;
}
-keepclasseswithmembers class com.boost.upgrades.** {
    kotlinx.serialization.KSerializer serializer(...);
}