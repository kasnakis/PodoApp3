package com.kasal.podoapp.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kasal.podoapp.R
import com.kasal.podoapp.data.Patient
import com.kasal.podoapp.data.PodologiaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAdapter
    private var fullList: List<Patient> = emptyList()
    private var currentQuery: String = ""
    private var currentCategory: String = "Όλες οι Κατηγορίες"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        recyclerView = findViewById(R.id.recyclerViewPatients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dao = PodologiaDatabase.getDatabase(this).patientDao()

        adapter = PatientAdapter(
            patients = mutableListOf(),
            context = this,
            patientDao = dao
        ) {}

        recyclerView.adapter = adapter

        setupSearchView()
        setupSortSpinner()
        setupCategoryFilterSpinner()
        loadPatients()
    }

    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                applyFilterAndSort()
                return true
            }
        })
    }

    private fun setupSortSpinner() {
        val spinner = findViewById<Spinner>(R.id.sortSpinner)
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilterAndSort()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupCategoryFilterSpinner() {
        val spinner = findViewById<Spinner>(R.id.filterCategorySpinner)
        val categoryOptions = resources.getStringArray(R.array.category_filter_options)
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = categoryOptions[position]
                applyFilterAndSort()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilterAndSort() {
        val searchQuery = currentQuery.lowercase()
        val sortSpinner = findViewById<Spinner>(R.id.sortSpinner)

        // Φιλτράρισμα με βάση αναζήτηση + κατηγορία
        var filtered = fullList.filter {
            it.fullName.lowercase().contains(searchQuery)
        }

        if (currentCategory != "Όλες οι Κατηγορίες") {
            filtered = filtered.filter { it.category == currentCategory }
        }

        val sorted = when (sortSpinner.selectedItemPosition) {
            0 -> filtered.sortedBy { it.fullName.lowercase() }
            1 -> filtered.sortedByDescending { it.fullName.lowercase() }
            else -> filtered
        }

        adapter.updateList(sorted)
    }

    private fun loadPatients() {
        val dao = PodologiaDatabase.getDatabase(this).patientDao()
        CoroutineScope(Dispatchers.IO).launch {
            dao.getAllPatients().collect { patientList ->
                withContext(Dispatchers.Main) {
                    fullList = patientList
                    applyFilterAndSort()
                }
            }
        }
    }
}
