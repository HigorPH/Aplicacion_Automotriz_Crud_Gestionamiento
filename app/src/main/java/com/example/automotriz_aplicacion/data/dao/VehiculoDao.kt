package com.example.automotriz_aplicacion.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.automotriz_aplicacion.data.model.Vehiculo
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehiculo: Vehiculo): Long

    @Update
    suspend fun update(vehiculo: Vehiculo)

    @Delete
    suspend fun delete(vehiculo: Vehiculo)

    @Query("SELECT * FROM vehiculos ORDER BY placa ASC")
    fun getAllVehiculos(): Flow<List<Vehiculo>>

    @Query("SELECT * FROM vehiculos WHERE clienteId = :clienteId")
    fun getVehiculosByCliente(clienteId: Int): Flow<List<Vehiculo>>

    @Query("SELECT * FROM vehiculos WHERE id = :id")
    fun getVehiculoById(id: Int): Flow<Vehiculo?>
}
