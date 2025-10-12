package com.example.campusvibe.data

import com.example.campusvibe.model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class StoryRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getStories(): List<Story> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        val followingIds = userRepository.getFollowingIds(currentUserId)

        val userIdsToQuery = followingIds + currentUserId

        if (userIdsToQuery.isEmpty()) {
            return emptyList()
        }

        val snapshot = firestore.collection("stories")
            .whereIn("userId", userIdsToQuery)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val stories = snapshot.toObjects(Story::class.java)

        return stories.map { story ->
            val user = userRepository.getUser(story.userId)
            story.copy(username = user?.displayName ?: "")
        }
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
