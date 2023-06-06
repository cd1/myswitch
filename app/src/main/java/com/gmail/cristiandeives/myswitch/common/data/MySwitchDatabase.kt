package com.gmail.cristiandeives.myswitch.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearch
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchDao

@Database(
    entities = [
        Game::class,
        RecentGameSearch::class,
    ],
    version = 1,
)
abstract class MySwitchDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun recentGameSearchDao(): RecentGameSearchDao
}
