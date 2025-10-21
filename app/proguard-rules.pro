# ========== Android & Jetpack Core ==========
# Keep source file names and line numbers for crash reports
-keepattributes SourceFile,LineNumberTable,Signature,Exceptions,*Annotation*
-renamesourcefileattribute SourceFile

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class *
-keep class **_Impl { *; }

# Keep Firebase
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ========== Navigation Compose (FIX FOR CRASH) ==========
# Keep navigation compose to fix: "Navigation destination cannot be found" crash
-keep class androidx.navigation.compose.** { *; }
-keep class com.alperen.spendcraft.navigation.** { *; }

# Keep NavController methods (critical for navigation)
-keepclassmembers class androidx.navigation.NavController {
    public <methods>;
}

# ========== App Models & Entities (CRITICAL FOR NAVIGATION ARGS) ==========
# Keep all data models used in navigation
-keep class com.alperen.spendcraft.core.model.** { *; }
-keep class com.alperen.spendcraft.data.db.entities.** { *; }

# ========== Compose & ViewModels ==========
# Keep Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep ViewModels and Hilt
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ========== DataStore & Proto ==========
-keep class com.alperen.spendcraft.feature.settings.SettingsOuterClass$Settings { *; }
-keepclassmembers class com.alperen.spendcraft.feature.settings.SettingsOuterClass$Settings { *; }

# ========== Play Billing ==========
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# ========== Common Android ==========
# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}

# ========== Google Common & Guava ==========
-dontwarn com.google.common.**
-dontwarn sun.misc.Unsafe
-dontwarn javax.annotation.**

# ========== Kotlin ==========
-dontwarn kotlin.**
-dontwarn kotlinx.**
