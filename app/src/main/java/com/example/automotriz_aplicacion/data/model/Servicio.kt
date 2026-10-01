package com.example.automotriz_aplicacion.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "servicios",
    foreignKeys = [
        ForeignKey(
            entity = Vehiculo::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["vehiculoId"])]
)
data class Servicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehiculoId: Int,
    val descripcion: String,
    val fecha: String,
    val fotoDanoUri: String? = null
)
