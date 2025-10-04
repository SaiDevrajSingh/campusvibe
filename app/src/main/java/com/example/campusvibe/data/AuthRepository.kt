package com.example.campusvibe.data

import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun signUp(email: String, password: String, username: String, fullName: String) {
        // Create Firebase Auth user
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw Exception("User creation failed")

        // Create user profile in Firestore
        val user = User(
            id = userId,
            email = email,
            username = username,
            name = fullName,
            college = "",
            userType = "student",
            profilePictureUrl = null,
            bio = "",
            followers = emptyList(),
            following = emptyList(),
            posts = 0,
            highlights = emptyList(),
            fcmToken = ""
        )

        firestore.collection("users").document(userId).set(user).await()
    }

    suspend fun login(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    fun logout() = firebaseAuth.signOut()

    suspend fun resetPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email).await()

    fun getCurrentUser() = firebaseAuth.currentUser
}
