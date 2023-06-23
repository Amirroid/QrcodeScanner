package ir.amirroid.qrcodescanner.utils

sealed class AppPages(
    val route: String
) {
    object PermissionScreen : AppPages("permission")
    object HomeScreen : AppPages("home")
    object LinkScreen : AppPages("link")
}