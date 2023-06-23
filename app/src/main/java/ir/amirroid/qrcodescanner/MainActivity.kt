package ir.amirroid.qrcodescanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.gson.Gson
import ir.amirroid.qrcodescanner.data.models.ResultBarcode
import ir.amirroid.qrcodescanner.pages.DataScreen
import ir.amirroid.qrcodescanner.pages.HomeScreen
import ir.amirroid.qrcodescanner.pages.PermissionScreen
import ir.amirroid.qrcodescanner.ui.theme.QrcodeScannerTheme
import ir.amirroid.qrcodescanner.utils.AppPages

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QrcodeScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(context: Context) {
    val navController = rememberAnimatedNavController()
    val isPermission =
        context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    AnimatedNavHost(
        navController = navController,
        startDestination = if (isPermission) AppPages.HomeScreen.route else AppPages.PermissionScreen.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { -200 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { 200 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { 200 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { -200 }) + fadeOut() },
    ) {
        composable(AppPages.PermissionScreen.route) {
            PermissionScreen(navigation = navController)
        }
        composable(AppPages.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(
            AppPages.LinkScreen.route + "?data={data}",
            arguments = listOf(
                navArgument("data") {
                    type = NavType.StringType
                }
            )
        ) {
            val data = it.arguments?.getString("data", null)
            if (data != null) {
                val dataC = Gson().fromJson(data, ResultBarcode::class.java)
                DataScreen(data = dataC, navController, context)
            } else {
                navController.navigate(AppPages.HomeScreen.route) {
                    popUpTo(AppPages.HomeScreen.route) {
                        inclusive = true
                    }
                }
                Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
