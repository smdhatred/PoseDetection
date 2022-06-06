package com.example.posedetection

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode.Companion.Points
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.TaskExecutors
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun CameraView(
    cameraSelector: CameraSelector,
    modifier: Modifier = Modifier,
    train: String,
    arrayTrainPoints: MutableList<List<Offset>>,
    navController: NavController

)
{

    //val viewState = poseDetectorViewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var sourceInfo by remember { mutableStateOf(SourceInfo(10, 10, false)) }
    val previewView = remember { PreviewView(context) }
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
                PoseCompare(pose = detectedPose, arrayTrainPoints = arrayTrainPoints, navController)
                Test(train, Alignment.BottomCenter,Color.White)
                DetectedPose(pose = detectedPose, sourceInfo = sourceInfo)

                //Timer(totalTime = 100L * 1000L, modifier = Modifier.size(200.dp))
            }
        }
    }
}



var Count: Int = 0


@Composable
fun Timer(

    // total time of the timer
    totalTime: Long,
    timerStart: Boolean,
    modifier: Modifier = Modifier,
    // set initial value to 1
    initialValue: Float = 1f,
    onTimerFinish:(Boolean) -> Unit

) {
    // create variable for
    var timerIsStart by remember {
        mutableStateOf(timerStart)
    }
    var timerIsFinish by remember {
        mutableStateOf(false)
    }
    // size of the composable
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    // create variable for value
    var value by remember {
        mutableStateOf(initialValue)
    }
    // create variable for current time
    var currentTime by remember {
        mutableStateOf(totalTime)
    }
    // create variable for isTimerRunning
    var isTimerRunning by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100L
            value = currentTime / totalTime.toFloat()

        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {

        // add value of the timer
        Text(
            text = (currentTime / 1000L).toString(),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        // create button to start or stop the timer
        if(timerIsStart) {
            timerIsStart=false
            if (currentTime <= 0L) {
                currentTime = totalTime
                isTimerRunning = true
            } else {
                isTimerRunning = !isTimerRunning
            }
        }
        if(currentTime==0L) {
            //Test(train = "Well Done", Alignment.TopCenter)
            //timerIsFinish -> onTimerFinish(timerIsFinish)
            timerIsFinish=true
            if(Count<2) {
                Count += 1
            }
            else
            {
                Test(train = "Well Done", Alignment.TopCenter, Color.Green)
            }
            onTimerFinish(timerIsFinish)
        }
    }
}



@Composable
fun Test(train: String, alignment: Alignment, color: Color )
{
    Box(
        modifier = Modifier
            .fillMaxSize(),

        contentAlignment = alignment,
    ) {
        Text(text = train,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color

        )
    }
}

@Composable
fun CameraPreview(previewView: PreviewView) {
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
): ListenableFuture<ProcessCameraProvider> {
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


@Composable
fun PoseCompare(pose: Pose?, arrayTrainPoints: MutableList<List<Offset>>, navController: NavController)
{
    if (pose != null)
    {
    //Контрольные точки
    var trainPointsFirst: List<Offset> = arrayTrainPoints[0]
    var trainPointsSecond:     List<Offset> = arrayTrainPoints[1]

    var TimerStart by remember {
        mutableStateOf(false)

    }
    var NextPose by remember {
        mutableStateOf(false)
    }
//    var Count by remember {
//        mutableStateOf(0)
//    }
    var TimerFinish by remember { mutableStateOf<Boolean>(false) }

    //Значения с камеры
    val points = mutableListOf<Offset>()


    val pointsTest = mutableListOf<Offset>()
    pointsTest.add(Offset(300F,600F))


    //points.add(Offset(x.toFloat(), y.toFloat()))





            val leftWrist = pose?.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val Xlw = leftWrist.position.x
            val Ylw = leftWrist.position.y
            points.add(Offset(Xlw, Ylw))

            val rightWrist = pose?.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val Xrw = rightWrist.position.x
            val Yrw = rightWrist.position.y
            points.add(Offset(Xrw, Yrw))




        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.dp.toPx()
            val whitePaint = SolidColor(Color.White)
            val leftPaint = SolidColor(Color.Green)
            val rightPaint = SolidColor(Color.Yellow)


            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            //val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
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


            fun drawPoint(
                point: List<Offset>

            ) {

                drawPoints(

                    points = point,
                    strokeWidth = 50F,
                    pointMode = Points,
                    color = Color.Blue


                )

            }



            if (trainPointsFirst != null) {
                if (!NextPose){
                    drawPoint(trainPointsFirst)
                }
                else
                {
                    drawPoint(arrayTrainPoints[Count])
                }
            }
        }

        fun Compare(trainPoints: List<Offset>)
        {
            if (((points[0].x >= trainPoints[0].x - 50F) && (points[0].x <= trainPoints[0].x + 50F)) && ((points[0].y >= trainPoints[0].y - 50F) && (points[0].y <= trainPoints[0].y + 50F))
                || ((points[1].x >= trainPoints[1].x - 50F) && (points[1].x <= trainPoints[1].x + 50F)) && ((points[1].y >= trainPoints[1].y - 50F) && (points[1].y <= trainPoints[1].y + 50F))
            ) {
                //drawPoint(pointsTest)
                TimerStart=true

            }
            else{
                TimerStart=false
            }
        }

        if(arrayTrainPoints[Count] != null) {
            if (NextPose) {
                //Compare(arrayTrainPoints[1])
                //NextPose=false
                //Compare(trainPointsSecond)

            }


            Compare(trainPointsFirst)
//        for (pose in arrayTrainPoints) {
//            if(NextPose) {
//                Compare(pose)
//                NextPose=false
//            }
//        }



            if (TimerFinish) {
                TimerStart = false
                NextPose = true

                //Test(train = "Well Done", Alignment.TopCenter, Color.Green)
            }

            if (NextPose) {
                Compare(arrayTrainPoints[Count])
                //NextPose=false
                //Compare(trainPointsSecond)

            }


            if (TimerStart) {
                Timer(
                    totalTime = 5L * 1000L,
                    modifier = Modifier.size(200.dp),
                    timerStart = TimerStart,
                    onTimerFinish = { TimerFinish = it })
            }

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

