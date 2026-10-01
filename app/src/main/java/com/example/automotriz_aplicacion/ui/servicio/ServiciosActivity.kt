package com.example.automotriz_aplicacion.ui.servicio

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Servicio
import com.example.automotriz_aplicacion.data.model.Vehiculo
import com.example.automotriz_aplicacion.databinding.ActivityServiciosBinding
import com.example.automotriz_aplicacion.databinding.DialogServicioBinding
import java.util.Calendar
import java.util.Locale

class ServiciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiciosBinding
    private val viewModel: ServicioViewModel by viewModels()
    private lateinit var adapter: ServicioAdapter

    private var vehiculoList: List<Vehiculo> = emptyList()
    private var vehiculoMap: Map<Int, String> = emptyMap()
    private var fullList: List<Servicio> = emptyList()

    private var currentFotoUri: String? = null
    private var activeDialogBinding: DialogServicioBinding? = null

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentFotoUri = it.toString()
            activeDialogBinding?.ivPreviewFoto?.setImageURI(it)
            activeDialogBinding?.ivPreviewFoto?.imageTintList = null
            Toast.makeText(this, getString(R.string.foto_capturada), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.fabAddServicio.setOnClickListener {
            showServicioDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = ServicioAdapter(
            onEditClick = { servicio -> showServicioDialog(servicio) },
            onDeleteClick = { servicio -> showDeleteConfirmationDialog(servicio) }
        )
        binding.rvServicios.layoutManager = LinearLayoutManager(this)
        binding.rvServicios.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { query, _, _, _ ->
            filterList(query?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.allVehiculos.observe(this) { vehiculos ->
            vehiculoList = vehiculos.orEmpty()
            vehiculoMap = vehiculoList.associate { it.id to "${it.marca} ${it.modelo} (${it.placa})" }
            adapter.updateVehiculos(vehiculoMap)
            filterList(binding.etSearch.text?.toString().orEmpty())
        }

        viewModel.allServicios.observe(this) { servicios ->
            fullList = servicios.orEmpty()
            filterList(binding.etSearch.text?.toString().orEmpty())
        }
    }

    private fun filterList(query: String) {
        val filtered = if (query.isBlank()) {
            fullList
        } else {
            val q = query.trim().lowercase()
            fullList.filter { s ->
                val vehiculoInfo = vehiculoMap[s.vehiculoId]?.lowercase().orEmpty()
                s.descripcion.lowercase().contains(q) ||
                s.fecha.lowercase().contains(q) ||
                vehiculoInfo.contains(q)
            }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvServicios.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvServicios.visibility = View.VISIBLE
        }
    }

    private fun showServicioDialog(servicio: Servicio?) {
        if (vehiculoList.isEmpty()) {
            Toast.makeText(this, getString(R.string.sin_vehiculos), Toast.LENGTH_LONG).show()
            return
        }

        val dialogBinding = DialogServicioBinding.inflate(layoutInflater)
        activeDialogBinding = dialogBinding
        currentFotoUri = servicio?.fotoDanoUri

        val vehiculoNombres = vehiculoList.map { "${it.marca} ${it.modelo} (${it.placa})" }
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            vehiculoNombres
        )
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        dialogBinding.spVehiculos.adapter = spinnerAdapter

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

        dialogBinding.btnTomarFoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        if (servicio != null) {
            dialogBinding.tvDialogTitle.setText(R.string.editar_servicio)
            dialogBinding.etDescripcion.setText(servicio.descripcion)
            dialogBinding.etFecha.setText(servicio.fecha)

            val selectedIndex = vehiculoList.indexOfFirst { it.id == servicio.vehiculoId }
            if (selectedIndex >= 0) {
                dialogBinding.spVehiculos.setSelection(selectedIndex)
            }

            if (!servicio.fotoDanoUri.isNullOrEmpty()) {
                try {
                    dialogBinding.ivPreviewFoto.setImageURI(Uri.parse(servicio.fotoDanoUri))
                    dialogBinding.ivPreviewFoto.imageTintList = null
                } catch (e: Exception) {
                    // Fallback
                }
            }
        } else {
            dialogBinding.tvDialogTitle.setText(R.string.nuevo_servicio)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { dialog, _ ->
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                val fecha = dialogBinding.etFecha.text.toString().trim()
                val selectedPosition = dialogBinding.spVehiculos.selectedItemPosition

                if (descripcion.isEmpty() || fecha.isEmpty() || selectedPosition < 0) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedVehiculoId = vehiculoList[selectedPosition].id

                if (servicio != null) {
                    val updatedServicio = servicio.copy(
                        vehiculoId = selectedVehiculoId,
                        descripcion = descripcion,
                        fecha = fecha,
                        fotoDanoUri = currentFotoUri
                    )
                    viewModel.update(updatedServicio)
                    Toast.makeText(this, "Servicio actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    val newServicio = Servicio(
                        vehiculoId = selectedVehiculoId,
                        descripcion = descripcion,
                        fecha = fecha,
                        fotoDanoUri = currentFotoUri
                    )
                    viewModel.insert(newServicio)
                    Toast.makeText(this, "Servicio registrado", Toast.LENGTH_SHORT).show()
                }
                activeDialogBinding = null
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ ->
                activeDialogBinding = null
                dialog.dismiss()
            }
            .setOnCancelListener {
                activeDialogBinding = null
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(servicio: Servicio) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminar)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.delete(servicio)
                Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
