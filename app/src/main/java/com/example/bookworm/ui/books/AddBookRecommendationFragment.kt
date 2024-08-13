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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddBookRecommendationFragment : Fragment() {

    private var _binding: FragmentAddBookRecommendationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: AddBookViewModel

    private lateinit var imageViewBook: ImageView
    private lateinit var bookNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonSubmitBook: Button
    private lateinit var uploadProgress: ProgressBar

    private var imageUrl: String? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewBook.setImageURI(it)
            uploadImageToFirebaseStorage(it) { url ->
                imageUrl = url
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
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

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Adjust layout when keyboard is visible
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                // Adjust layout or scroll if necessary
            }
        }
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

        // Ensure the view scrolls to the EditText when it's focused
        descriptionInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    // Scroll to the EditText, but adjust as needed
                    binding.scrollView.smoothScrollTo(0, descriptionInput.top)
                }
            }

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
            creator = auth.currentUser?.uid ?: "",
            timestamp = Timestamp.now(),
            bookName = bookName,
            description = description,
            rating = rating,
            imageUrl = imageUrl ?: ""
        )

        viewModel.addBookRecommendation(book)
        Toast.makeText(context, "Book recommendation added successfully", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    private fun uploadImageToFirebaseStorage(uri: Uri, onComplete: (String?) -> Unit) {
        val fileRef = storageRef.child("book_images/${System.currentTimeMillis()}.jpg")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onComplete(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                onComplete(null)
            }
    }

    private fun clearForm() {
        bookNameInput.text.clear()
        descriptionInput.text.clear()
        ratingBar.rating = 0f
        imageViewBook.setImageResource(R.drawable.placeholder_book_image)
        imageUrl = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddBookRecommendationFragment()
    }
}
