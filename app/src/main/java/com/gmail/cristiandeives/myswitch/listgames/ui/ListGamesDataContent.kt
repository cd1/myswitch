package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@VisibleForTesting const val ListGamesGameItemTestTag = "ListGamesGameItem"

@ExperimentalMaterial3Api
@Composable
fun ListGamesDataContent(
    games: List<GameUiState>,
    modifier: Modifier = Modifier,
) {
    if (games.isNotEmpty()) {
        GameList(
            games = games,
            modifier = modifier,
        )
    } else {
        EmptyGameList(
            modifier = modifier
                .padding(16.dp)
                .wrapContentSize(Alignment.Center),
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun GameList(
    games: List<GameUiState>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(games) { game ->
            GameItem(
                game = game,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ListGamesGameItemTestTag),
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun GameItem(
    game: GameUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(game.coverUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_broken_image)
                .fallback(R.drawable.ic_image)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .size(64.dp)
                .clip(MaterialTheme.shapes.small),
        )

        Text(
            text = game.name,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun EmptyGameList(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.list_games_empty_message),
        modifier = modifier,
    )
}

// region Previews
@Preview(showBackground = true)
private annotation class ListGamesDataContentPreview

@ExperimentalMaterial3Api
@ListGamesDataContentPreview
@Composable
private fun EmptyPreview() {
    MySwitchTheme {
        ListGamesDataContent(
            games = emptyList(),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExperimentalMaterial3Api
@ListGamesDataContentPreview
@Composable
private fun OneItemPreview() {
    MySwitchTheme {
        ListGamesDataContent(
            games = listOf(
                GameUiState("The Legend of Zelda: Breath of the Wild", ""),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExperimentalMaterial3Api
@ListGamesDataContentPreview
@Composable
private fun ManyItemsPreview() {
    MySwitchTheme {
        ListGamesDataContent(
            games = List(100) { i ->
                GameUiState("Game #$i", "")
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}
// endregion
