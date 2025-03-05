package com.lfsolutions.retail.ui.settings.printer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityPrinterSettingsBinding
import com.lfsolutions.retail.ui.documents.history.HistoryFilterSheet
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PrinterSettingsSheet : BottomSheetDialogFragment() {

    private lateinit var widths: Array<String>
    private lateinit var devices: ArrayList<BluetoothDevice>
    private var names = ArrayList<String>()
    private lateinit var binding: ActivityPrinterSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ActivityPrinterSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        widths = resources.getStringArray(R.array.paperWidth)
        setHeader()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && checkSelfPermission(
                requireActivity(), Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH), 1
            )
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && checkSelfPermission(
                requireActivity(), Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 2
            )
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && checkSelfPermission(
                requireActivity(), Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 3
            )
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && checkSelfPermission(
                requireActivity(), Manifest.permission.BLUETOOTH_SCAN
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
            binding.printers.adapter =
                ArrayAdapter(requireActivity(), R.layout.simple_text_item, names)
            names.indexOf(PrinterManager.getDefaultPrinterBluetoothDevice()?.name)
                .let { if (it > -1) binding.printers.setSelection(it) }

            widths.indexOf(AppSession[Constants.PRINTER_WIDTH])
                .let { if (it > -1) binding.width.setSelection(it) }

            binding.characters.setText(
                AppSession.getInt(Constants.CHARACTER_PER_LINE, 48).toString()
            )


            binding.printers.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
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

            binding.testPrint.setDebouncedClickListener {
                val position = binding.printers.selectedItemPosition - 1
                if (position > -1) PrinterManager.setDefaultPrinter(devices[position])
                else {
                    Notify.toastLong("Please select a printer")
                    return@setDebouncedClickListener
                }

                AppSession.put(
                    Constants.PRINTER_WIDTH, widths[binding.width.selectedItemPosition]
                )
                AppSession.put(
                    Constants.CHARACTER_PER_LINE, binding.characters.text.toString().toInt()
                )
                GlobalScope.launch((Dispatchers.IO)) {
                    try {
                        if (PrinterManager.sendConnectionPrint(devices[position]).not()) {
                            withContext(Dispatchers.Main) {
                                Notify.toastLong("Unable to connect or print...")
                            }
                        } else {
                            this@PrinterSettingsSheet.dismiss()
                            Main.app.restart(requireActivity())
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
            binding.instructions.setDebouncedClickListener {
                PrinterManager.openBluetoothSettings(requireActivity())
            }
        }

    }


    private fun setHeader() {
        binding.header.setBackText("Printer Settings")
        binding.header.setOnBackClick {
            this.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) checkPermissions()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val layoutParams = bottomSheet.layoutParams

                val windowHeight = getWindowHeight()
                if (layoutParams != null) {
                    layoutParams.height = windowHeight
                }
                bottomSheet.layoutParams = layoutParams
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    companion object {
        fun show(activity: AppCompatActivity) {
            val printerSettings = PrinterSettingsSheet()
            activity.supportFragmentManager.let {
                printerSettings.show(
                    it, HistoryFilterSheet.TAG
                )
            }
        }
    }
}