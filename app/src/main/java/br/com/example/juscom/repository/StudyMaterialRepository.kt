package br.com.example.juscom.repository

import android.net.Uri
import br.com.example.juscom.StudyMaterial
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StudyMaterialRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
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

    suspend fun uploadFile(fileUri: Uri): Result<String> {
        return try {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("study_materials/$fileName")

            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addStudyMaterial(material: StudyMaterial): Result<Unit> {
        return try {
            studyMaterialsCollection.add(material).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
