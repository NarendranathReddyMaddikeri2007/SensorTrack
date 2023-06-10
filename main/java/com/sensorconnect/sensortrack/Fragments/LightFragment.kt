package com.sensorconnect.sensortrack.Fragments

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R


class LightFragment : Fragment(), SensorEventListener {


    private var sensorManager : SensorManager? = null
    private var lightSensor : Sensor? = null
    private var progressBar : ProgressBar? = null
    private var textView : TextView? =null
    private var review_tv : TextView? = null
    private var health_review_tv : TextView? =null
    private var energy_consumption_review_tv : TextView? = null
    private var context : Context? = null
    private var view : View? = null

    private val lightIntensityRanges = arrayOf(
        Pair(0, 100),
        Pair(101, 500),
        Pair(501, 1000),
        Pair(1001, 2000),
        Pair(2000, Int.MAX_VALUE)
    )

    private val lightIntensityReviews = arrayOf(
        "Very Low",
        "Low",
        "Moderate",
        "High",
        "Very High"
    )

    private val health_effects_review_light_intensity = arrayOf(
        "Ensure proper lighting for tasks, use supplemental light if needed",
        "Consider brighter lighting for activities requiring focus",
        "Maintain suitable lighting for comfortable living",
        "Avoid excessive glare, use curtains or blinds if necessary",
        "Shield direct light, limit exposure to prevent eye strain"
    )


    private val energy_consumption_effects_review_light_intensity = arrayOf(
        "Utilize energy-efficient lighting sources",
        "Optimize lighting setup, consider LED or CFL bulbs",
        "Regularly clean and maintain lighting fixtures",
        "Use lighting controls, dimmers, or motion sensors",
        "Employ energy-saving practices, switch off when not in use"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_light, container, false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        textView = view?.findViewById(R.id.textView)
        progressBar = view?.findViewById(R.id.progressBar)
        review_tv = view?.findViewById(R.id.review_on_range_fragment_light)
        health_review_tv = view?.findViewById(R.id.health_effects_review_fragment_light_tv)
        energy_consumption_review_tv = view?.findViewById(R.id.energy_consumption_effects_review_fragment_light_tv)
        // Get the light sensor
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        return view
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
        // Register the light sensor listener
        lightSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the light sensor listener
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this example
    }

    override fun onSensorChanged(event: SensorEvent?) {
        synchronized(this) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                var lightValue = event.values[0]
                //--------
                lightValue = Math.round(lightValue*1000).toFloat()
                lightValue = lightValue/1000
                //--------
                val rotationDegree = (lightValue / 700f) * 360
                val reviewValue = getLightIntensityReview(lightValue.toInt())
                val healthReviewValue = get_Health_Review(lightValue.toInt())
                val energyConsumptionReviewValue = get_Energy_Consumption_Review(lightValue.toInt())
                updateProgressBar(rotationDegree)
                updateTextView(lightValue)
                updateReviewTextView(reviewValue)
                updateHealthReview(healthReviewValue)
                updateEnergyConsumptionReview(energyConsumptionReviewValue)
            }
        }
    }

    private fun updateEnergyConsumptionReview(energyConsumptionReviewValue: String) {
          energy_consumption_review_tv?.text = "${energyConsumptionReviewValue}"
    }

    private fun updateHealthReview(healthReviewValue: String) {
          health_review_tv?.text = "${healthReviewValue}"
    }

    private fun updateReviewTextView(reviewValue: String) {
        if(reviewValue.equals("Very High") || reviewValue.equals("High")){
            review_tv?.setTextColor(Color.RED)
            review_tv?.text="${reviewValue}"
        }
        else{
            review_tv?.setTextColor(Color.WHITE)
            review_tv?.text="${reviewValue}"
        }

    }

    private fun updateProgressBar(rotationDegree: Float) {
        progressBar?.progress = rotationDegree.toInt()
    }

    private fun updateTextView(lightValue: Float) {
        if(lightValue > 1000f){
            textView?.setTextColor(Color.RED)
            textView?.text = "$lightValue lux"
        }
        else{
            textView?.setTextColor(Color.WHITE)
            textView?.text = "$lightValue lux"
        }

    }

    private fun get_Health_Review(lightValue: Int): String {
        for (i in lightIntensityRanges.indices) {
            val range = lightIntensityRanges[i]
            if (lightValue in range.first..range.second) {
                return health_effects_review_light_intensity[i]
            }
        }
        return "Unknown"
    }

    private fun get_Energy_Consumption_Review(lightValue: Int): String {
        for (i in lightIntensityRanges.indices) {
            val range = lightIntensityRanges[i]
            if (lightValue in range.first..range.second) {
                return energy_consumption_effects_review_light_intensity[i]
            }
        }
        return "Unknown"
    }

    private fun getLightIntensityReview(lightValue: Int): String {
        for (i in lightIntensityRanges.indices) {
            val range = lightIntensityRanges[i]
            if (lightValue in range.first..range.second) {
                return lightIntensityReviews[i]
            }
        }
        return "Unknown"
    }

}