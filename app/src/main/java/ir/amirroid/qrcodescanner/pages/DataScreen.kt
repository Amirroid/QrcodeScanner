package ir.amirroid.qrcodescanner.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ir.amirroid.qrcodescanner.data.models.ResultBarcode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    data: ResultBarcode,
    navigation: NavController,
    context: Context
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                MediumTopAppBar(
                    title = { Text(text = data.type) },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = { navigation.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }
                    })
            }
            items(data.data.size) {
                val model = data.data[it]
                if (model.result.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ListItem(headlineText = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.title + " : ", style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = model.result, style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (model.title == "url") {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, model.result.toUri())
                                )
                            }
                        })
                }
            }
            if (data.listData != null) {
                items(data.listData.size) {
                    val model = data.listData[it]
                    if (model.result.isNotEmpty()) {
                        Divider()
                        Text(
                            text = model.title, style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        for (i in model.result) {

                            ListItem(headlineText = {
                                Text(
                                    text = i, style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (model.title.contains("url")) {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, i.toUri())
                                        )
                                    } else
                                        if (model.title == "phones") {
                                            val intent =
                                                Intent(
                                                    Intent.ACTION_DIAL,
                                                    Uri.parse("tel:${i}")
                                                )
                                            context.startActivity(intent)
                                        }
                                })
                        }
                    }
                }
            }
        }
        if (data.type == "phone" || data.type == "location")
            Button(
                onClick = {
                    if (data.type == "phone") {
                        val intent =
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${data.data.first().result}"))
                        context.startActivity(intent)
                    } else {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("geo:${data.data.first().result},${data.data[1].result}")
                            )
                        context.startActivity(intent)
                    }
                }, modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "open")
            }
    }
}