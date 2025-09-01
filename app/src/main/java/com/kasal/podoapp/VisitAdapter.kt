package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Visit

class VisitAdapter(
    private val onVisitAction: (Visit, ActionType) -> Unit
) : RecyclerView.Adapter<VisitAdapter.VisitViewHolder>() {

    private var visits: List<Visit> = emptyList()

    fun submitList(list: List<Visit>) {
        visits = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_visit, parent, false)
        return VisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(visits[position])
    }

    override fun getItemCount(): Int = visits.size

    inner class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reasonText: TextView = itemView.findViewById(R.id.textViewReason)
        private val diagnosisText: TextView = itemView.findViewById(R.id.textViewDiagnosis)
        private val treatmentText: TextView = itemView.findViewById(R.id.textViewTreatment)
        private val notesText: TextView = itemView.findViewById(R.id.textViewNotes)
        private val recyclerViewPhotos: RecyclerView = itemView.findViewById(R.id.recyclerViewPhotos)
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEditVisit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDeleteVisit)

        fun bind(visit: Visit) {
            reasonText.text = "Λόγος: ${visit.reason ?: "-"}"
            diagnosisText.text = "Διάγνωση: ${visit.diagnosis ?: "-"}"
            treatmentText.text = "Θεραπεία: ${visit.treatment ?: "-"}"
            notesText.text = "Σημειώσεις: ${visit.notes ?: "-"}"

            val photos = visit.photoUris ?: emptyList()
            if (photos.isNotEmpty()) {
                recyclerViewPhotos.visibility = View.VISIBLE
                recyclerViewPhotos.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewPhotos.adapter = VisitPhotoAdapter(photos)
            } else {
                recyclerViewPhotos.visibility = View.GONE
            }

            buttonEdit.setOnClickListener {
                onVisitAction(visit, ActionType.EDIT)
            }

            buttonDelete.setOnClickListener {
                onVisitAction(visit, ActionType.DELETE)
            }
        }
    }

    enum class ActionType {
        EDIT, DELETE
    }
}
