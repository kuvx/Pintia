package com.example.pintia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pintia.components.Header
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

class RequestVisitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_visit)
        val header = findViewById<Header>(R.id.header)
        header.title = getString(R.string.request)

        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        // Configura un listener para cuando se haga clic en el campo de fecha
        etDate.setOnClickListener {
            // Obtiene la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Muestra el DatePickerDialog
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Cuando el usuario selecciona la fecha, actualiza el campo de texto
                val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                etDate.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Configura un listener para cuando se haga clic en el campo de fecha
        etTime.setOnClickListener {
            // Obtiene la fecha actual
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // Muestra el DatePickerDialog
            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Cuando el usuario selecciona la fecha, actualiza el campo de texto
                val formattedTime = String.format("%02d:%02d", selectedHour,selectedMinute)
                    // "${selectedHour}:${selectedMinute}"
                etTime.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        btnSubmit.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && date.isNotEmpty()) {

                Toast.makeText(this, "Visita solicitada exitosamente", Toast.LENGTH_SHORT).show()

                etName.text.clear()
                etEmail.text.clear()
                etPhone.text.clear()
                etDate.text.clear()
            } else {

                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



