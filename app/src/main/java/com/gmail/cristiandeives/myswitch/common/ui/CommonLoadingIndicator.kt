package com.gmail.cristiandeives.myswitch.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@Composable
fun CommonLoadingIndicator(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()

        if (text != null) {
            Text(
                text = text,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

// region Previews
@Preview(showBackground = true)
private annotation class CommonLoadingIndicatorPreview

@CommonLoadingIndicatorPreview
@Composable
private fun DefaultCommonLoadingIndicatorPreview() {
    MySwitchTheme {
        CommonLoadingIndicator(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@CommonLoadingIndicatorPreview
@Composable
private fun CommonLoadingIndicatorWithTextPreview() {
    MySwitchTheme {
        CommonLoadingIndicator(
            text = "Loading...",
            modifier = Modifier.padding(16.dp),
        )
    }
}
// endregion
