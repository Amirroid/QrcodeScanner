package ir.amirroid.qrcodescanner.data.models

import android.os.Parcelable
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultBarcode(
    val type: String,
    val data: List<Info>,
    val listData: List<ListInfo>? = null,
) : Parcelable {
    @Parcelize
    data class Info(
        val title: String,
        val result: String
    ) : Parcelable

    @Parcelize
    data class ListInfo(
        val title: String,
        val result: List<String>
    ) : Parcelable
}