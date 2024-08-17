package com.example.bookworm.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookworm.R
import com.example.bookworm.ui.common.BaseBookListFragment

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
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            // Optionally perform additional view setup here
        }
    }
}
