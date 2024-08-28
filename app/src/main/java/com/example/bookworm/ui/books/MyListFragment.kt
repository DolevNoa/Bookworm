package com.example.bookworm.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MyListFragment : BaseBookListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get current user ID
        currentUserId = viewModel.getCurrentUserId() ?: ""
    }

    override fun fetchBookRecommendations() {
        fetchRecommendationsFromViewModel { viewModel.getUserBookRecommendations() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the common layout
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
        }
    }
}
