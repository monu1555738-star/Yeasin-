package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CallRecordEntity
import com.example.model.ChatMessageEntity
import com.example.model.ContactEntity
import com.example.model.SettingEntity
import com.example.model.StoryEntity
import com.example.model.UserSessionEntity

@Database(
    entities = [
        UserSessionEntity::class,
        ContactEntity::class,
        ChatMessageEntity::class,
        CallRecordEntity::class,
        StoryEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSessionDao(): UserSessionDao
    abstract fun contactDao(): ContactDao
    abstract fun chatDao(): ChatDao
    abstract fun callDao(): CallDao
    abstract fun storyDao(): StoryDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "beshell_call_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
