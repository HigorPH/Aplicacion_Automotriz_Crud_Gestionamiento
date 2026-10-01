package com.example.automotriz_aplicacion.ui.vehiculo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.data.model.Vehiculo
import com.example.automotriz_aplicacion.data.repository.ClienteRepository
import com.example.automotriz_aplicacion.data.repository.VehiculoRepository
import kotlinx.coroutines.launch

class VehiculoViewModel(application: Application) : AndroidViewModel(application) {

    private val vehiculoRepository: VehiculoRepository
    private val clienteRepository: ClienteRepository

    val allVehiculos: LiveData<List<Vehiculo>>
    val allClientes: LiveData<List<Cliente>>

    init {
        val db = AppDatabase.getDatabase(application)
        vehiculoRepository = VehiculoRepository(db.vehiculoDao())
        clienteRepository = ClienteRepository(db.clienteDao())

        allVehiculos = vehiculoRepository.allVehiculos.asLiveData()
        allClientes = clienteRepository.allClientes.asLiveData()
    }

    fun insert(vehiculo: Vehiculo) = viewModelScope.launch {
        vehiculoRepository.insert(vehiculo)
    }

    fun update(vehiculo: Vehiculo) = viewModelScope.launch {
        vehiculoRepository.update(vehiculo)
    }

    fun delete(vehiculo: Vehiculo) = viewModelScope.launch {
        vehiculoRepository.delete(vehiculo)
    }
}
