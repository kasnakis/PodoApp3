package com.kasal.podoapp.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent // Προσθήκη import για Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PatientDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientAdapter(
    private var patients: MutableList<Patient>,
    private val context: Context,
    private val patientDao: PatientDao,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    private val categories = listOf("Κανονικός", "Νέος", "Έκτακτος")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient)

        // Η νέα λογική για το κλικ στο item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PatientDetailActivity::class.java)
            // Σημείωση: Για να περάσεις ένα ολόκληρο αντικείμενο Patient,
            // η κλάση Patient πρέπει να υλοποιεί την Parcelable ή Serializable.
            // Η Parcelable είναι πιο αποδοτική.
            intent.putExtra("patient", patient)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = patients.size

    // Η μέθοδος showEditDialog() δεν καλείται πλέον στο setOnClickListener του item,
    // αλλά μπορεί να την κρατήσεις αν την καλείς από αλλού (π.χ. ένα κουμπί επεξεργασίας εντός του item).
    // Αν όχι, μπορείς να την αφαιρέσεις αν δεν χρησιμοποιείται πια.
    private fun showEditDialog(position: Int) {
        val patient = patients[position]
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_patient, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editTextName)
        val phoneInput = dialogView.findViewById<EditText>(R.id.editTextPhone)
        val addressInput = dialogView.findViewById<EditText>(R.id.editTextAddress)
        val notesInput = dialogView.findViewById<EditText>(R.id.editTextNotes)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        nameInput.setText(patient.fullName)
        phoneInput.setText(patient.phone)
        addressInput.setText(patient.address ?: "")
        notesInput.setText(patient.notes ?: "")

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter
        categorySpinner.setSelection(categories.indexOf(patient.category))

        AlertDialog.Builder(context)
            .setTitle("Επεξεργασία ή Διαγραφή")
            .setView(dialogView)
            .setPositiveButton("Αποθήκευση") { _, _ ->
                val updatedName = nameInput.text.toString().trim()
                val updatedPhone = phoneInput.text.toString().trim()
                val updatedAddress = addressInput.text.toString().trim()
                val updatedNotes = notesInput.text.toString().trim()
                val updatedCategory = categorySpinner.selectedItem.toString()

                if (updatedName.isNotEmpty() && updatedPhone.isNotEmpty()) {
                    val updatedPatient = patient.copy(
                        fullName = updatedName,
                        phone = updatedPhone,
                        address = updatedAddress,
                        notes = updatedNotes,
                        category = updatedCategory
                    )
                    patients[position] = updatedPatient
                    notifyItemChanged(position)

                    CoroutineScope(Dispatchers.IO).launch {
                        patientDao.update(updatedPatient)
                    }

                    onDataChanged()
                } else {
                    Toast.makeText(context, "Συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Διαγραφή") { _, _ ->
                val toRemove = patients[position]
                patients.removeAt(position)
                notifyItemRemoved(position)

                CoroutineScope(Dispatchers.IO).launch {
                    patientDao.delete(toRemove)
                }

                Toast.makeText(context, "Ο πελάτης διαγράφηκε", Toast.LENGTH_SHORT).show()
                onDataChanged()
            }
            .setNeutralButton("Άκυρο", null)
            .show()
    }

    fun updateList(newList: List<Patient>) {
        patients = newList.toMutableList()
        notifyDataSetChanged()
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullNameText: TextView = itemView.findViewById(R.id.textFullName)
        private val phoneText: TextView = itemView.findViewById(R.id.textPhone)
        private val addressText: TextView = itemView.findViewById(R.id.textAddress)
        private val notesText: TextView = itemView.findViewById(R.id.textNotes)
        private val categoryText: TextView = itemView.findViewById(R.id.textCategory)

        fun bind(patient: Patient) {
            fullNameText.text = patient.fullName
            phoneText.text = patient.phone
            addressText.text = "Διεύθυνση: ${patient.address ?: "-"}"
            notesText.text = "Σημειώσεις: ${patient.notes ?: "-"}"
            categoryText.text = "Κατηγορία: ${patient.category}"
        }
    }
}