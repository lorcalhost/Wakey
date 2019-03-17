package com.lorcalhost.wakey

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var isFaceWhite = false

    private var nfcAdapter : NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null

    private var currentWhiteFace = R.drawable.facecute2w
    private var currentBlackFace = R.drawable.facecute2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNFCStatus()


        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        if (intent != null) {
            processIntent(intent)
        }


        myBackground.setBackgroundColor(Color.rgb(200,224,236))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideStatusBar()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        btnNFCsettings.setOnClickListener {
            showNFCSettings()
            NFCstatus.visibility = View.INVISIBLE
            btnNFCsettings.visibility = View.INVISIBLE

        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.values[0] < 10){
            isFaceWhite = true
            myBackground.setBackgroundColor(Color.rgb(0,0,0))
            NFCstatus.setTextColor(Color.rgb(255, 255, 255))
            cuteFace.setImageResource(currentWhiteFace)
        }
        else {
            isFaceWhite = false
            myBackground.setBackgroundColor(Color.rgb(200,224,236))
            NFCstatus.setTextColor(Color.rgb(0, 0, 0))
            cuteFace.setImageResource(currentBlackFace)
        }


    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
        sensorManager!!.unregisterListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) processIntent(intent)
    }


    private fun processIntent(checkIntent: Intent) {
        if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null && rawMessages.isNotEmpty()) {
                val ndefMsg = rawMessages[0] as NdefMessage
                if (ndefMsg.records != null && ndefMsg.records.isNotEmpty()) {
                    val ndefRecord = ndefMsg.records[0]
                    if(ndefRecord.toUri().toString() == "bedroom"){
                        Toast.makeText(this, "Goodnight!", Toast.LENGTH_SHORT).show()
                        currentBlackFace = R.drawable.facesleep3
                        currentWhiteFace = R.drawable.facesleep3w

                        if(isFaceWhite){
                            cuteFace.setImageResource(currentWhiteFace)
                        } else {
                            cuteFace.setImageResource(currentBlackFace)
                        }

                    }
                    else {
                        Toast.makeText(this, "NFC tag not recognized", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }



    private fun checkNFCStatus(){
        val isNfcSupported: Boolean = nfcAdapter != null
        if (isNfcSupported) {
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

    private fun showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    private fun hideStatusBar(){
        val actionBar = supportActionBar
        actionBar?.hide()
        window.setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT)
        window.decorView.systemUiVisibility = 3328
    }

}