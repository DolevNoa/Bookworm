package com.example.bookworm.ui.books

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentAddBookRecommendationBinding
import com.example.bookworm.data.books.BookRecommendation

class AddBookRecommendationFragment : Fragment() {

    private var _binding: FragmentAddBookRecommendationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddBookViewModel

    private lateinit var imageViewBook: ImageView
    private lateinit var bookNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonSubmitBook: Button
    private lateinit var uploadProgress: ProgressBar

    private var imageUrl: String? = null

    companion object {
        fun newInstance() = AddBookRecommendationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setupListeners()
        viewModel = ViewModelProvider(this).get(AddBookViewModel::class.java)
    }

    private fun initializeViews() {
        imageViewBook = binding.imageViewBook
        bookNameInput = binding.bookNameInput
        descriptionInput = binding.descriptionInput
        ratingBar = binding.ratingBar
        buttonSubmitBook = binding.buttonSubmitBook
        uploadProgress = binding.uploadProgress
    }

    private fun setupListeners() {
        buttonSubmitBook.setOnClickListener {
            submitBookRecommendation()
        }

        imageViewBook.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun submitBookRecommendation() {
        val bookName = bookNameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val rating = ratingBar.rating

        if (bookName.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val book = BookRecommendation(
            bookName = bookName,
            description = description,
            rating = rating,
            imageUrl = imageUrl ?: "" // Use the imageUrl if available
        )

        viewModel.addBookRecommendation(book)
        Toast.makeText(context, "Book recommendation added successfully", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewBook.setImageURI(it)
            // TODO: Upload the image to get a URL
            // Example: uploadImage(uri) { url -> imageUrl = url }
        }
    }

    private fun clearForm() {
        bookNameInput.text.clear()
        descriptionInput.text.clear()
        ratingBar.rating = 0f
        imageViewBook.setImageResource(R.drawable.placeholder_book_image) // Set a default image
        imageUrl = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
