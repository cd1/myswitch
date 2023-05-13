package com.gmail.cristiandeives.myswitch.common.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Game::class,
    ],
    version = 1,
)
abstract class MySwitchDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
