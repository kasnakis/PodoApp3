package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Visit

class VisitForDayAdapter(
    private val onVisitClick: (Visit) -> Unit,
    private val onEditClick: (Visit) -> Unit,
    private val onDeleteClick: (Visit) -> Unit
) : RecyclerView.Adapter<VisitForDayAdapter.VisitViewHolder>() {

    private var visits: List<Visit> = emptyList()

    fun submitList(list: List<Visit>) {
        visits = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visit_day, parent, false)
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
        private val overflowBtn: ImageButton = itemView.findViewById(R.id.buttonOverflow)

        fun bind(visit: Visit) {
            reasonText.text = "Θεραπεία: ${visit.treatment ?: "-"}"
            diagnosisText.text = "Χρέωση: ${visit.charge ?: "-"}"
            notesText.text = "Σημειώσεις: ${visit.notes ?: "-"}"

            itemView.setOnClickListener { onVisitClick(visit) }

            overflowBtn.setOnClickListener { v ->
                val menu = PopupMenu(v.context, v)
                menu.inflate(R.menu.menu_visit_item)
                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_edit_visit -> { onEditClick(visit); true }
                        R.id.action_delete_visit -> { onDeleteClick(visit); true }
                        else -> false
                    }
                }
                menu.show()
            }
        }
    }
}
