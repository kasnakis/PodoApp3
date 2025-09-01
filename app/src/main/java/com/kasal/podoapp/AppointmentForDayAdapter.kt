package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.data.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentForDayAdapter(
    private var items: List<Appointment> = emptyList(),
    private val onConvertToVisit: (Appointment) -> Unit,
    private val onEdit: (Appointment) -> Unit,
    private val onDelete: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentForDayAdapter.VH>() {

    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submitList(newItems: List<Appointment>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.title.text = a.treatment ?: "Ραντεβού"
        holder.subtitle.text = timeFmt.format(Date(a.dateTime))

        holder.itemView.setOnClickListener { onEdit(a) }

        holder.itemView.setOnLongClickListener {
            val ctx = it.context
            val actions = arrayOf("Μετατροπή σε Επίσκεψη", "Επεξεργασία", "Διαγραφή")
            AlertDialog.Builder(ctx)
                .setTitle("Ενέργειες")
                .setItems(actions) { _, which ->
                    when (which) {
                        0 -> onConvertToVisit(a)
                        1 -> onEdit(a)
                        2 -> onDelete(a)
                    }
                }.show()
            true
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(android.R.id.text1)
        val subtitle: TextView = v.findViewById(android.R.id.text2)
    }
}
