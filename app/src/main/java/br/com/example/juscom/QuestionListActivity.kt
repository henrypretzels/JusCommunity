package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityQuestionListBinding

class QuestionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionListBinding
    private val viewModel: QuestionListViewModel by viewModels()
    private lateinit var questionAdapter: QuestionListAdapter
    private var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("ROOM_ID")

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the questions list when returning to the activity
        roomId?.let { viewModel.loadQuestions(it) }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Perguntas da Sala"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        questionAdapter = QuestionListAdapter(emptyList()) { question ->
            val intent = Intent(this, QuestionDetailActivity::class.java)
            intent.putExtra("QUESTION_ID", question.id)
            startActivity(intent)
        }

        binding.questionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@QuestionListActivity)
            adapter = questionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAskQuestion.setOnClickListener {
            val intent = Intent(this, CreateQuestionActivity::class.java)
            intent.putExtra("ROOM_ID", roomId)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.questions.observe(this, Observer { questions ->
            if (questions.isEmpty()) {
                binding.questionsRecyclerView.visibility = View.GONE
                binding.emptyStateLayout.visibility = View.VISIBLE
            } else {
                binding.questionsRecyclerView.visibility = View.VISIBLE
                binding.emptyStateLayout.visibility = View.GONE
                questionAdapter.updateQuestions(questions)
            }
        })

        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
