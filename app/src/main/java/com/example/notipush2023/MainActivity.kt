package com.example.notipush2023

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var btnNotifi : Button
    var idNotificacion = 1

    lateinit var botonRegistro: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val campoCorreo: EditText = findViewById(R.id.editTextMain)
        val campoPassword: EditText = findViewById(R.id.editTextPasswordMain)
        val btnIngresar: Button = findViewById(R.id.buttonIngresarMain)
        botonRegistro = findViewById(R.id.buttonRegistroMain)

        btnIngresar.setOnClickListener {
            val correo = campoCorreo.text.toString()
            val contraseña = campoPassword.text.toString()

            val mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // El inicio de sesión fue exitoso
                        // Aquí puedes realizar la acción adicional que deseas
                        Toast.makeText(this,"si existe el correo entre", Toast.LENGTH_LONG).show()
                    } else {
                        // El inicio de sesión falló
                        if (task.exception is FirebaseAuthInvalidUserException) {
                            // No existe un usuario con ese correo en la base de datos
                            Toast.makeText(this,"No existe ese correo",Toast.LENGTH_LONG).show()
                        } else {
                            // Ocurrió un error desconocido
                            Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

        botonRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }




    //TODO visto en clase
        /*//definir un canal - mostrar notificación
        crearCanal()
        //definir la notificacion - datos
        //mostrarla en el click
        btnNotifi =  findViewById(R.id.btnMostrar)
        btnNotifi.setOnClickListener {
            mostrarNOtificacion("hola","becas")
        }*/

    }

    private fun mostrarNOtificacion(titulo:String,cuerpo:String) {
        val construirNotificacion = NotificationCompat.Builder(this,"1")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //mostrar la notificacion
        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(idNotificacion,construirNotificacion.build())
            idNotificacion++
        }
    }

    private fun crearCanal() {
        //implementacion
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val nombreCanal = "Mi Canal"
            val descripcionCanal = "Este canal es para notificaciones"
            val importanciaCanal = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("1",nombreCanal,importanciaCanal).apply {
                description = descripcionCanal
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}