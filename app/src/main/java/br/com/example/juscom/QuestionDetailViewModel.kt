package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class VoteType {
    UP,
    DOWN
}

class QuestionDetailViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val incentiveManager = IncentiveManager()

    private val _question = MutableLiveData<Question?>()
    val question: LiveData<Question?> = _question

    private val _answers = MutableLiveData<List<Answer>>()
    val answers: LiveData<List<Answer>> = _answers

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _voteStatus = MutableLiveData<Map<String, VoteType?>>()
    val voteStatus: LiveData<Map<String, VoteType?>> = _voteStatus

    private val _postResult = MutableLiveData<Boolean>()
    val postResult: LiveData<Boolean> = _postResult

    fun loadQuestionAndAnswers(questionId: String) {
        if (questionId.isEmpty()) {
            _error.value = "Question ID is missing."
            return
        }

        firestore.collection("questions").document(questionId).get()
            .addOnSuccessListener { document ->
                val questionData = document.toObject(Question::class.java)
                questionData?.id = document.id
                _question.value = questionData
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to load question: ${exception.message}"
            }

        firestore.collection("answers")
            .whereEqualTo("questionId", questionId)
            .orderBy("voteCount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    _error.value = "Failed to load answers: ${e.message}"
                    return@addSnapshotListener
                }

                val answerList = snapshots!!.documents.map { doc ->
                    val answer = doc.toObject(Answer::class.java)!!
                    answer.id = doc.id
                    answer
                }
                _answers.value = answerList
                checkUserVotes(answerList.map { it.id })
            }
    }

    private fun checkUserVotes(answerIds: List<String>) {
        val userId = auth.currentUser?.uid ?: return
        if (answerIds.isEmpty()) return

        val userVotes = mutableMapOf<String, VoteType?>()

        answerIds.forEach { answerId ->
            firestore.collection("answers").document(answerId)
                .collection("votes").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        when (document.getString("voteType")) {
                            "up" -> userVotes[answerId] = VoteType.UP
                            "down" -> userVotes[answerId] = VoteType.DOWN
                            else -> userVotes[answerId] = null
                        }
                    } else {
                        userVotes[answerId] = null
                    }
                    _voteStatus.value = userVotes
                }
        }
    }

    fun handleVote(answerId: String, voteType: VoteType) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated."
            return
        }

        val answerRef = firestore.collection("answers").document(answerId)
        val voteRef = answerRef.collection("votes").document(userId)

        // First, get the answer to find its author
        answerRef.get().addOnSuccessListener { answerDoc ->
            val answerAuthorId = answerDoc.getString("authorId")

            // Prevent users from upvoting their own answers for points
            if (userId == answerAuthorId) {
                return@addOnSuccessListener
            }

            firestore.runTransaction { transaction ->
                val voteDoc = transaction.get(voteRef)
                val currentVoteString = if (voteDoc.exists()) voteDoc.getString("voteType") else null
                val currentVote = when (currentVoteString) {
                    "up" -> VoteType.UP
                    "down" -> VoteType.DOWN
                    else -> null
                }
                
                var isNewUpvote = false

                if (currentVote == voteType) {
                    // User is undoing their vote
                    transaction.delete(voteRef)
                    val increment = if (voteType == VoteType.UP) -1L else 1L
                    transaction.update(answerRef, "voteCount", FieldValue.increment(increment))
                } else if (currentVote != null) {
                    // User is changing their vote
                    transaction.set(voteRef, mapOf("voteType" to voteType.name.lowercase()))
                    val increment = if (voteType == VoteType.UP) 2L else -2L // UP to DOWN is -2, DOWN to UP is +2
                    transaction.update(answerRef, "voteCount", FieldValue.increment(increment))
                    if (voteType == VoteType.UP) isNewUpvote = true
                } else {
                    // User is casting a new vote
                    transaction.set(voteRef, mapOf("voteType" to voteType.name.lowercase()))
                    val increment = if (voteType == VoteType.UP) 1L else -1L
                    transaction.update(answerRef, "voteCount", FieldValue.increment(increment))
                    if (voteType == VoteType.UP) isNewUpvote = true
                }
                
                isNewUpvote // Return this value from the transaction

            }.addOnSuccessListener { isNewUpvote ->
                if (isNewUpvote && answerAuthorId != null) {
                     incentiveManager.handleAnswerUpvoted(answerAuthorId)
                }
            }.addOnFailureListener { e ->
                _error.value = "Vote failed: ${e.message}"
            }
        }.addOnFailureListener { e ->
             _error.value = "Could not get answer details: ${e.message}"
        }
    }

    fun postAnswer(questionId: String, body: String) {
        val userId = auth.currentUser?.uid
        val userName = auth.currentUser?.displayName

        if (userId == null || userName == null) {
            _error.value = "User not authenticated or name is missing."
            return
        }

        val newAnswer = Answer(
            questionId = questionId,
            body = body,
            authorId = userId,
            authorName = userName,
            timestamp = Date() // Will be replaced by server timestamp
        )

        val questionRef = firestore.collection("questions").document(questionId)
        val newAnswerRef = firestore.collection("answers").document()

        firestore.runTransaction { transaction ->
            transaction.update(questionRef, "answerCount", FieldValue.increment(1))
            transaction.set(newAnswerRef, newAnswer)
            null
        }.addOnSuccessListener {
            _postResult.value = true
            incentiveManager.handleAnswerCreated(userId)
        }.addOnFailureListener { e ->
            _error.value = "Failed to post answer: ${e.message}"
            _postResult.value = false
        }
    }
}
