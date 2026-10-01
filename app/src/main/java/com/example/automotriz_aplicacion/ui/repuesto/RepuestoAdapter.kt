package com.example.automotriz_aplicacion.ui.repuesto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Repuesto
import com.example.automotriz_aplicacion.databinding.ItemRepuestoBinding

class RepuestoAdapter(
    private val onEditClick: (Repuesto) -> Unit,
    private val onDeleteClick: (Repuesto) -> Unit
) : ListAdapter<Repuesto, RepuestoAdapter.RepuestoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepuestoViewHolder {
        val binding = ItemRepuestoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepuestoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepuestoViewHolder, position: Int) {
        val repuesto = getItem(position)
        holder.bind(repuesto)
    }

    inner class RepuestoViewHolder(private val binding: ItemRepuestoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repuesto: Repuesto) {
            val context = binding.root.context
            binding.tvNombreRepuesto.text = repuesto.nombre
            binding.tvCodigoQR.text = context.getString(R.string.codigo_qr_format, repuesto.codigoQR)
            binding.tvStock.text = context.getString(R.string.stock_format, repuesto.stock)

            binding.btnEdit.setOnClickListener { onEditClick(repuesto) }
            binding.btnDelete.setOnClickListener { onDeleteClick(repuesto) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Repuesto>() {
        override fun areItemsTheSame(oldItem: Repuesto, newItem: Repuesto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Repuesto, newItem: Repuesto): Boolean {
            return oldItem == newItem
        }
    }
}
