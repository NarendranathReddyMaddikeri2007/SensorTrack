package com.sensorconnect.sensortrack.Fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R

class BallGameFragment : Fragment(), SensorEventListener {

    private var context: Context? = null
    private var view: View? = null
    private var ball_imageview: ImageView? = null
    private var x_axis_tv: TextView? = null
    private var y_axis_tv: TextView? = null
    private var z_axis_tv: TextView? = null
    private var sensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null

    // Low-pass filter variables
    private val alpha = 0.8f
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private val scaleFactor = 25f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_ball_game, container, false)
        //-----------------------------------------------------------
        initializeIds()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //-----------------------------------------------------------
        return view
    }

    private fun initializeIds() {
        ball_imageview = view?.findViewById(R.id.ball_fragment_ball_game_iv) as ImageView
        x_axis_tv = view?.findViewById(R.id.value_xaxis_fragment_ball_game_tv) as TextView
        y_axis_tv = view?.findViewById(R.id.value_yaxis_fragment_ball_game_tv) as TextView
        z_axis_tv = view?.findViewById(R.id.value_zaxis_fragment_ball_game_tv) as TextView
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
        sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        synchronized(this) {
            val x = event?.values?.get(0)
            val y = event?.values?.get(1)
            val z = event?.values?.get(2)
            if (x == null || y == null || z == null) {
                return
            }

            // Apply low-pass filter to smooth out the values
            val smoothedX = lastX * alpha + x * (1 - alpha)
            val smoothedY = lastY * alpha + y * (1 - alpha)
            val smoothedZ = lastZ * alpha + z * (1 - alpha)

            // Update the ImageView position based on the accelerometer values
            val newX = -(smoothedX * screenWidth() / scaleFactor) + screenWidth() / 2
            val newY = (smoothedY * screenHeight() / scaleFactor) + screenHeight() / 2

            // Update TextViews
            updateTextViews(smoothedX, smoothedY, smoothedZ)

            // Set the new position of the ImageView
            ball_imageview?.x = newX
            ball_imageview?.y = newY

            // Update last values for the next iteration
            lastX = smoothedX
            lastY = smoothedY
            lastZ = smoothedZ
        }
    }

    private fun updateTextViews(x: Float, y: Float, z: Float) {
        x_axis_tv?.text = "X-axis :   $x"
        y_axis_tv?.text = "Y-axis :   $y"
        z_axis_tv?.text = "Z-axis :   $z"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Empty
    }

    private fun screenWidth(): Float {
        return resources.displayMetrics.widthPixels.toFloat()
    }

    private fun screenHeight(): Float {
        return resources.displayMetrics.heightPixels.toFloat()
    }
}
