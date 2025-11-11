package br.com.example.juscom

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var badgeAdapter: BadgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Meu Perfil"
    }

    private fun setupRecyclerView() {
        // The adapter is initialized with an empty list first
        badgeAdapter = BadgeAdapter(emptyList())
        binding.badgesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.badgesRecyclerView.adapter = badgeAdapter
    }

    private fun observeViewModel() {
        viewModel.user.observe(this, Observer { user ->
            user?.let { populateUi(it) }
        })

        viewModel.earnedBadges.observe(this, Observer { badges ->
            // When the list of badges is fetched, update the adapter
            badgeAdapter = BadgeAdapter(badges)
            binding.badgesRecyclerView.adapter = badgeAdapter
        })

        // Observers for XP Progress
        viewModel.xpProgress.observe(this, Observer { progress ->
            binding.profileXpBar.progress = progress
        })

        viewModel.xpProgressMax.observe(this, Observer { max ->
            binding.profileXpBar.max = max
        })

        viewModel.xpProgressText.observe(this, Observer { text ->
            binding.profileXpProgress.text = text
        })

        viewModel.updateResult.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                exitEditMode()
            } else {
                // Errors are handled by a separate observer
            }
        })

        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }

    private fun populateUi(user: User) {
        binding.profileName.setText(user.name)
        binding.profileEmail.setText(user.email)
        binding.profileInstitution.setText(user.institution)
        binding.profileFedUnit.setText(user.uf)
        binding.profileLevel.text = "Nível ${user.level}"
    }

    private fun setupClickListeners() {
        binding.buttonEditProfile.setOnClickListener {
            enterEditMode()
        }

        binding.buttonSaveChanges.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun enterEditMode() {
        binding.profileName.isEnabled = true
        binding.profileInstitution.isEnabled = true
        binding.profileFedUnit.isEnabled = true
        binding.profileEmail.isEnabled = false

        binding.buttonEditProfile.visibility = View.GONE
        binding.buttonSaveChanges.visibility = View.VISIBLE
    }

    private fun exitEditMode() {
        binding.profileName.isEnabled = false
        binding.profileInstitution.isEnabled = false
        binding.profileFedUnit.isEnabled = false
        binding.profileEmail.isEnabled = false

        binding.buttonEditProfile.visibility = View.VISIBLE
        binding.buttonSaveChanges.visibility = View.GONE
    }

    private fun saveProfileChanges() {
        viewModel.updateUserData(
            name = binding.profileName.text.toString(),
            institution = binding.profileInstitution.text.toString(),
            uf = binding.profileFedUnit.text.toString()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
