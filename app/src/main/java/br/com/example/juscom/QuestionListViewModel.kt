package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class QuestionListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadQuestions(roomId: String) {
        if (roomId.isEmpty()) {
            _error.value = "Room ID is missing."
            return
        }

        // Remove existing listener if any
        listenerRegistration?.remove()

        // Set up real-time listener for questions
        listenerRegistration = firestore.collection("questions")
            .whereEqualTo("roomId", roomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    _error.value = "Failed to load questions: ${e.message}"
                    return@addSnapshotListener
                }

                val questionList = snapshots?.documents?.mapNotNull { doc ->
                    val question = doc.toObject(Question::class.java)
                    question?.id = doc.id
                    question
                } ?: emptyList()
                _questions.value = questionList
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
