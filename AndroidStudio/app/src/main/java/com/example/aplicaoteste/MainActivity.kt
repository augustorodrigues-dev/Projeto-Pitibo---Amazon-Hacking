package com.example.aplicaoteste

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var usuarioEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var confirmarSenhaEditText: EditText

    private lateinit var errorTextEmail: TextView
    private lateinit var errorTextSenha: TextView
    private lateinit var errorTextConfirmarSenha: TextView

    private lateinit var botaoCadastro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        usuarioEditText = findViewById(R.id.usuarioEditText)
        emailEditText = findViewById(R.id.emailEditText)
        senhaEditText = findViewById(R.id.senhaEditText)
        confirmarSenhaEditText = findViewById(R.id.confirmarSenhaEditText)

        errorTextEmail = findViewById(R.id.errorTextEmail)
        errorTextSenha = findViewById(R.id.errorTextSenha)
        errorTextConfirmarSenha = findViewById(R.id.errorTextConfirmarSenha)

        botaoCadastro = findViewById(R.id.botaoCadastro)

        botaoCadastro.setOnClickListener {
            if (validarCampos()) {
                val intent = Intent(this, BluetoothActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        val email = emailEditText.text.toString()
        if (!email.contains("@") || !email.contains(".")) {
            errorTextEmail.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextEmail.visibility = View.GONE
        }


        val senha = senhaEditText.text.toString()
        if (senha.length < 6) {
            errorTextSenha.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextSenha.visibility = View.GONE
        }


        val confirmarSenha = confirmarSenhaEditText.text.toString()
        if (senha != confirmarSenha) {
            errorTextConfirmarSenha.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextConfirmarSenha.visibility = View.GONE
        }

        return isValid
    }
}
