package com.example.automotriz_aplicacion.ui.cliente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.data.repository.ClienteRepository
import kotlinx.coroutines.launch

class ClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClienteRepository
    val allClientes: LiveData<List<Cliente>>

    init {
        val clienteDao = AppDatabase.getDatabase(application).clienteDao()
        repository = ClienteRepository(clienteDao)
        allClientes = repository.allClientes.asLiveData()
    }

    fun insert(cliente: Cliente) = viewModelScope.launch {
        repository.insert(cliente)
    }

    fun update(cliente: Cliente) = viewModelScope.launch {
        repository.update(cliente)
    }

    fun delete(cliente: Cliente) = viewModelScope.launch {
        repository.delete(cliente)
    }
}
