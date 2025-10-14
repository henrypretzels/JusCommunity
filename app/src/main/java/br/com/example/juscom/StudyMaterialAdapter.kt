package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class StudyMaterialAdapter(
    private var materials: MutableList<StudyMaterial>,
    private val onMaterialClick: (StudyMaterial) -> Unit
) : RecyclerView.Adapter<StudyMaterialAdapter.StudyMaterialViewHolder>() {

    class StudyMaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.studyMaterialCard)
        val materialImage: ImageView = itemView.findViewById(R.id.studyMaterialImage)
        val materialTitle: TextView = itemView.findViewById(R.id.studyMaterialTitle)
        val materialDescription: TextView = itemView.findViewById(R.id.studyMaterialDescription)
        val materialCategory: TextView = itemView.findViewById(R.id.studyMaterialCategory)
        val materialAuthor: TextView = itemView.findViewById(R.id.studyMaterialAuthor)
        val materialDate: TextView = itemView.findViewById(R.id.studyMaterialDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyMaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_study_material, parent, false)
        return StudyMaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyMaterialViewHolder, position: Int) {
        val material = materials[position]

        holder.materialImage.setImageResource(material.imageResource)
        holder.materialTitle.text = material.title
        holder.materialDescription.text = material.description
        holder.materialCategory.text = material.category
        holder.materialAuthor.text = material.author
        holder.materialDate.text = material.date

        holder.cardView.setOnClickListener {
            onMaterialClick(material)
        }
    }

    override fun getItemCount(): Int = materials.size
}
