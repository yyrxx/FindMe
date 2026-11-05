package com.findme.app.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: FindMeDatabase? = null

    fun getDatabase(context: Context): FindMeDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                FindMeDatabase::class.java,
                "findme_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
