package com.linguapulse.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.linguapulse.app.data.local.dao.LessonDao
import com.linguapulse.app.data.local.dao.OfflineMutationDao
import com.linguapulse.app.data.local.dao.UserProgressDao
import com.linguapulse.app.data.local.entity.LessonEntity
import com.linguapulse.app.data.local.entity.OfflineMutationEntity
import com.linguapulse.app.data.local.entity.UserProgressEntity

@Database(
    entities = [
        LessonEntity::class,
        UserProgressEntity::class,
        OfflineMutationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LinguaPulseDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun offlineMutationDao(): OfflineMutationDao

    companion object {
        @Volatile
        private var INSTANCE: LinguaPulseDatabase? = null

        fun getInstance(context: Context): LinguaPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LinguaPulseDatabase::class.java,
                    "linguapulse_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
