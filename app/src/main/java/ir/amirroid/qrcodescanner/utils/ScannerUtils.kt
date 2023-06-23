package ir.amirroid.qrcodescanner.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.common.Barcode.DriverLicense
import com.google.mlkit.vision.barcode.common.Barcode.Email
import com.google.mlkit.vision.barcode.common.Barcode.Phone
import com.google.mlkit.vision.barcode.common.Barcode.WiFi.EncryptionType
import com.google.mlkit.vision.common.InputImage
import ir.amirroid.qrcodescanner.data.models.ResultBarcode

class ScannerUtils {
    private fun getOptions() = BarcodeScannerOptions.Builder()
        .enableAllPotentialBarcodes()
        .build()

    fun scan(
        mediaImage: Image,
        rotationDegrees: Int,
        onCallback: (Barcode) -> Unit,
        onErrorCallback: () -> Unit,
        onComplete: () -> Unit,
    ) {
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)
        val scanner = BarcodeScanning.getClient(getOptions())
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNullOrEmpty().not()) {
                    for (barcode in barcodes) {
                        onCallback.invoke(barcode)
                    }
                }
            }
            .addOnCanceledListener(onErrorCallback)
            .addOnFailureListener { onErrorCallback.invoke() }
            .addOnCompleteListener { onComplete.invoke() }
    }

    fun scan(
        bitmap: Bitmap,
        onCallback: (Barcode) -> Unit,
        onErrorCallback: () -> Unit,
        onComplete: () -> Unit = {},
    ) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient(getOptions())
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNullOrEmpty().not()) {
                    for (barcode in barcodes) {
                        onCallback.invoke(barcode)
                    }
                }
            }
            .addOnCanceledListener(onErrorCallback)
            .addOnFailureListener { onErrorCallback.invoke() }
            .addOnCompleteListener { onComplete.invoke() }
    }

    fun asResultBarcode(barcode: Barcode): ResultBarcode? {
        when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi!!.ssid
                val encryptionType = barcode.wifi!!.encryptionType
                val encryptTypes = listOf(
                    "none",
                    "wpa",
                    "wep"
                )
                val password = barcode.wifi!!.password
                return ResultBarcode(
                    "wifi",
                    listOf(
                        ResultBarcode.Info("ssid", ssid.toString()),
                        ResultBarcode.Info("encrypt type", encryptTypes[encryptionType.minus(1)]),
                        ResultBarcode.Info("password", password.toString())
                    )
                )
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                val start = barcode.calendarEvent!!.start
                val status = barcode.calendarEvent!!.status
                val description = barcode.calendarEvent!!.description
                val location = barcode.calendarEvent!!.location
                val summary = barcode.calendarEvent!!.summary
                val organizer = barcode.calendarEvent!!.organizer
                val end = barcode.calendarEvent!!.end
                return ResultBarcode(
                    "calendar event",
                    listOf(
                        ResultBarcode.Info("start", start.toDate()),
                        ResultBarcode.Info("status", status.toString()),
                        ResultBarcode.Info("description", description.toString()),
                        ResultBarcode.Info("location", location.toString()),
                        ResultBarcode.Info("summary", summary.toString()),
                        ResultBarcode.Info("organizer", organizer.toString()),
                        ResultBarcode.Info("end", end.toDate()),
                    )
                )
            }

            Barcode.TYPE_CONTACT_INFO -> {
                val name = barcode.contactInfo!!.name?.formattedName
                val address = barcode.contactInfo!!.addresses
                val email = barcode.contactInfo!!.emails
                val phones = barcode.contactInfo!!.phones
                val organization = barcode.contactInfo!!.organization
                val urls = barcode.contactInfo!!.urls
                return ResultBarcode(
                    "contact info",
                    listOf(
                        ResultBarcode.Info("name", name.toString()),
                        ResultBarcode.Info("organization", organization.toString()),
                    ),
                    listOf(
                        ResultBarcode.ListInfo("phones", phones.map { it.number.toString() }),
                        ResultBarcode.ListInfo(
                            "addresses",
                            address.map { it.addressLines.toFormattedString() }),
                        ResultBarcode.ListInfo("urls", urls.map { it.toString() }),
                        ResultBarcode.ListInfo("emails", email.map { it.address.toString() }),
                    )
                )
            }

            Barcode.TYPE_EMAIL -> {
                val subject = barcode.email!!.subject
                val address = barcode.email!!.address
                val body = barcode.email!!.body
                val type = barcode.email!!.type
                val emailTypes = listOf(
                    "unknown",
                    "work",
                    "home",
                )
                return ResultBarcode(
                    "calendar event",
                    listOf(
                        ResultBarcode.Info("subject", subject.toString()),
                        ResultBarcode.Info("address", address.toString()),
                        ResultBarcode.Info("body", body.toString()),
                        ResultBarcode.Info("type", emailTypes[type]),
                    ),
                )
            }

            Barcode.TYPE_GEO -> {
                val lat = barcode.geoPoint!!.lat
                val lng = barcode.geoPoint!!.lng
                return ResultBarcode(
                    "location",
                    listOf(
                        ResultBarcode.Info("latitude", lat.toString()),
                        ResultBarcode.Info("longitude", lng.toString()),
                    ),
                )
            }

            Barcode.TYPE_PHONE -> {
                val number = barcode.phone!!.number
                val type = barcode.phone!!.type
                val types = listOf(
                    "unknown",
                    "work",
                    "home",
                    "fax",
                    "mobile"
                )
                return ResultBarcode(
                    "phone",
                    listOf(
                        ResultBarcode.Info("number", number.toString()),
                        ResultBarcode.Info("type", types[type]),
                    ),
                )
            }

            Barcode.TYPE_TEXT -> {
                return ResultBarcode(
                    "text",
                    listOf(
                        ResultBarcode.Info("text", barcode.rawValue.toString())
                    )
                )
            }

            Barcode.TYPE_DRIVER_LICENSE -> {
                val licenseNumber = barcode.driverLicense?.licenseNumber
                val addressCity = barcode.driverLicense?.addressCity
                val addressStreet = barcode.driverLicense?.addressStreet
                val addressZip = barcode.driverLicense?.addressZip
                val birthDate = barcode.driverLicense?.birthDate
                val firstName = barcode.driverLicense?.firstName
                val lastName = barcode.driverLicense?.lastName
                val issueDate = barcode.driverLicense?.issueDate
                val middleName = barcode.driverLicense?.middleName
                val expiryDate = barcode.driverLicense?.expiryDate
                val gender = barcode.driverLicense?.gender
                val addressState = barcode.driverLicense?.addressState
                return ResultBarcode(
                    "driver license",
                    listOf(
                        ResultBarcode.Info("first name", firstName.toString()),
                        ResultBarcode.Info("middle name", middleName.toString()),
                        ResultBarcode.Info("last name", lastName.toString()),
                        ResultBarcode.Info("license number", licenseNumber.toString()),
                        ResultBarcode.Info("gender", gender.toString()),
                        ResultBarcode.Info("birth date", birthDate.toString()),
                        ResultBarcode.Info("issues data", issueDate.toString()),
                        ResultBarcode.Info("address state", addressState.toString()),
                        ResultBarcode.Info("address zip", addressZip.toString()),
                        ResultBarcode.Info("address city", addressCity.toString()),
                        ResultBarcode.Info("address street", addressStreet.toString()),
                        ResultBarcode.Info("expiry date", expiryDate.toString()),
                    )
                )
            }

            Barcode.TYPE_URL -> {
                val url = barcode.url!!.url
                return ResultBarcode(
                    "url",
                    listOf(
                        ResultBarcode.Info("url", url.toString())
                    )
                )
            }
        }
        return null
    }

    private fun Barcode.CalendarDateTime?.toDate(): String {
        return if (this != null) {
            "${year}/${month}/${day}/${hours}"
        } else ""
    }

    private fun Array<String>.toFormattedString(): String {
        var text = ""
        for (i in this) {
            text += "$i "
        }
        return text
    }
}