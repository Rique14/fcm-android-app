package com.mzansi.icetask4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.messaging.FirebaseMessaging
import com.mzansi.icetask4.ui.theme.IceTask4Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import androidx.compose.ui.unit.dp


// Shared state for UI updates
object NotificationState {
    private val _message = MutableStateFlow("Waiting for notification...")
    val message = _message.asStateFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Fetch FCM device token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)

            // -----------------------------
            // Send token to backend
            val json = """
                {
                    "token": "$token",
                    "title": "Hello",
                    "body": "This is a test notification"
                }
            """.trimIndent()
            val client = OkHttpClient()
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(), json
            )
            val request = Request.Builder()
                .url("http://10.0.2.2:3000/send")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("FCM_POST", "Failed to send token", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("FCM_POST", "Response: ${response.body?.string()}")
                }
            })
            // -----------------------------
        }

        setContent {
            IceTask4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    val message by NotificationState.message.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FCM Notification:",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview() {
    IceTask4Theme {
        NotificationScreen()
    }
}
