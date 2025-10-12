package com.example.campusvibe.data

import com.example.campusvibe.model.ChatMessage
import com.example.campusvibe.utils.asFlow
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val chats = firestore.collection("chats")
    private val follows = firestore.collection("follows")
    
    fun chatIdFor(a: String, b: String): String = if (a < b) "${a}_$b" else "${b}_$a"

    suspend fun canChat(myUid: String, otherUid: String): Boolean {
        // check if myUid follows otherUid OR otherUid follows myUid
        val a = follows.document(myUid).collection("following").document(otherUid).get()
        val b = follows.document(otherUid).collection("following").document(myUid).get()
        val results = listOf(a, b).map { it.await() }
        return results.any { it.exists() }
    }

    // observe chat list for user
    fun observeChatsFor(uid: String): Flow<QuerySnapshot> {
        return chats.whereArrayContains("participants", uid)
            .orderBy("lastMessage.timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .asFlow()
    }

    // observe messages in chat (real-time)
    fun observeMessages(chatId: String, limit: Int = 50): Flow<QuerySnapshot> {
        return chats.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .asFlow()
    }

    suspend fun loadMoreMessages(chatId: String, lastDoc: DocumentSnapshot?, pageSize: Int = 50): List<DocumentSnapshot> {
        val q = chats.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(pageSize.toLong())
        val finalQ = if (lastDoc != null) q.startAfter(lastDoc) else q
        val snap = finalQ.get().await()
        return snap.documents
    }

    // send message with atomic update: create message doc + update chat meta
    suspend fun sendMessage(chatId: String, message: ChatMessage) {
        val chatRef = chats.document(chatId)
        val messages = chatRef.collection("messages")
        firestore.runTransaction { tx ->
            val now = Timestamp.now()
            // if chat doc doesn't exist, create with participants (client should set participants)
            val chatSnap = tx.get(chatRef)
            if (!chatSnap.exists()) {
                val participants = listOf(message.from, message.to)
                val lastMsgMap = mapOf(
                    "text" to message.text,
                    "from" to message.from,
                    "timestamp" to now,
                    "type" to message.type
                )
                tx.set(chatRef, mapOf(
                    "chatId" to chatId,
                    "participants" to participants,
                    "lastMessage" to lastMsgMap,
                    "unreadCounts" to mapOf(message.to to 1L, message.from to 0L),
                    "createdAt" to now,
                    "updatedAt" to now
                ))
            } else {
                // update lastMessage & unreadCounts
                val lastMessageUpdate = mapOf(
                    "lastMessage" to mapOf("text" to message.text, "from" to message.from, "timestamp" to now, "type" to message.type),
                    "updatedAt" to now
                )
                // increment unread for recipient
                val existingUnread = chatSnap.get("unreadCounts") as? Map<String, Long> ?: emptyMap()
                val newRecipientCount = (existingUnread[message.to] ?: 0L) + 1L
                val unreadMap = existingUnread.toMutableMap()
                unreadMap[message.to] = newRecipientCount
                unreadMap[message.from] = unreadMap[message.from] ?: 0L
                tx.update(chatRef, lastMessageUpdate + mapOf("unreadCounts" to unreadMap))
            }
            tx.set(messages.document(), mapOf(
                "text" to message.text,
                "from" to message.from,
                "to" to message.to,
                "timestamp" to Timestamp.now(),
                "type" to message.type,
                "mediaUrl" to message.mediaUrl,
                "status" to "sent"
            ))
            null
        }.await()
    }

    // mark messages read - set unreadCounts for uid -> 0, and optionally update message statuses to 'seen'
    suspend fun markChatAsRead(chatId: String, myUid: String) {
        val chatRef = chats.document(chatId)
        firestore.runTransaction { tx ->
            val snap = tx.get(chatRef)
            if (!snap.exists()) return@runTransaction null
            val existingUnread = snap.get("unreadCounts") as? Map<String, Long> ?: emptyMap()
            val newMap = existingUnread.toMutableMap()
            newMap[myUid] = 0L
            tx.update(chatRef, mapOf("unreadCounts" to newMap))
            null
        }.await()
    }

    // search users prefix (simple)
    suspend fun searchUsers(query: String, limit: Int = 20): List<Pair<String, Map<String, Any>>> {
        val snap = firestore.collection("users")
            .orderBy("username")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(limit.toLong())
            .get()
            .await()
        return snap.documents.map { it.id to it.data.orEmpty() }
    }
}
