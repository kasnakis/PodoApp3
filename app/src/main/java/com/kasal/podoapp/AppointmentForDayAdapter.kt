package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import java.text.SimpleDateFormat
import java.util.*

/**
 * Προβάλλει ραντεβού ημέρας με το όνομα πελάτη.
 * Tap: onItemClick(Appointment)  |  Long-tap: onItemLongClick(Appointment)
 */
class AppointmentForDayAdapter(
    private val onItemClick: (Appointment) -> Unit,
    private val onItemLongClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentForDayAdapter.AppointmentViewHolder>() {

    private var items: List<Pair<Appointment, String>> = emptyList()

    fun submitList(newItems: List<Pair<Appointment, String>>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_for_day, parent, false)
        return AppointmentViewHolder(v)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val (appt, patientName) = items[position]
        holder.bind(appt, patientName)
        holder.itemView.setOnClickListener { onItemClick(appt) }
        holder.itemView.setOnLongClickListener { onItemLongClick(appt); true }
    }

    override fun getItemCount(): Int = items.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvApptTime)
        private val tvPatient: TextView = itemView.findViewById(R.id.tvApptPatient)
        private val tvTreatment: TextView = itemView.findViewById(R.id.tvApptTreatment)

        private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(appt: Appointment, patientName: String) {
            tvTime.text = timeFmt.format(Date(appt.dateTime))
            tvPatient.text = patientName.ifBlank { "—" }
            tvTreatment.text = appt.treatment ?: "-"
        }
    }
}
