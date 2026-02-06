# SQLCipher
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
