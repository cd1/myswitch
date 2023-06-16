package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.ui.UiResult
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@VisibleForTesting const val AddGameDetailsContentNameTestTag = "AddGameDetailsContentName"
@VisibleForTesting const val AddGameDetailsContentSummaryTestTag = "AddGameDetailsContentSummary"
@VisibleForTesting const val AddGameDetailsContentPublisherTestTag = "AddGameDetailsContentPublisher"
@VisibleForTesting const val AddGameDetailsContentYearTestTag = "AddGameDetailsContentYear"
@VisibleForTesting const val AddGameDetailsContentPhysicalMediaTypeTestTag = "AddGameDetailsContentPhysicalMediaType"
@VisibleForTesting const val AddGameDetailsContentDigitalMediaTypeTestTag = "AddGameDetailsContentDigitalMediaType"
@VisibleForTesting const val AddGameDetailsContentAddResultLoadingTestTag = "AddGameDetailsContentAddResultLoading"

@Composable
fun AddGameDetailsContent(
    artworkUrl: String?,
    name: String,
    summary: String?,
    publisher: String?,
    year: String?,
    mediaType: GameMediaType?,
    onMediaTypeChange: (GameMediaType) -> Unit,
    isAddButtonEnabled: Boolean,
    addGameDetailsResult: UiResult?,
    displayErrorMessage: suspend (String) -> Unit,
    onAddButtonClick: () -> Unit,
    onErrorMessageDisplayed: () -> Unit,
    onGameAdded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = addGameDetailsResult) {
        when (addGameDetailsResult) {
            UiResult.Success -> {
                onGameAdded()
            }
            UiResult.Error -> {
                displayErrorMessage(context.getString(R.string.add_game_details_add_error))
                onErrorMessageDisplayed()
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artworkUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.ic_broken_image)
                    .fallback(R.drawable.ic_image)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier.aspectRatio(16F / 9),
            )

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(AddGameDetailsContentNameTestTag),
            )

            Text(
                text = summary
                    ?: stringResource(R.string.add_game_details_no_summary_available),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(AddGameDetailsContentSummaryTestTag),
            )

            if (publisher != null) {
                Text(
                    text = publisher,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .testTag(AddGameDetailsContentPublisherTestTag),
                )
            }

            if (year != null) {
                Text(
                    text = year,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .testTag(AddGameDetailsContentYearTestTag),
                )
            }

            val mediaTypeTextTopPadding = if (publisher != null || year != null) {
                16.dp
            } else {
                0.dp
            }

            Divider(
                modifier = Modifier.padding(
                    top = mediaTypeTextTopPadding,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            )

            Text(
                text = stringResource(R.string.add_game_details_media_type_label),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            ) {
                GameMediaTypeToggleButton(
                    icon = R.drawable.ic_sim_card,
                    label = stringResource(R.string.add_game_details_media_type_physical),
                    checked = (mediaType == GameMediaType.Physical),
                    onCheckedChange = { onMediaTypeChange(GameMediaType.Physical) },
                    modifier = Modifier.testTag(AddGameDetailsContentPhysicalMediaTypeTestTag),
                )

                GameMediaTypeToggleButton(
                    icon = R.drawable.ic_cloud_download,
                    label = stringResource(R.string.add_game_details_media_type_digital),
                    checked = (mediaType == GameMediaType.Digital),
                    onCheckedChange = { onMediaTypeChange(GameMediaType.Digital) },
                    modifier = Modifier.testTag(AddGameDetailsContentDigitalMediaTypeTestTag),
                )
            }

            Button(
                onClick = onAddButtonClick,
                enabled = isAddButtonEnabled,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(R.string.add_game_details_button))
            }
        }

        if (addGameDetailsResult == UiResult.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AddGameDetailsContentAddResultLoadingTestTag),
            )
        }
    }
}

@Composable
private fun GameMediaTypeToggleButton(
    @DrawableRes icon: Int,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedIconToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

// region Previews
@Preview(showBackground = true)
private annotation class AddGameDetailsContentPreview

@AddGameDetailsContentPreview
@Composable
private fun AddGameDetailsDefaultPreview() {
    MySwitchTheme {
        AddGameDetailsContent(
            name = "Game title",
            summary = "Game summary",
            artworkUrl = "",
            publisher = "Nintendo",
            year = "2023",
            mediaType = GameMediaType.Physical,
            isAddButtonEnabled = true,
            addGameDetailsResult = null,
            displayErrorMessage = {},
            onMediaTypeChange = {},
            onErrorMessageDisplayed = {},
            onAddButtonClick = {},
            onGameAdded = {},
        )
    }
}

@AddGameDetailsContentPreview
@Composable
private fun AddGameDetailsNoPublisherPreview() {
    MySwitchTheme {
        AddGameDetailsContent(
            name = "Game title",
            summary = "Game summary",
            artworkUrl = "",
            publisher = null,
            year = "2023",
            mediaType = null,
            isAddButtonEnabled = false,
            addGameDetailsResult = null,
            displayErrorMessage = {},
            onMediaTypeChange = {},
            onErrorMessageDisplayed = {},
            onAddButtonClick = {},
            onGameAdded = {},
        )
    }
}

@AddGameDetailsContentPreview
@Composable
private fun AddGameDetailsNoYearPreview() {
    MySwitchTheme {
        AddGameDetailsContent(
            name = "Game title",
            summary = "Game summary",
            artworkUrl = "",
            publisher = "Nintendo",
            year = null,
            mediaType = null,
            isAddButtonEnabled = false,
            addGameDetailsResult = null,
            displayErrorMessage = {},
            onMediaTypeChange = {},
            onErrorMessageDisplayed = {},
            onAddButtonClick = {},
            onGameAdded = {},
        )
    }
}

@AddGameDetailsContentPreview
@Composable
private fun AddGameDetailsNoPublisherAndYearPreview() {
    MySwitchTheme {
        AddGameDetailsContent(
            name = "Game title",
            summary = "Game summary",
            artworkUrl = "",
            publisher = null,
            year = null,
            mediaType = null,
            isAddButtonEnabled = false,
            addGameDetailsResult = null,
            displayErrorMessage = {},
            onMediaTypeChange = {},
            onErrorMessageDisplayed = {},
            onAddButtonClick = {},
            onGameAdded = {},
        )
    }
}
// endregion