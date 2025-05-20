package com.example.aplicaoteste
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TelaInicialActivity : AppCompatActivity() {
    private lateinit var botaoLogin: Button
    private lateinit var botaoCadastro: Button
    private lateinit var botaoEsp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_iniclal)
        botaoCadastro = findViewById(R.id.BotaoCadastro)
        botaoLogin = findViewById(R.id.BotaoLogin)
        botaoEsp = findViewById(R.id.BotaoEsp)

        botaoCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
            finish()
        }
        botaoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        botaoEsp.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}