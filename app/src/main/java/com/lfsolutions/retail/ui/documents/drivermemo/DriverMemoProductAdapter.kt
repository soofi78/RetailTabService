package com.lfsolutions.retail.ui.documents.drivermemo

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemDocumentBinding
import com.lfsolutions.retail.databinding.ItemDriverDetailsBinding
import com.lfsolutions.retail.model.Documents
import com.lfsolutions.retail.model.memo.DriverMemoDetail
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.stocktransfer.outgoing.OutGoingStockSummaryAdapter.OnItemUpdated
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.formatDecimalSeparator

class DriverMemoProductAdapter(
    val activity: FragmentActivity,
    val items: ArrayList<DriverMemoDetail>,
) :
    RecyclerView.Adapter<DriverMemoProductAdapter.ViewHolder>() {

    private lateinit var onItemUpdate: OnItemUpdated

    class ViewHolder(val binding: ItemDriverDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
            return binding.swipeAble
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemDriverDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView)
            .load(Main.app.getBaseUrl() + item.productImage)
            .centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.binding.txtQty.text = item.qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + item.totalPrice.formatDecimalSeparator()
        holder.binding.txtProductName.text = item.productName

        holder.binding.btnAdd.tag = position
        holder.binding.btnAdd.setOnClickListener {
            openQuantityUpdateDialog(it.tag as Int)
        }

        holder.binding.btnSub.tag = position
        holder.binding.btnSub.setOnClickListener {
            openQuantityUpdateDialog(it.tag as Int)
        }
    }

    private fun openQuantityUpdateDialog(position: Int) {
        val modal = ProductQuantityUpdateSheet()
        val product = items[position]
        modal.setProductDetails(
            product.productImage.toString(),
            product.productName.toString(),
            product.qty ?: 0.0,
            product.cost ?: 0.0,
            product.uom.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                items[position].qty = quantity
                items[position].updateTotal()
                if (::onItemUpdate.isInitialized) items[position]
                    ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
                notifyItemChanged(position)
            }

            override fun onPriceChanged(price: Double) {
                items[position].cost = price
                items[position].price = price
                items[position].updateTotal()
                notifyItemChanged(position)
            }
        })
        activity.supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    fun remove(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnItemUpdated {
        fun OnItemUpdated(driverMemoDetail: DriverMemoDetail)
    }
}