package com.example.aplicaoteste

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var usuarioEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var confirmarSenhaEditText: EditText

    private lateinit var errorTextEmail: TextView
    private lateinit var errorTextSenha: TextView
    private lateinit var errorTextConfirmarSenha: TextView

    private lateinit var botaoCadastro: Button
    private lateinit var botaoLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()

        usuarioEditText = findViewById(R.id.usuarioEditText)
        emailEditText = findViewById(R.id.emailEditText)
        senhaEditText = findViewById(R.id.senhaEditText)
        confirmarSenhaEditText = findViewById(R.id.confirmarSenhaEditText)

        errorTextEmail = findViewById(R.id.errorTextEmail)
        errorTextSenha = findViewById(R.id.errorTextSenha)
        errorTextConfirmarSenha = findViewById(R.id.errorTextConfirmarSenha)

        botaoCadastro = findViewById(R.id.botaoCadastro)
        botaoLogin = findViewById(R.id.botaoLogin)

        botaoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        botaoCadastro.setOnClickListener {
            if (validarCampos()) {
                val email = emailEditText.text.toString().trim()
                val senha = senhaEditText.text.toString()
                cadastrarUsuario(email, senha)
            }
        }
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        val email = emailEditText.text.toString().trim()
        if (!email.contains("@") || !email.contains(".")) {
            errorTextEmail.text = "Formato de e-mail inválido."
            errorTextEmail.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextEmail.visibility = View.GONE
        }

        val senha = senhaEditText.text.toString()
        if (senha.length < 6) {
            errorTextSenha.text = "A senha deve ter no mínimo 6 caracteres."
            errorTextSenha.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextSenha.visibility = View.GONE
        }

        val confirmarSenha = confirmarSenhaEditText.text.toString()
        if (senha != confirmarSenha) {
            errorTextConfirmarSenha.text = "As senhas não coincidem."
            errorTextConfirmarSenha.visibility = View.VISIBLE
            isValid = false
        } else {
            errorTextConfirmarSenha.visibility = View.GONE
        }

        return isValid
    }

    private fun cadastrarUsuario(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BluetoothActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthUserCollisionException -> {
                            errorTextEmail.text = "Este e-mail já está cadastrado a uma conta."
                            errorTextEmail.visibility = View.VISIBLE
                        }
                        else -> {
                            Toast.makeText(this, "Erro no cadastro: ${exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }
}
