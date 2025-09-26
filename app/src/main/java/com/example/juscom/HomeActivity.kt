package com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juscom.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var roomAdapter: RoomAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDrawer()
        setupUserInfo()
        setupRecyclerView()
        setupClickListeners()
        setupOnBackPressed()
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
    
    private fun setupUserInfo() {
        // Configurar informações do usuário (dados fake por enquanto)
        binding.userNameTextView.text = getString(R.string.user_name)
        binding.userPointsTextView.text = getString(R.string.user_points)
        binding.userLevelTextView.text = getString(R.string.user_level)
        binding.userOabTextView.text = getString(R.string.user_oab)
        
        // Configurar avatar do usuário
        binding.userAvatarImageView.setImageResource(R.drawable.scales)
    }
    
    private fun setupRecyclerView() {
        val rooms = getSampleRooms()
        roomAdapter = RoomAdapter(rooms) { room ->
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }
        
        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = roomAdapter
        }
    }
    
    private fun getSampleRooms(): List<Room> {
        return listOf(
            Room(1, "Direito Civil", "Discussões sobre direito civil, contratos e obrigações", "Civil", 1250),
            Room(2, "Direito Penal", "Debates sobre direito penal e processo penal", "Penal", 980),
            Room(3, "Direito Trabalhista", "Temas de direito do trabalho e previdenciário", "Trabalhista", 750),
            Room(4, "Direito Tributário", "Discussões sobre direito tributário e fiscal", "Tributário", 650),
            Room(5, "Direito Constitucional", "Debates sobre direito constitucional", "Constitucional", 890),
            Room(6, "Direito Administrativo", "Temas de direito administrativo", "Administrativo", 720)
        ).sortedByDescending { it.subscribersCount }
    }
    
    private fun setupClickListeners() {
        // Listener for the profile image in the navigation header
        val headerView = binding.navigationView.getHeaderView(0)
        val profileImageView = headerView.findViewById<ImageView>(R.id.logoImageView)
        profileImageView.setOnClickListener {
            // Close the drawer before navigating
            drawerLayout.closeDrawer(GravityCompat.START)
            // Show a toast or navigate to the profile activity
            Toast.makeText(this, R.string.opening_profile, Toast.LENGTH_SHORT).show()
        }
        
        // Listener for user header card
        binding.userHeaderCard.setOnClickListener {
            Toast.makeText(this, "Abrindo perfil do usuário", Toast.LENGTH_SHORT).show()
        }
        
        // Listener for "Ver todas as salas" button
        binding.viewAllRoomsButton.setOnClickListener {
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Perfil selecionado", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Configurações selecionadas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_help -> {
                Toast.makeText(this, "Ajuda selecionada", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
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
                    // Disable this callback and call the default back pressed behavior
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
