package com.example.canvasdemo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameState {
    var pacManX by mutableStateOf(0)
    var pacManY by mutableStateOf(300)
    var direction by mutableStateOf(DIRECTION.RIGTH)
    var gameRunning by mutableStateOf(true)
}