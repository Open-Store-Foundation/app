# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-verbose
#-dontobfuscate
-ignorewarnings

# These lines allow optimisation whilst preserving stack traces
-optimizations !code/allocation/variable
-optimizations !class/unboxing/enum
-keepattributes SourceFile, LineNumberTable
-keep,allowshrinking,allowoptimization class * { <methods>; }
-keepattributes Signature

# Strip all Android logging for security and performance
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Strip all SLF4J logging in the JAR for security and performance
-assumenosideeffects class * implements org.slf4j.Logger {
    public *** trace(...);
    public *** debug(...);
    public *** info(...);
    public *** warn(...);
    public *** error(...);
}

# Don't mess with classes with native methods
-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

# slf4j
-dontwarn org.slf4j.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

-keep class okhttp3.internal.*
