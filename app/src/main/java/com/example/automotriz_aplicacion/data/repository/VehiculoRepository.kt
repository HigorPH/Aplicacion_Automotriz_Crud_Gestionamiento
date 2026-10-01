package com.example.automotriz_aplicacion.data.repository

import com.example.automotriz_aplicacion.data.dao.VehiculoDao
import com.example.automotriz_aplicacion.data.model.Vehiculo
import kotlinx.coroutines.flow.Flow

class VehiculoRepository(private val vehiculoDao: VehiculoDao) {
    val allVehiculos: Flow<List<Vehiculo>> = vehiculoDao.getAllVehiculos()

    suspend fun insert(vehiculo: Vehiculo): Long = vehiculoDao.insert(vehiculo)

    suspend fun update(vehiculo: Vehiculo) = vehiculoDao.update(vehiculo)

    suspend fun delete(vehiculo: Vehiculo) = vehiculoDao.delete(vehiculo)

    fun getVehiculosByCliente(clienteId: Int): Flow<List<Vehiculo>> = vehiculoDao.getVehiculosByCliente(clienteId)

    fun getVehiculoById(id: Int): Flow<Vehiculo?> = vehiculoDao.getVehiculoById(id)
}
