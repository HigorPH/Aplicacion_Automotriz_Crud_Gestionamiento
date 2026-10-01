package com.example.automotriz_aplicacion.data.repository

import com.example.automotriz_aplicacion.data.dao.RepuestoDao
import com.example.automotriz_aplicacion.data.model.Repuesto
import kotlinx.coroutines.flow.Flow

class RepuestoRepository(private val repuestoDao: RepuestoDao) {
    val allRepuestos: Flow<List<Repuesto>> = repuestoDao.getAllRepuestos()

    suspend fun insert(repuesto: Repuesto): Long = repuestoDao.insert(repuesto)

    suspend fun update(repuesto: Repuesto) = repuestoDao.update(repuesto)

    suspend fun delete(repuesto: Repuesto) = repuestoDao.delete(repuesto)

    suspend fun getRepuestoByCodigo(codigoQR: String): Repuesto? = repuestoDao.getRepuestoByCodigo(codigoQR)

    fun getRepuestoById(id: Int): Flow<Repuesto?> = repuestoDao.getRepuestoById(id)
}
