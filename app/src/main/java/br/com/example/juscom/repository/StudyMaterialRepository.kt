package br.com.example.juscom.repository

import br.com.example.juscom.StudyMaterial
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class StudyMaterialRepository {

    private val db = FirebaseFirestore.getInstance()
    private val studyMaterialsCollection = db.collection("study_materials")

    suspend fun getStudyMaterials(): Result<List<StudyMaterial>> {
        return try {
            val snapshot = studyMaterialsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val materials = snapshot.toObjects(StudyMaterial::class.java)
            Result.success(materials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
