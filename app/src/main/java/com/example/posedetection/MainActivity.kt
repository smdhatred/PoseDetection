package com.example.posedetection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                NavHost(navController = navController, startDestination = "First Screen")
                {
                    composable("First Screen"){ FirstScreen(navController) }
                    composable("Train"){ CameraView(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA, train = nameTrain, arrayTrainPoints = arrayPointsTrain, navController = navController)}
                    composable("Info"){ CardInfo(infoTrain)}

                }
                // FirstScreen()


            }

            //DashboardScreen()
            //CameraPreview(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA)

        }

}
