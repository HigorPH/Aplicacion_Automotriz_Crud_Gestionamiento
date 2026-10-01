package com.example.automotriz_aplicacion.data.repository

import com.example.automotriz_aplicacion.data.dao.CitaDao
import com.example.automotriz_aplicacion.data.model.Cita
import kotlinx.coroutines.flow.Flow

class CitaRepository(private val citaDao: CitaDao) {
    val allCitas: Flow<List<Cita>> = citaDao.getAllCitas()

    suspend fun insert(cita: Cita): Long = citaDao.insert(cita)

    suspend fun update(cita: Cita) = citaDao.update(cita)

    suspend fun delete(cita: Cita) = citaDao.delete(cita)

    fun getCitasByCliente(clienteId: Int): Flow<List<Cita>> = citaDao.getCitasByCliente(clienteId)

    fun getCitaById(id: Int): Flow<Cita?> = citaDao.getCitaById(id)
}
