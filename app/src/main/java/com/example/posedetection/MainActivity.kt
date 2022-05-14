package com.example.posedetection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.posedetection.ui.theme.PoseDetectionTheme
import com.google.android.gms.tasks.TaskExecutors
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (permissionGaranted()) {
            initView()
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


    private fun initView()
    {
        setContent {

            PoseDetectionTheme {
                // A surface container using the 'background' color from the theme

                Surface(color = MaterialTheme.colors.background) {
                    FirstScreen()
                    //DashboardScreen()
                    //CameraPreview(cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA)
                }
            }
        }
    }

    @Composable
    fun CameraPreview( cameraSelector: CameraSelector,
                       modifier: Modifier = Modifier
    )
    {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        var sourceInfo by remember { mutableStateOf(SourceInfo(10, 10, false)) }
        val previewView = remember {PreviewView(context)}
        var detectedPose by remember { mutableStateOf<Pose?>(null) }
        val cameraProvider = remember (sourceInfo, cameraSelector) { ProcessCameraProvider.getInstance(context).configureCamera(
            previewView, lifecycleOwner, cameraSelector, context,
            setSourceInfo = { sourceInfo = it },
            onPoseDetected = { detectedPose = it }
        )
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            with(LocalDensity.current) {
                Box(
                    modifier = Modifier
                        .size(
                            height = sourceInfo.height.toDp(),
                            width = sourceInfo.width.toDp()
                        )
                        .scale(
                            calculateScale(
                                constraints,
                                sourceInfo,
                                PreviewScaleType.CENTER_CROP
                            )
                        )
                )
                {
                    CameraPreview(previewView)
                    DetectedPose(pose = detectedPose, sourceInfo = sourceInfo)
                }
            }
        }
    }
}
@Composable
private fun CameraPreview(previewView: PreviewView) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            previewView.apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Preview is incorrectly scaled in Compose on some devices without this
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            previewView
        })
}

private fun ListenableFuture<ProcessCameraProvider>.configureCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    context: Context,
    setSourceInfo: (SourceInfo) -> Unit,
    onPoseDetected: (Pose) -> Unit
):ListenableFuture<ProcessCameraProvider>{
    addListener({
        val preview=androidx.camera.core.Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
        val analysis =
            bindAnalysisUseCase(cameraSelector, setSourceInfo, onPoseDetected)
        try {
            get().apply {
                unbindAll()
                bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                bindToLifecycle(lifecycleOwner, cameraSelector, analysis)
            }
        }catch (exc: Exception){ }

    }, ContextCompat.getMainExecutor(context))
    return this
}

