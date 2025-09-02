package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Visit
import java.text.SimpleDateFormat
import java.util.*

class VisitForDayAdapter(
    private val onVisitClick: (Visit) -> Unit
) : RecyclerView.Adapter<VisitForDayAdapter.VisitViewHolder>() {

    private var visits: List<Visit> = emptyList()
    private val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submit(list: List<Visit>) {
        visits = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_visit_day, parent, false)
        return VisitViewHolder(v, onVisitClick, fmt)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(visits[position])
    }

    override fun getItemCount(): Int = visits.size

    class VisitViewHolder(
        itemView: View,
        private val onVisitClick: (Visit) -> Unit,
        private val fmt: SimpleDateFormat
    ) : RecyclerView.ViewHolder(itemView) {

        // IDs που υπάρχουν στο repo: textVisitReason, textVisitDiagnosis, textVisitNotes, recyclerViewVisitPhotos
        private val tvReason: TextView? = itemView.findViewById(R.id.textVisitReason)
        private val tvDiagnosis: TextView? = itemView.findViewById(R.id.textVisitDiagnosis)
        private val tvNotes: TextView? = itemView.findViewById(R.id.textVisitNotes)
        private val rvPhotos: RecyclerView? = itemView.findViewById(R.id.recyclerViewVisitPhotos)

        fun bind(visit: Visit) {
            // Προβάλλουμε με ασφάλεια μόνο πεδία που σίγουρα υπάρχουν στο entity: dateTime, treatment, notes, charge
            val timeStr = try { fmt.format(Date(visit.dateTime)) } catch (_: Exception) { "--:--" }
            tvReason?.text = "Ώρα: $timeStr • Θεραπεία: ${visit.treatment ?: "-"}"
            tvDiagnosis?.text = "Χρέωση: ${visit.charge ?: "-"}"
            tvNotes?.text = visit.notes ?: ""

            // Αν δεν υποστηρίζεις ακόμα φωτογραφίες εδώ, απλά κρύψε το RecyclerView
            rvPhotos?.visibility = View.GONE
            // Για μελλοντική ενεργοποίηση φωτογραφιών:
            // rvPhotos?.visibility = View.VISIBLE
            // rvPhotos?.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            // rvPhotos?.adapter = VisitPhotoAdapter(visit.photoUris ?: emptyList())

            itemView.setOnClickListener { onVisitClick(visit) }
        }
    }
}
