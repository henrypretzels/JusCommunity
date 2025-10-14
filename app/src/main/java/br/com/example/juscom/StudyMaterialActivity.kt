package br.com.example.juscom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.study_material_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        val materials = getStudyMaterials().toMutableList()
        studyMaterialAdapter = StudyMaterialAdapter(materials) { material ->
            // Handle click on study material
        }

        binding.studyMaterialRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@StudyMaterialActivity)
            adapter = studyMaterialAdapter
        }
    }

    private fun getStudyMaterials(): List<StudyMaterial> {
        return listOf(
            StudyMaterial(1, "Vade Mecum 2024", "Vade Mecum completo e atualizado para 2024", "Legislação", "Editora JusPodivm", "01/01/2024"),
            StudyMaterial(2, "Manual de Direito Constitucional", "Manual completo de Direito Constitucional", "Doutrina", "Alexandre de Moraes", "15/02/2024"),
            StudyMaterial(3, "Peças Práticas de Direito Penal", "Modelos de peças práticas para 2ª fase da OAB", "Prática", "Equipe JusCom", "20/03/2024"),
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
