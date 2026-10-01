package com.example.automotriz_aplicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.automotriz_aplicacion.data.database.AppDatabase
import com.example.automotriz_aplicacion.databinding.ActivityMainBinding
import com.example.automotriz_aplicacion.ui.cita.CitasActivity
import com.example.automotriz_aplicacion.ui.cliente.ClientesActivity
import com.example.automotriz_aplicacion.ui.repuesto.RepuestosActivity
import com.example.automotriz_aplicacion.ui.servicio.ServiciosActivity
import com.example.automotriz_aplicacion.ui.vehiculo.VehiculosActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificación e inserción inicial automática de datos de prueba
        checkAndSeedDemoData()

        binding.cardClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        binding.cardVehiculos.setOnClickListener {
            startActivity(Intent(this, VehiculosActivity::class.java))
        }

        binding.cardServicios.setOnClickListener {
            startActivity(Intent(this, ServiciosActivity::class.java))
        }

        binding.cardRepuestos.setOnClickListener {
            startActivity(Intent(this, RepuestosActivity::class.java))
        }

        binding.cardCitas.setOnClickListener {
            startActivity(Intent(this, CitasActivity::class.java))
        }

        binding.btnCargarDatosPrueba.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                AppDatabase.populateDemoData(db)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Datos de prueba cargados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkAndSeedDemoData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val currentClientes = db.clienteDao().getAllClientes().first()
            if (currentClientes.isEmpty()) {
                AppDatabase.populateDemoData(db)
            }
        }
    }
}
