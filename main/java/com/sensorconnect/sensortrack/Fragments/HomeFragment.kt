package com.sensorconnect.sensortrack.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R

class HomeFragment : Fragment() {
    private val PERMISSION_ID : Int= 2
    private var view : View? = null
    private var context : Context? = null
    private var light_sensor_mb : ImageView? = null
    private var compass_mb : ImageView? = null
    private var step_counter_mb : ImageView? = null
    private var metal_detection_mb : ImageView? = null
    private var location_iv : ImageView? = null
    private var about_app_iv : ImageView? = null
    private var heart_rate : ImageView? = null
    private var sos_iv : ImageView? = null

    private val permissions = arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CAMERA
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_home, container, false)
        //-------------------------------------------------------------------
        initializeIds()
        buttonOnClicks()
        return view;
    }

    private fun initializeIds() {
        about_app_iv = view?.findViewById(R.id.to_about_fragment_home_iv) as ImageView
        location_iv = view?.findViewById(R.id.to_find_location_fragment_home_iv) as ImageView
        sos_iv = view?.findViewById(R.id.to_sos_fragment_home_iv) as ImageView
        heart_rate = view?.findViewById(R.id.to_heart_rate_fragment_home_iv) as ImageView
        light_sensor_mb = view?.findViewById(R.id.to_light_sensor_fragment_home_iv) as ImageView
        compass_mb = view?.findViewById(R.id.to_compass_fragment_home_iv) as ImageView
        step_counter_mb = view?.findViewById(R.id.to_step_counter_fragment_home_iv) as ImageView
        metal_detection_mb = view?.findViewById(R.id.to_metal_detection_fragment_home_iv) as ImageView
    }

    private fun buttonOnClicks() {
        //--------------
            light_sensor_mb?.setOnClickListener {
                if(checkPermissions()){
                    view?.findNavController()?.navigate(R.id.action_homeFragment3_to_lightFragment)
                }
                else{
                    requestPermissions()
                }
            }
        //--------------
        compass_mb?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_compassFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
        step_counter_mb?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_stepsTargetFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
        metal_detection_mb?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_temperatureFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
        about_app_iv?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_sensorsListFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
        location_iv?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_ballGameFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
        sos_iv?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_soundFragment2)
            }
            else{
                requestPermissions()
            }
        }

        heart_rate?.setOnClickListener {
            if(checkPermissions()){
                view?.findNavController()?.navigate(R.id.action_homeFragment3_to_heartRateFragment)
            }
            else{
                requestPermissions()
            }
        }
        //--------------
    }


    private fun checkPermissions(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {

            }
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

}