package com.findme.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class FindMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
