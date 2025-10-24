package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuestionListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadQuestions(roomId: String) {
        if (roomId.isEmpty()) {
            _error.value = "Room ID is missing."
            return
        }

        firestore.collection("questions")
            .whereEqualTo("roomId", roomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val questionList = result.toObjects(Question::class.java)
                _questions.value = questionList
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to load questions: ${exception.message}"
            }
    }
}
