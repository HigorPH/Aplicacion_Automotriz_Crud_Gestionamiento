package com.example.automotriz_aplicacion.ui.cita

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Cita
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.databinding.ActivityCitasBinding
import com.example.automotriz_aplicacion.databinding.DialogCitaBinding
import java.util.Calendar
import java.util.Locale

class CitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitasBinding
    private val viewModel: CitaViewModel by viewModels()
    private lateinit var adapter: CitaAdapter

    private var clienteList: List<Cliente> = emptyList()
    private var clienteMap: Map<Int, String> = emptyMap()
    private var fullList: List<Cita> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.fabAddCita.setOnClickListener {
            showCitaDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = CitaAdapter(
            onEditClick = { cita -> showCitaDialog(cita) },
            onDeleteClick = { cita -> showDeleteConfirmationDialog(cita) }
        )
        binding.rvCitas.layoutManager = LinearLayoutManager(this)
        binding.rvCitas.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { query, _, _, _ ->
            filterList(query?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.allClientes.observe(this) { clientes ->
            clienteList = clientes.orEmpty()
            clienteMap = clienteList.associate { it.id to "${it.nombre} (${it.dni})" }
            adapter.updateClientes(clienteMap)
            filterList(binding.etSearch.text?.toString().orEmpty())
        }

        viewModel.allCitas.observe(this) { citas ->
            fullList = citas.orEmpty()
            filterList(binding.etSearch.text?.toString().orEmpty())
        }
    }

    private fun filterList(query: String) {
        val filtered = if (query.isBlank()) {
            fullList
        } else {
            val q = query.trim().lowercase()
            fullList.filter { c ->
                val clienteNombre = clienteMap[c.clienteId]?.lowercase().orEmpty()
                c.fecha.lowercase().contains(q) ||
                c.hora.lowercase().contains(q) ||
                clienteNombre.contains(q)
            }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvCitas.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvCitas.visibility = View.VISIBLE
        }
    }

    private fun showCitaDialog(cita: Cita?) {
        if (clienteList.isEmpty()) {
            Toast.makeText(this, getString(R.string.sin_clientes), Toast.LENGTH_LONG).show()
            return
        }

        val dialogBinding = DialogCitaBinding.inflate(layoutInflater)

        val clienteNombres = clienteList.map { "${it.nombre} (${it.dni})" }
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            clienteNombres
        )
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        dialogBinding.spClientes.adapter = spinnerAdapter

        dialogBinding.etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    dialogBinding.etFecha.setText(fecha)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dialogBinding.etHora.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    dialogBinding.etHora.setText(hora)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        if (cita != null) {
            dialogBinding.tvDialogTitle.setText(R.string.editar_cita)
            dialogBinding.etFecha.setText(cita.fecha)
            dialogBinding.etHora.setText(cita.hora)

            val selectedIndex = clienteList.indexOfFirst { it.id == cita.clienteId }
            if (selectedIndex >= 0) {
                dialogBinding.spClientes.setSelection(selectedIndex)
            }
        } else {
            dialogBinding.tvDialogTitle.setText(R.string.nueva_cita)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { dialog, _ ->
                val fecha = dialogBinding.etFecha.text.toString().trim()
                val hora = dialogBinding.etHora.text.toString().trim()
                val selectedPosition = dialogBinding.spClientes.selectedItemPosition

                if (fecha.isEmpty() || hora.isEmpty() || selectedPosition < 0) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedClienteId = clienteList[selectedPosition].id

                if (cita != null) {
                    val updatedCita = cita.copy(
                        fecha = fecha,
                        hora = hora,
                        clienteId = selectedClienteId
                    )
                    viewModel.update(updatedCita)
                    Toast.makeText(this, "Cita actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    val newCita = Cita(
                        fecha = fecha,
                        hora = hora,
                        clienteId = selectedClienteId
                    )
                    viewModel.insert(newCita)
                    Toast.makeText(this, "Cita programada", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmationDialog(cita: Cita) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.delete(cita)
                Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
