package com.kasal.podoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private val onCompleted: (Appointment) -> Unit,
    private val onEdit: (Appointment) -> Unit,
    private val onDelete: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.VH>() {

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private var items: List<Appointment> = emptyList()

    fun submit(list: List<Appointment>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], fmt, onCompleted, onEdit, onDelete)
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWhen: TextView = itemView.findViewById(R.id.tvApptWhen)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvApptStatus)
        private val tvCharge: TextView = itemView.findViewById(R.id.tvApptCharge)
        private val tvTreatment: TextView = itemView.findViewById(R.id.tvApptTreatment)
        private val btnDone: Button? = itemView.findViewById(R.id.btnMarkCompleted)

        // Βρίσκουμε overflow ImageButton με βάση ΠΙΘΑΝΑ ονόματα IDs (αν δεν υπάρχει, κάνουμε long-press fallback)
        private fun findOverflow(): ImageButton? {
            val ctx = itemView.context
            val names = listOf(
                "buttonOverflow", "buttonMore", "imageButtonOverflow", "btnMore", "moreButton", "overflow"
            )
            for (name in names) {
                val id = ctx.resources.getIdentifier(name, "id", ctx.packageName)
                if (id != 0) {
                    val found = itemView.findViewById<ImageButton?>(id)
                    if (found != null) return found
                }
            }
            return null
        }

        fun bind(
            a: Appointment,
            fmt: SimpleDateFormat,
            onCompleted: (Appointment) -> Unit,
            onEdit: (Appointment) -> Unit,
            onDelete: (Appointment) -> Unit
        ) {
            tvWhen.text = fmt.format(Date(a.dateTime))
            tvStatus.text = "Κατάσταση: ${a.status}"
            tvCharge.text = "Χρέωση: ${a.charge ?: "-"}"
            tvTreatment.text = "Θεραπεία: ${a.treatment ?: "-"}"

            btnDone?.isEnabled = a.status != "COMPLETED"
            btnDone?.setOnClickListener { onCompleted(a) }

            // Overflow menu αν υπάρχει
            val overflow = findOverflow()
            val showMenu: (View) -> Unit = { anchor ->
                val pm = PopupMenu(itemView.context, anchor)
                pm.menuInflater.inflate(R.menu.menu_appointment_item, pm.menu)
                pm.setOnMenuItemClickListener { mi ->
                    when (mi.itemId) {
                        R.id.action_edit_appointment -> { onEdit(a); true }
                        R.id.action_delete_appointment -> { onDelete(a); true }
                        else -> false
                    }
                }
                pm.show()
            }
            overflow?.setOnClickListener { v -> showMenu(v) }

            // Long-press fallback σε όλο το item
            itemView.setOnLongClickListener {
                showMenu(it)
                true
            }
        }
    }
}
