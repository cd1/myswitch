package com.gmail.cristiandeives.myswitch.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchEntity
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchesDao

@Database(
    entities = [
        GameEntity::class,
        RecentGameSearchEntity::class,
    ],
    version = 1,
)
abstract class MySwitchDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
    abstract fun recentGameSearchesDao(): RecentGameSearchesDao
}
