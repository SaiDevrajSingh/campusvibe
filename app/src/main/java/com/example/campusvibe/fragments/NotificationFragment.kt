package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.adapter.NotificationAdapter
import com.example.campusvibe.Models.Notification
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentNotificationBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = ArrayList<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        notificationAdapter = NotificationAdapter(requireContext(), notificationList)
        binding.notificationRv.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRv.adapter = notificationAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            fetchNotifications()
        }

        return binding.root
    }

    private suspend fun fetchNotifications() {
        try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
            val response = SupabaseClient.client.postgrest["notifications"].select {
                filter {
                    eq("userId", userId)
                }
            }
            val fetchedNotifications = response.decodeList<Notification>()
            notificationList.clear()
            notificationList.addAll(fetchedNotifications)
            notificationAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
}