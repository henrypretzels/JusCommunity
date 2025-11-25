package br.com.example.juscom

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityStudyMaterialBinding

class StudyMaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyMaterialBinding
    private lateinit var studyMaterialAdapter: StudyMaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadLocalStudyMaterials()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.study_material_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        studyMaterialAdapter = StudyMaterialAdapter(mutableListOf()) { material ->
            showMaterialPreview(material)
        }

        binding.studyMaterialRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@StudyMaterialActivity)
            adapter = studyMaterialAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddMaterial.isVisible = false
        binding.fabAddMaterial.setOnClickListener(null)
    }

    private fun loadLocalStudyMaterials() {
        val directory = "materias de esrtudo"
        val assetManager = assets
        val files = assetManager.list(directory)?.sorted() ?: emptyList()

        val materials = files.mapNotNull { fileName ->
            val path = "$directory/$fileName"
            val preview = readAssetFile(path)

            StudyMaterial(
                title = formatTitle(fileName),
                description = preview.take(200).ifEmpty { getString(R.string.study_material_preview_placeholder) },
                category = getString(R.string.study_material_mock_category),
                authorName = getString(R.string.study_material_mock_author),
                fileUrl = path
            )
        }

        binding.studyMaterialRecyclerView.isVisible = materials.isNotEmpty()
        binding.emptyStateText.isVisible = materials.isEmpty()
        if (materials.isNotEmpty()) {
            studyMaterialAdapter.updateMaterials(materials)
        }
    }

    private fun formatTitle(fileName: String): String {
        val nameWithoutExtension = fileName.substringBeforeLast('.')
        return nameWithoutExtension.split('_', '-')
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    private fun readAssetFile(path: String): String {
        return assets.open(path).bufferedReader().use { it.readText() }
    }

    private fun showMaterialPreview(material: StudyMaterial) {
        val assetPath = material.fileUrl ?: return
        val content = readAssetFile(assetPath)

        AlertDialog.Builder(this)
            .setTitle(material.title)
            .setMessage(content.take(1200))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
