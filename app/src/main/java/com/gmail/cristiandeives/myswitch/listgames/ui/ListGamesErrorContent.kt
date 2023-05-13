package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@Composable
fun ListGamesErrorContent(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.list_games_error),
        modifier = modifier
            .background(MaterialTheme.colorScheme.error)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.onError,
    )
}

// region Previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MySwitchTheme {
        ListGamesErrorContent(
            modifier = Modifier.padding(16.dp),
        )
    }
}
// endregion
