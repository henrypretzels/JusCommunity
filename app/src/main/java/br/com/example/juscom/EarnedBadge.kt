package br.com.example.juscom

import com.google.firebase.firestore.Exclude
import java.util.Date

data class EarnedBadge(
    val badgeId: String = "",
    val timestamp: Date = Date(),
    // This field will be populated manually after fetching the badge details
    @get:Exclude var badge: Badge? = null
)
