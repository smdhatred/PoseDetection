package com.example.posedetection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckScreen()
{

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        horizontalAlignment = Alignment.Start) {
        Text(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),text="Train Calendar:", fontSize = 25.sp,textAlign = TextAlign.Center)
        CheckCard(text = "First Week")
        CheckCard(text ="Second Week")
        CheckCard(text ="Third Week")
    }
}

@Composable
fun CheckCard(text: String)
{
    val check = remember {
        mutableStateOf(false)
    }



        Row(modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement= Arrangement.Center,
            verticalAlignment= Alignment.Top) {
            Checkbox(checked = check.value, onCheckedChange = { check.value = it })
            Text(text, fontSize = 22.sp)
        }

}