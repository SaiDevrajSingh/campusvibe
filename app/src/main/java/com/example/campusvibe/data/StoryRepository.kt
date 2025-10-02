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

        // For demo purposes, add some sample stories if none exist
        val stories = snapshot.toObjects(Story::class.java)
        if (stories.isEmpty()) {
            // Add sample stories for demonstration
            val sampleStories = listOf(
                Story(
                    id = "sample1",
                    userId = "user1",
                    imageUrl = "https://picsum.photos/300/400?random=1",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    isPlaceholder = false
                ),
                Story(
                    id = "sample2",
                    userId = "user2",
                    imageUrl = "https://picsum.photos/300/400?random=2",
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    isPlaceholder = false
                ),
                Story(
                    id = "sample3",
                    userId = "user3",
                    imageUrl = "https://picsum.photos/300/400?random=3",
                    timestamp = System.currentTimeMillis() - 10800000, // 3 hours ago
                    isPlaceholder = false
                )
            )

            // Add sample stories to Firestore (in a real app, this would be done when users create stories)
            sampleStories.forEach { story ->
                firestore.collection("stories").document(story.id).set(story).await()
            }

            return sampleStories
        }

        return stories
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

