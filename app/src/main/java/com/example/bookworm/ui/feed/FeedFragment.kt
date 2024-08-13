package com.example.bookworm.ui.feed
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

class FeedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter
    private val viewModel: GetBooksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = view.findViewById(R.id.recyclerFeed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        feedAdapter = FeedAdapter(emptyList()) // Initialize with empty list
        recyclerView.adapter = feedAdapter

        // Fetch book recommendations from the ViewModel
        fetchBookRecommendations()

        return view
    }



    private fun fetchBookRecommendations() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookRecommendations = viewModel.getBookRecommendations()
                withContext(Dispatchers.Main) {
                    // Log the fetched data
                    Log.d("FeedFragment", "Fetched book recommendations: $bookRecommendations")
                    // Update the adapter with the fetched book recommendations
                    feedAdapter.updateData(bookRecommendations)
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., show error message)
                Log.e("FeedFragment", "Error fetching book recommendations", e)
            }
        }
    }
}
