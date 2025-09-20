package com.mzansi.icetask4

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", token)
        // You can send this token to your backend for testing
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Check data payload first
        val title = message.data["title"] ?: message.notification?.title ?: "No title"
        val body = message.data["body"] ?: message.notification?.body ?: "No body"

        Log.d("FCM_MESSAGE", "Title: $title, Body: $body")
    }

}
