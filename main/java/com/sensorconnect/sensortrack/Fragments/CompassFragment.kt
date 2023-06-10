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
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.R

class CompassFragment : Fragment(), SensorEventListener {

    private var imageView: ImageView? = null
    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth: Float = 0f
    private var currentAzimuth: Float = 0f
    private var mSensorManager: SensorManager? = null
    private var angle_tv : TextView? = null
    private var direction_tv : TextView? = null
    private var view : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_compass, container, false)
        imageView = view?.findViewById(R.id.compass)
        angle_tv = view?.findViewById(R.id.angle_ofcompass_fragment_compass_tv) as TextView
        direction_tv = view?.findViewById(R.id.direction_ofcompass_fragment_compass_tv) as TextView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(
            this,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
        mSensorManager?.registerListener(
            this,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val alpha: Float = 0.97f
        synchronized(this) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
            }
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
            }
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success: Boolean = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                val anim: Animation = RotateAnimation(
                    -currentAzimuth,
                    -azimuth,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                currentAzimuth = azimuth
                anim.duration = 500
                anim.repeatCount = 0
                anim.fillAfter = true
                imageView?.startAnimation(anim)
                //-----------------------------
                val angle = String.format("%.2f", azimuth)
                angle_tv?.text = "$angle°"

                val direction = getDirection(azimuth)
                direction_tv?.text = "$direction"
            }
        }
    }

    private fun getDirection(azimuth: Float): String {
        val directions = arrayOf("NORTH", "NORTH EAST", "EAST", "SOUTH EAST", "SOUTH", "SOUTH WEST", "WEST", "NORTH WEST")
        val index = ((azimuth + 22.5f) / 45).toInt() and 7
        return directions[index]
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}
