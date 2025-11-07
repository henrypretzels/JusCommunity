package br.com.example.juscom

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.juscom.repository.AuthRepository
import br.com.example.juscom.repository.StudyMaterialRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class StudyMaterialViewModel : ViewModel() {

    private val studyMaterialRepository = StudyMaterialRepository()
    private val authRepository = AuthRepository()

    private val _materials = MutableLiveData<List<StudyMaterial>>()
    val materials: LiveData<List<StudyMaterial>> = _materials

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> = _uploadSuccess

    fun loadStudyMaterials() {
        viewModelScope.launch {
            val result = studyMaterialRepository.getStudyMaterials()
            result.onSuccess { materialList ->
                _materials.value = materialList
            }.onFailure { exception ->
                _error.value = "Failed to load study materials: ${exception.message}"
            }
        }
    }

    fun uploadStudyMaterial(fileUri: Uri, title: String, description: String, category: String) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            _error.value = "User not logged in"
            return
        }

        viewModelScope.launch {
            val result = studyMaterialRepository.uploadFile(fileUri)
            result.onSuccess { downloadUrl ->
                val newMaterial = StudyMaterial(
                    title = title,
                    description = description,
                    authorName = currentUser.displayName ?: "Unknown Author",
                    category = category,
                    fileUrl = downloadUrl,
                    timestamp = Timestamp.now()
                )
                studyMaterialRepository.addStudyMaterial(newMaterial)
                _uploadSuccess.value = true
                loadStudyMaterials() // Refresh the list
            }.onFailure { exception ->
                _error.value = "Upload failed: ${exception.message}"
            }
        }
    }
}
