package com.kasal.podoapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.data.PatientPhoto
import java.text.SimpleDateFormat
import java.util.*

class PatientPhotoAdapter(
    private val onClick: (PatientPhoto) -> Unit,
    private val onLongClick: ((PatientPhoto) -> Unit)? = null // Νέο: Προσθήκη listener για long-press
) : ListAdapter<PatientPhoto, PatientPhotoAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PatientPhoto>() {
            override fun areItemsTheSame(old: PatientPhoto, new: PatientPhoto) = old.id == new.id
            override fun areContentsTheSame(old: PatientPhoto, new: PatientPhoto) = old == new
        }
        private val DATE_FMT = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    inner class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_patient_photo, parent, false)
    ) {
        val img: ImageView = itemView.findViewById(R.id.imgThumb)
        val tv: TextView = itemView.findViewById(R.id.tvWhen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        try {
            holder.img.setImageURI(Uri.parse(item.photoUri))
        } catch (_: SecurityException) {
            holder.img.setImageResource(android.R.drawable.ic_menu_report_image) // fallback icon
        }
        holder.tv.text = DATE_FMT.format(Date(item.takenAtMillis))
        holder.itemView.setOnClickListener { onClick(item) }

        // Νέο: Προσθήκη του onLongClickListener
        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(item)
            true // Επιστρέφουμε true για να υποδείξουμε ότι το event καταναλώθηκε
        }
    }
}