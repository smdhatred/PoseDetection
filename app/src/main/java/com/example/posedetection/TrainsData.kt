package com.example.posedetection

import androidx.compose.ui.geometry.Offset

object TrainsData{
    val list = listOf(
        Train(name = "Train One",
            price = "$570.00",
            image = R.drawable.train_one,
            pointsConst = mutableListOf(Offset(300F,200F),Offset(200F,200F))
        ),
        Train(name = "Train Two",
            price = "$650.00",
            image = R.drawable.train_two,
            pointsConst = mutableListOf(Offset(200F,100F),Offset(300F,100F))
        )
    )
}