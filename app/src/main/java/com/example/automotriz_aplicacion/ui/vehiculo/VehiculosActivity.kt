package com.example.automotriz_aplicacion.ui.vehiculo

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
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.data.model.Vehiculo
import com.example.automotriz_aplicacion.databinding.ActivityVehiculosBinding
import com.example.automotriz_aplicacion.databinding.DialogVehiculoBinding

class VehiculosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehiculosBinding
    private val viewModel: VehiculoViewModel by viewModels()
    private lateinit var adapter: VehiculoAdapter

    private var clienteList: List<Cliente> = emptyList()
    private var clienteMap: Map<Int, String> = emptyMap()
    private var fullList: List<Vehiculo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiculosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.fabAddVehiculo.setOnClickListener {
            showVehiculoDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = VehiculoAdapter(
            onEditClick = { vehiculo -> showVehiculoDialog(vehiculo) },
            onDeleteClick = { vehiculo -> showDeleteConfirmationDialog(vehiculo) }
        )
        binding.rvVehiculos.layoutManager = LinearLayoutManager(this)
        binding.rvVehiculos.adapter = adapter
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

        viewModel.allVehiculos.observe(this) { vehiculos ->
            fullList = vehiculos.orEmpty()
            filterList(binding.etSearch.text?.toString().orEmpty())
        }
    }

    private fun filterList(query: String) {
        val filtered = if (query.isBlank()) {
            fullList
        } else {
            val q = query.trim().lowercase()
            fullList.filter { v ->
                val clienteNombre = clienteMap[v.clienteId]?.lowercase().orEmpty()
                v.placa.lowercase().contains(q) ||
                v.marca.lowercase().contains(q) ||
                v.modelo.lowercase().contains(q) ||
                clienteNombre.contains(q)
            }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvVehiculos.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvVehiculos.visibility = View.VISIBLE
        }
    }

    private fun showVehiculoDialog(vehiculo: Vehiculo?) {
        if (clienteList.isEmpty()) {
            Toast.makeText(this, getString(R.string.sin_clientes), Toast.LENGTH_LONG).show()
            return
        }

        val dialogBinding = DialogVehiculoBinding.inflate(layoutInflater)

        val clienteNombres = clienteList.map { "${it.nombre} (${it.dni})" }
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            clienteNombres
        )
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        dialogBinding.spClientes.adapter = spinnerAdapter

        if (vehiculo != null) {
            dialogBinding.tvDialogTitle.setText(R.string.editar_vehiculo)
            dialogBinding.etPlaca.setText(vehiculo.placa)
            dialogBinding.etMarca.setText(vehiculo.marca)
            dialogBinding.etModelo.setText(vehiculo.modelo)

            val selectedIndex = clienteList.indexOfFirst { it.id == vehiculo.clienteId }
            if (selectedIndex >= 0) {
                dialogBinding.spClientes.setSelection(selectedIndex)
            }
        } else {
            dialogBinding.tvDialogTitle.setText(R.string.nuevo_vehiculo)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { dialog, _ ->
                val placa = dialogBinding.etPlaca.text.toString().trim()
                val marca = dialogBinding.etMarca.text.toString().trim()
                val modelo = dialogBinding.etModelo.text.toString().trim()
                val selectedPosition = dialogBinding.spClientes.selectedItemPosition

                if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || selectedPosition < 0) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedClienteId = clienteList[selectedPosition].id

                if (vehiculo != null) {
                    val updatedVehiculo = vehiculo.copy(
                        placa = placa,
                        marca = marca,
                        modelo = modelo,
                        clienteId = selectedClienteId
                    )
                    viewModel.update(updatedVehiculo)
                    Toast.makeText(this, "Vehículo actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    val newVehiculo = Vehiculo(
                        placa = placa,
                        marca = marca,
                        modelo = modelo,
                        clienteId = selectedClienteId
                    )
                    viewModel.insert(newVehiculo)
                    Toast.makeText(this, "Vehículo registrado", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmationDialog(vehiculo: Vehiculo) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.delete(vehiculo)
                Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
