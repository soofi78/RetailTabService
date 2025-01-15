package com.lfsolutions.retail.ui.settings.printer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.checkSelfPermission
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityPrinterSettingsBinding
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.videotel.digital.util.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PrinterSettingsActivity : BaseActivity() {

    private lateinit var widths: Array<String>
    private lateinit var devices: ArrayList<BluetoothDevice>
    private var names = ArrayList<String>()
    private lateinit var binding: ActivityPrinterSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        widths = getResources().getStringArray(R.array.paperWidth)
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
            )
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                2
            )
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                3
            )
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

    @SuppressLint("MissingPermission")
    private fun getPrinters() {
        names.clear()
        devices = PrinterManager.getBluetoothPrinterDevices()
        devices.forEach { names.add(it.name.toString()) }
        names.add(0, "Select Printer")

        if (devices.isNotEmpty()) {
            binding.instructions.visibility = View.GONE
            binding.printerSettingsView.visibility = View.VISIBLE
            binding.printers.adapter = ArrayAdapter(this, R.layout.simple_text_item, names)
            names.indexOf(PrinterManager.getDefaultPrinterBluetoothDevice()?.name)
                .let { if (it > -1) binding.printers.setSelection(it) }

            widths.indexOf(AppSession[Constants.PRINTER_WIDTH])
                .let { if (it > -1) binding.width.setSelection(it) }

            binding.characters.setText(AppSession.getInt(Constants.CHARACTER_PER_LINE).toString())


            binding.printers.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        return
                    }
                    val device = devices[position - 1]
                    Notify.toastLong(device.name + "\n" + device.address)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            binding.testPrint.setOnClickListener {
                val position = binding.printers.selectedItemPosition - 1
                if (position > -1) PrinterManager.setDefaultPrinter(devices[position])
                else {
                    Notify.toastLong("Please select a printer")
                    return@setOnClickListener
                }

                AppSession.put(
                    Constants.PRINTER_WIDTH,
                    widths[binding.width.selectedItemPosition]
                )
                AppSession.put(
                    Constants.CHARACTER_PER_LINE,
                    binding.characters.text.toString().toInt()
                )
                GlobalScope.launch((Dispatchers.IO)) {
                    try {
                        if (PrinterManager.sendConnectionPrint(devices[position]).not()) {
                            withContext(Dispatchers.Main) {
                                Notify.toastLong("Unable to connect or print...")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Notify.toastLong("Unable to connect or print...")
                        }
                    }
                }
            }
        } else {
            binding.instructions.visibility = View.VISIBLE
            binding.printerSettingsView.visibility = View.GONE
            binding.instructions.setOnClickListener {
                PrinterManager.openBluetoothSettings(this)
            }
        }

    }


    private fun setHeader() {
        binding.header.setBackText("Printer Settings")
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