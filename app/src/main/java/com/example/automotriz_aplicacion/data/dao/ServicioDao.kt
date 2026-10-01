package com.example.automotriz_aplicacion.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.automotriz_aplicacion.data.model.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(servicio: Servicio): Long

    @Update
    suspend fun update(servicio: Servicio)

    @Delete
    suspend fun delete(servicio: Servicio)

    @Query("SELECT * FROM servicios ORDER BY id DESC")
    fun getAllServicios(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE vehiculoId = :vehiculoId")
    fun getServiciosByVehiculo(vehiculoId: Int): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE id = :id")
    fun getServicioById(id: Int): Flow<Servicio?>
}
