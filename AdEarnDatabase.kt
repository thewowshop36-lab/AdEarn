package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppNotification
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        TransactionRecord::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AdEarnDatabase : RoomDatabase() {
    abstract fun dao(): AdEarnDao

    companion object {
        @Volatile
        private var INSTANCE: AdEarnDatabase? = null

        fun getDatabase(context: Context): AdEarnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdEarnDatabase::class.java,
                    "adearn_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
