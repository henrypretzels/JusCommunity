package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuestionDetailViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _question = MutableLiveData<Question?>()
    val question: LiveData<Question?> = _question

    private val _answers = MutableLiveData<List<Answer>>()
    val answers: LiveData<List<Answer>> = _answers

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadQuestionAndAnswers(questionId: String) {
        if (questionId.isEmpty()) {
            _error.value = "Question ID is missing."
            return
        }

        // Load the question details
        firestore.collection("questions").document(questionId).get()
            .addOnSuccessListener { document ->
                _question.value = document.toObject(Question::class.java)
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to load question: ${exception.message}"
            }

        // Load the answers for the question, ordered by vote count
        firestore.collection("answers")
            .whereEqualTo("questionId", questionId)
            .orderBy("voteCount", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                _answers.value = result.toObjects(Answer::class.java)
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to load answers: ${exception.message}"
            }
    }

    fun handleVote(answer: Answer, voteType: VoteType) {
        // TODO: Implement the Firestore transaction logic for voting in Phase 4.
        _error.value = "Voting not implemented yet."
    }
}
