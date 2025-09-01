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

class AppointmentForDayAdapter : RecyclerView.Adapter<AppointmentForDayAdapter.AppointmentViewHolder>() {

    private var items: List<Pair<Appointment, String>> = emptyList()
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submitList(data: List<Pair<Appointment, String>>) {
        items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_day, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val (appointment, fullName) = items[position]
        holder.bind(appointment, fullName, timeFmt)
    }

    override fun getItemCount(): Int = items.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textPatientName: TextView = itemView.findViewById(R.id.textPatientName)
        private val textType: TextView = itemView.findViewById(R.id.textType)
        private val textTime: TextView = itemView.findViewById(R.id.textAppointmentTime)
        private val textNotes: TextView = itemView.findViewById(R.id.textAppointmentNotes)

        fun bind(appointment: Appointment, fullName: String, fmt: SimpleDateFormat) {
            textPatientName.text = "Όνομα: $fullName"
            textType.text = "Κατάσταση: ${appointment.status}"
            textTime.text = "Ώρα: ${fmt.format(Date(appointment.dateTime))}"
            textNotes.text = "Σημειώσεις: ${appointment.notes ?: "-"}"
        }
    }
}
