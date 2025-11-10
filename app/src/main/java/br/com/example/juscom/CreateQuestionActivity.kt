package br.com.example.juscom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.example.juscom.databinding.ActivityCreateQuestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CreateQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuestionBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var roomId: String? = null
    private lateinit var incentiveManager: IncentiveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        roomId = intent.getStringExtra("ROOM_ID")
        incentiveManager = IncentiveManager()

        setupToolbar()

        binding.buttonSubmit.setOnClickListener {
            submitQuestion()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun submitQuestion() {
        val title = binding.titleEditText.text.toString().trim()
        val body = binding.bodyEditText.text.toString().trim()
        val currentUser = auth.currentUser

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to ask a question.", Toast.LENGTH_SHORT).show()
            return
        }

        if (roomId == null) {
            Toast.makeText(this, "Error: Room ID is missing.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val question = Question(
            roomId = roomId!!,
            title = title,
            body = body,
            authorId = currentUser.uid,
            authorName = currentUser.displayName ?: "Anonymous",
            timestamp = Date(),
            answerCount = 0
        )

        firestore.collection("questions").add(question)
            .addOnSuccessListener {
                Toast.makeText(this, "Question submitted successfully!", Toast.LENGTH_SHORT).show()
                incentiveManager.handleQuestionCreated(currentUser.uid)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit question: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
