package com.example.aplicaoteste

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class BluetoothActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BluetoothActivity"
        private const val SCAN_PERIOD: Long = 10000
        private const val REQUEST_CODE_PERMISSIONS = 1001

        private val REQUIRED_PERMISSIONS = mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }.toTypedArray()

        private const val NOTIFICATION_CHANNEL_ID = "ble_notifications"
        private const val NOTIFICATION_CHANNEL_NAME = "BLE Notifications"

        private const val ESP_DEVICE_NAME = "ESP32-BLE"
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private var bluetoothGatt: BluetoothGatt? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var botaoScan: Button


    private val devicesList = mutableListOf<BluetoothDevice>()
    private lateinit var devicesAdapter: DevicesAdapter

    private val leScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name ?: "Dispositivo sem nome"
            Log.d(TAG, "Scan result: device name = $deviceName, address = ${device.address}")


            if (!devicesList.any { it.address == device.address }) {
                devicesList.add(device)
                runOnUiThread {
                    devicesAdapter.notifyItemInserted(devicesList.size - 1)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan falhou com erro: $errorCode")
            runOnUiThread {
                Toast.makeText(this@BluetoothActivity, "Scan falhou com erro: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange: status=$status, newState=$newState")
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Erro na conexão: status $status")
                runOnUiThread {
                    Toast.makeText(this@BluetoothActivity, "Erro na conexão: $status", Toast.LENGTH_SHORT).show()
                }
                gatt.close()
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Conectado ao dispositivo")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Conectado ao ESP", Toast.LENGTH_SHORT).show()
                    }


                    val refreshResult = refreshDeviceCache(gatt)
                    Log.d(TAG, "refreshDeviceCache resultado: $refreshResult")

                    val discovered = gatt.discoverServices()
                    Log.d(TAG, "Iniciando descoberta de serviços: $discovered")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Desconectado do dispositivo")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Desconectado do ESP", Toast.LENGTH_SHORT).show()
                    }
                    gatt.close()
                }
                else -> {
                    Log.d(TAG, "Estado de conexão alterado: $newState")
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "onServicesDiscovered status: $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {


                Log.d(TAG, "Listando serviços disponíveis:")
                gatt.services.forEach { service ->
                    Log.d(TAG, "Serviço UUID: ${service.uuid}")
                    service.characteristics.forEach { characteristic ->
                        Log.d(TAG, "  Característica UUID: ${characteristic.uuid}")
                    }
                }

                val serviceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
                val characteristicUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

                val service = gatt.getService(serviceUUID)
                if (service == null) {
                    Log.e(TAG, "Serviço BLE não encontrado: $serviceUUID")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Serviço BLE não encontrado", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val characteristic = service.getCharacteristic(characteristicUUID)
                if (characteristic == null) {
                    Log.e(TAG, "Característica BLE não encontrada: $characteristicUUID")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Característica BLE não encontrada", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                Log.d(TAG, "Ativando notificações para a característica")
                if (!gatt.setCharacteristicNotification(characteristic, true)) {
                    Log.e(TAG, "Falha ao ativar notificações")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Falha ao ativar notificações", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                val descriptor = characteristic.getDescriptor(descriptorUUID)
                if (descriptor == null) {
                    Log.e(TAG, "Descriptor para notificações não encontrado")
                    runOnUiThread {
                        Toast.makeText(this@BluetoothActivity, "Descriptor para notificações não encontrado", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val writeDescResult = gatt.writeDescriptor(descriptor)
                Log.d(TAG, "Escrevendo descriptor para habilitar notificações: $writeDescResult")

            } else {
                Log.e(TAG, "Falha ao descobrir serviços com status: $status")
                runOnUiThread {
                    Toast.makeText(this@BluetoothActivity, "Falha ao descobrir serviços: $status", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.d(TAG, "onDescriptorWrite: status=$status, descriptor=${descriptor.uuid}")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Descriptor escrito com sucesso, notificações habilitadas")
            } else {
                Log.e(TAG, "Falha ao escrever descriptor")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val valorRecebido = characteristic.getStringValue(0)
            Log.d(TAG, "Notificação recebida: $valorRecebido")
            showNotification("Mensagem BLE", valorRecebido)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        recyclerView = findViewById(R.id.recyclerView)
        botaoScan = findViewById(R.id.botaoScan)

        recyclerView.layoutManager = LinearLayoutManager(this)
        devicesAdapter = DevicesAdapter(devicesList) { device ->

            if (hasConnectPermission()) {
                connectToDevice(device)
            } else {
                Toast.makeText(this, "Permissão BLUETOOTH_CONNECT necessária", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = devicesAdapter

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth não está habilitado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        botaoScan.setOnClickListener {
            if (hasScanPermission()) {
                startBleScan()
            } else {
                Toast.makeText(this, "Permissão BLUETOOTH_SCAN necessária", Toast.LENGTH_SHORT).show()
            }
        }

        createNotificationChannel()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun startBleScan() {
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (scanning) return

        devicesList.clear()
        devicesAdapter.notifyDataSetChanged()

        handler.postDelayed({
            if (scanning) {
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
                runOnUiThread {
                    Toast.makeText(this, "Scan finalizado", Toast.LENGTH_SHORT).show()
                }
            }
        }, SCAN_PERIOD)

        scanning = true
        bluetoothLeScanner?.startScan(leScanCallback)
        Toast.makeText(this, "Iniciando scan BLE", Toast.LENGTH_SHORT).show()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt?.close()
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Toast.makeText(this, "Conectando ao dispositivo ${device.name ?: device.address}", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificações BLE"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, BluetoothActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Certifique-se de ter esse ícone em res/drawable
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val uniqueNotificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(uniqueNotificationId, notification)
    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }


    private fun refreshDeviceCache(gatt: BluetoothGatt): Boolean {
        return try {
            val refreshMethod = gatt.javaClass.getMethod("refresh")
            refreshMethod.invoke(gatt) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao limpar cache GATT", e)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissões negadas", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

   
    private class DevicesAdapter(
        private val devices: List<BluetoothDevice>,
        private val onClick: (BluetoothDevice) -> Unit
    ) : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return DeviceViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            val device = devices[position]
            holder.bind(device)
            holder.itemView.setOnClickListener { onClick(device) }
        }

        override fun getItemCount(): Int = devices.size

        class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(device: BluetoothDevice) {
                text1.text = device.name ?: "Dispositivo sem nome"
                text2.text = device.address
            }
        }
    }
}
