package ir.amirroid.qrcodescanner.pages

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import ir.amirroid.qrcodescanner.R
import ir.amirroid.qrcodescanner.components.QrcodeCover
import ir.amirroid.qrcodescanner.data.models.ResultBarcode
import ir.amirroid.qrcodescanner.utils.AppPages
import ir.amirroid.qrcodescanner.utils.ScannerAnalyzer
import ir.amirroid.qrcodescanner.utils.ScannerUtils
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnusedCrossfadeTargetStateParameter", "Recycle")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigation: NavHostController) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val scannerUtils = ScannerUtils()
    val snackBarState = remember {
        SnackbarHostState()
    }
    val fileSelectorLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it != null) {
                scope.launch {
                    val fd = context.contentResolver.openFileDescriptor(it, "r")
                    val bitmap = BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor)
                    scannerUtils.scan(
                        bitmap,
                        { barcode ->
                            val data = scannerUtils.asResultBarcode(
                                barcode
                            )
                            if (data != null) {
                                navigation.navigate(
                                    AppPages.LinkScreen.route + "?data=" + Gson().toJson(
                                        data
                                    )
                                )
                            }
                        },
                        onErrorCallback = {
                            launch {
                                snackBarState.showSnackbar("error...")
                            }
                        }
                    )
                }
            }
        }
    val cameraFeaturesProvider = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var flash by remember {
        mutableStateOf(false)
    }
    var cameraController: CameraControl? = null

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(factory = {
                val previewView = PreviewView(it)
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(previewView.height, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                try {
                    val cameraFeature = cameraFeaturesProvider.get()
                    analyzer.setAnalyzer(
                        ContextCompat.getMainExecutor(it),
                        ScannerAnalyzer(
                            scannerUtils,
                            previewView,
                            { barcode ->
                                val data = scannerUtils.asResultBarcode(
                                    barcode
                                )
                                if (data != null) {
                                    cameraFeature.unbindAll()
                                    navigation.navigate(
                                        AppPages.LinkScreen.route + "?data=" + Gson().toJson(
                                            data
                                        )
                                    )
                                }
                            }
                        ) {
                            scope.launch {
                                snackBarState.showSnackbar(it.toString())
                            }
                        }
                    )
                    cameraFeature.unbindAll()
                    val camera = cameraFeature.bindToLifecycle(
                        lifecycle,
                        selector,
                        preview,
                        analyzer
                    )
                    cameraController = camera.cameraControl

                } catch (e: Exception) {
                    scope.launch {
                        snackBarState.showSnackbar(e.message.toString(), null, true)
                    }
                    e.printStackTrace()
                }
                previewView
            }, modifier = Modifier.fillMaxSize())
            QrcodeCover()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.15f)
                    .clip(RoundedCornerShape(topEnd = 26.dp, topStart = 26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircleButton(onClick = {
                        fileSelectorLauncher.launch("image/*")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_image_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    CircleButton(onClick = {
                        flash = flash.not()
                        cameraController?.enableTorch(flash)
                    }) {
                        Crossfade(targetState = flash, label = "") {
                            if (flash) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_flash_off_24),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_flash_on_24),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val size by animateDpAsState(targetValue = if (isPressed) 40.dp else 48.dp, label = "")
    Box(
        modifier = Modifier
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clickable(interactionSource, null, onClick = onClick)
                .size(size)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            content.invoke()
        }
    }
}
