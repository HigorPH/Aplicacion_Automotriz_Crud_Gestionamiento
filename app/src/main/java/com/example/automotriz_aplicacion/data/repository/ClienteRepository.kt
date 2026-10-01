package com.example.automotriz_aplicacion.data.repository

import com.example.automotriz_aplicacion.data.dao.ClienteDao
import com.example.automotriz_aplicacion.data.model.Cliente
import kotlinx.coroutines.flow.Flow

class ClienteRepository(private val clienteDao: ClienteDao) {
    val allClientes: Flow<List<Cliente>> = clienteDao.getAllClientes()

    suspend fun insert(cliente: Cliente): Long = clienteDao.insert(cliente)

    suspend fun update(cliente: Cliente) = clienteDao.update(cliente)

    suspend fun delete(cliente: Cliente) = clienteDao.delete(cliente)

    fun getClienteById(id: Int): Flow<Cliente?> = clienteDao.getClienteById(id)
}
