package com.example.campusvibe.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.ChatRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModel(private val repo: ChatRepository) : ViewModel() {
    private val _uid = MutableStateFlow<String?>(null)
    fun setUser(uid: String) { _uid.value = uid }

    private val uid: StateFlow<String?> = _uid.asStateFlow()

    @Suppress("UNCHECKED_CAST")
    val filteredChats: StateFlow<List<DocumentSnapshot>?> = uid.filterNotNull().flatMapLatest { uid ->
        flow {
            val followList = repo.getFollowList(uid)
            repo.observeChatsFor(uid).collect { querySnapshot ->
                val filteredDocs = querySnapshot.documents.filter { document ->
                    val participants = document.get("participants") as? List<String> ?: emptyList()
                    val otherUid = participants.find { it != uid }
                    otherUid in followList
                }
                emit(filteredDocs)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

}
