package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@Composable
fun ListGamesLoadingContent(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
    )
}

// region Previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MySwitchTheme {
        ListGamesLoadingContent(
            modifier = Modifier.padding(16.dp),
        )
    }
}
// endregion
