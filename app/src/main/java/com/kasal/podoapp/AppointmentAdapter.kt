package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private val onEdit: (Appointment) -> Unit,
    private val onDelete: (Appointment) -> Unit,
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
        return VH(v, onEdit, onDelete, onCompleted, fmt)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(
        itemView: View,
        private val onEdit: (Appointment) -> Unit,
        private val onDelete: (Appointment) -> Unit,
        private val onCompleted: (Appointment) -> Unit,
        private val fmt: SimpleDateFormat
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvWhen: TextView = itemView.findViewById(R.id.tvApptWhen)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvApptStatus)
        private val tvCharge: TextView = itemView.findViewById(R.id.tvApptCharge)
        private val tvTreatment: TextView = itemView.findViewById(R.id.tvApptTreatment)
        private val btnDone: Button? = itemView.findViewById(R.id.btnMarkCompleted)

        // προαιρετικό overflow κουμπί (αν δεν υπάρχει στο layout, το fallback είναι long-press στο item)
        private val btnMore: ImageButton? = itemView.findViewById(R.id.btnMore)

        fun bind(a: Appointment) {
            tvWhen.text = fmt.format(Date(a.dateTime))
            tvStatus.text = "Κατάσταση: ${a.status}"
            tvCharge.text = "Χρέωση: ${a.charge ?: "-"}"
            tvTreatment.text = "Θεραπεία: ${a.treatment ?: "-"}"

            btnDone?.apply {
                isEnabled = a.status != "COMPLETED"
                setOnClickListener { onCompleted(a) }
            }

            val anchor = (btnMore ?: itemView)

            // Overflow
            btnMore?.setOnClickListener { showPopup(anchor, a) }
            // Fallback: long-press στο item
            itemView.setOnLongClickListener {
                showPopup(anchor, a)
                true
            }
        }

        private fun showPopup(anchor: View, a: Appointment) {
            val popup = PopupMenu(anchor.context, anchor)
            popup.menu.add(0, 1, 0, "Επεξεργασία")
            popup.menu.add(0, 2, 1, "Διαγραφή")
            if (a.status != "COMPLETED") popup.menu.add(0, 3, 2, "Ολοκληρώθηκε")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> { onEdit(a); true }
                    2 -> { onDelete(a); true }
                    3 -> { onCompleted(a); true }
                    else -> false
                }
            }
            popup.show()
        }
    }
}
