package br.com.example.juscom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityHelpBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    private lateinit var helpAdapter: HelpAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadHelpItems()
    }

    private fun setupRecyclerView() {
        helpAdapter = HelpAdapter(mutableListOf())
        binding.helpRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@HelpActivity)
            adapter = helpAdapter
        }
    }

    private fun loadHelpItems() {
        firestore.collection("help_items")
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val helpItems = result.toObjects(HelpItem::class.java)
                helpAdapter.updateHelpItems(helpItems)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load help items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
