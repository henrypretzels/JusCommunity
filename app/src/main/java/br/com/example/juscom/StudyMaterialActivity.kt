package br.com.example.juscom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityStudyMaterialBinding
import br.com.example.juscom.databinding.DialogAddStudyMaterialBinding

class StudyMaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyMaterialBinding
    private lateinit var studyMaterialAdapter: StudyMaterialAdapter
    private val viewModel: StudyMaterialViewModel by viewModels()

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                showAddMaterialDialog(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        binding.fabAddMaterial.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            filePickerLauncher.launch(intent)
        }

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

        viewModel.uploadSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Material enviado com sucesso!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddMaterialDialog(fileUri: Uri) {
        val dialogBinding = DialogAddStudyMaterialBinding.inflate(LayoutInflater.from(this))

        AlertDialog.Builder(this)
            .setTitle("Adicionar Material")
            .setView(dialogBinding.root)
            .setPositiveButton("Enviar") { _, _ ->
                val title = dialogBinding.titleEditText.text.toString()
                val description = dialogBinding.descriptionEditText.text.toString()
                val category = dialogBinding.categoryEditText.text.toString()

                if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()) {
                    viewModel.uploadStudyMaterial(fileUri, title, description, category)
                } else {
                    Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
