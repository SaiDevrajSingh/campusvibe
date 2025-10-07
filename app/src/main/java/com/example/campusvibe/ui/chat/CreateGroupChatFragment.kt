package com.example.campusvibe.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        viewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.updateUsers(users)
        }

        viewModel.creationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ChatCreationStatus.Success -> {
                    Toast.makeText(requireContext(), "Chat created successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                        putExtra("conversationId", status.conversationId)
                    }
                    startActivity(intent)
                    parentFragmentManager.popBackStack()
                }
                is ChatCreationStatus.Error -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                    binding.buttonCreateGroup.isEnabled = true
                    binding.buttonCreateGroup.text = "Create"
                }
                ChatCreationStatus.Loading -> {
                    binding.buttonCreateGroup.isEnabled = false
                    binding.buttonCreateGroup.text = "Creating..."
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        userAdapter = UserSearchAdapter { user ->
            if (!selectedUsers.any { it.id == user.id }) {
                selectedUsers.add(user)
                updateSelectedUsersChips()
            }
        }
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = userAdapter

        selectedUsersAdapter = SelectedUsersAdapter { user ->
            selectedUsers.remove(user)
            updateSelectedUsersChips()
        }
        binding.recyclerViewSelectedUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSelectedUsers.adapter = selectedUsersAdapter
    }

    private fun setupSearch() {
        binding.searchViewUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUsers(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUsers(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupCreateButton() {
        binding.buttonCreateGroup.setOnClickListener {
            when (selectedUsers.size) {
                0 -> {
                    Toast.makeText(requireContext(), "Please select at least one user", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    viewModel.createOneOnOneChat(selectedUsers.first().id)
                }
                else -> {
                    val groupName = binding.editTextGroupName.text.toString().trim()
                    if (groupName.isNotEmpty()) {
                        val participantIds = selectedUsers.map { it.id } + (FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        viewModel.createGroupChat(participantIds, groupName)
                    } else {
                        Toast.makeText(requireContext(), "Please enter a group name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateSelectedUsersChips() {
        binding.chipGroupSelectedUsers.removeAllViews()
        selectedUsers.forEach { user ->
            val chip = Chip(requireContext()).apply {
                text = user.username
                isCloseIconVisible = true
                setOnCloseIconClickListener { _ ->
                    selectedUsers.remove(user)
                    updateSelectedUsersChips()
                }
            }
            binding.chipGroupSelectedUsers.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
