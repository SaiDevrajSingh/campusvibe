package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.adapter.NotificationAdapter
import com.example.campusvibe.Models.Notification
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentNotificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        fetchNotifications()

        return binding.root
    }

    private fun fetchNotifications() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("notifications").whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                notificationList.clear()
                for (doc in snapshots!!) {
                    val notification = doc.toObject(Notification::class.java)
                    notificationList.add(notification)
                }
                notificationAdapter.notifyDataSetChanged()
            }
    }
}