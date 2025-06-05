# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# https://issuetracker.google.com/u/2/issues/371227633
# https://github.com/Kotlin/kotlinx.serialization/issues/2825
-repackageclasses
#-keep @kotlinx.serialization.Serializable class * {*;}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}