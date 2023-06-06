package com.gmail.cristiandeives.myswitch.addgame.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentGameSearchDao {
    @Upsert
    suspend fun upsert(recentGameSearch: RecentGameSearch)

    @Query("SELECT id, `query` FROM RecentGameSearch ORDER BY lastUpdated DESC LIMIT 10")
    fun readAll(): Flow<List<SimpleRecentGameSearch>>

    @Query("SELECT id FROM RecentGameSearch WHERE `query` = :query")
    suspend fun readIdByQuery(query: String): Long?

    @Query("DELETE FROM RecentGameSearch WHERE id = :id")
    suspend fun deleteById(id: Long)
}

