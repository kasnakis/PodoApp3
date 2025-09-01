package com.kasal.podoapp.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.squareup.picasso.Picasso

class VisitPhotoAdapter(
    private val photoUris: List<String>
) : RecyclerView.Adapter<VisitPhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoUris[position])
    }

    override fun getItemCount(): Int = photoUris.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewPhoto)

        fun bind(uriString: String) {
            val uri = Uri.parse(uriString)
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_image_placeholder) // προαιρετική προσωρινή εικόνα
                .error(R.drawable.ic_image_error) // προαιρετικό fallback
                .into(imageView)

            imageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                it.context.startActivity(intent)
            }
        }
    }
}
