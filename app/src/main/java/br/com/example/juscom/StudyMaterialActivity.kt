package br.com.example.juscom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityStudyMaterialBinding

class StudyMaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyMaterialBinding
    private lateinit var studyMaterialAdapter: StudyMaterialAdapter
    private val viewModel: StudyMaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadStudyMaterials()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.study_material_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        studyMaterialAdapter = StudyMaterialAdapter(mutableListOf()) { material ->
            material.fileUrl?.let {
                if (it.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Link do material indisponível.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.studyMaterialRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@StudyMaterialActivity)
            adapter = studyMaterialAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.materials.observe(this) { materials ->
            studyMaterialAdapter.updateMaterials(materials)
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
