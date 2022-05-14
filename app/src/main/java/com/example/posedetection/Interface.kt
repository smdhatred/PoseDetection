package com.example.posedetection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@ExperimentalFoundationApi
@Composable
fun DashboardScreen() {

    LazyColumn(
        content = {
            stickyHeader {
                //Toolbar()
                //ImageHeader()
                PopularFlowersList()
            }

        },
        modifier = Modifier.fillMaxSize()
    )

}

@Composable
private fun PopularFlowersList() {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(TrainsData.list) { item ->
            TrainCard(item)
        }
    }
}

@Composable
fun TrainCard(train: Trains)
{
    Card(
        shape = RoundedCornerShape(14.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(10.dp)
            .width(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Image(
                painter = painterResource(id = train.image),
                contentDescription = null,
                modifier = Modifier.size(140.dp),
            )
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = train.name,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = train.price,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier.background(
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(10.dp)
                    )
                ) {
                    Icon(Icons.Default.Add, tint = Color.White,  contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ImageHeader()
{
    Box(
        modifier = Modifier
            .background(color = Color.Gray)
            .height(300.dp)
            .fillMaxWidth()
    )
}

@Composable
fun Toolbar() {
    Row(
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
    ) {
        Text("Back", modifier = Modifier.weight(1f))
        Text("Menu")
    }
}

@ExperimentalFoundationApi
@Composable
fun FirstScreen() {
    val selectedIndex = remember { mutableStateOf(0) }
    Surface(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                CustomTopAppBar()
            },
            content = {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
                    Card(
                        backgroundColor = Color.LightGray,
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(modifier = Modifier.padding(bottom = 96.dp)) {
                            when (selectedIndex.value) {
                                0 -> {
                                    DashboardScreen()
                                }
                                1 -> {
                                }
                                2 -> {
                                }
                                3 -> {
                                }
                            }
                        } //bodyContent()

                    }
                }
            },
            bottomBar = {
                CustomBottomBar(selectedIndex = selectedIndex)
            }
        )

    }
}

@Composable
fun CustomTopAppBar() {
    TopAppBar(
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pose Detector",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    style = TextStyle(
                        fontStyle = FontStyle.Italic,
                        //fontFamily = fontFamily(font(R.font.josefin_sans_semibold_italic)),
                        fontSize = 22.sp
                    )
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { }
                ) {
                    Image(
                        ImageBitmap.imageResource(id = R.drawable.ic_search),
                        contentDescription= "ds"
                    )
                }
            }
        },
        backgroundColor = Color.DarkGray,
    )
}

@Composable
fun CustomBottomBar(selectedIndex: MutableState<Int>) {

    val listItems = listOf("Home", "Location", "Cart", "Profile")

    Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(64.dp)
    ) {
        BottomNavigation(backgroundColor = Color.White) {
            listItems.forEachIndexed { index, label ->
                val isSelected = selectedIndex.value == index
                BottomNavigationItem(
                    icon = {
                        when (index) {
                            0 -> {
                                TabIcons(ImageBitmap.imageResource(id = R.drawable.ic_search), isSelected)
                            }
                            1 -> {
                                TabIcons(ImageBitmap.imageResource(id = R.drawable.ic_search), isSelected)
                            }

                        }
                    },
                    selected = isSelected,
                    onClick = { selectedIndex.value = index },
                    alwaysShowLabel = false
                )
            }
        }
    }
}

@Composable
fun TabIcons(icon: ImageBitmap, isTintColor: Boolean) {
    if (isTintColor) {
        Image(
            modifier = Modifier.wrapContentSize(),
            contentDescription= "ds",
            bitmap = icon,
            colorFilter = ColorFilter.tint(Color.LightGray),
            contentScale = ContentScale.Fit,
        )
    } else {
        Image(
            modifier = Modifier.wrapContentSize(),
            contentDescription= "ds",
            bitmap = icon,
            contentScale = ContentScale.Fit,
        )
    }
}