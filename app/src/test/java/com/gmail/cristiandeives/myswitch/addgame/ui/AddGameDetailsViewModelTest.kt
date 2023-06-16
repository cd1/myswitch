package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.testing.TestLifecycleOwner
import arrow.core.left
import arrow.core.right
import com.gmail.cristiandeives.myswitch.MainDispatcherRule
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GameDetails
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.FakeLogger
import com.gmail.cristiandeives.myswitch.common.ui.UiResult
import com.gmail.cristiandeives.myswitch.navigation.NavRoute
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AddGameDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var gamesRepository: GamesRepository

    private lateinit var mockitoAnnotations: AutoCloseable
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: AddGameDetailsViewModel
    private lateinit var lifecycleOwner: TestLifecycleOwner

    @Before
    fun before() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)

        savedStateHandle = SavedStateHandle()
        viewModel = AddGameDetailsViewModel(gamesRepository, savedStateHandle, FakeLogger())
        lifecycleOwner = TestLifecycleOwner(Lifecycle.State.INITIALIZED).apply {
            lifecycle.addObserver(viewModel)
        }
    }

    @After
    fun after() {
        mockitoAnnotations.close()
    }

    @Test
    fun onInit_initialValueIsSet() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        moveViewModelToOnCreate()

        with(viewModel.uiState.value) {
            assertGameArtworkUrlIs(defaultGameDetails.artworkUrl!!)
            assertGameNameIs(defaultGameDetails.name)
            assertGameSummaryIs(defaultGameDetails.summary!!)
            assertGamePublisherIs(defaultGameDetails.publishers.first())
            assertGameYearIs(defaultGameDetails.year.toString())
            assertGameMediaTypeIsNull()
            assertAddButtonIsNotEnabled()
            assertAddGameDetailsResultIsNull()
        }
    }

    @Test
    fun onInit_whenGameIdSet_thenGameIdFetched() = runTest {
        val gameId = 42L

        setTestGameId(gameId)
        mockRepositoryGetGameDetails(defaultGameDetails)
        moveViewModelToOnCreate()

        verify(gamesRepository).getGameDetails(gameId)
    }

    @Test
    fun onInit_whenGameIdNotFound_thenErrorState() = runTest {
        setTestGameId(null)
        mockRepositoryGetGameDetailsError()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertIsErrorState()
    }

    @Test
    fun onInit_whenRepositoryGetGameDetailsHasError_thenErrorState() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetailsError()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertIsErrorState()
    }

    @Test
    fun onInit_whenMultiplePublishers_thenPublishersJoinedIntoOneString() = runTest {
        val publishers = listOf("Publisher 1", "Publisher 2")
        
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails.copy(publishers = publishers))
        moveViewModelToOnCreate()
        
        viewModel.uiState.value.assertGamePublisherIs(publishers.joinToString(", "))
    }

    @Test
    fun onMediaTypeChange_mediaTypeIsChanged() = runTest {
        val mediaType = GameMediaType.Digital

        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        moveViewModelToOnCreate()

        viewModel.onMediaTypeChange(mediaType)

        viewModel.uiState.value.assertGameMediaTypeIs(mediaType)
    }

    @Test
    fun onMediaTypeChange_addButtonIsEnabled() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        moveViewModelToOnCreate()

        viewModel.onMediaTypeChange(GameMediaType.Digital)

        viewModel.uiState.value.assertAddButtonIsEnabled()
    }

    @Test
    fun onAddButtonClick_whenSuccess_thenLoadingAndSuccessStates() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameSuccess()
        moveViewModelToOnCreate()

        viewModel.apply {
            onMediaTypeChange(GameMediaType.Physical)
            onAddButtonClick()
        }

        viewModel.uiState.value.assertAddGameDetailsResultIs(UiResult.Success)
    }

    @Test
    fun onAddButtonClick_whenRepositoryInsertGameHasError_thenLoadingAndErrorStates() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameError()
        moveViewModelToOnCreate()

        viewModel.apply {
            onMediaTypeChange(GameMediaType.Physical)
            onAddButtonClick()
        }

        viewModel.uiState.value.assertAddGameDetailsResultIs(UiResult.Error)
    }

    @Test
    fun onAddButtonClick_correctValuesAreInserted() = runTest {
        val mediaType = GameMediaType.Digital

        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameSuccess()
        moveViewModelToOnCreate()

        viewModel.apply {
            onMediaTypeChange(mediaType)
            onAddButtonClick()
        }

        val insertedGame = Game(
            coverUrl = defaultGameDetails.coverUrl,
            name = defaultGameDetails.name,
            mediaType = mediaType,
        )

        verify(gamesRepository).insertGame(insertedGame)
    }

    @Test
    fun onAddButtonClick_whenNoMediaTypeIsSelected_nothingHappens() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameSuccess()
        moveViewModelToOnCreate()

        viewModel.onAddButtonClick()

        viewModel.uiState.value.assertAddGameDetailsResultIsNull()
    }

    @Test
    fun onMarkErrorAsDisplayed_whenErrorState_errorStateIsCleared() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameError()
        moveViewModelToOnCreate()

        viewModel.apply {
            onMediaTypeChange(GameMediaType.Physical)
            onAddButtonClick()
            markErrorAsDisplayed()
        }

        viewModel.uiState.value.assertAddGameDetailsResultIsNull()
    }

    @Test
    fun onMarkErrorAsDisplayed_whenSuccessState_nothingHappens() = runTest {
        setTestGameId(42)
        mockRepositoryGetGameDetails(defaultGameDetails)
        mockRepositoryInsertGameSuccess()
        moveViewModelToOnCreate()

        viewModel.apply {
            onMediaTypeChange(GameMediaType.Physical)
            onAddButtonClick()
            markErrorAsDisplayed()
        }

        viewModel.uiState.value.assertAddGameDetailsResultIs(UiResult.Success)
    }

    private fun moveViewModelToOnCreate() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    private fun setTestGameId(gameId: Long?) {
        savedStateHandle[NavRoute.AddGameDetails.gameIdArg] = gameId
    }

    private suspend fun mockRepositoryGetGameDetails(gameDetails: GameDetails) {
        whenever(gamesRepository.getGameDetails(any()))
            .thenReturn(gameDetails.right())
    }

    private suspend fun mockRepositoryGetGameDetailsError() {
        whenever(gamesRepository.getGameDetails(any()))
            .thenReturn(Unit.left())
    }

    private suspend fun mockRepositoryInsertGameSuccess() {
        whenever(gamesRepository.insertGame(any()))
            .thenReturn(Unit.right())
    }

    private suspend fun mockRepositoryInsertGameError() {
        whenever(gamesRepository.insertGame(any()))
            .thenReturn(Unit.left())
    }

    private fun AddGameDetailsUiState.assertIsErrorState() {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Error::class.java)
    }

    private fun AddGameDetailsUiState.assertGameArtworkUrlIs(expectedUrl: String) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).artworkUrl).isEqualTo(expectedUrl)
    }

    private fun AddGameDetailsUiState.assertGameNameIs(expectedName: String) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).name).isEqualTo(expectedName)
    }

    private fun AddGameDetailsUiState.assertGameSummaryIs(expectedSummary: String) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).summary).isEqualTo(expectedSummary)
    }

    private fun AddGameDetailsUiState.assertGamePublisherIs(expectedPublisher: String) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).publisher).isEqualTo(expectedPublisher)
    }

    private fun AddGameDetailsUiState.assertGameYearIs(expectedYear: String) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).year).isEqualTo(expectedYear)
    }

    private fun AddGameDetailsUiState.assertGameMediaTypeIsNull() {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).mediaType).isNull()
    }

    private fun AddGameDetailsUiState.assertGameMediaTypeIs(expectedMediaType: GameMediaType) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).mediaType).isEqualTo(expectedMediaType)
    }

    private fun AddGameDetailsUiState.assertAddButtonIsEnabled() {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).isAddButtonEnabled).isTrue()
    }

    private fun AddGameDetailsUiState.assertAddButtonIsNotEnabled() {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).isAddButtonEnabled).isFalse()
    }

    private fun AddGameDetailsUiState.assertAddGameDetailsResultIsNull() {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).addGameDetailsResult).isNull()
    }

    private fun AddGameDetailsUiState.assertAddGameDetailsResultIs(expectedResult: UiResult) {
        assertThat(this).isInstanceOf(AddGameDetailsUiState.Data::class.java)
        assertThat((this as AddGameDetailsUiState.Data).addGameDetailsResult).isEqualTo(expectedResult)
    }

    companion object {
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