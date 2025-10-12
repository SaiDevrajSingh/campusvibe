package com.example.campusvibe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.campusvibe.databinding.FragmentChatMessagesBinding
import com.example.campusvibe.model.User
import com.example.campusvibe.ui.chat.ChatViewModel
import com.example.campusvibe.ui.chat.MessageAdapter

class ChatFragment : Fragment() {

    private var _binding: FragmentChatMessagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

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

        val user = arguments?.getParcelable<User>("user")

        if (user != null) {
            messageAdapter = MessageAdapter(viewModel.auth.currentUser?.uid.toString())
            binding.rvMessages.adapter = messageAdapter

            viewModel.getMessages(user.id).observe(viewLifecycleOwner) { messages ->
                messageAdapter.submitList(messages)
            }

            binding.btnSend.setOnClickListener {
                val message = binding.etMessage.text.toString()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(user.id, message)
                    binding.etMessage.setText("")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
