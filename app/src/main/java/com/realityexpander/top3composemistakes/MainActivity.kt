package com.realityexpander.top3composemistakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.realityexpander.top3composemistakes.ui.theme.Top3ComposeMistakesTheme
import kotlinx.coroutines.launch

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
                    LoginScreen()
                    RegisterScreen(true)
                }
            }
        }
    }
}

//////////////////////// MISTAKE 1 ////////////////////////
// Passing state causing unwanted recomposition (lag).

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
//                translationX = scrollOffset  // BAD because it will be recomposed every time the scroll state changes.
                translationX = getScrollOffset() // GOOD because it will not cause recomposition.
            }
    )
}



//////////////////////// MISTAKE 2 ////////////////////////
// Putting the coroutine scope in the wrong place.

class MainViewModel: ViewModel() {
    suspend fun loginBad() {
        // BAD because it may be cancelled by the caller.
        // api.login()
    }

    fun login() {
        viewModelScope.launch {// GOOD because it will cancel the coroutine only if the ViewModel is cleared.
            // api.login()
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: MainViewModel = MainViewModel()
) {
//    val scope = rememberCoroutineScope() // BAD bc it will be cancelled on configuration change, this is only useful for animations.

    Box {
        Button(
            onClick = {
//                scope.launch {  // BAD because upon config change the coroutine will be cancelled
//                    viewModel.loginBad()
//                }
                viewModel.login()  // GOOD because the coroutine will be cancelled when the ViewModel is cleared
            }
        ) {
            Text("Login")
        }
    }
}

//////////////////////// MISTAKE 3 ////////////////////////
// Use Effect handlers.

@Composable
fun RegisterScreen(
    isLoggedIn: Boolean,
    navController: NavController = NavController(LocalContext.current),
) {

//    if(isLoggedIn) { // BAD - not recommended bc could be called out of order.
//        navController.navigate("home_screen")
//    }

    LaunchedEffect(isLoggedIn) { // GOOD recommended use of Effect handler
        if(isLoggedIn) {
            navController.navigate("home_screen")
        }
    }

    Box {
        Button(
            onClick = {
                navController.navigate("register")
            }
        ) {
            Text("Register")
        }
    }
}