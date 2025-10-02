package com.example.campusvibe.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentConversationsBinding

class ConversationsFragment : Fragment() {

    private var _binding: FragmentConversationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConversationsViewModel by viewModels()
    private lateinit var adapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationsBinding.inflate(inflater, container, false)

        adapter = ConversationAdapter(emptyList()) { conversation ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("conversationId", conversation.id)
            startActivity(intent)
        }
        binding.recyclerViewConversations.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewConversations.adapter = adapter

        viewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            adapter.updateConversations(conversations)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

