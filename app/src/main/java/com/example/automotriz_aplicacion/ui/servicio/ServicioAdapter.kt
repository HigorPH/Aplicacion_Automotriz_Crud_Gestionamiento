package com.example.automotriz_aplicacion.ui.servicio

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.automotriz_aplicacion.R
import com.example.automotriz_aplicacion.data.model.Servicio
import com.example.automotriz_aplicacion.databinding.ItemServicioBinding

class ServicioAdapter(
    private val onEditClick: (Servicio) -> Unit,
    private val onDeleteClick: (Servicio) -> Unit
) : ListAdapter<Servicio, ServicioAdapter.ServicioViewHolder>(DiffCallback) {

    private var vehiculoMap: Map<Int, String> = emptyMap()

    fun updateVehiculos(map: Map<Int, String>) {
        this.vehiculoMap = map
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val binding = ItemServicioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServicioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = getItem(position)
        holder.bind(servicio)
    }

    inner class ServicioViewHolder(private val binding: ItemServicioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(servicio: Servicio) {
            val context = binding.root.context
            binding.tvDescripcion.text = servicio.descripcion
            binding.tvFecha.text = context.getString(R.string.fecha_format, servicio.fecha)

            val vehiculoInfo = vehiculoMap[servicio.vehiculoId] ?: "ID: ${servicio.vehiculoId}"
            binding.tvVehiculoInfo.text = context.getString(R.string.vehiculo_format, vehiculoInfo)

            if (!servicio.fotoDanoUri.isNullOrEmpty()) {
                try {
                    binding.ivFotoDano.setImageURI(Uri.parse(servicio.fotoDanoUri))
                    binding.ivFotoDano.imageTintList = null
                } catch (e: Exception) {
                    binding.ivFotoDano.setImageResource(R.drawable.ic_servicios)
                    binding.ivFotoDano.setColorFilter(ContextCompat.getColor(context, R.color.card_servicios))
                }
            } else {
                binding.ivFotoDano.setImageResource(R.drawable.ic_servicios)
                binding.ivFotoDano.setColorFilter(ContextCompat.getColor(context, R.color.card_servicios))
            }

            binding.btnEdit.setOnClickListener { onEditClick(servicio) }
            binding.btnDelete.setOnClickListener { onDeleteClick(servicio) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Servicio>() {
        override fun areItemsTheSame(oldItem: Servicio, newItem: Servicio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Servicio, newItem: Servicio): Boolean {
            return oldItem == newItem
        }
    }
}
