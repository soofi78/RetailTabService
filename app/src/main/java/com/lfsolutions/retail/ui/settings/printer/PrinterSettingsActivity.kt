package com.lfsolutions.retail.ui.settings.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.checkSelfPermission
import com.lfsolutions.retail.databinding.ActivityPrinterSettingsBinding
import com.lfsolutions.retail.ui.BaseActivity


class PrinterSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityPrinterSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setHeader()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH),
                1
            );
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                2
            );
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                3
            );
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
            && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), 4)
        } else {
            getPrinters()
        }
    }

    private fun getPrinters() {
        val devices = PrinterManager.getBluetoothPrinterDevices()
        if (devices.isNotEmpty()) {
            binding.instructions.visibility = View.GONE
            binding.printers.visibility = View.VISIBLE
            binding.printers.adapter = BluetoothDevicesAdapter(devices)
        } else {
            binding.instructions.visibility = View.VISIBLE
            binding.printers.visibility = View.GONE
            binding.instructions.setOnClickListener {
                PrinterManager.openBluetoothSettings(this)
            }
        }

    }


    private fun setHeader() {
        binding.header.setBackText("Profile")
        binding.header.setOnBackClick {
            this.finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty())
            checkPermissions()
    }
}