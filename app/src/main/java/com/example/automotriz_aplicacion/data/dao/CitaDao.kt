package com.example.automotriz_aplicacion.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.automotriz_aplicacion.data.model.Cita
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cita: Cita): Long

    @Update
    suspend fun update(cita: Cita)

    @Delete
    suspend fun delete(cita: Cita)

    @Query("SELECT * FROM citas ORDER BY fecha ASC, hora ASC")
    fun getAllCitas(): Flow<List<Cita>>

    @Query("SELECT * FROM citas WHERE clienteId = :clienteId")
    fun getCitasByCliente(clienteId: Int): Flow<List<Cita>>

    @Query("SELECT * FROM citas WHERE id = :id")
    fun getCitaById(id: Int): Flow<Cita?>
}
