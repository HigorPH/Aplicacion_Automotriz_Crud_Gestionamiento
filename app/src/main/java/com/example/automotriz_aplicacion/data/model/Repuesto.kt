package com.example.automotriz_aplicacion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repuestos")
data class Repuesto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigoQR: String,
    val nombre: String,
    val stock: Int
)
