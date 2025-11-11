package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _earnedBadges = MutableLiveData<List<EarnedBadge>>()
    val earnedBadges: LiveData<List<EarnedBadge>> = _earnedBadges

    // New LiveData for XP Progress
    private val _xpProgress = MutableLiveData<Int>()
    val xpProgress: LiveData<Int> = _xpProgress

    private val _xpProgressMax = MutableLiveData<Int>()
    val xpProgressMax: LiveData<Int> = _xpProgressMax

    private val _xpProgressText = MutableLiveData<String>()
    val xpProgressText: LiveData<String> = _xpProgressText

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated."
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _user.value = user
                user?.let {
                    calculateXpProgress(it)
                    fetchEarnedBadges(userId)
                }
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to fetch user data: ${exception.message}"
            }
    }

    private fun calculateXpProgress(user: User) {
        val currentLevel = user.level.toInt()
        val currentPoints = user.points

        val xpForCurrentLevel = LevelingManager.getPointsForLevel(currentLevel)
        val xpForNextLevel = LevelingManager.getPointsForLevel(currentLevel + 1)

        val progressInLevel = currentPoints - xpForCurrentLevel
        val totalInRange = xpForNextLevel - xpForCurrentLevel

        _xpProgress.value = progressInLevel.toInt()
        _xpProgressMax.value = totalInRange.toInt()
        _xpProgressText.value = "$currentPoints / $xpForNextLevel XP"
    }

    private fun fetchEarnedBadges(userId: String) {
        firestore.collection("users").document(userId).collection("earned_badges").get()
            .addOnSuccessListener { snapshot ->
                val earnedBadgesList = snapshot.toObjects(EarnedBadge::class.java)
                // Now, for each earned badge, fetch the full badge details
                fetchBadgeDetails(earnedBadgesList)
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to fetch earned badges: ${exception.message}"
            }
    }

    private fun fetchBadgeDetails(earnedBadges: List<EarnedBadge>) {
        if (earnedBadges.isEmpty()) {
            _earnedBadges.value = emptyList()
            return
        }

        val badgeDocs = firestore.collection("badges")
        val populatedBadges = mutableListOf<EarnedBadge>()
        var completedCount = 0

        for (earnedBadge in earnedBadges) {
            badgeDocs.document(earnedBadge.badgeId).get()
                .addOnSuccessListener { badgeSnapshot ->
                    val badge = badgeSnapshot.toObject(Badge::class.java)
                    if (badge != null) {
                        earnedBadge.badge = badge
                        populatedBadges.add(earnedBadge)
                    }
                }
                .addOnCompleteListener {
                    completedCount++
                    if (completedCount == earnedBadges.size) {
                        _earnedBadges.value = populatedBadges
                    }
                }
        }
    }

    fun updateUserData(name: String, institution: String, uf: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated."
            return
        }

        val userUpdates = mapOf(
            "name" to name,
            "institution" to institution,
            "uf" to uf
        )

        firestore.collection("users").document(userId).update(userUpdates)
            .addOnSuccessListener {
                _updateResult.value = true
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to update profile: ${e.message}"
                _updateResult.value = false
            }
    }
}
