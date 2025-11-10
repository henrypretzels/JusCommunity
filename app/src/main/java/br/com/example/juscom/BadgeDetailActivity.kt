package br.com.example.juscom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.example.juscom.databinding.ActivityBadgeDetailBinding

class BadgeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBadgeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val badgeName = intent.getStringExtra("BADGE_NAME")
        val badgeDescription = intent.getStringExtra("BADGE_DESCRIPTION")
        val badgeIcon = intent.getStringExtra("BADGE_ICON")

        setupToolbar(badgeName)
        populateUi(badgeName, badgeDescription, badgeIcon)
    }

    private fun setupToolbar(title: String?) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title ?: "Badge Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun populateUi(name: String?, description: String?, iconName: String?) {
        binding.badgeNameDetail.text = name
        binding.badgeDescriptionDetail.text = description

        if (iconName != null) {
            val resourceId = resources.getIdentifier(iconName, "drawable", packageName)
            if (resourceId != 0) {
                binding.badgeIconDetail.setImageResource(resourceId)
            } else {
                binding.badgeIconDetail.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
