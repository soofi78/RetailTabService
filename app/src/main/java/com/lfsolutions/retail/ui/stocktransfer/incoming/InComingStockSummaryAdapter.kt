package com.lfsolutions.retail.ui.stocktransfer.incoming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
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
import com.lfsolutions.retail.util.disableQtyFields
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class InComingStockSummaryAdapter(
    val activity: FragmentActivity,
    val stockReceiveProducts: ArrayList<StockTransferProduct>?
) :
    RecyclerView.Adapter<InComingStockSummaryAdapter.ViewHolder>() {

    private lateinit var onItemUpdate: OnItemUpdated
    private lateinit var serialNumbers: ArrayList<SerialNumber>
    private var mListener: OutGoingStockItemClick? = null

    class ViewHolder(val binding: ItemOutStockSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
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

    override fun getItemCount(): Int = stockReceiveProducts?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(Main.app.getBaseUrl() + stockReceiveProducts?.get(position)?.imagePath)
            .centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.binding.txtQty.text = stockReceiveProducts?.get(position)?.qty.toString()
        holder.binding.txtPrice.text = Main.app.getSession().currencySymbol + stockReceiveProducts?.get(position)?.price?.formatDecimalSeparator()
        holder.binding.txtProductName.text = stockReceiveProducts?.get(position)?.productName
       /* if(stockReceiveProducts?.get(position)?.isAsset==true){
            holder.binding.txtSerials.visibility=View.VISIBLE
            holder.binding.txtSerials.text = stockReceiveProducts[position].getSerialNumbers()
        }*/
        holder.itemView.setDebouncedClickListener {
            mListener?.onOutGoingStockItemClick()
        }

        holder.binding.btnAdd.tag = position
        holder.binding.btnSub.tag = position
        holder.binding.txtQty.tag = position

        val batchList = stockReceiveProducts?.get(position)?.productBatchList ?: emptyList()
        batchList.disableQtyFields(
            holder.binding.txtQty,
            holder.binding.btnSub,
            holder.binding.btnAdd
        )

        holder.binding.inComingSerialNumberContainer.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 2)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()

        holder.binding.btnSub.setOnClickListener {
            if (holder.binding.txtQty.text.toString().toDouble() <= 0) {
                holder.binding.txtQty.text = "1"
                return@setOnClickListener
            }
            holder.binding.txtQty.text =
                holder.binding.txtQty.text.toString().toDouble().minus(1).toString()
            stockReceiveProducts?.get(it.tag as Int)?.qty =
                holder.binding.txtQty.text.toString().toDouble()
            stockReceiveProducts?.get(it.tag as Int)?.updateTotal()
            if (::onItemUpdate.isInitialized) stockReceiveProducts?.get(it.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
            notifyItemChanged(it.tag as Int)
        }

        holder.binding.btnAdd.setOnClickListener {
            holder.binding.txtQty.text =
                holder.binding.txtQty.text.toString().toDouble().plus(1).toString()
            stockReceiveProducts?.get(it.tag as Int)?.qty =
                holder.binding.txtQty.text.toString().toDouble()
            stockReceiveProducts?.get(it.tag as Int)?.updateTotal()
            if (::onItemUpdate.isInitialized) stockReceiveProducts?.get(it.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
            notifyItemChanged(it.tag as Int)
        }

        holder.binding.txtQty.setOnClickListener {
            openQuantityUpdateDialog(it.tag as Int)
        }


        holder.binding.btnAddSerial.visibility =
            if (stockReceiveProducts?.get(position)?.type.equals("S") || stockReceiveProducts?.get(
                    position
                )?.isAsset == true
            ) View.VISIBLE else View.GONE

        holder.binding.btnAddSerial.tag = position
        holder.binding.btnAddSerial.setOnClickListener {
            getSerialNumbersList(holder.binding.btnAddSerial.tag as Int)
        }

        holder.binding.btnAddSerial.setBackgroundResource(
            if ((stockReceiveProducts?.get(position)?.productBatchList?.size ?: 0) > 0)
                R.drawable.round_green_background else R.drawable.round_red_background
        )
    }

    private fun openQuantityUpdateDialog(position: Int) {
        val modal = ProductQuantityUpdateSheet()
        val product = stockReceiveProducts?.get(position)
        modal.setProductDetails(
            product?.imagePath.toString(),
            product?.productName.toString(),
            product?.qty ?: 0.0,
            product?.price ?: 0.0,
            product?.unitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                stockReceiveProducts?.get(position)?.qty = quantity
                stockReceiveProducts?.get(position)?.updateTotal()
                if (::onItemUpdate.isInitialized) stockReceiveProducts?.get(position)
                    ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
                notifyItemChanged(position)
            }

            override fun onPriceChanged(price: Double) {
                stockReceiveProducts?.get(position)?.cost = price
                stockReceiveProducts?.get(position)?.price = price
                stockReceiveProducts?.get(position)?.updateTotal()
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
                    stockReceiveProducts?.get(position)?.productId,
                    Main.app.getSession().wareHouseLocationId?.toLong()
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
                    stockReceiveProducts?.get(position)?.getPreSelectedSerialNumbers(serialNumbers)
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
                        stockReceiveProducts?.get(position)?.productBatchList = batchList
                        stockReceiveProducts?.get(position)?.qty = batchList.size.toDouble()
                        notifyItemChanged(position)
                        if (::onItemUpdate.isInitialized) {
                            stockReceiveProducts?.get(position)
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
        stockReceiveProducts?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OutGoingStockItemClick {
        fun onOutGoingStockItemClick()
    }

    interface OnItemUpdated {
        fun OnItemUpdated(stockTransferProduct: StockTransferProduct)
    }

}