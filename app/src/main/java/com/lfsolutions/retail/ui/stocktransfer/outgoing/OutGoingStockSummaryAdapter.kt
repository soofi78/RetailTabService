package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOutStockSummaryBinding
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class OutGoingStockSummaryAdapter(
    val activity: FragmentActivity,
    val stockTransferProducts: ArrayList<StockTransferProduct>?
) :
    RecyclerView.Adapter<OutGoingStockSummaryAdapter.ViewHolder>() {

    private lateinit var onItemUpdate: OnItemUpdated
    private lateinit var serialNumbers: ArrayList<SerialNumber>
    private var mListener: OutGoingStockItemClick? = null
    private var mRemoveSerialNumberClick: RemoveSerialNumberClick? = null

    class ViewHolder(val binding: ItemOutStockSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View? {
            return binding.swipeAble
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemOutStockSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = stockTransferProducts?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val products=stockTransferProducts?.get(position)
        Glide.with(holder.itemView)
            .load(Main.app.getBaseUrl() + stockTransferProducts?.get(position)?.imagePath)
            .centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.binding.txtPrice.text = Main.app.getSession().currencySymbol + stockTransferProducts?.get(position)?.subTotal?.formatDecimalSeparator()
        holder.binding.txtProductName.text = stockTransferProducts?.get(position)?.productName
        //holder.binding.txtSerials.text = stockTransferProducts?.get(position)?.getSerialNumbers()
        holder.binding.txtQty.text =  products?.getQty()

        holder.binding.btnAdd.tag = position
        holder.binding.btnSub.tag = position
        holder.binding.txtQty.tag = position

        if(products?.isAsset==true || !products?.productBatchList.isNullOrEmpty()){
            holder.binding.txtQty.text=products?.productBatchList?.sumOf { it.Qty }.toString()
            holder.binding.txtQty.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
            holder.binding.btnSub.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
                setBackgroundResource(R.drawable.oval_disable_bg)
            }

            holder.binding.btnAdd.apply {
                isEnabled = false
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
                setBackgroundResource(R.drawable.oval_disable_bg)
            }

            val container = holder.binding.serialNumberContainer
            container.removeAllViews() // Clear previous views (important for recycling)
            // Copy to mutable list if you want to remove later
            val batchSerialNumberList = products?.productBatchList?.toMutableList()?: mutableListOf()

            batchSerialNumberList.forEachIndexed { index, batch ->
                val serialView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.serial_number_item_view, container, false)

                val txtSerial = serialView.findViewById<TextView>(R.id.txtSerials)
                val btnClose = serialView.findViewById<ImageView>(R.id.btnClose)
                btnClose.tag=index

                Log.d("Serial: ", batch.SerialNumber.toString())
                txtSerial.text = batch.SerialNumber

                btnClose.setOnClickListener {
                    batchSerialNumberList.removeAt(index)
                    println("RemoveBatchList: $batchSerialNumberList")

                    products?.productBatchList = batchSerialNumberList // update in your model if needed

                    // Recalculate qty from remaining batch list
                    val newQty = batchSerialNumberList.sumOf { it.Qty }

                    // Update qty in your data list
                    stockTransferProducts?.get(holder.adapterPosition)?.qty = newQty
                    // Update totals
                    stockTransferProducts?.get(holder.adapterPosition)?.updateTotal()

                    // Notify listener
                    if (::onItemUpdate.isInitialized) {
                        stockTransferProducts?.get(holder.adapterPosition)?.let {
                            onItemUpdate.OnItemUpdated(it)
                        }
                    }

                    // Refresh view
                    notifyItemChanged(holder.adapterPosition)
                }
                container.addView(serialView)
            }
        }
        else{
            holder.binding.txtQty.text=products?.qty.toString()
        }

        holder.itemView.setOnClickListener {
            // if(products?.isAsset==false)
            mListener?.onOutGoingStockItemClick()
        }

        holder.binding.btnSub.setOnClickListener {
            if (holder.binding.txtQty.text.toString().toDouble() <= 0) {
                holder.binding.txtQty.text = "1"
                return@setOnClickListener
            }
            holder.binding.txtQty.text = holder.binding.txtQty.text.toString().toDouble().minus(1).toString()
            stockTransferProducts?.get(it.tag as Int)?.qty = holder.binding.txtQty.text.toString().toDouble()
            stockTransferProducts?.get(it.tag as Int)?.updateTotal()
            if (::onItemUpdate.isInitialized) stockTransferProducts?.get(it.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
            notifyItemChanged(it.tag as Int)
        }

        holder.binding.btnAdd.setOnClickListener {
            holder.binding.txtQty.text = holder.binding.txtQty.text.toString().toDouble().plus(1).toString()
            stockTransferProducts?.get(it.tag as Int)?.qty = holder.binding.txtQty.text.toString().toDouble()
            stockTransferProducts?.get(it.tag as Int)?.updateTotal()
            if (::onItemUpdate.isInitialized) stockTransferProducts?.get(it.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
            notifyItemChanged(it.tag as Int)
        }

        holder.binding.txtQty.setOnClickListener {
            openQuantityUpdateDialog(it.tag as Int)
        }

        holder.binding.btnAddSerial.visibility =
            if (stockTransferProducts?.get(position)?.type.equals("S") || stockTransferProducts?.get(
                    position
                )?.isAsset == true
            ) View.GONE else View.GONE //View.Visible when isAsset is true

        holder.binding.btnAddSerial.tag = position
        holder.binding.btnAddSerial.setOnClickListener {
            getSerialNumbersList(holder.binding.btnAddSerial.tag as Int)
        }

        holder.binding.btnAddSerial.setBackgroundResource(
            if ((stockTransferProducts?.get(position)?.productBatchList?.size ?: 0) > 0)
                R.drawable.round_green_background else R.drawable.round_red_background
        )
    }



    private fun openQuantityUpdateDialog(position: Int) {
        val modal = ProductQuantityUpdateSheet()
        val product = stockTransferProducts?.get(position)
        modal.setProductDetails(
            product?.imagePath.toString(),
            product?.productName.toString(),
            product?.qty ?: 0.0,
            product?.cost ?: 0.0,
            product?.unitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                stockTransferProducts?.get(position)?.qty = quantity
                stockTransferProducts?.get(position)?.updateTotal()
                if (::onItemUpdate.isInitialized) stockTransferProducts?.get(position)
                    ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
                notifyItemChanged(position)
            }

            override fun onPriceChanged(price: Double) {
                stockTransferProducts?.get(position)?.cost = price
                stockTransferProducts?.get(position)?.price = price
                stockTransferProducts?.get(position)?.updateTotal()
                notifyItemChanged(position)
            }
        })
        activity.supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    private fun getSerialNumbersList(position: Int) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(activity, "Loading serial numbers"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ArrayList<SerialNumber>>).result?.let {
                        serialNumbers = it
                    }
                    showSerialNumbersList(position)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get serial numbers list")
                }
            }).enque(
                Network.api()?.getSerialNumbers(
                    stockTransferProducts?.get(position)?.productId,
                    Main.app.getSession().defaultLocationId?.toLong()
                )
            ).execute()
    }


    private fun showSerialNumbersList(position: Int) {
        val multiSelectDialog =
            MultiSelectDialog().title("Select serial numbers")
                .titleSize(25f).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1)
                .setMaxSelectionLimit(serialNumbers.size)
                .preSelectIDsList(
                    stockTransferProducts?.get(position)?.getPreSelectedSerialNumbers(serialNumbers)
                )
                .multiSelectList(serialNumbers)
                .onSubmit(object : SubmitCallbackListener {
                    override fun onSelected(
                        selectedIds: ArrayList<MultiSelectModelInterface>?,
                        selectedNames: ArrayList<String>?,
                        commonSeperatedData: String?
                    ) {
                        val batchList = arrayListOf<ProductBatchList>()
                        if (selectedIds != null && selectedIds.size > 0) {
                            selectedIds.forEach { serialItem ->
                                batchList.add(
                                    ProductBatchList(
                                        Id = serialItem.getId().toInt(),
                                        SerialNumber = serialItem.getText()
                                    )
                                )
                            }
                        }
                        stockTransferProducts?.get(position)?.productBatchList = batchList
                        stockTransferProducts?.get(position)?.qty = batchList.size.toDouble()
                        notifyItemChanged(position)
                        if (::onItemUpdate.isInitialized) {
                            stockTransferProducts?.get(position)
                                ?.let { onItemUpdate.OnItemUpdated(it) }
                        }
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(activity.supportFragmentManager, "serialNumber")
    }


    fun setOnItemUpdateListener(param: OnItemUpdated) {
        onItemUpdate = param
    }

    fun remove(position: Int) {
        stockTransferProducts?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface RemoveSerialNumberClick{
        fun onRemoveSerialNumber()
    }

    interface OutGoingStockItemClick {
        fun onOutGoingStockItemClick()
    }

    interface OnItemUpdated {
        fun OnItemUpdated(stockTransferProduct: StockTransferProduct)
    }

}