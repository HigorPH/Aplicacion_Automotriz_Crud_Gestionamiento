package com.example.automotriz_aplicacion.ui.vehiculo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Vehiculo
import com.example.automotriz_aplicacion.databinding.ItemVehiculoBinding

class VehiculoAdapter(
    private val onEditClick: (Vehiculo) -> Unit,
    private val onDeleteClick: (Vehiculo) -> Unit
) : ListAdapter<Vehiculo, VehiculoAdapter.VehiculoViewHolder>(DiffCallback) {

    private var clienteMap: Map<Int, String> = emptyMap()

    fun updateClientes(map: Map<Int, String>) {
        this.clienteMap = map
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val binding = ItemVehiculoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VehiculoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val vehiculo = getItem(position)
        holder.bind(vehiculo)
    }

    inner class VehiculoViewHolder(private val binding: ItemVehiculoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vehiculo: Vehiculo) {
            val context = binding.root.context
            binding.tvPlaca.text = context.getString(R.string.placa_format, vehiculo.placa)
            binding.tvMarcaModelo.text = context.getString(
                R.string.marca_modelo_format,
                vehiculo.marca,
                vehiculo.modelo
            )

            val clienteNombre = clienteMap[vehiculo.clienteId] ?: "ID: ${vehiculo.clienteId}"
            binding.tvClienteNombre.text = context.getString(R.string.cliente_format, clienteNombre)

            binding.btnEdit.setOnClickListener { onEditClick(vehiculo) }
            binding.btnDelete.setOnClickListener { onDeleteClick(vehiculo) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Vehiculo>() {
        override fun areItemsTheSame(oldItem: Vehiculo, newItem: Vehiculo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vehiculo, newItem: Vehiculo): Boolean {
            return oldItem == newItem
        }
    }
}
