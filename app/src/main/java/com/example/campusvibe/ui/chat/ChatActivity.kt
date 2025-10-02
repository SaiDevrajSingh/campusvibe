package com.example.campusvibe.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val conversationId = intent.getStringExtra("conversationId")

        if (savedInstanceState == null) {
            val fragment = ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("conversationId", conversationId)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}

