package com.example.bookworm.ui.mylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.adapters.FeedAdapter
import com.example.bookworm.ui.books.GetBooksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter
    private val viewModel: GetBooksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerMyList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        feedAdapter = FeedAdapter(emptyList()) // Initialize with empty list
        recyclerView.adapter = feedAdapter

        // Fetch book recommendations specific to the user
        fetchUserBooks()

        return view
    }

    private fun fetchUserBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookRecommendations = viewModel.getUserBookRecommendations()
                withContext(Dispatchers.Main) {
                    // Log the fetched data
                    Log.d("MyListFragment", "Fetched user books: $bookRecommendations")
                    // Update the adapter with the fetched book recommendations
                    feedAdapter.updateData(bookRecommendations)
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., show error message)
                Log.e("MyListFragment", "Error fetching user books", e)
            }
        }
    }
}
