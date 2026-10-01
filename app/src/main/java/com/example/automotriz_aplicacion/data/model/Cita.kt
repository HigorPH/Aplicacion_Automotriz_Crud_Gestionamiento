package com.example.automotriz_aplicacion.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "citas",
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
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,
    val hora: String,
    val clienteId: Int
)
