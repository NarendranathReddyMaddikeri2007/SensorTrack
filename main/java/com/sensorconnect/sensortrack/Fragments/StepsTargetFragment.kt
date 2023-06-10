package com.sensorconnect.sensortrack.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R
import com.google.android.material.button.MaterialButton


class StepsTargetFragment : Fragment(), SensorEventListener {

    private var view: View? = null
    private var context: Context? = null
    private val STEPS_PER_KM = 1300f
    private var OLD_STEPS : Float? = 0f
    private var OLD_GOAL : Float? = 0f
    private var CURRENT_COMPLETED_STEPS = 0f
    //-----------------------------------
    private var progressBar_pb: ProgressBar? = null
    private var putTargetButton_mb : MaterialButton? = null
    private var resetButton_mb: MaterialButton? = null
    private var GOAL_STEPS : Float = 0f
    private var goalKms_tv: TextView? = null
    private var progress_perc_tv: TextView? = null
    private var value_progressbar_tv : TextView? = null
    //-----------------------------------
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    //-----------------------------------
    private var sharedpreferences : SharedPreferences? = null
    private val FILE_NAME = "TARGET_STEPS"
    private val IS_RUNNING = "is_running"
    private val OLD_GOAL_VALUE = "old_goal"
    private val OLD_STEP_COUNT = "old_step_count"
    //-----------------------------------
    //-----------------------------------
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_steps_target, container, false)
        //----------------------------------------------------------

        initializeIds()
        loadData()
        buttonOnClicks()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //----------------------------------------------------------
        return view
    }

    private fun initializeSharedpreferences() {
        sharedpreferences = requireContext().getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        if(sharedpreferences?.getBoolean(IS_RUNNING,false)==true){//Previously running state
            running = true
            OLD_GOAL = sharedpreferences?.getFloat(OLD_GOAL_VALUE,0f)
            OLD_STEPS = sharedpreferences?.getFloat(OLD_STEP_COUNT,0f)
        }
        else{//Previously not in running state
            running = false
            OLD_GOAL = 0f
            OLD_STEPS = 0f
        }
        //Pending
    }

    private fun buttonOnClicks() {
        resetButton_mb?.setOnClickListener {
            resetData()
        }
        putTargetButton_mb?.setOnClickListener {
            showSetTargetDialog {target->
                GOAL_STEPS = target*STEPS_PER_KM
                goalKms_tv?.text = "${(target).toInt()} kms"
            }
        }
    }

    private fun initializeIds() {
        goalKms_tv = view?.findViewById(R.id.goal_kilometers_fragment_steps_target_tv) as TextView
        progressBar_pb = view?.findViewById(R.id.progressbar_fragment_steps_target_pb) as ProgressBar
        progress_perc_tv = view?.findViewById(R.id.progress_percentage_done_fragment_steps_target_tv) as TextView
        putTargetButton_mb = view?.findViewById(R.id.target_fragment_steps_target_mb) as MaterialButton
        resetButton_mb = view?.findViewById(R.id.reset_fragment_steps_target_mb) as MaterialButton
        value_progressbar_tv = view?.findViewById(R.id.value_progressbar_steps_fragment_steps_target_tv) as TextView
    }

    override fun onResume() {
        super.onResume()
        running = true
        initializeSharedpreferences()
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this.context, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }
        else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
            if (running && GOAL_STEPS!=0f) {
                totalSteps = event!!.values[0]
                val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt() + OLD_STEPS?.toInt()!!
                if(currentSteps>0){
                    value_progressbar_tv?.text = ("${currentSteps} steps")
                    CURRENT_COMPLETED_STEPS = currentSteps.toFloat()
                    var kk : Float = currentSteps/GOAL_STEPS
                    progressBar_pb?.progress = (kk.toInt()*100)
                    kk = Math.round(kk*10000).toFloat()
                    kk = kk/10000
                    progress_perc_tv?.text = "${(kk*100)} %"
                    if (currentSteps>=GOAL_STEPS){
                        Toast.makeText(this.context,"Congratulations",Toast.LENGTH_SHORT).show()
                        resetData()
                    }
                }
            }
        else{
            previousTotalSteps = event!!.values[0]
            }
    }


    override fun onPause() {
        super.onPause()
        //-------------------------
        var editor : SharedPreferences.Editor? = sharedpreferences?.edit()
        editor?.putBoolean(IS_RUNNING,running)
        editor?.putFloat(OLD_GOAL_VALUE,GOAL_STEPS/1300f)
        editor?.putFloat(OLD_STEP_COUNT,CURRENT_COMPLETED_STEPS)
        editor?.apply()
        //-------------------------
    }

    private fun resetData(){
        running = false
        //-------------------------
        var editor : SharedPreferences.Editor? = sharedpreferences?.edit()
        editor?.clear()
        editor?.apply()
        //-------------------------
        previousTotalSteps = totalSteps
        value_progressbar_tv?.text = 0.toString()
        progressBar_pb?.progress = 0
        GOAL_STEPS = 0f
        sensorManager?.unregisterListener(this)
        saveData()
    }


    private fun showSetTargetDialog(callback: (Float) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set Target Kilometers")
        val editText = EditText(context)
        editText.hint = "Enter target kilometers"
        builder.setView(editText)
        builder.setPositiveButton("OK") { _, _ ->
            val target = editText.text.toString()
            if (!target.isNullOrEmpty()) {
                val targetKilometers = target.toFloat()
                callback(targetKilometers) // Pass the target value to the callback function
            } else {
                Toast.makeText(context, "Please enter a valid target", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
        /*
        *
         fun exampleUsage() {
              showSetTargetDialog { target ->
                     val assignedValue: Float = target
              }
           }
        * */
    }




    private fun saveData() {
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentsCallback) {
            this.context = context.getContext()
        } else {
            throw IllegalArgumentException("Parent activity must implement FragmentsCallback")
        }
    }

}