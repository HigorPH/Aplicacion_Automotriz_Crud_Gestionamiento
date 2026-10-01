package com.example.automotriz_aplicacion.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehiculos",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clienteId"])]
)
data class Vehiculo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val marca: String,
    val modelo: String,
    val clienteId: Int
)
