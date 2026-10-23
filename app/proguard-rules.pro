# ProGuard / R8 Rules for LinguaPulse

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Data models
-keepclassmembers class com.linguapulse.app.domain.model.** { *; }
-keepclassmembers class com.linguapulse.app.data.local.entity.** { *; }

# WorkManager
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
