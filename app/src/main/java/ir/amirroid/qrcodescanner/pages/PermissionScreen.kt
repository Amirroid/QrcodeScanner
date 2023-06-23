package ir.amirroid.qrcodescanner.pages

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import ir.amirroid.qrcodescanner.utils.AppPages

@Composable
fun PermissionScreen(navigation: NavController) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            navigation.navigate(AppPages.HomeScreen.route)
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "my should get permission")
        Button(onClick = {
            launcher.launch(
                Manifest.permission.CAMERA
            )
        }) {
            Text(text = "accept")
        }
    }
}