package com.example.campusvibe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentChatMessagesBinding
import com.example.campusvibe.model.ChatMessage
import com.example.campusvibe.ui.chat.MessageAdapter
import com.example.campusvibe.ui.chat.ChatViewModel
import com.google.firebase.Timestamp

class ChatFragment : Fragment() {

    private var _binding: FragmentChatMessagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otherUid = arguments?.getString("otherUid") ?: return

        adapter = MessageAdapter(viewModel.auth.currentUser?.uid ?: "")
        binding.rvMessages.layoutManager = LinearLayoutManager(context)
        binding.rvMessages.adapter = adapter

        viewModel.getMessages(otherUid).observe(viewLifecycleOwner) { messages ->
            val chatMessages = messages.map { message ->
                val timestampLong = message["timestamp"] as? Long ?: 0
                ChatMessage(
                    id = message["id"] as? String ?: "",
                    text = message["text"] as? String ?: "",
                    from = message["from"] as? String ?: "",
                    timestamp = Timestamp(timestampLong / 1000, 0)
                )
            }
            adapter.submitList(chatMessages)
        }

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(otherUid, messageText)
                binding.etMessage.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
