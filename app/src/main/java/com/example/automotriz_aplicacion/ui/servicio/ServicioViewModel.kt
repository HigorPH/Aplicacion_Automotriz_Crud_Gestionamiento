package com.example.automotriz_aplicacion.ui.servicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.data.model.Servicio
import com.example.automotriz_aplicacion.data.model.Vehiculo
import com.example.automotriz_aplicacion.data.repository.ServicioRepository
import com.example.automotriz_aplicacion.data.repository.VehiculoRepository
import kotlinx.coroutines.launch

class ServicioViewModel(application: Application) : AndroidViewModel(application) {

    private val servicioRepository: ServicioRepository
    private val vehiculoRepository: VehiculoRepository

    val allServicios: LiveData<List<Servicio>>
    val allVehiculos: LiveData<List<Vehiculo>>

    init {
        val db = AppDatabase.getDatabase(application)
        servicioRepository = ServicioRepository(db.servicioDao())
        vehiculoRepository = VehiculoRepository(db.vehiculoDao())

        allServicios = servicioRepository.allServicios.asLiveData()
        allVehiculos = vehiculoRepository.allVehiculos.asLiveData()
    }

    fun insert(servicio: Servicio) = viewModelScope.launch {
        servicioRepository.insert(servicio)
    }

    fun update(servicio: Servicio) = viewModelScope.launch {
        servicioRepository.update(servicio)
    }

    fun delete(servicio: Servicio) = viewModelScope.launch {
        servicioRepository.delete(servicio)
    }
}
