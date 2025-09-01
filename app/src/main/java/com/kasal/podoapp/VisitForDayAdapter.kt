package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Visit
import com.kasal.podoapp.data.reason
import com.kasal.podoapp.data.diagnosis
import com.kasal.podoapp.data.photoUris

class VisitForDayAdapter(
    private val onVisitClick: (Visit) -> Unit
) : RecyclerView.Adapter<VisitForDayAdapter.VisitViewHolder>() {

    private var visits: List<Visit> = emptyList()

    fun submitList(list: List<Visit>) {
        visits = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_visit_day, parent, false)
        return VisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(visits[position])
    }

    override fun getItemCount(): Int = visits.size

    inner class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reasonText: TextView = itemView.findViewById(R.id.textVisitReason)
        private val diagnosisText: TextView = itemView.findViewById(R.id.textVisitDiagnosis)
        private val notesText: TextView = itemView.findViewById(R.id.textVisitNotes)
        private val photoRecycler: RecyclerView = itemView.findViewById(R.id.recyclerViewVisitPhotos)

        fun bind(visit: Visit) {
            reasonText.text = "Λόγος: ${visit.reason ?: "-"}"
            diagnosisText.text = "Διάγνωση: ${visit.diagnosis ?: "-"}"
            notesText.text = "Σημειώσεις: ${visit.notes ?: "-"}"

            val photos = visit.photoUris
            if (photos.isNotEmpty()) {
                photoRecycler.visibility = View.VISIBLE
                photoRecycler.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                photoRecycler.adapter = VisitPhotoAdapter(photos)
            } else {
                photoRecycler.visibility = View.GONE
            }

            itemView.setOnClickListener { onVisitClick(visit) }
        }
    }
}
