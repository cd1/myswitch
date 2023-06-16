package com.gmail.cristiandeives.myswitch.common.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.db.GameEntity
import com.gmail.cristiandeives.myswitch.common.data.db.GamesDao
import com.gmail.cristiandeives.myswitch.common.data.db.MySwitchDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamesDaoTest {
    private lateinit var db: MySwitchDatabase
    private lateinit var dao: GamesDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MySwitchDatabase::class.java).build()
        dao = db.gamesDao()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun insertAndReadAll_success() = runTest {
        val game = GameEntity(
            id = 42,
            name = "Game",
            coverUrl = "http://www.example.test/cover.jpg",
            mediaType = GameMediaType.Physical,
        )

        dao.insert(game)
        val allGames = dao.readAll()

        assertEquals(listOf(game), allGames.first())
    }

    @Test
    fun readAll_resultsAreSorted() = runTest {
        val gameA = GameEntity(
            id = 43,
            name = "Game A",
            coverUrl = "http://www.example.test/cover.jpg",
            mediaType = GameMediaType.Digital,
        )
        val gameZ = GameEntity(
            id = 42,
            name = "Game Z",
            coverUrl = "http://www.example.test/cover.jpg",
            mediaType = GameMediaType.Physical,
        )

        dao.insert(gameZ)
        dao.insert(gameA)
        val allGames = dao.readAll()

        assertEquals(listOf(gameA, gameZ), allGames.first())
    }
}