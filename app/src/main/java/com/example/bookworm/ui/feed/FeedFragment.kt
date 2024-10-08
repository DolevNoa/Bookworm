package com.example.bookworm.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookworm.ui.books.BaseBookListFragment

class FeedFragment : BaseBookListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get current user ID
        currentUserId = viewModel.getCurrentUserId() ?: ""
    }

    override fun fetchBookRecommendations() {
        fetchRecommendationsFromViewModel { viewModel.getBookRecommendations() }
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
