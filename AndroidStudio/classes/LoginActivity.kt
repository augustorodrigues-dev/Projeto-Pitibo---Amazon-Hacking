package com.example.aplicaoteste

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var errorTextEmail: TextView
    private lateinit var errorTextSenha: TextView

    private lateinit var botaoLogin: Button
    private lateinit var botaoCadastro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        senhaEditText = findViewById(R.id.senhaEditText)
        errorTextEmail = findViewById(R.id.errorTextEmail)
        errorTextSenha = findViewById(R.id.errorTextSenha)

        botaoLogin = findViewById(R.id.botaoLogin)
        botaoCadastro = findViewById(R.id.botaoCadastro)

        botaoLogin.setOnClickListener {
            if (validarCampos()) {
                val email = emailEditText.text.toString()
                val senha = senhaEditText.text.toString()
                fazerLogin(email, senha)
            }
        }

        botaoCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        val email = emailEditText.text.toString().trim()
        val password = senhaEditText.text.toString()


        if (!email.contains("@") || !email.contains(".")) {
            errorTextEmail.text = "Formato de e-mail inválido."
            errorTextEmail.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextEmail.visibility = View.GONE
        }


        if (password.isEmpty()) {
            errorTextSenha.text = "Digite a senha."
            errorTextSenha.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextSenha.visibility = View.GONE
        }

        return isValid
    }

    private fun fazerLogin(email: String, senha: String) {
        val emailLimpo = email.trim().lowercase()

        auth.signInWithEmailAndPassword(emailLimpo, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BluetoothActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {

                            errorTextEmail.text = "Este e-mail não está cadastrado."
                            errorTextEmail.visibility = View.VISIBLE
                            errorTextSenha.visibility = View.GONE
                        }
                        is FirebaseAuthInvalidCredentialsException -> {

                            errorTextSenha.text = "Senha incorreta."
                            errorTextSenha.visibility = View.VISIBLE
                            errorTextEmail.visibility = View.GONE
                        }
                        else -> {

                            Toast.makeText(this, "Erro: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }



}
