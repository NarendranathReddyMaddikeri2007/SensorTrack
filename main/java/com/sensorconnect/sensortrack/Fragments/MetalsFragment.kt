package com.sensorconnect.sensortrack.Fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R
import com.google.android.material.button.MaterialButton

class MetalsFragment : Fragment(), SensorEventListener {

    private var context : Context? = null
    private var view : View? = null
    private var sensorManager : SensorManager? = null
    private var metalsSensor : Sensor? = null
    private var value_textView : TextView? =null
    private var possible_metals_textView : TextView? = null
    private var refresh_calculation_efab : MaterialButton? = null
    private var start_stop_calculation_efab : MaterialButton? = null
    private var sensorDelay: Int = SensorManager.SENSOR_DELAY_UI
    private var scanningDuration: Long = 15000 // 15 seconds
    private var scanningHandler: Handler? = null
    private var isScanning: Boolean = false


    private val metalRanges = mapOf(
        "Aluminum" to -50..50,
        "Iron" to -1000..1000,
        "Copper" to -200..200,
        "Zinc" to -150..150,
        "Platinum" to -10..10,
        "Silver" to -100..100,
        "Gold" to -50..50,
        "Bismuth" to -80..80
    )

    private var metalValues = mutableListOf<Float>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_metals, container, false)

        value_textView = view?.findViewById(R.id.metals_value_fragment_temperature_tv) as TextView
        possible_metals_textView = view?.findViewById(R.id.possible_metals_fragment_metals_tv) as TextView
        refresh_calculation_efab = view?.findViewById(R.id.refresh_calculation_fragment_metals_mb) as MaterialButton
        start_stop_calculation_efab = view?.findViewById(R.id.start_stop_calculation_fragment_metals_mb) as MaterialButton

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        metalsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val hasMagneticFieldSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        if (!hasMagneticFieldSensor) {
            Toast.makeText(this.context,"Sensor not available",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this.context,"Sensor available",Toast.LENGTH_SHORT).show()
            value_textView?.text="----"
            possible_metals_textView?.text="----"
            buttonOnClicks()
        }
        return view
    }

    private fun buttonOnClicks() {
        refresh_calculation_efab?.setOnClickListener {
            refreshCalculation()
        }

        start_stop_calculation_efab?.setOnClickListener {
            if (isScanning) {
                stopScanning()
            } else {
                startScanning()
            }
        }
    }

    private fun startScanning() {
        start_stop_calculation_efab?.text = "Stop"
        start_stop_calculation_efab?.setIconResource(R.drawable.baseline_pause_24)
        isScanning = true
        metalValues.clear()
        value_textView?.text = "----"
        possible_metals_textView?.text = "Scanning..."

        scanningHandler = Handler()
        scanningHandler?.postDelayed(scanningDuration) {
            stopScanning()
        }

        metalsSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, sensorDelay)
        }
    }

    private fun stopScanning() {
        if(isScanning){
            start_stop_calculation_efab?.text = "Start"
            start_stop_calculation_efab?.setIconResource(R.drawable.baseline_play_arrow_24)
            isScanning = false
            scanningHandler?.removeCallbacksAndMessages(null)
            sensorManager?.unregisterListener(this)

            val detectedMetals = detectMetals()
            if (detectedMetals.isNotEmpty()) {
                possible_metals_textView?.text = detectedMetals.joinToString("\n")
            } else {
                possible_metals_textView?.text = "NO METALS ARE DETECTED"
            }
        }
    }

    private fun refreshCalculation() {
        if (isScanning) {
            stopScanning()
        }
        //startScanning()
        possible_metals_textView?.text = "----"
        value_textView?.text = "----"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentsCallback) {
            this.context = context.getContext()
        } else {
            throw IllegalArgumentException("Parent activity must implement FragmentsCallback")
        }
    }

    override fun onResume() {
        super.onResume()
        if (isScanning) {
            metalsSensor?.let { sensor ->
                sensorManager?.registerListener(this, sensor, sensorDelay)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this example
    }

    override fun onSensorChanged(event: SensorEvent?) {
        synchronized(this) {
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                val magneticFieldStrength = event.values[0]
                metalValues.add(magneticFieldStrength)
                updateMetalValue(magneticFieldStrength)
            }
        }
    }

    private fun updateMetalValue(magneticFieldStrength: Float) {
        value_textView?.text = "$magneticFieldStrength μT"
    }

    private fun detectMetals(): List<String> {
        val detectedMetals = mutableListOf<String>()
        for ((metal, range) in metalRanges) {
            val isMetalDetected = metalValues.all { it.toInt() in range }
            if (isMetalDetected) {
                detectedMetals.add(metal)
            }
        }
        return detectedMetals
    }

}