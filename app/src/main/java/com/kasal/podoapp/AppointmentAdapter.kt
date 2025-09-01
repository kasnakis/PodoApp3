package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private val onCompleted: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.VH>() {

    private val items = mutableListOf<Appointment>()
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun submit(list: List<Appointment>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], onCompleted, fmt)
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWhen: TextView = itemView.findViewById(R.id.tvApptWhen)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvApptStatus)
        private val tvCharge: TextView = itemView.findViewById(R.id.tvApptCharge)
        private val tvTreatment: TextView = itemView.findViewById(R.id.tvApptTreatment)
        private val btnDone: Button = itemView.findViewById(R.id.btnMarkCompleted)

        fun bind(a: Appointment, onCompleted: (Appointment) -> Unit, fmt: SimpleDateFormat) {
            tvWhen.text = fmt.format(Date(a.dateTime))
            tvStatus.text = "Κατάσταση: ${a.status}"
            tvCharge.text = "Χρέωση: ${a.charge ?: "-"}"
            tvTreatment.text = "Θεραπεία: ${a.treatment ?: "-"}"
            btnDone.isEnabled = a.status != "COMPLETED"
            btnDone.setOnClickListener { onCompleted(a) }
        }
    }
}
