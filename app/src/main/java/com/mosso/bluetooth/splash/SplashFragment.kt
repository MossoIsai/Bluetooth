package com.mosso.bluetooth.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mosso.bluetooth.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            goToBluetoothSearch()
        }, TIME_OUT_SPLASH)
    }

    private fun goToBluetoothSearch() {
        activity?.let {
            findNavController().navigate(R.id.action_fragmentSplash_to_fragmentSearchBluetooth)
        }
    }

    companion object {
        const val TIME_OUT_SPLASH = 3000L
    }

}