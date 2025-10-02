package com.example.campusvibe.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.R

class ConversationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConversationsFragment())
                .commit()
        }
    }
}

