package com.lfsolutions.retail.ui.serviceform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.model.product.Asset

class AllocatedAssetsAdapter(private val assets: ArrayList<Asset>) :
    RecyclerView.Adapter<AllocatedAssetsAdapter.AssetViewHolder>() {

    class AssetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name)
        val serialNumber: TextView = view.findViewById(R.id.serialNumber)
        val position: TextView = view.findViewById(R.id.position)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.allocated_asset_item, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.nameTextView.text = assets[position].productName
        holder.serialNumber.text = assets[position].serialNumber
        (position + 1).toString().also { holder.position.text = it }
        holder.status.text = assets[position].status
    }

    override fun getItemCount(): Int = assets.size
}
