package com.lfsolutions.retail.ui.settings.printer

import android.bluetooth.BluetoothDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.ui.widgets.options.OptionsAdapter


class BluetoothDevicesAdapter(
    var devices: ArrayList<BluetoothDevice>? = ArrayList(),
) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    //    private var mListener: OnItemClickListener? = null
//
//    class BluetoothDeviceViewHolder(val binding: PrinterItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): BluetoothDeviceViewHolder = BluetoothDeviceViewHolder(
//        PrinterItemBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//    )
//
//    @SuppressLint("MissingPermission")
//    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
//        val device = devices?.get(position)
//        holder.binding.icon.setImageResource(R.drawable.ic_bluetooth)
//        holder.binding.name.text = device?.name
//        holder.binding.address.text = device?.address
//        holder.binding.selected.visibility =
//            if (AppSession[Constants.SELECTED_BLUETOOTH] == device?.address) View.VISIBLE else View.GONE
//        holder.binding.parent.tag = device
//        holder.binding.parent.setOnClickListener {
//            val device = it.tag as BluetoothDevice
//            GlobalScope.launch((Dispatchers.IO)) {
//                if (PrinterManager.sendConnectionPrint(device)) {
//                    PrinterManager.setDefaultPrinter(device)
//                }
//                withContext(Dispatchers.Main) {
//                    notifyDataSetChanged()
//                }
//            }
//        }
//
//    }
//
//    override fun getItemCount(): Int = devices?.size ?: 0
//
//    fun remove(position: Int) {
//        devices?.removeAt(position)
//        notifyItemRemoved(position)
//    }
//
//
//    fun setListener(listener: OnItemClickListener) {
//        mListener = listener
//    }
//
//    interface OnItemClickListener {
//        fun onItemClick(position: Int)
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: OptionsAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}