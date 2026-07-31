package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GhostDetectionEntity::class], version = 1, exportSchema = false)
abstract class GhostDatabase : RoomDatabase() {
    abstract fun ghostDao(): GhostDao

    companion object {
        @Volatile
        private var INSTANCE: GhostDatabase? = null

        fun getDatabase(context: Context): GhostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GhostDatabase::class.java,
                    "ghost_detector_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
