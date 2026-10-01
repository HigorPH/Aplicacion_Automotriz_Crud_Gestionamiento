package com.example.automotriz_aplicacion.ui.cita

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.data.model.Cita
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.data.repository.CitaRepository
import com.example.automotriz_aplicacion.data.repository.ClienteRepository
import kotlinx.coroutines.launch

class CitaViewModel(application: Application) : AndroidViewModel(application) {

    private val citaRepository: CitaRepository
    private val clienteRepository: ClienteRepository

    val allCitas: LiveData<List<Cita>>
    val allClientes: LiveData<List<Cliente>>

    init {
        val db = AppDatabase.getDatabase(application)
        citaRepository = CitaRepository(db.citaDao())
        clienteRepository = ClienteRepository(db.clienteDao())

        allCitas = citaRepository.allCitas.asLiveData()
        allClientes = clienteRepository.allClientes.asLiveData()
    }

    fun insert(cita: Cita) = viewModelScope.launch {
        citaRepository.insert(cita)
    }

    fun update(cita: Cita) = viewModelScope.launch {
        citaRepository.update(cita)
    }

    fun delete(cita: Cita) = viewModelScope.launch {
        citaRepository.delete(cita)
    }
}
