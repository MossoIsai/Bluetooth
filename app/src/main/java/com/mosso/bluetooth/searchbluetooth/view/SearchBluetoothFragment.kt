package com.mosso.bluetooth.searchbluetooth.view

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.mosso.bluetooth.R
import com.mosso.bluetooth.searchbluetooth.domain.DevicesBluetooth
import kotlinx.android.synthetic.main.fragment_search_bluetooth.*

class SearchBluetoothFragment : Fragment() {

    private var adapter = BluetoothDevicesAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner?
        get() {
            return bluetoothAdapter?.bluetoothLeScanner
        }
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        activity?.let {
            val bluetoothManager =
                it.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_bluetooth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rippleRadar.startRippleAnimation()
        recyclerDevices.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        recyclerDevices.setHasFixedSize(true)
        recyclerDevices.adapter = adapter
        Handler().postDelayed({ animationLoader() }, 30)
        verifyBluetoothPermission()
        requestPermissions(
            arrayOf(
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
        activity?.let {
            displayLocationSettingsRequest(it)
        }
    }

    private fun animationLoader() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 400
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        val animatorList =
            ArrayList<Animator>()
        val scaleXAnimator =
            ObjectAnimator.ofFloat(rippleRadar, "ScaleX", 0f, 1.2f, 1f)
        animatorList.add(scaleXAnimator)
        val scaleYAnimator =
            ObjectAnimator.ofFloat(rippleRadar, "ScaleY", 0f, 1.2f, 1f)
        animatorList.add(scaleYAnimator)
        animatorSet.playTogether(animatorList)
        animatorSet.start()
    }

    private fun verifyBluetoothPermission() {
        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionBluetooth = false
        var permissionLocation = false
        for (permission in permissions) {
            when (permission) {
                Manifest.permission.BLUETOOTH_ADMIN ->
                    permissionBluetooth =
                        grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION ->
                    permissionLocation =
                        grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (permissionBluetooth && permissionLocation) {
            scanLeDevice()
        }

    }

    private fun displayLocationSettingsRequest(context: Context) {

        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()

        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    // Location settings are not satisfied. Show the user a dialog to upgrade location settings
                    try { // Show the dialog by calling startResolutionForResult()
                        status.startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun scanLeDevice() {
        bluetoothLeScanner?.startScan(callBack)
    }

    private val callBack: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            adapter.addDevice(
                DevicesBluetooth(
                    result.rssi.toString(),
                    result.device?.name ?: "WITHOUT_NAME"
                )
            )
        }
    }

    override fun onStop() {
        super.onStop()
        bluetoothLeScanner?.stopScan(callBack)
    }

    companion object {
        const val REQUEST_CODE = 0
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_CHECK_SETTINGS = 2
    }
}