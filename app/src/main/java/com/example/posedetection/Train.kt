package com.example.posedetection

import androidx.compose.ui.geometry.Offset

data class Train(
    val name: String,
    val price: String,
    val image: Int,
    val info: String,
    val pointsConst: List<List<Offset>>,
    val imageConst: List<Int>
)