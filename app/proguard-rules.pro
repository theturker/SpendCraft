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

# Keep ViewModels and Hilt (CRITICAL - Fix for duplicate key crash)
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Keep ViewModel names to prevent obfuscation conflicts
-keepnames class * extends androidx.lifecycle.ViewModel

# Keep Hilt-generated ViewModelFactory
-keep class dagger.hilt.android.internal.lifecycle.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory$ViewModelFactoriesEntryPoint { *; }

# Prevent ViewModel obfuscation that causes duplicate keys
-keepclassmembers @dagger.hilt.android.lifecycle.HiltViewModel class * {
    <init>(...);
}

# Keep all ViewModel constructors and their parameter types
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <methods>;
}

# ========== DataStore & Proto ==========
-keep class com.alperen.spendcraft.feature.settings.SettingsOuterClass$Settings { *; }
-keepclassmembers class com.alperen.spendcraft.feature.settings.SettingsOuterClass$Settings { *; }

# ========== Play Billing ==========
# Billing removed - no in-app purchases on Android (like iOS)

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
# Fix for "Multiple entries with same key" crash
-dontwarn com.google.common.**
-dontwarn sun.misc.Unsafe
-dontwarn javax.annotation.**

# Keep Guava ImmutableMap to prevent duplicate key issues
-keep class com.google.common.collect.ImmutableMap { *; }
-keep class com.google.common.collect.ImmutableMap$Builder { *; }
-keepclassmembers class com.google.common.collect.ImmutableMap$Builder {
    *;
}

# ========== Kotlin ==========
-dontwarn kotlin.**
-dontwarn kotlinx.**

# ========== Hilt Dagger (CRITICAL FIX) ==========
# Keep all Hilt-generated components to prevent ViewModel key conflicts
-keep class **_HiltComponents { *; }
-keep class **_HiltComponents$* { *; }
-keep class **Hilt_** { *; }
-keep class dagger.hilt.** { *; }

# Keep Hilt entry points
-keep interface * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory$ViewModelFactoriesEntryPoint

# Prevent method/field name obfuscation in Hilt modules
-keepclassmembers class * {
    @dagger.hilt.android.qualifiers.* <fields>;
    @javax.inject.* <fields>;
}

# Keep module bindings
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
