package com.example.campusvibe.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUp(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

    suspend fun login(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    fun logout() = firebaseAuth.signOut()

    suspend fun resetPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email).await()

    fun getCurrentUser() = firebaseAuth.currentUser
}
