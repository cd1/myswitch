package com.gmail.cristiandeives.myswitch.common.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.db.GameEntity
import com.gmail.cristiandeives.myswitch.common.data.db.GamesDao
import com.gmail.cristiandeives.myswitch.common.data.log.FakeLogger
import com.gmail.cristiandeives.myswitch.common.data.network.IgdbRepository
import com.gmail.cristiandeives.myswitch.isLeft
import com.gmail.cristiandeives.myswitch.isRight
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GamesRepositoryTest {
    @Mock
    lateinit var gamesDao: GamesDao

    @Mock
    lateinit var igdbRepository: IgdbRepository

    private lateinit var mockitoAnnotations: AutoCloseable

    @Before
    fun before() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)
    }

    @After
    fun after() {
        mockitoAnnotations.close()
    }

    @Test
    fun searchGames_igdbRepositoryIsCalled() = runTest {
        val query = "foo"
        val repository = createRepository()

        repository.searchGames(query)

        verify(igdbRepository).searchGames(query)
    }

    @Test
    fun searchGames_whenIgdbRepositoryIsSuccess_thenResultIsSuccess() = runTest {
        val query = "foo"
        mockIgdbRepositorySearchGamesSuccess(emptyList())
        val repository = createRepository()

        val result = repository.searchGames(query)

        assertThat(result).isRight()
    }

    @Test
    fun searchGames_whenIgdbRepositoryIsError_thenResultIsError() = runTest {
        val query = "foo"
        mockIgdbRepositorySearchGamesError()
        val repository = createRepository()

        val result = repository.searchGames(query)

        assertThat(result).isLeft()
    }

    @Test
    fun searchGames_whenIgdbRepositoryIsSuccess_thenCorrectResultIsReturned() = runTest {
        val query = "foo"
        val games = List(3) { i ->
            Game(
                id = i.toLong(),
                coverUrl = "http://www.example.test/$i",
                name = "Game $i",
                mediaType = GameMediaType.values()[i % GameMediaType.values().size],
            )
        }
        mockIgdbRepositorySearchGamesSuccess(games)
        val repository = createRepository()

        val result = repository.searchGames(query)

        assertThat((result as Either.Right).value).isEqualTo(games)
    }

    @Test
    fun getGames_daoIsCalled() = runTest {
        val repository = createRepository()
        repository.getGames()

        verify(gamesDao).readAll()
    }

    @Test
    fun getGames_daoReturnGames_gamesAreReturned() = runTest {
        val gameEntities = List(3) { i ->
            GameEntity(id = i.toLong(), name = "Game $i", coverUrl = "http://www.example.test/$i", mediaType = null)
        }

        mockDaoReadAll(gameEntities)

        val repository = createRepository()
        val returnedGames = repository.getGames().first()

        assertThat(returnedGames).isEqualTo(gameEntities.map { it.toData() })
    }

    @Test
    fun getGameDetails_igdbRepositoryIsCalled() = runTest {
        val gameId = 42L
        val repository = createRepository()

        repository.getGameDetails(gameId)

        verify(igdbRepository).getGameDetails(gameId)
    }

    @Test
    fun getGameDetails_whenIgdbRepositoryIsSuccess_thenResultIsSuccess() = runTest {
        val repository = createRepository()

        mockIgdbRepositoryGetGameDetailsSuccess(defaultGameDetails)

        val result = repository.getGameDetails(42)

        assertThat(result).isRight()
    }

    @Test
    fun getGameDetails_whenIgdbRepositoryIsError_thenResultIsError() = runTest {
        val repository = createRepository()

        mockIgdbRepositoryGetGameDetailsError()

        val result = repository.getGameDetails(42)

        assertThat(result).isLeft()
    }

    @Test
    fun getGameDetails_whenIgdbRepositoryIsSuccess_thenCorrectResultIsReturned() = runTest {
        val repository = createRepository()

        mockIgdbRepositoryGetGameDetailsSuccess(defaultGameDetails)

        val result = repository.getGameDetails(42)

        val resultGameDetails = result.getOrNull()!!
        assertThat(resultGameDetails.artworkUrl).isEqualTo(defaultGameDetails.artworkUrl)
        assertThat(resultGameDetails.coverUrl).isEqualTo(defaultGameDetails.coverUrl)
        assertThat(resultGameDetails.name).isEqualTo(defaultGameDetails.name)
        assertThat(resultGameDetails.summary).isEqualTo(defaultGameDetails.summary)
        assertThat(resultGameDetails.publishers).isEqualTo(defaultGameDetails.publishers)
        assertThat(resultGameDetails.year).isEqualTo(defaultGameDetails.year)
    }

    @Test
    fun insertGame_daoIsCalled() = runTest {
        val repository = createRepository()
        val gameEntityCaptor = argumentCaptor<GameEntity>()

        repository.insertGame(defaultGame)

        verify(gamesDao).insert(gameEntityCaptor.capture())

        val resultGameEntity = gameEntityCaptor.firstValue
        assertThat(resultGameEntity.coverUrl).isEqualTo(defaultGame.coverUrl)
        assertThat(resultGameEntity.name).isEqualTo(defaultGame.name)
        assertThat(resultGameEntity.mediaType).isEqualTo(defaultGame.mediaType)
    }

    @Test
    fun insertGame_whenDaoIsSuccess_thenResultIsSuccess() = runTest {
        val repository = createRepository()

        val result = repository.insertGame(defaultGame)

        assertThat(result).isRight()
    }

    @Test
    fun insertGame_whenDaoIsError_thenResultIsError() = runTest {
        val repository = createRepository()
        mockDaoInsertGameError()

        val result = repository.insertGame(defaultGame)

        assertThat(result).isLeft()
    }

    private fun createRepository() = GamesRepository(
        gamesDao, igdbRepository, UnconfinedTestDispatcher(), FakeLogger(),
    )

    private fun mockDaoReadAll(games: List<GameEntity>) {
        whenever(gamesDao.readAll())
            .thenReturn(flowOf(games))
    }

    private suspend fun mockDaoInsertGameError() {
        whenever(gamesDao.insert(any()))
            .thenThrow(RuntimeException::class.java)
    }

    private suspend fun mockIgdbRepositorySearchGamesSuccess(games: List<Game>) {
        whenever(igdbRepository.searchGames(any()))
            .thenReturn(games.right())
    }

    private suspend fun mockIgdbRepositorySearchGamesError() {
        whenever(igdbRepository.searchGames(any()))
            .thenReturn(Unit.left())
    }

    private suspend fun mockIgdbRepositoryGetGameDetailsSuccess(gameDetails: GameDetails) {
        whenever(igdbRepository.getGameDetails(any()))
            .thenReturn(gameDetails.right())
    }

    private suspend fun mockIgdbRepositoryGetGameDetailsError() {
        whenever(igdbRepository.getGameDetails(any()))
            .thenReturn(Unit.left())
    }

    companion object {
        private val defaultGame = Game(
            coverUrl = "http://www.example.test/cover.png",
            name = "Game name",
            mediaType = GameMediaType.Digital,
        )

        private val defaultGameDetails = GameDetails(
            artworkUrl = "http://www.example.test/artwork.png",
            coverUrl = "http://www.example.test/cover.png",
            name = "Game name",
            summary = "Game summary",
            publishers = listOf("Publisher 1"),
            year = 2023,
        )
    }
}