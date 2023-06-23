package com.example.notipush2023

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class Registro : AppCompatActivity() {

    val topicList: MutableList<String> = mutableListOf()

    val collections = mutableListOf<String>()
    lateinit var etCorreo: EditText
    lateinit var etPassword: EditText
    lateinit var seleccionarGrupo: EditText

    lateinit var tokenRegistro: String

    lateinit var refBaseDatos: DatabaseReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("firebaseToken", "")

        tokenRegistro = token.toString()

        // Obtén el token de registro del dispositivo del usuario
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

                // Guarda el token en tu base de datos o en otro lugar según tus necesidades
                // y luego regístralo en los temas deseados
                subscribeToTopics(tokenRegistro)
                Toast.makeText(this,"Ya cree los topics",Toast.LENGTH_LONG).show()
            } else {
                // Manejar la obtención del token fallida
            }
        }

        // Obtén los temas de Firestore y guárdalos en la lista
        val firestore = FirebaseFirestore.getInstance()
        val topicsCollection = firestore.collection("topics")

        topicsCollection.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val topic = document.id
                topicList.add(topic)
                Toast.makeText(this,topicList.toString(),Toast.LENGTH_LONG).show()
            }

            // Aquí puedes utilizar la lista de temas (topicList) como desees
        }.addOnFailureListener { exception ->
            // Manejar el error al obtener los temas desde Firestore
        }


        Toast.makeText(this,topicList.toString(),Toast.LENGTH_LONG).show()
        val butoonnn: Button = findViewById(R.id.buttonRegistroR)

        seleccionarGrupo = findViewById(R.id.spinnerColecciones)
        etCorreo = findViewById(R.id.editTextR)
        etPassword = findViewById(R.id.editTextPasswordR)


        butoonnn.setOnClickListener {
            guardarUsuario()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val database = FirebaseDatabase.getInstance()

        val databaseRef = database.reference
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (childSnapshot in snapshot.children) {
                    val collectionName = childSnapshot.key
                    collectionName?.let { collections.add(it) }
                }

                // Lógica para mostrar las colecciones en el Spinner
                // Puedes utilizar el adapter del Spinner para mostrar las colecciones
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }

        })

        seleccionarGrupo.isFocusable = false
        seleccionarGrupo.isFocusableInTouchMode = false
        seleccionarGrupo.inputType = InputType.TYPE_NULL

        seleccionarGrupo.setOnClickListener {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, collections)
            val listView = ListView(this)
            listView.adapter = adapter

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Selecciona al grupo que quieras entrar:")
            builder.setView(listView)

            val dialog = builder.create()
            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val opcionSeleccionada = collections[position]
                seleccionarGrupo.setText(opcionSeleccionada)
                refBaseDatos= FirebaseDatabase.getInstance().getReference(opcionSeleccionada)
                dialog.dismiss() // Cerrar el diálogo después de seleccionar una opción
            }

            dialog.show()
        }
    }

    private fun subscribeToTopics(token: String?) {
        val topic1 = "Grupo TT1"
        val topic2 = "Grupo TT2"
        val topic3 = "Becas institucionales"
        val topic4 = "Grupo de programación"

        token?.let {
            FirebaseMessaging.getInstance().subscribeToTopic(topic1)
            FirebaseMessaging.getInstance().subscribeToTopic(topic2)
            FirebaseMessaging.getInstance().subscribeToTopic(topic3)
            FirebaseMessaging.getInstance().subscribeToTopic(topic4)
        }
    }

    private fun guardarUsuario() {

        val correo = etCorreo.text.toString()
        val password = etPassword.text.toString()
//        val grupo = seleccionarGrupo.text.toString()
        //hijo
        val idUsuario = refBaseDatos.push().key!!
        //intancia
        val usuario = Usuario(idUsuario,correo,password,tokenRegistro)

        refBaseDatos.child(idUsuario).setValue(usuario)
            .addOnCompleteListener {
                Toast.makeText(this,"El registro se guardo en la nube",Toast.LENGTH_LONG).show()
            }

    }

    data class Usuario(val id: String?=null,val correo: String?=null,val password: String?=null,val token: String?=null)


}