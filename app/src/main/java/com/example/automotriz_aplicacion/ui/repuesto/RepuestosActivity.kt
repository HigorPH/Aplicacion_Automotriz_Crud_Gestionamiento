package com.example.automotriz_aplicacion.ui.repuesto

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Repuesto
import com.example.automotriz_aplicacion.databinding.ActivityRepuestosBinding
import com.example.automotriz_aplicacion.databinding.DialogRepuestoBinding

class RepuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepuestosBinding
    private val viewModel: RepuestoViewModel by viewModels()
    private lateinit var adapter: RepuestoAdapter

    private var fullList: List<Repuesto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepuestosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.fabAddRepuesto.setOnClickListener {
            showRepuestoDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = RepuestoAdapter(
            onEditClick = { repuesto -> showRepuestoDialog(repuesto) },
            onDeleteClick = { repuesto -> showDeleteConfirmationDialog(repuesto) }
        )
        binding.rvRepuestos.layoutManager = LinearLayoutManager(this)
        binding.rvRepuestos.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { query, _, _, _ ->
            filterList(query?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.allRepuestos.observe(this) { repuestos ->
            fullList = repuestos.orEmpty()
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
                it.codigoQR.lowercase().contains(q)
            }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvRepuestos.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvRepuestos.visibility = View.VISIBLE
        }
    }

    private fun showRepuestoDialog(repuesto: Repuesto?) {
        val dialogBinding = DialogRepuestoBinding.inflate(layoutInflater)

        if (repuesto != null) {
            dialogBinding.tvDialogTitle.setText(R.string.editar_repuesto)
            dialogBinding.etCodigoQR.setText(repuesto.codigoQR)
            dialogBinding.etNombreRepuesto.setText(repuesto.nombre)
            dialogBinding.etStock.setText(repuesto.stock.toString())
        } else {
            dialogBinding.tvDialogTitle.setText(R.string.nuevo_repuesto)
        }

        // Simulación de escáner QR para prototipo
        dialogBinding.btnEscanearQR.setOnClickListener {
            val randomCode = "REP-QR-${(1000..9999).random()}"
            dialogBinding.etCodigoQR.setText(randomCode)
            Toast.makeText(this, getString(R.string.qr_escaneado), Toast.LENGTH_SHORT).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { dialog, _ ->
                val codigoQR = dialogBinding.etCodigoQR.text.toString().trim()
                val nombre = dialogBinding.etNombreRepuesto.text.toString().trim()
                val stockStr = dialogBinding.etStock.text.toString().trim()

                if (codigoQR.isEmpty() || nombre.isEmpty() || stockStr.isEmpty()) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val stock = stockStr.toIntOrNull() ?: 0

                if (repuesto != null) {
                    val updatedRepuesto = repuesto.copy(
                        codigoQR = codigoQR,
                        nombre = nombre,
                        stock = stock
                    )
                    viewModel.update(updatedRepuesto)
                    Toast.makeText(this, "Repuesto actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    val newRepuesto = Repuesto(
                        codigoQR = codigoQR,
                        nombre = nombre,
                        stock = stock
                    )
                    viewModel.insert(newRepuesto)
                    Toast.makeText(this, "Repuesto registrado", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmationDialog(repuesto: Repuesto) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.delete(repuesto)
                Toast.makeText(this, "Repuesto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
