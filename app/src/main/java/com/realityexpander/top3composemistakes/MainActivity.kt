package com.realityexpander.top3composemistakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.realityexpander.top3composemistakes.ui.theme.Top3ComposeMistakesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Top3ComposeMistakesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Mistake1()
                }
            }
        }
    }
}

@Composable
fun Mistake1() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        for(i in 1..20) {
            MyListItem(
//                scrollOffset = scrollState.value.toFloat(), // BAD because it will be recomposed every time the scroll state changes
                getScrollOffset = { scrollState.value.toFloat() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
//fun MyListItem(scrollOffset: Float, modifier: Modifier) {
fun MyListItem(getScrollOffset: () -> Float, modifier: Modifier) {
    Text(
        text = "Scroll item",
        modifier = modifier
            .padding(32.dp)
            .graphicsLayer {
//                translationX = scrollOffset  // BAD because it will be recomposed every time the scroll state changes
                translationX = getScrollOffset()
            }
    )
}