package com.example.campusvibe.data

import com.example.campusvibe.model.Story
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.jvm.java

class StoryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getStories(): List<Story> {
        val snapshot = firestore.collection("stories").orderBy("timestamp", Query.Direction.DESCENDING).get().await()
        return snapshot.toObjects(Story::class.java)
    }

    suspend fun uploadStory(story: Story) {
        firestore.collection("stories").add(story).await()
    }

    suspend fun deleteExpiredStories() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -24)
        val twentyFourHoursAgo = calendar.time

        val expiredStories = firestore.collection("stories")
            .whereLessThan("timestamp", twentyFourHoursAgo)
            .get()
            .await()

        for (document in expiredStories.documents) {
            firestore.collection("stories").document(document.id).delete().await()
        }
    }
}

