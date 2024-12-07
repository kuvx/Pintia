package com.example.pintia

import EMAIL_DEST
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pintia.components.Header
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class RequestVisitFragment : Fragment() {
    private fun getMain(): MainActivity {
        return requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_request_visit, container, false)

        val context = requireContext()

        val header = getMain().findViewById<Header>(R.id.header)
        header.title = getString(R.string.request)

        val etName = rootView.findViewById<EditText>(R.id.et_name)
        val etGroupSize = rootView.findViewById<Spinner>(R.id.et_group_size)

        // Establecer valores al spinner
        val hint = getString(R.string.visit_request_group_size_hint)
        val numberList: List<Any> =
            listOf(hint) + (3..7).toMutableList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, numberList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etGroupSize.adapter = adapter

        val etPhone = rootView.findViewById<EditText>(R.id.et_phone)
        val etDate = rootView.findViewById<EditText>(R.id.et_date)
        val etTime = rootView.findViewById<EditText>(R.id.et_time)
        val btnSubmit = rootView.findViewById<Button>(R.id.btn_submit)

        // Configura un listener para cuando se haga clic en el campo de fecha
        etDate.setOnClickListener {
            // Obtiene la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Muestra el DatePickerDialog
            val datePickerDialog =
                DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
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
            val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                // Cuando el usuario selecciona la fecha, actualiza el campo de texto
                val formattedTime =
                    String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                etTime.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        btnSubmit.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()

            val body = genBody(name, etGroupSize, phone, date, time)

            if (body.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    openEmailClient(body);
                }

                Toast.makeText(context, "Visita solicitada exitosamente", Toast.LENGTH_SHORT).show()

                etName.text.clear()
                etGroupSize.setSelection(0)
                etPhone.text.clear()
                etDate.text.clear()
            } else {
                Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return rootView
    }

    private fun genBody(
        name: String,
        etGroupSize: Spinner,
        phone: String,
        date: String,
        time: String
    ): String {
        if (name.isNotEmpty() && etGroupSize.selectedItemPosition > 0 && phone.isNotEmpty() && date.isNotEmpty()) {
            val fecha = if (time.isNotEmpty()) " a las ${time}h" else ""
            return "¡Hola Carlos!\n\n" +
                    "Soy $name y estoy interesado en hacer una visita por pintia.\n" +
                    "Somos un grupo compuesto por ${etGroupSize.selectedItem} personas, la visita" +
                    " nos gustaría que fuese para el día $date$fecha.\n\n" +
                    "También, nos gustaría conocer el precio de la visita.\n\n" +
                    "Quedamos pendientes de tu confirmación o contrapropuesta.\n\n" +
                    "Podemos continuar a través de este correo o a través del teléfono $phone.\n\n" +
                    "Atentamente $name."
        } else {
            return ""
        }
    }

    private fun openEmailClient(body: String) {
        val subject = "Solicitud de visita a Pintia"
        val recipient = EMAIL_DEST

        // Crear el URI para el correo con los parámetros
        val emailUri =
            Uri.parse("mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")

        // Crear el Intent
        val emailIntent = Intent(Intent.ACTION_SENDTO, emailUri)

        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No hay cliente de correo instalado", Toast.LENGTH_SHORT).show()
        }
    }

}



