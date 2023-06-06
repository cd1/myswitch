package com.gmail.cristiandeives.myswitch.addgame.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RecentGameSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val query: String,
    val lastUpdated: Long,
)