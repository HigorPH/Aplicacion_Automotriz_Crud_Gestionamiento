package com.example.automotriz_aplicacion.ui.cita

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Cita
import com.example.automotriz_aplicacion.databinding.ItemCitaBinding

class CitaAdapter(
    private val onEditClick: (Cita) -> Unit,
    private val onDeleteClick: (Cita) -> Unit
) : ListAdapter<Cita, CitaAdapter.CitaViewHolder>(DiffCallback) {

    private var clienteMap: Map<Int, String> = emptyMap()

    fun updateClientes(map: Map<Int, String>) {
        this.clienteMap = map
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = getItem(position)
        holder.bind(cita)
    }

    inner class CitaViewHolder(private val binding: ItemCitaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cita: Cita) {
            val context = binding.root.context
            binding.tvFechaHora.text = context.getString(
                R.string.fecha_hora_format,
                cita.fecha,
                cita.hora
            )

            val clienteNombre = clienteMap[cita.clienteId] ?: "ID: ${cita.clienteId}"
            binding.tvClienteNombre.text = context.getString(R.string.cliente_format, clienteNombre)

            binding.btnEdit.setOnClickListener { onEditClick(cita) }
            binding.btnDelete.setOnClickListener { onDeleteClick(cita) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Cita>() {
        override fun areItemsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem == newItem
        }
    }
}
