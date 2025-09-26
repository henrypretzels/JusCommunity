package com.example.juscom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juscom.databinding.ActivityQaBinding

class QAActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQaBinding
    private lateinit var qaAdapter: QAAdapter
    private lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        room = intent.getSerializableExtra("room") as Room

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.qa_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        val questions = getSampleQuestions()
        qaAdapter = QAAdapter(questions)

        binding.qaRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@QAActivity)
            adapter = qaAdapter
        }

        if (questions.isEmpty()) {
            binding.emptyStateLayout.visibility = android.view.View.VISIBLE
            binding.qaRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateLayout.visibility = android.view.View.GONE
            binding.qaRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun getSampleQuestions(): List<Question> {
        return listOf(
            Question(1, "Qual a diferença entre dano moral e dano material?", "Dano material é o prejuízo econômico mensurável, enquanto dano moral é a dor, sofrimento ou constrangimento causado.", "Dr. João Silva", "15/01/2024", 12),
            Question(2, "Como funciona o prazo prescricional no direito civil?", "O prazo prescricional varia conforme o tipo de ação. Para cobrança de dívidas, por exemplo, é de 3 anos.", "Dra. Maria Santos", "12/01/2024", 8),
            Question(3, "Posso rescindir um contrato unilateralmente?", "Depende do tipo de contrato e das cláusulas. Em regra, não é possível sem justa causa ou acordo entre as partes.", "Dr. Carlos Oliveira", "10/01/2024", 15)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
