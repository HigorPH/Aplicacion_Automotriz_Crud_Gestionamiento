package com.example.automotriz_aplicacion.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.automotriz_aplicacion.data.model.Repuesto
import kotlinx.coroutines.flow.Flow

@Dao
interface RepuestoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repuesto: Repuesto): Long

    @Update
    suspend fun update(repuesto: Repuesto)

    @Delete
    suspend fun delete(repuesto: Repuesto)

    @Query("SELECT * FROM repuestos ORDER BY nombre ASC")
    fun getAllRepuestos(): Flow<List<Repuesto>>

    @Query("SELECT * FROM repuestos WHERE codigoQR = :codigoQR LIMIT 1")
    suspend fun getRepuestoByCodigo(codigoQR: String): Repuesto?

    @Query("SELECT * FROM repuestos WHERE id = :id")
    fun getRepuestoById(id: Int): Flow<Repuesto?>
}
