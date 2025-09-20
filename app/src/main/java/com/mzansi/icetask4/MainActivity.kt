package com.mzansi.icetask4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.messaging.FirebaseMessaging
import com.mzansi.icetask4.ui.theme.IceTask4Theme
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

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
            Log.d("FCM_TOKEN", token)  // This is your device token

            // -----------------------------
            // Send token to backend
            val json = """
                {
                    "token": "$token",
                    "title": "Hello",
                    "body": "This is a test notification"
                }
            """
            val client = OkHttpClient()
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(), json
            )
            val request = Request.Builder()
                .url("http://10.0.2.2:3000/send")  // emulator URL
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
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IceTask4Theme {
        Greeting("Android")
    }
}
