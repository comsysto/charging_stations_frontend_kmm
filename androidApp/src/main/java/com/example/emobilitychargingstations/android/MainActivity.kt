package com.comsystoreply.emobilitychargingstations.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.comsystoreply.emobilitychargingstations.Greeting
import com.example.emobilitychargingstations.android.StationsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val stationsViewModel: StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GreetingView(Greeting().greet(), stationsViewModel)
                }
            }
        }
    }
}


@Composable
fun GreetingView(text: String, stationsViewModel: StationsViewModel) {
    val testStation = stationsViewModel._stationsData.observeAsState()
    stationsViewModel.getTestStations()
    LazyColumn() {
        items(1) {
            Text(text = testStation.value?.address ?: "")
        }
    }

}

//@Preview
//@Composable
//fun DefaultPreview() {
//    MyApplicationTheme {
//        GreetingView("Hello, Android!")
//    }
//}
