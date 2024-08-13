package com.example.bookworm.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.adapters.FeedAdapter
import com.example.bookworm.models.BookPost

class FeedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = view.findViewById(R.id.recyclerFeed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create a sample list of BookPost items
        val bookPosts = listOf(
            BookPost("Book 1", 5, "Description 1", "https://example.com/image1.jpg", "2024-08-12", "User1"),
            BookPost("Book 2", 4, "Description 2", "https://example.com/image2.jpg", "2024-08-12", "User2")
            // Add more BookPost items with images here
        )

        feedAdapter = FeedAdapter(bookPosts)
        recyclerView.adapter = feedAdapter

        return view
    }
}
