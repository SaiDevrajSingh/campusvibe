package com.example.campusvibe.ui.chat

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(arguments?.getString("conversationId") ?: "")
    }
    private lateinit var messageAdapter: MessageAdapter

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { sendMediaMessage(it, "image") }
    }

    private val selectVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { sendMediaMessage(it, "video") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(context)

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter = MessageAdapter(messages)
            binding.recyclerViewMessages.adapter = messageAdapter
        }

        binding.buttonAttachMedia.setOnClickListener {
            showMediaOptions()
        }

        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            if (messageText.isNotBlank()) {
                val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                viewModel.sendMessage(messageText, senderId)
                binding.editTextMessage.text.clear()
            }
        }
    }

    private fun showMediaOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Send Video")
        AlertDialog.Builder(requireContext())
            .setTitle("Attach Media")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectImageLauncher.launch("image/*")
                    1 -> selectImageLauncher.launch("image/*")
                    2 -> selectVideoLauncher.launch("video/*")
                }
            }
            .show()
    }

    private fun sendMediaMessage(mediaUri: Uri, mediaType: String) {
        viewModel.sendMediaMessage(mediaUri, mediaType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
