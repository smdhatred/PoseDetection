package com.example.posedetection

import androidx.compose.ui.geometry.Offset

data class Train(
    val name: String,
    val price: String,
    val image: Int,
    val pointsConst: List<Offset>
)