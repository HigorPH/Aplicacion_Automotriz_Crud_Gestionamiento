package com.example.automotriz_aplicacion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.automotriz_aplicacion.data.dao.CitaDao
import com.example.automotriz_aplicacion.data.dao.ClienteDao
import com.example.automotriz_aplicacion.data.dao.RepuestoDao
import com.example.automotriz_aplicacion.data.dao.ServicioDao
import com.example.automotriz_aplicacion.data.dao.VehiculoDao
import com.example.automotriz_aplicacion.data.model.Cita
import com.example.automotriz_aplicacion.data.model.Cliente
import com.example.automotriz_aplicacion.data.model.Repuesto
import com.example.automotriz_aplicacion.data.model.Servicio
import com.example.automotriz_aplicacion.data.model.Vehiculo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Cliente::class,
        Vehiculo::class,
        Servicio::class,
        Repuesto::class,
        Cita::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun servicioDao(): ServicioDao
    abstract fun repuestoDao(): RepuestoDao
    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taller_automotriz_db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDemoData(database)
                    }
                }
            }
        }

        suspend fun populateDemoData(database: AppDatabase) {
            val clienteDao = database.clienteDao()
            val vehiculoDao = database.vehiculoDao()
            val servicioDao = database.servicioDao()
            val repuestoDao = database.repuestoDao()
            val citaDao = database.citaDao()

            // 1. Clientes
            val c1Id = clienteDao.insert(Cliente(nombre = "Juan Pérez", dni = "12345678", telefono = "987654321")).toInt()
            val c2Id = clienteDao.insert(Cliente(nombre = "María Rodríguez", dni = "87654321", telefono = "912345678")).toInt()
            val c3Id = clienteDao.insert(Cliente(nombre = "Carlos Gómez", dni = "45678912", telefono = "955443322")).toInt()

            // 2. Vehículos
            val v1Id = vehiculoDao.insert(Vehiculo(placa = "ABC-123", marca = "Toyota", modelo = "Corolla", clienteId = c1Id)).toInt()
            val v2Id = vehiculoDao.insert(Vehiculo(placa = "XYZ-789", marca = "Nissan", modelo = "Sentra", clienteId = c2Id)).toInt()
            vehiculoDao.insert(Vehiculo(placa = "MNO-456", marca = "Honda", modelo = "Civic", clienteId = c3Id))

            // 3. Servicios
            servicioDao.insert(Servicio(vehiculoId = v1Id, descripcion = "Mantenimiento preventivo y cambio de aceite", fecha = "15/05/2025"))
            servicioDao.insert(Servicio(vehiculoId = v2Id, descripcion = "Revisión del sistema de frenos y amortiguadores", fecha = "18/05/2025"))

            // 4. Repuestos
            repuestoDao.insert(Repuesto(codigoQR = "REP-QR-1001", nombre = "Filtro de Aceite Sintético", stock = 25))
            repuestoDao.insert(Repuesto(codigoQR = "REP-QR-1002", nombre = "Pastillas de Freno Delanteras", stock = 10))
            repuestoDao.insert(Repuesto(codigoQR = "REP-QR-1003", nombre = "Bujías Iridium de Alto Rendimiento", stock = 30))

            // 5. Citas
            citaDao.insert(Cita(fecha = "25/05/2025", hora = "09:00", clienteId = c1Id))
            citaDao.insert(Cita(fecha = "26/05/2025", hora = "14:30", clienteId = c2Id))
        }
    }
}
