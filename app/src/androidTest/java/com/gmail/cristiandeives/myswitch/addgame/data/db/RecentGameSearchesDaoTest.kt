package com.gmail.cristiandeives.myswitch.addgame.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gmail.cristiandeives.myswitch.common.data.db.MySwitchDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecentGameSearchesDaoTest {
    private lateinit var db: MySwitchDatabase
    private lateinit var dao: RecentGameSearchesDao

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MySwitchDatabase::class.java).build()
        dao = db.recentGameSearchesDao()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun insertAndReadAll_success() = runTest {
        val gameSearch = RecentGameSearchEntity(
            id = 42,
            query = "foo",
            lastUpdated = 1,
        )

        dao.upsert(gameSearch)
        val allGameSearches = dao.readAll()

        assertEquals(listOf(gameSearch.toSimple()), allGameSearches.first())
    }

    @Test
    fun updateAndReadAll_success() = runTest {
        val gameSearch = RecentGameSearchEntity(
            id = 42,
            query = "foo",
            lastUpdated = 1,
        )
        val updatedGameSearch = gameSearch.copy(lastUpdated = 2)

        dao.upsert(gameSearch)
        dao.upsert(updatedGameSearch)
        val allGameSearches = dao.readAll()

        assertEquals(listOf(updatedGameSearch.toSimple()), allGameSearches.first())
    }

    @Test
    fun readAll_resultsAreSorted() = runTest {
        val earlyGameSearch = RecentGameSearchEntity(
            id = 42,
            query = "foo",
            lastUpdated = 1,
        )
        val lateGameSearch = RecentGameSearchEntity(
            id = 43,
            query = "bar",
            lastUpdated = 2,
        )

        dao.upsert(earlyGameSearch)
        dao.upsert(lateGameSearch)
        val allGameSearches = dao.readAll()

        assertEquals(listOf(lateGameSearch.toSimple(), earlyGameSearch.toSimple()), allGameSearches.first())
    }

    @Test
    fun readAll_resultsAreLimitedTo10() = runTest {
        val gameSearches = List(12) { i ->
            RecentGameSearchEntity(
                id = i.toLong(),
                query = "Query $i",
                lastUpdated = 1,
            )
        }

        for (gameSearch in gameSearches) {
            dao.upsert(gameSearch)
        }
        val allGameSearches = dao.readAll()

        assertEquals(10, allGameSearches.first().size)
    }

    @Test
    fun readIdByQuery_gameSearchExists() = runTest {
        val id = 42L
        val query = "foo"
        val gameSearch = RecentGameSearchEntity(
            id = id,
            query = query,
            lastUpdated = 1,
        )

        dao.upsert(gameSearch)
        val searchedId = dao.readIdByQuery(query)

        assertEquals(id, searchedId)
    }

    @Test
    fun readIdByQuery_gameSearchDoesNotExist() = runTest {
        val searchedId = dao.readIdByQuery("foo")

        assertNull(searchedId)
    }

    @Test
    fun deleteById_success() = runTest {
        val id = 42L
        val gameSearch = RecentGameSearchEntity(
            id = id,
            query = "foo",
            lastUpdated = 1,
        )

        dao.upsert(gameSearch)
        dao.deleteById(id)
        val allGameSearches = dao.readAll()

        assertTrue(allGameSearches.first().isEmpty())
    }

    @Test
    fun deleteById_gameSearchDoesNotExist() = runTest {
        dao.deleteById(42)
        val allGameSearches = dao.readAll()

        assertTrue(allGameSearches.first().isEmpty())
    }

    companion object {
        private fun RecentGameSearchEntity.toSimple() = SimpleRecentGameSearch(
            id = this.id,
            query = this.query,
        )
    }
}