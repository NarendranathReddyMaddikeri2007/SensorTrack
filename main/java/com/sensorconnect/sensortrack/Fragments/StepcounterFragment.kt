package com.sensorconnect.sensortrack.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class StepcounterFragment : Fragment(), SensorEventListener {

    private var rootView: View? = null
    private var context: Context? = null
    private var sensorManager: SensorManager? = null
    private var isRunning = false
    private val STEPS_PER_KM = 1300

    // For Hour
    private var totalStepsHour = 0f
    private var previousTotalStepsHour = 0f
    private var totalCaloriesHour = 0f
    private var totalKmsHour = 0f
    private var hourTimer: CountDownTimer? = null

    // For Day
    private var totalStepsDay = 0f
    private var previousTotalStepsDay = 0f
    private var totalCaloriesDay = 0f
    private var totalKmsDay = 0f

    // TextViews
    private var stepCountHourTv: TextView? = null
    private var stepCountDayTv: TextView? = null
    private var kilometersHourTv: TextView? = null
    private var kilometersDayTv: TextView? = null
    private var caloriesBurntHourTv: TextView? = null
    private var caloriesBurntDayTv: TextView? = null

    private var resetHourTv: MaterialButton? = null
    private var resetDayTv: MaterialButton? = null

    //SharedPreferences
    private val TOTAL_STEPS_HOUR = "total_steps_hour"
    private val TOTAL_KILOMETERS_HOUR = "total_kilometers_hour"
    private val TOTAL_CALORIES_HOUR = "total_calories_hour"
    private val TOTAL_STEPS_DAY = "total_steps_day"
    private val TOTAL_KILOMETERS_DAY = "total_kilometers_day"
    private val TOTAL_CALORIES_DAY = "total_calories_day"
    private var sharedpreferences : SharedPreferences? = null
    private val FILE_NAME = "STEP_COUNTER_SP"

    //Calculated values
    private var steps_hour = 0
    private var kilometers_hour = 0.00f
    private var calories_hour = 0.00f
    private var steps_day = 0
    private var kilometers_day = 0.00f
    private var calories_day = 0.00f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_stepcounter, container, false)
        initializeIds()
        initializeValues()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setupButtonClicks()
        return rootView
    }

    private fun initializeValues() {
        stepCountHourTv?.text = "${sharedpreferences?.getInt(TOTAL_STEPS_HOUR,0)}"
        stepCountDayTv?.text = "${sharedpreferences?.getInt(TOTAL_STEPS_DAY,0)}"
        kilometersHourTv?.text = "${sharedpreferences?.getFloat(TOTAL_KILOMETERS_HOUR,0.00f)}"
        kilometersDayTv?.text = "${sharedpreferences?.getFloat(TOTAL_KILOMETERS_DAY,0.00f)}"
        caloriesBurntHourTv?.text = "${sharedpreferences?.getFloat(TOTAL_CALORIES_HOUR,0.00f)}"
        caloriesBurntDayTv?.text = "${sharedpreferences?.getFloat(TOTAL_CALORIES_DAY,0.00f)}"
    }

    private fun initializeIds() {
        stepCountHourTv = rootView?.findViewById(R.id.total_steps_hour_fragment_stepcounter_tv)
        stepCountDayTv = rootView?.findViewById(R.id.total_steps_day_fragment_stepcounter_tv)
        kilometersHourTv = rootView?.findViewById(R.id.total_kilometers_hour_fragment_stepcounter_tv)
        kilometersDayTv = rootView?.findViewById(R.id.total_kilometers_day_fragment_stepcounter_tv)
        caloriesBurntHourTv = rootView?.findViewById(R.id.total_calories_hour_fragment_stepcounter_tv)
        caloriesBurntDayTv = rootView?.findViewById(R.id.total_calories_day_fragment_stepcounter_tv)
        resetHourTv = rootView?.findViewById(R.id.refresh_hour_fragment_step_counter_mb)
        resetDayTv = rootView?.findViewById(R.id.refresh_day_fragment_step_counter_mb)
    }

    private fun setupButtonClicks() {
        resetHourTv?.setOnClickListener {
            resetHourValues()
        }

        resetDayTv?.setOnClickListener {
            resetDayValues()
        }
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
        isRunning = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(context, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.unregisterListener(this)
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
        resetDayValues()
        resetHourValues()
    }

    private fun resetHourValues() {
        previousTotalStepsHour = totalStepsHour
        totalCaloriesHour = 0f
        totalKmsHour = 0f
        setHourTotalStepsValue()
        setHourCaloriesValue()
        setHourKmsValue()
        startHourTimer()
    }

    private fun resetDayValues() {
        val currentTime = System.currentTimeMillis()
        val midnightTime = getMidnightTime()
        if (currentTime >= midnightTime) {
            previousTotalStepsDay = totalStepsDay
            totalCaloriesDay = 0f
            totalKmsDay = 0f
        }
        setDayTotalStepsValue()
        setDayCaloriesValue()
        setDayKmsValue()
    }

    private fun getMidnightTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun setDayTotalStepsValue() {
        val currentSteps = (totalStepsDay - previousTotalStepsDay).toInt()
        steps_day = currentSteps
        stepCountDayTv?.text = currentSteps.toString()
    }

    private fun setDayCaloriesValue() {
        val caloriesBurned = (totalStepsDay - previousTotalStepsDay) * 0.04f
        totalCaloriesDay += caloriesBurned
        totalCaloriesDay = Math.round(totalCaloriesDay * 10) / 100f
        calories_day = totalCaloriesDay
        caloriesBurntDayTv?.text = totalCaloriesDay.toString()
    }

    private fun setDayKmsValue() {
        val kmsDone = (totalStepsDay - previousTotalStepsDay) / STEPS_PER_KM
        totalKmsDay += kmsDone
        totalKmsDay = Math.round(totalKmsDay * 10) / 100f
        kilometers_day = totalKmsDay
        kilometersDayTv?.text = totalKmsDay.toString()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        synchronized(this) {
            if (isRunning) {
                totalStepsDay = event?.values?.get(0) ?: 0f
                totalStepsHour = totalStepsDay
                setDayTotalStepsValue()
                setDayCaloriesValue()
                setDayKmsValue()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onPause() {
        super.onPause()
        hourTimer?.cancel()
        updateSharedPreferences()
    }

    private fun updateSharedPreferences() {
        var editor : SharedPreferences.Editor? = sharedpreferences?.edit()
        //--------------
        editor?.putInt(TOTAL_STEPS_HOUR,steps_hour)
        editor?.putFloat(TOTAL_CALORIES_HOUR,calories_hour)
        editor?.putFloat(TOTAL_KILOMETERS_HOUR,kilometers_hour)
        editor?.putInt(TOTAL_STEPS_DAY,steps_day)
        editor?.putFloat(TOTAL_CALORIES_DAY,calories_day)
        editor?.putFloat(TOTAL_KILOMETERS_DAY,kilometers_day)
        //--------------
        editor?.apply()
    }

    private fun startHourTimer() {
        hourTimer?.cancel()
        hourTimer = object : CountDownTimer(1000 * 60 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setHourTotalStepsValue()
                setHourCaloriesValue()
                setHourKmsValue()
            }

            override fun onFinish() {
                resetHourValues()
            }
        }.start()
    }

    private fun setHourTotalStepsValue() {
        val currentSteps = (totalStepsHour - previousTotalStepsHour).toInt()
        steps_hour = currentSteps
        stepCountHourTv?.text = currentSteps.toString()
    }

    private fun setHourCaloriesValue() {
        val caloriesBurned = (totalStepsHour - previousTotalStepsHour) * 0.04f
        totalCaloriesHour += caloriesBurned
        totalCaloriesHour = Math.round(totalCaloriesHour * 10) / 100f
        calories_hour = totalCaloriesHour
        caloriesBurntHourTv?.text = totalCaloriesHour.toString()
    }

    private fun setHourKmsValue() {
        val kmsDone = (totalStepsHour - previousTotalStepsHour) / STEPS_PER_KM
        totalKmsHour += kmsDone
        totalKmsHour = Math.round(totalKmsHour * 10) / 100f
        kilometers_hour = totalKmsHour
        kilometersHourTv?.text = totalKmsHour.toString()
    }

}
