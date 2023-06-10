package com.sensorconnect.sensortrack.Fragments



import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sensorconnect.sensortrack.FragmentsCallback
import com.sensorconnect.sensortrack.R

class SOSFragment : Fragment(){

    private var context : Context? = null
    private var view : View? = null
    private var sos_button_iv : ImageView? = null
    private var torch_button_iv : ImageView? = null
    private var isSosClicked = false
    private lateinit var cameraManager: CameraManager
    private var isTorchOn = false
    private val CAMERA_PERMISSION_REQUEST_CODE = 123
    private var mediaPlayer : MediaPlayer? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        view =  inflater.inflate(R.layout.fragment_sos, container, false)
        //-------------------------------
        //-------------------------------
        sos_button_iv = view?.findViewById(R.id.sos_button_fragment_sos_iv)
        torch_button_iv = view?.findViewById(R.id.torch_button_fragment_sos_iv)
        cameraManager = requireContext().getSystemService(CAMERA_SERVICE) as CameraManager

        // Check and request camera permission if not granted
        if (this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.context as Activity,arrayOf(Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE)
        }
        mediaPlayer = MediaPlayer.create(this.context, R.raw.ringtone1)
        mediaPlayer?.isLooping = true
        buttonClicks()
        //-------------------------------
        return view
    }

    private fun buttonClicks() {
        //----------------------------------
        sos_button_iv?.setOnClickListener {
            if(isSosClicked){
                //on -> off
                offSos()
            }
            else{
                //off -> on
                onSos()
            }
            isSosClicked = !isSosClicked
        }
        //----------------------------------
        torch_button_iv?.setOnClickListener {
            if(isTorchOn){
               offTorch()
            }
            else{
               onTorch()
            }
            isTorchOn = !isTorchOn
        }
    }

    private fun onSos() {
        startPlaying()
        sos_button_iv?.setBackgroundResource(R.drawable.sos_on)
        sos_button_iv?.elevation = 20F
    }

    private fun offSos() {
        stopPlaying()
        sos_button_iv?.setBackgroundResource(R.drawable.sos_off)
        sos_button_iv?.elevation = 0F
    }

    private fun onTorch() {
        if(!isTorchOn){
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, true)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            torch_button_iv?.setBackgroundResource(R.drawable.sos_on)
            torch_button_iv?.elevation = 20F
        }
    }

    private fun offTorch() {
        if(isTorchOn){
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, false)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            torch_button_iv?.setBackgroundResource(R.drawable.sos_off)
            torch_button_iv?.elevation = 0F
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable torch button
            } else {
                // Permission denied, disable torch button
            }
        }
    }

    override fun onResume() {
        super.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
        offTorch()
    }


    private fun stopPlaying() {
        if(isSosClicked){
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
        }
    }

    private fun startPlaying() {
        if(!isSosClicked){
            mediaPlayer?.start()
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

