package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var roomAdapter: RoomAdapter

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        setupToolbar()
        setupDrawer()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        setupOnBackPressed()

        // Load dynamic data
        loadUserInfo()
        loadRooms()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.home_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupDrawer() {
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun loadUserInfo() {
        val uid = currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        binding.userNameTextView.text = document.getString("name")
                        binding.userOabTextView.text = document.getString("uf") // Assuming UF for OAB placeholder

                        val level = document.getLong("level") ?: 1
                        val points = document.getLong("points") ?: 0
                        binding.userLevelTextView.text = "Nível $level"
                        binding.userPointsTextView.text = "$points XP"

                        // Also update the navigation drawer header if needed
                        val headerView = binding.navigationView.getHeaderView(0)
                        val navUserName = headerView.findViewById<TextView>(R.id.userNameTextView)
                        val navUserEmail = headerView.findViewById<TextView>(R.id.userEmailTextView)
                        navUserName.text = document.getString("name")
                        navUserEmail.text = document.getString("email")
                    }
                }
        }
    }

    private fun setupRecyclerView() {
        // Initialize with an empty list. It will be populated from Firestore.
        roomAdapter = RoomAdapter(mutableListOf(), { room ->
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }, { isEmpty ->
            binding.noResultsText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })

        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = roomAdapter
        }
    }

    private fun loadRooms() {
        firestore.collection("rooms")
            .orderBy("subscribersCount", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.toObjects(Room::class.java)
                roomAdapter.updateRooms(rooms)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao carregar salas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                roomAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        val headerView = binding.navigationView.getHeaderView(0)
        val profileImageView = headerView.findViewById<ImageView>(R.id.logoImageView)
        profileImageView.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.userHeaderCard.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.studyMaterialButton.setOnClickListener {
            val intent = Intent(this, StudyMaterialActivity::class.java)
            startActivity(intent)
        }

        binding.viewAllRoomsButton.setOnClickListener {
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupOnBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
