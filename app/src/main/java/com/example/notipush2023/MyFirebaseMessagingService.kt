package com.example.notipush2023

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService(){

    var idNotificacion = 1

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //Podemos recuperar los mensajes que se manden desde la consola de Firebase o desde mi servidor (app Node.js)
        // Revisar si el msj trae datos adicionales
        if (message.data.isNotEmpty()) {
            Log.d("MENSAJE", "Mensaje con datos adicionales: ${message.data}")
        }
        // Revisar si es una notificacion
        message.notification?.let {

            mostrarNOtificacion(it.title.toString(),it.body.toString())
            Log.d("NOTIFICACION", "Mensaje Notificacion Titulo: ${it.body}")
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //Cuando se instala por primera vez
        // Guardar el token en SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("firebaseToken", token)
        editor.apply()

        enviarTokenAMiServidor(token)
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


    private fun enviarTokenAMiServidor(token: String){
        Log.d("TOKEN", "Este es mi token: $token")

    }
}