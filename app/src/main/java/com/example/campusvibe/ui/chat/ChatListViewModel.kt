package com.example.campusvibe.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.ChatRepository
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListViewModel @Inject constructor(private val repo: ChatRepository) : ViewModel() {
    private val _uid = MutableStateFlow<String?>(null)
    fun setUser(uid: String) { _uid.value = uid }

    val chats: StateFlow<QuerySnapshot?> = _uid.filterNotNull().flatMapLatest { uid ->
        repo.observeChatsFor(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun searchUsers(q: String, callback: (List<Pair<String, Map<String, Any>>>) -> Unit) {
        viewModelScope.launch {
            val res = repo.searchUsers(q)
            callback(res)
        }
    }
}
