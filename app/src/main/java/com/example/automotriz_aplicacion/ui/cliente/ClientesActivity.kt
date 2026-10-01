package com.example.automotriz_aplicacion.ui.cliente

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.databinding.ActivityClientesBinding
import com.example.automotriz_aplicacion.databinding.DialogClienteBinding

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var adapter: ClienteAdapter

    private var fullList: List<Cliente> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.fabAddCliente.setOnClickListener {
            showClienteDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter(
            onEditClick = { cliente -> showClienteDialog(cliente) },
            onDeleteClick = { cliente -> showDeleteConfirmationDialog(cliente) }
        )
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { query, _, _, _ ->
            filterList(query?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.allClientes.observe(this) { clientes ->
            fullList = clientes.orEmpty()
            filterList(binding.etSearch.text?.toString().orEmpty())
        }
    }

    private fun filterList(query: String) {
        val filtered = if (query.isBlank()) {
            fullList
        } else {
            val q = query.trim().lowercase()
            fullList.filter {
                it.nombre.lowercase().contains(q) ||
                it.dni.contains(q) ||
                it.telefono.contains(q)
            }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvClientes.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvClientes.visibility = View.VISIBLE
        }
    }

    private fun showClienteDialog(cliente: Cliente?) {
        val dialogBinding = DialogClienteBinding.inflate(layoutInflater)

        if (cliente != null) {
            dialogBinding.tvDialogTitle.setText(R.string.editar_cliente)
            dialogBinding.etNombre.setText(cliente.nombre)
            dialogBinding.etDni.setText(cliente.dni)
            dialogBinding.etTelefono.setText(cliente.telefono)
        } else {
            dialogBinding.tvDialogTitle.setText(R.string.nuevo_cliente)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { dialog, _ ->
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val dni = dialogBinding.etDni.text.toString().trim()
                val telefono = dialogBinding.etTelefono.text.toString().trim()

                if (nombre.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (cliente != null) {
                    val updatedCliente = cliente.copy(
                        nombre = nombre,
                        dni = dni,
                        telefono = telefono
                    )
                    viewModel.update(updatedCliente)
                    Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    val newCliente = Cliente(
                        nombre = nombre,
                        dni = dni,
                        telefono = telefono
                    )
                    viewModel.insert(newCliente)
                    Toast.makeText(this, "Cliente registrado", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmationDialog(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.delete(cliente)
                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
