package com.findme.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val isLoggedIn: Boolean
)
