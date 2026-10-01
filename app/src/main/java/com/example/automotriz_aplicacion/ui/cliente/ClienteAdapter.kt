package com.example.automotriz_aplicacion.ui.cliente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.databinding.ItemClienteBinding

class ClienteAdapter(
    private val onEditClick: (Cliente) -> Unit,
    private val onDeleteClick: (Cliente) -> Unit
) : ListAdapter<Cliente, ClienteAdapter.ClienteViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = getItem(position)
        holder.bind(cliente)
    }

    inner class ClienteViewHolder(private val binding: ItemClienteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Cliente) {
            val context = binding.root.context
            binding.tvNombre.text = cliente.nombre
            binding.tvDni.text = context.getString(R.string.dni_format, cliente.dni)
            binding.tvTelefono.text = context.getString(R.string.telefono_format, cliente.telefono)

            binding.btnEdit.setOnClickListener { onEditClick(cliente) }
            binding.btnDelete.setOnClickListener { onDeleteClick(cliente) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}
