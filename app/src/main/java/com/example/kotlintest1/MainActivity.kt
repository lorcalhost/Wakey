package com.example.kotlintest1

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import java.io.IOException
import android.graphics.Color




class MainActivity : AppCompatActivity(), SensorEventListener {

    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private var isRunning = false

    override fun onSensorChanged(event: SensorEvent?) {
        try{
            if (event!!.values[0] < 30 && isRunning == false){
                isRunning = false
                myBackground.setBackgroundColor(Color.rgb(0,0,0))
                NFCsupport.setTextColor(Color.rgb(255, 255, 255))
                NFCstatus.setTextColor(Color.rgb(255, 255, 255))
            }
            else {
                isRunning = false
                myBackground.setBackgroundColor(Color.rgb(200,224,236))
                NFCsupport.setTextColor(Color.rgb(0, 0, 0))
                NFCstatus.setTextColor(Color.rgb(0, 0, 0))
            }
        }
        catch (e : IOException){

        }
    }

    // NFC adapter for checking NFC state in the device
    private val nfcAdapter : NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myBackground.setBackgroundColor(Color.rgb(200,224,236))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideStatusBar()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        val actionBar = supportActionBar
        actionBar?.hide()

        btnNFCsettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            NFCstatus.visibility = View.INVISIBLE
            btnNFCsettings.visibility = View.INVISIBLE

        }
        checkNFCStatus()


    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onNewIntent(intent: Intent){
        setIntent(intent)
        Toast.makeText(this, intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES).toString(), Toast.LENGTH_LONG).show()

        if (true){
            cuteFace.setImageResource(R.drawable.kawaii)
        }
    }
    private fun hideStatusBar(){
        getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT)
        getWindow().getDecorView().setSystemUiVisibility(3328)
    }

    private fun checkNFCStatus(){
        val isNfcSupported: Boolean = this.nfcAdapter == null
        if (!isNfcSupported) {
            NFCsupport.setText("Nfc is not supported on this device")
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val manager = this.getSystemService(Context.NFC_SERVICE) as NfcManager
            val adapter = manager.defaultAdapter
            if (!(adapter != null && adapter.isEnabled)) {
                NFCstatus.visibility = View.VISIBLE
                btnNFCsettings.visibility = View.VISIBLE
            }
        }
    }





}