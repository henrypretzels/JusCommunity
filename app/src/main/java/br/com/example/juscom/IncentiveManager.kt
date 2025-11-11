package br.com.example.juscom

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class IncentiveManager {

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        // Define points for actions
        private const val POINTS_FOR_QUESTION = 10L
        private const val POINTS_FOR_ANSWER = 50L
        private const val POINTS_FOR_UPVOTE_RECEIVED = 2L
    }

    fun handleQuestionCreated(userId: String) {
        // 1. Award points
        awardPoints(userId, POINTS_FOR_QUESTION)

        // 2. Check for "Question Starter" badge
        checkForBadge(userId, "create_question")
    }

    fun handleAnswerCreated(userId: String) {
        // 1. Award points
        awardPoints(userId, POINTS_FOR_ANSWER)

        // 2. Check for "Contributor" badge
        checkForBadge(userId, "create_answer")
    }

    fun handleAnswerUpvoted(answerAuthorId: String) {
        // 1. Award points to the author of the answer
        awardPoints(answerAuthorId, POINTS_FOR_UPVOTE_RECEIVED)

        // 2. Check for "First Upvote" badge
        checkForBadge(answerAuthorId, "receive_upvote")
    }

    private fun awardPoints(userId: String, points: Long) {
        val userRef = firestore.collection("users").document(userId)

        // Atomically increment the points
        userRef.update("points", FieldValue.increment(points))
            .addOnSuccessListener {
                // After points are awarded, check for a level up
                checkAndApplyLevelUp(userId)
            }
    }

    private fun checkAndApplyLevelUp(userId: String) {
        val userRef = firestore.collection("users").document(userId)

        userRef.get().addOnSuccessListener { userSnapshot ->
            if (!userSnapshot.exists()) return@addOnSuccessListener

            val currentPoints = userSnapshot.getLong("points") ?: 0L
            val currentLevel = userSnapshot.getLong("level") ?: 1L

            val newCalculatedLevel = LevelingManager.calculateLevelFromPoints(currentPoints)

            if (newCalculatedLevel > currentLevel) {
                userRef.update("level", newCalculatedLevel)
            }
        }
    }

    private fun checkForBadge(userId: String, action: String) {
        val badgesRef = firestore.collection("badges")
        val userBadgesRef = firestore.collection("users").document(userId).collection("earned_badges")

        // Find the badge that corresponds to this action
        badgesRef.whereEqualTo("criteria.action", action).limit(1).get()
            .addOnSuccessListener { badgeSnapshot ->
                if (badgeSnapshot.isEmpty) return@addOnSuccessListener
                val badgeDoc = badgeSnapshot.documents.first()
                val badgeId = badgeDoc.id

                // Check if the user already has this badge
                userBadgesRef.document(badgeId).get()
                    .addOnSuccessListener { earnedBadgeDoc ->
                        if (earnedBadgeDoc.exists()) return@addOnSuccessListener

                        // If not, check if they meet the criteria now
                        checkActionCountAndAward(userId, badgeId, action)
                    }
            }
    }

    private fun checkActionCountAndAward(userId: String, badgeId: String, action: String) {
        val collectionToCheck = when (action) {
            "create_question" -> firestore.collection("questions")
            "create_answer" -> firestore.collection("answers")
            // For "receive_upvote", the logic is simpler
            else -> {
                awardBadge(userId, badgeId)
                return
            }
        }

        collectionToCheck.whereEqualTo("authorId", userId).limit(1).get()
            .addOnSuccessListener { userActionsSnapshot ->
                // The criteria is count == 1. If we find one document, they qualify.
                if (!userActionsSnapshot.isEmpty) {
                    awardBadge(userId, badgeId)
                }
            }
    }

    private fun awardBadge(userId: String, badgeId: String) {
        val newBadge = mapOf(
            "badgeId" to badgeId,
            "timestamp" to Date()
        )
        firestore.collection("users").document(userId)
            .collection("earned_badges").document(badgeId).set(newBadge)
    }
}
