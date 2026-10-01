package com.example.automotriz_aplicacion.data.repository

import com.example.automotriz_aplicacion.data.dao.ServicioDao
import com.example.automotriz_aplicacion.data.model.Servicio
import kotlinx.coroutines.flow.Flow

class ServicioRepository(private val servicioDao: ServicioDao) {
    val allServicios: Flow<List<Servicio>> = servicioDao.getAllServicios()

    suspend fun insert(servicio: Servicio): Long = servicioDao.insert(servicio)

    suspend fun update(servicio: Servicio) = servicioDao.update(servicio)

    suspend fun delete(servicio: Servicio) = servicioDao.delete(servicio)

    fun getServiciosByVehiculo(vehiculoId: Int): Flow<List<Servicio>> = servicioDao.getServiciosByVehiculo(vehiculoId)

    fun getServicioById(id: Int): Flow<Servicio?> = servicioDao.getServicioById(id)
}
