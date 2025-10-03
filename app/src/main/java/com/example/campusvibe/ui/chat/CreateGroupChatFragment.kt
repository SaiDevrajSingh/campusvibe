package com.example.campusvibe.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentCreateGroupChatBinding
import com.example.campusvibe.model.User
import com.example.campusvibe.ui.chat.adapter.SelectedUsersAdapter
import com.example.campusvibe.ui.chat.adapter.UserSearchAdapter
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth

class CreateGroupChatFragment : Fragment() {

    private var _binding: FragmentCreateGroupChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateGroupChatViewModel by viewModels()
    private lateinit var userAdapter: UserSearchAdapter
    private lateinit var selectedUsersAdapter: SelectedUsersAdapter
    private val selectedUsers = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        setupCreateButton()

        // Observe group creation status
        viewModel.groupCreationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is GroupCreationStatus.Success -> {
                    android.widget.Toast.makeText(requireContext(), "Group created successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                is GroupCreationStatus.Error -> {
                    android.widget.Toast.makeText(requireContext(), status.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                GroupCreationStatus.Loading -> {
                    binding.buttonCreateGroup.isEnabled = false
                    binding.buttonCreateGroup.text = "Creating..."
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        // Setup user search results
        userAdapter = UserSearchAdapter { user ->
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user)
                selectedUsersAdapter.updateUsers(selectedUsers)
                updateSelectedUsersChips()
            }
        }

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = userAdapter

        // Setup selected users
        selectedUsersAdapter = SelectedUsersAdapter { user ->
            selectedUsers.remove(user)
            selectedUsersAdapter.updateUsers(selectedUsers)
            updateSelectedUsersChips()
        }

        binding.recyclerViewSelectedUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSelectedUsers.adapter = selectedUsersAdapter
    }

    private fun setupSearch() {
        binding.searchViewUsers.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchUsers(it) }
                return true
            }
        })
    }

    private fun setupCreateButton() {
        binding.buttonCreateGroup.setOnClickListener {
            val groupName = binding.editTextGroupName.text.toString().trim()
            if (selectedUsers.size >= 2 && groupName.isNotEmpty()) {
                createGroupChat(groupName)
            } else {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Please select at least 2 users and enter a group name",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateSelectedUsersChips() {
        binding.chipGroupSelectedUsers.removeAllViews()
        selectedUsers.forEach { user ->
            val chip = Chip(requireContext()).apply {
                text = user.username
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    selectedUsers.remove(user)
                    selectedUsersAdapter.updateUsers(selectedUsers)
                    updateSelectedUsersChips()
                }
            }
            binding.chipGroupSelectedUsers.addView(chip)
        }
    }

    private fun createGroupChat(groupName: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val participantIds = selectedUsers.map { it.id } + currentUserId

        viewModel.createGroupChat(participantIds, groupName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
