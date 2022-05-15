package com.example.posedetection

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController



@Composable
fun DashboardScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        PopularFlowersList(navController)
    }

//    LazyColumn(
//        content = {
//            stickyHeader {
//                //Toolbar()
//                //ImageHeader()
//                PopularFlowersList()
//            }
//
//        },
//        modifier = Modifier.fillMaxSize()
//    )

}

@Composable
private fun PopularFlowersList(navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(TrainsData.list) { item ->
            TrainCard(item,navController)
        }
    }
}
var nameTrain = ""
var infoTrain= ""

@Composable
fun TrainCard(train: Trains,navController: NavController)
{
    //val navController = rememberNavController()
    var info: String = "test"
    Card(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(10.dp)
            .width(180.dp)
            .clickable {
                infoTrain = train.price
                navController.navigate("Info")
            }
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
                    onClick = {
                        navController.navigate("Train")
                        nameTrain=train.name
                    },
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
fun CardInfo(info: String)
{
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center )
    {
        Column {
            Text(text = info)
        }
    }
}

@Composable
fun FirstScreen(navController: NavController) {
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
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(modifier = Modifier.padding(bottom = 96.dp)) {
                            when (selectedIndex.value) {
                                0 -> {
                                    DashboardScreen(navController)
                                }
                                1 -> {
                                    CameraPreview(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA,train = "Что это?")
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

    val listItems = listOf("Home", "Location")

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