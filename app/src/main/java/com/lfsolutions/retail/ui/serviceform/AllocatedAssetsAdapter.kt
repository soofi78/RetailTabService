package com.lfsolutions.retail.ui.serviceform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
        val checkAllocatedAssets: CheckBox = view.findViewById(R.id.checkAllocatedAssets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.allocated_asset_item, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]
        holder.nameTextView.text = asset.productName
        holder.serialNumber.text = asset.serialNumber
        (position + 1).toString().also { holder.position.text = it }
        holder.status.text = asset.status

        // Remove existing listener before updating checkbox state
        holder.checkAllocatedAssets.setOnCheckedChangeListener(null)
        holder.checkAllocatedAssets.isChecked = asset.isProductChecked

        // Add listener again
        holder.checkAllocatedAssets.setOnCheckedChangeListener { _, isChecked ->
            asset.isProductChecked = isChecked
        }
    }

    override fun getItemCount(): Int = assets.size

    fun getSelectedAssets(): ArrayList<Asset> {
        return ArrayList(assets.filter { it.isProductChecked })
    }
}
