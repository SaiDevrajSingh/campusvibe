package com.example.campusvibe.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentChatListBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatListViewModel by viewModels()
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.setUser(currentUid)

        adapter = ChatListAdapter(currentUid) { chatId, otherUid ->
            val action = ChatListFragmentDirections.actionChatListToChat(otherUid)
            findNavController().navigate(R.id.action_chat_list_to_chat, Bundle().apply {
                putString("otherUid", otherUid)
            })
        }

        binding.rvChatList.adapter = adapter
        binding.rvChatList.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            viewModel.filteredChats.collect { chats ->
                if (chats != null) {
                    adapter.submitList(chats)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
