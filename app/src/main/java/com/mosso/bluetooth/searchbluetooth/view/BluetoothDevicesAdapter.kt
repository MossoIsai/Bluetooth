package com.mosso.bluetooth.searchbluetooth.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mosso.bluetooth.R
import com.mosso.bluetooth.databinding.ItemBluetoothDevicesBinding
import com.mosso.bluetooth.searchbluetooth.domain.DevicesBluetooth

class BluetoothDevicesAdapter : RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder>() {

    private var bluetoothDevices: MutableList<DevicesBluetooth> = arrayListOf()

    inner class ViewHolder(val bind: ItemBluetoothDevicesBinding) :
        RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_bluetooth_devices,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = bluetoothDevices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind.nameDevice.text = bluetoothDevices[position].rss
        holder.bind.macAdressDevice.text = bluetoothDevices[position].macAddress
    }

    fun addDevice(item: DevicesBluetooth) {
        bluetoothDevices.add(item)
        notifyDataSetChanged()
    }
}