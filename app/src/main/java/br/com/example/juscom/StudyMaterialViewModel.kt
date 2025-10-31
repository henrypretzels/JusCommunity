package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.juscom.repository.StudyMaterialRepository
import kotlinx.coroutines.launch

class StudyMaterialViewModel : ViewModel() {

    private val repository = StudyMaterialRepository()

    private val _materials = MutableLiveData<List<StudyMaterial>>()
    val materials: LiveData<List<StudyMaterial>> = _materials

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadStudyMaterials() {
        viewModelScope.launch {
            val result = repository.getStudyMaterials()
            result.onSuccess { materialList ->
                _materials.value = materialList
            }.onFailure { exception ->
                _error.value = "Failed to load study materials: ${exception.message}"
            }
        }
    }
}
