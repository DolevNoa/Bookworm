package com.example.bookworm.ui.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.adapters.FeedAdapter
import com.example.bookworm.data.books.BookRecommendation
import com.example.bookworm.ui.books.HandleBooksViewModel
import com.example.bookworm.ui.feed.FeedFragment
import com.example.bookworm.ui.mylist.MyListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseBookListFragment : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected lateinit var feedAdapter: FeedAdapter
    //    protected val viewModel: HandleBooksViewModel by viewModels()
    protected lateinit var currentUserId: String
    protected val viewModel: HandleBooksViewModel by activityViewModels()

    abstract fun fetchBookRecommendations()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false) // Use a common layout if needed

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        feedAdapter = FeedAdapter(
            bookRecommendations = emptyList(),
            onEditClick = { bookRecommendation ->
                viewModel.selectedBookRecommendation.value = bookRecommendation
                val action = when (this) {
                    is FeedFragment -> R.id.action_feedFragment_to_editBookRecommendationFragment
                    is MyListFragment -> R.id.action_myListFragment_to_editBookRecommendationFragment
                    else -> throw IllegalArgumentException("Unknown Fragment")
                }
                // Navigate to the EditBookRecommendationFragment
                findNavController().navigate(action)
            },
            onDeleteClick = { bookRecommendation ->
                CoroutineScope(Dispatchers.Main).launch {
                    val success = viewModel.deleteBookRecommendation(bookRecommendation)
                    if (success) {
                        Toast.makeText(context, "Book recommendation deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchBookRecommendations() // Refresh the list
                    } else {
                        Toast.makeText(context, "Failed to delete book recommendation", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            currentUserId = currentUserId
        )
        recyclerView.adapter = feedAdapter
        fetchBookRecommendations()
        return view
    }

    protected fun updateAdapter(bookRecommendations: List<BookRecommendation>) {
        feedAdapter.updateData(bookRecommendations)
    }

    protected fun fetchRecommendationsFromViewModel(fetchMethod: suspend () -> List<BookRecommendation>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookRecommendations = fetchMethod()
                withContext(Dispatchers.Main) {
                    // Log the fetched data
                    Log.d("BaseBookListFragment", "Fetched book recommendations: $bookRecommendations")
                    // Update the adapter with the fetched book recommendations
                    updateAdapter(bookRecommendations)
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., show error message)
                Log.e("BaseBookListFragment", "Error fetching book recommendations", e)
            }
        }
    }
}