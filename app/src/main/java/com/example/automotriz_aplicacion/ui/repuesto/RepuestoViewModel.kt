package com.example.automotriz_aplicacion.ui.repuesto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.data.model.Repuesto
import com.example.automotriz_aplicacion.data.repository.RepuestoRepository
import kotlinx.coroutines.launch

class RepuestoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RepuestoRepository
    val allRepuestos: LiveData<List<Repuesto>>

    init {
        val repuestoDao = AppDatabase.getDatabase(application).repuestoDao()
        repository = RepuestoRepository(repuestoDao)
        allRepuestos = repository.allRepuestos.asLiveData()
    }

    fun insert(repuesto: Repuesto) = viewModelScope.launch {
        repository.insert(repuesto)
    }

    fun update(repuesto: Repuesto) = viewModelScope.launch {
        repository.update(repuesto)
    }

    fun delete(repuesto: Repuesto) = viewModelScope.launch {
        repository.delete(repuesto)
    }
}
