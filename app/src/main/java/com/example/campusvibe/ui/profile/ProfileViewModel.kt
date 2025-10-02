package com.example.campusvibe.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    private val _followersCount = MutableLiveData<Int>()
    val followersCount: LiveData<Int> = _followersCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> = _followingCount

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _user.value = repository.getUser(userId)
            _posts.value = repository.getUserPosts(userId)
            _followersCount.value = repository.getFollowersCount(userId)
            _followingCount.value = repository.getFollowingCount(userId)
            if (auth.currentUser != null && auth.currentUser!!.uid != userId) {
                _isFollowing.value = repository.isFollowing(userId)
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            repository.followUser(userId)
            _isFollowing.value = true
            _followersCount.value = (_followersCount.value ?: 0) + 1
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            repository.unfollowUser(userId)
            _isFollowing.value = false
            _followersCount.value = (_followersCount.value ?: 0) - 1
        }
    }

    fun updateUserProfile(username: String, bio: String) {
        viewModelScope.launch {
            repository.updateUserProfile(username, bio, null)
            _user.value = repository.getUser(auth.currentUser!!.uid)
        }
    }

    fun updateUserProfileWithImage(username: String, bio: String, imageUri: Uri) {
        viewModelScope.launch {
            val imageUrl = repository.uploadProfileImage(imageUri)
            repository.updateUserProfile(username, bio, imageUrl)
            _user.value = repository.getUser(auth.currentUser!!.uid)
        }
    }
}

