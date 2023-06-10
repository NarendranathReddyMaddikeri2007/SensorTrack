package com.sensorconnect.sensortrack.Fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.sensorconnect.sensortrack.R

class SplashScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        Handler().postDelayed(3000) {
            view.findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment3)
        }
        return view;
    }

}