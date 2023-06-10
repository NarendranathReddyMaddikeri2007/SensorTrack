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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R


class HeartRateFragment : Fragment(), SensorEventListener{

    private var context : Context? = null
    private var view : View? = null
    //------------
    private var sensorManager: SensorManager? = null
    private var heartrate_sensor: Sensor? = null
    private var progressbar_pd: ProgressBar? = null
    private var value_tv: TextView? = null
    private var review_tv: TextView? = null
    private var precautions_tv: TextView? = null

    private val heartRateRanges = arrayOf(
        Pair(0, 60),
        Pair(60, 100),
        Pair(100, 140),
        Pair(140, 180),
        Pair(180, 220),
        Pair(220, Int.MAX_VALUE)
    )

    private val heartRateReviews = arrayOf(
        "Below Normal",
        "Normal",
        "Moderate Exercise",
        "Vigorous Exercise",
        "High-Intensity Exercise or Stress",
        "Medical Attention Required"
    )

    private val heartRatePrecautions = arrayOf(
        "Consult a healthcare professional",
        "Normal range",
        "Moderate exercise",
        "Vigorous exercise",
        "High-intensity exercise or stress",
        "Medical attention required"
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_heart_rate, container, false)
        //------------------------------------
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        progressbar_pd = view?.findViewById(R.id.progressBar_fragment_heart_rate_pb)
        value_tv = view?.findViewById(R.id.value_heart_rate_fragment_heart_rate_tv)
        precautions_tv = view?.findViewById(R.id.precautions_fragment_heart_rate_tv)
        review_tv = view?.findViewById(R.id.review_fragment_heart_rate_tv)
        //-----
        val hasHeartRateSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null
        if (!hasHeartRateSensor) {
            value_tv?.text="----"
            precautions_tv?.text="----"
            review_tv?.text="----"
            Toast.makeText(this.context,"Sensor not available", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this.context,"Sensor available", Toast.LENGTH_SHORT).show()
            heartrate_sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        }
        //------------------------------------
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
        // Register the heart rate sensor listener
        heartrate_sensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
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
            if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
                val heartRateValue = event.values[0]
                val rotationDegree = ((heartRateValue / 220) * 360).toInt()
                val reviewValue = getHeartRateReview(heartRateValue.toInt())
                val precautionsValue = getHeartRatePrecautions(heartRateValue.toInt())
                updateProgressBar(rotationDegree)
                updateTextView(heartRateValue)
                updateReviewTextView(reviewValue)
                updatePrecautionsTextView(precautionsValue)
            }
        }
    }

    private fun updatePrecautionsTextView(precautionsValue: String) {
        precautions_tv?.text = "$precautionsValue"
    }

    private fun updateReviewTextView(reviewValue: String) {
        if (reviewValue.equals("Medical Attention Required")) {
            review_tv?.setTextColor(Color.RED)
            review_tv?.text = reviewValue
        } else {
            review_tv?.setTextColor(Color.WHITE)
            review_tv?.text = reviewValue
        }
    }

    private fun updateProgressBar(rotationValue: Int) {
        progressbar_pd?.progress = rotationValue
    }

    private fun updateTextView(heartRateValue: Float) {
        value_tv?.text = "$heartRateValue bpm"
    }


    private fun getHeartRateReview(heartRateValue: Int): String {
        for (i in heartRateRanges.indices) {
            val range = heartRateRanges[i]
            if (heartRateValue in range.first..range.second) {
                return heartRateReviews[i]
            }
        }
        return "Unknown"
    }

    private fun getHeartRatePrecautions(heartRateValue: Int): String {
        for (i in heartRateRanges.indices) {
            val range = heartRateRanges[i]
            if (heartRateValue in range.first..range.second) {
                return heartRatePrecautions[i]
            }
        }
        return "Unknown"
    }



    // ...

}