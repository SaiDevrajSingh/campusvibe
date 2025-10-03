package com.example.campusvibe.data

import com.example.campusvibe.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getNotifications(): Flow<List<Notification>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            // If there's no logged-in user, we can close the flow immediately.
            // This prevents the IllegalStateException because awaitClose is still reachable.
            close()
        } else {
            val listenerRegistration = firestore.collection("notifications")
                .whereEqualTo("recipientId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // If there's an error, close the flow with the exception.
                        close(e)
                        return@addSnapshotListener
                    }
                    val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                    // Send the latest list of notifications to the collector.
                    trySend(notifications)
                }
            
            // The awaitClose block is now guaranteed to be the last statement executed
            // in the callbackFlow block, which resolves the IllegalStateException.
            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("notifications").document(notificationId)
            .update("isRead", true).await()
    }

    suspend fun markAllNotificationsAsRead() {
        val currentUserId = auth.currentUser?.uid ?: return
        val notifications = firestore.collection("notifications")
            .whereEqualTo("recipientId", currentUserId)
            .whereEqualTo("isRead", false)
            .get().await()

        notifications.documents.forEach { doc ->
            doc.reference.update("isRead", true).await()
        }
    }

    suspend fun getUnreadCount(): Int {
        val currentUserId = auth.currentUser?.uid ?: return 0
        return try {
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("recipientId", currentUserId)
                .whereEqualTo("isRead", false)
                .get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
}
