package com.example.campusvibe.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        _user.value = authRepository.getCurrentUser()
    }

    fun signUp(email: String, password: String, username: String, fullName: String) {
        viewModelScope.launch {
            try {
                authRepository.signUp(email, password, username, fullName)
                _user.value = authRepository.getCurrentUser()
                _authState.value = AuthState.SUCCESS
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.login(email, password)
                _user.value = authRepository.getCurrentUser()
                _authState.value = AuthState.SUCCESS
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _user.value = null
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                authRepository.resetPassword(email)
                _authState.value = AuthState.SUCCESS
            } catch (e: Exception) {
                _authState.value = AuthState.ERROR(e.message)
            }
        }
    }
}

sealed class AuthState {
    object SUCCESS : AuthState()
    data class ERROR(val message: String?) : AuthState()
}
