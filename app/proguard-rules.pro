-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class br.com.apkbox.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class br.com.apkbox.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
