package com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juscom.databinding.ActivityRoomListBinding

class RoomListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var allRooms: List<Room> // A fonte de dados principal, carregada apenas uma vez.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carrega a lista de salas uma única vez para otimizar a busca.
        allRooms = getAllRooms()

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Salas de Discussão"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        // Inicializa o adapter usando a lista completa.
        roomAdapter = RoomAdapter(allRooms.toMutableList(), { room ->
            // Ação de clique para cada item da lista.
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }, { isEmpty ->
            // Aqui você pode lidar com o estado de filtro vazio, se necessário.
            // Por exemplo, mostrar uma mensagem de "nenhum resultado".
        })

        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoomListActivity)
            adapter = roomAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Buscar por nome, categoria..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Não é necessário para busca em tempo real.
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus() // Esconde o teclado
                return true
            }

            // Filtra a lista a cada caractere digitado.
            override fun onQueryTextChange(newText: String?): Boolean {
                filterRooms(newText.orEmpty())
                return true
            }
        })
        return true
    }

    /**
     * Filtra a lista 'allRooms' com base na query e atualiza o adapter.
     */
    private fun filterRooms(query: String) {
        val filteredRooms = if (query.isBlank()) {
            // Se a busca estiver vazia, exibe a lista completa.
            allRooms
        } else {
            // Filtra a lista principal com base no nome, descrição ou categoria.
            allRooms.filter { room ->
                room.name.contains(query, ignoreCase = true) ||
                        room.description.contains(query, ignoreCase = true) ||
                        room.category.contains(query, ignoreCase = true)
            }
        }
        // Atualiza o adapter com a lista filtrada.
        roomAdapter.updateData(filteredRooms)
    }

    /**
     * Retorna a lista completa de salas, já ordenada por número de inscritos.
     */
    private fun getAllRooms(): List<Room> {
        return listOf(
            Room(1, "Direito Civil", "Discussões sobre direito civil, contratos e obrigações", "Civil", 1250),
            Room(2, "Direito Penal", "Debates sobre direito penal e processo penal", "Penal", 980),
            Room(3, "Direito Trabalhista", "Temas de direito do trabalho e previdenciário", "Trabalhista", 750),
            Room(4, "Direito Tributário", "Discussões sobre direito tributário e fiscal", "Tributário", 650),
            Room(5, "Direito Constitucional", "Debates sobre direito constitucional", "Constitucional", 890),
            Room(6, "Direito Administrativo", "Temas de direito administrativo", "Administrativo", 720),
            Room(7, "Direito Empresarial", "Discussões sobre direito empresarial e societário", "Empresarial", 580),
            Room(8, "Direito Ambiental", "Temas de direito ambiental e sustentabilidade", "Ambiental", 420),
            Room(9, "Direito da Família", "Debates sobre direito de família e sucessões", "Família", 680),
            Room(10, "Direito do Consumidor", "Discussões sobre direito do consumidor", "Consumidor", 540)
        ).sortedByDescending { it.subscribersCount }
    }

    // Permite que o botão "voltar" na toolbar funcione.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
