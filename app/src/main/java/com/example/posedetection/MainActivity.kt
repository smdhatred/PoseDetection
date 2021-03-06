package com.example.posedetection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.posedetection.ui.theme.PoseDetectionTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (permissionGaranted()) {
            setContent {
                initView()
            }

        } else {
            requestPermission()
        }
    }


    private fun permissionGaranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.CAMERA),0)
    }


}

@Composable
fun initView()
{


    val navController = rememberNavController()
        PoseDetectionTheme {
            // A surface container using the 'background' color from the theme

            Surface(color = MaterialTheme.colors.background)
            {
                //val navController = rememberNavController()
                var ChoosenTrain by remember { mutableStateOf<Train?>(null)}
                NavHost(navController = navController, startDestination = "First Screen")
                {
                    composable("First Screen"){ FirstScreen(navController, onTrainChoosen = { ChoosenTrain = it }) }
                    composable("Train"){ CameraView(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA, navController = navController, train = ChoosenTrain)}
                    composable("Info") { ChoosenTrain?.let { it1 -> CardInfo(it1.info) } }

                }
                // FirstScreen()


            }

            //DashboardScreen()
            //CameraPreview(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA)

        }

}