@Composable
fun DetectedPose(
    pose: Pose?,
    sourceInfo: SourceInfo
) {
    if (pose != null) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.dp.toPx()
            val whitePaint = SolidColor(Color.White)
            val leftPaint = SolidColor(Color.Green)
            val rightPaint = SolidColor(Color.Yellow)

            val needToMirror = sourceInfo.isImageFlipped
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
            val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
            val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
            val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
            val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
            val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
            val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
            val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
            val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

            fun drawLine(
                startLandmark: PoseLandmark?,
                endLandmark: PoseLandmark?,
                paint: Brush
            ) {
                if (startLandmark != null && endLandmark != null) {
                    val startX =
                        if (needToMirror) size.width - startLandmark.position.x else startLandmark.position.x
                    val startY = startLandmark.position.y
                    val endX =
                        if (needToMirror) size.width - endLandmark.position.x else endLandmark.position.x
                    val endY = endLandmark.position.y
                    drawLine(
                        brush = paint,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth,
                    )
                }
            }

            drawLine(leftShoulder, rightShoulder, whitePaint)
            drawLine(leftHip, rightHip, whitePaint)
            // Left body
            drawLine(leftShoulder, leftElbow, leftPaint)
            drawLine(leftElbow, leftWrist, leftPaint)
            drawLine(leftShoulder, leftHip, leftPaint)
            drawLine(leftHip, leftKnee, leftPaint)
            drawLine(leftKnee, leftAnkle, leftPaint)
            drawLine(leftWrist, leftThumb, leftPaint)
            drawLine(leftWrist, leftPinky, leftPaint)
            drawLine(leftWrist, leftIndex, leftPaint)
            drawLine(leftIndex, leftPinky, leftPaint)
            drawLine(leftAnkle, leftHeel, leftPaint)
            drawLine(leftHeel, leftFootIndex, leftPaint)
            // Right body
            drawLine(rightShoulder, rightElbow, rightPaint)
            drawLine(rightElbow, rightWrist, rightPaint)
            drawLine(rightShoulder, rightHip, rightPaint)
            drawLine(rightHip, rightKnee, rightPaint)
            drawLine(rightKnee, rightAnkle, rightPaint)
            drawLine(rightWrist, rightThumb, rightPaint)
            drawLine(rightWrist, rightPinky, rightPaint)
            drawLine(rightWrist, rightIndex, rightPaint)
            drawLine(rightIndex, rightPinky, rightPaint)
            drawLine(rightAnkle, rightHeel, rightPaint)
            drawLine(rightHeel, rightFootIndex, rightPaint)
        }
    }
}
private fun bindAnalysisUseCase(
    cameraSelector: CameraSelector,
    setSourceInfo: (SourceInfo) -> Unit,
    onPoseDetected: (Pose) -> Unit
): ImageAnalysis? {


    val poseProcessor = try {
        PoseDetectorProcessor()
    } catch (e: Exception) {
        Log.e("CAMERA", "Can not create pose processor", e)
        return null
    }
    val builder = ImageAnalysis.Builder()
    val analysisUseCase = builder.build()

    var sourceInfoUpdated = false

    analysisUseCase.setAnalyzer(
        TaskExecutors.MAIN_THREAD,
        { imageProxy: ImageProxy ->
            if (!sourceInfoUpdated) {
                setSourceInfo(obtainSourceInfo(cameraSelector, imageProxy))
                sourceInfoUpdated = true
            }
            try {
                poseProcessor.processImageProxy(imageProxy, onPoseDetected)
            } catch (e: MlKitException) {
                Log.e(
                    "CAMERA", "Failed to process image. Error: " + e.localizedMessage
                )
            }
        }
    )
    return analysisUseCase
}
class PoseDetectorProcessor {

    private val detector: PoseDetector

    private val executor = TaskExecutors.MAIN_THREAD

    init {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        detector = PoseDetection.getClient(options)
    }

    fun stop() {
        detector.close()
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    fun processImageProxy(image: ImageProxy, onDetectionFinished: (Pose) -> Unit) {
        detector.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { results: Pose -> onDetectionFinished(results) }
            .addOnFailureListener(executor) { e: Exception ->
                Log.e("Camera", "Error detecting pose", e)
            }
            .addOnCompleteListener { image.close() }
    }
}


private fun obtainSourceInfo(cameraSelector: CameraSelector, imageProxy: ImageProxy): SourceInfo {
    val isImageFlipped = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    return if (rotationDegrees == 0 || rotationDegrees == 180) {
        SourceInfo(
            height = imageProxy.height, width = imageProxy.width, isImageFlipped = isImageFlipped
        )
    } else {
        SourceInfo(
            height = imageProxy.width, width = imageProxy.height, isImageFlipped = isImageFlipped
        )
    }
}

data class SourceInfo(
    val width: Int,
    val height: Int,
    val isImageFlipped: Boolean,
)

private enum class PreviewScaleType {
    FIT_CENTER,
    CENTER_CROP
}

private fun calculateScale(
    constraints: Constraints,
    sourceInfo: SourceInfo,
    scaleType: PreviewScaleType
): Float {
    val heightRatio = constraints.maxHeight.toFloat() / sourceInfo.height
    val widthRatio = constraints.maxWidth.toFloat() / sourceInfo.width
    return when (scaleType) {
        PreviewScaleType.FIT_CENTER -> kotlin.math.min(heightRatio, widthRatio)
        PreviewScaleType.CENTER_CROP -> kotlin.math.max(heightRatio, widthRatio)
    }
}