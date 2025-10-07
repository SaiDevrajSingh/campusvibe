package com.example.campusvibe.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.UserListFragmentBinding
import com.example.campusvibe.model.User
import com.example.campusvibe.ui.chat.adapter.UserSearchAdapter

class UserListFragment : Fragment() {

    private var _binding: UserListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()
    private lateinit var userAdapter: UserSearchAdapter

    private val chatViewModel: CreateGroupChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()

        viewModel.filteredUsers.observe(viewLifecycleOwner) { users ->
            userAdapter.updateUsers(users)
        }

        chatViewModel.creationStatus.observe(viewLifecycleOwner) { status ->
            if (status is ChatCreationStatus.Success) {
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("conversationId", status.conversationId)
                }
                startActivity(intent)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupRecyclerView() {
        userAdapter = UserSearchAdapter { user ->
            chatViewModel.createOneOnOneChat(user.id)
        }
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = userAdapter
    }

    private fun setupSearch() {
        binding.searchViewUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUsers(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.searchUsers("")
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
