package com.lfsolutions.retail.ui.outgoingstock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOutStockSummaryBinding
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.outgoingstock.OutGoingProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class OutGoingStockSummaryAdapter(
    val activity: AppCompatActivity,
    val outGoingProducts: ArrayList<OutGoingProduct>?
) :
    RecyclerView.Adapter<OutGoingStockSummaryAdapter.ViewHolder>() {

    private lateinit var onItemUpdate: OnItemUpdated
    private lateinit var serialNumbers: ArrayList<SerialNumber>
    private var mListener: OutGoingStockItemClick? = null

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

    override fun getItemCount(): Int = outGoingProducts?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(Main.app.getBaseUrl() + outGoingProducts?.get(position)?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.binding.txtQty.text = outGoingProducts?.get(position)?.qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + outGoingProducts?.get(position)?.price.toString()
        holder.binding.txtProductName.text = outGoingProducts?.get(position)?.productName
        holder.binding.txtSerials.text = outGoingProducts?.get(position)?.getSerialNumbers()
        holder.itemView.setOnClickListener {
            mListener?.onOutGoingStockItemClick()
        }

        holder.binding.btnAdd.tag = position
        holder.binding.btnAdd.setOnClickListener {
            val qty = holder.binding.txtQty.text.toString().toInt() + 1
            holder.binding.txtQty.text = qty.toString()
            outGoingProducts?.get(holder.binding.btnAdd.tag as Int)?.qty = qty
            if (::onItemUpdate.isInitialized) outGoingProducts?.get(holder.binding.btnAdd.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
        }

        holder.binding.btnSub.tag = position
        holder.binding.btnSub.setOnClickListener {
            val qty = holder.binding.txtQty.text.toString().toInt() - 1
            holder.binding.txtQty.text = if (qty > -1) qty.toString() else 0.toString()
            outGoingProducts?.get(holder.binding.btnSub.tag as Int)?.qty = qty
            if (::onItemUpdate.isInitialized) outGoingProducts?.get(holder.binding.btnSub.tag as Int)
                ?.let { it1 -> onItemUpdate.OnItemUpdated(it1) }
        }

        holder.binding.btnAddSerial.visibility =
            if (outGoingProducts?.get(position)?.type.equals("S") || outGoingProducts?.get(position)?.isAsset == true) View.VISIBLE else View.GONE

        holder.binding.btnAddSerial.tag = position
        holder.binding.btnAddSerial.setOnClickListener {
            getSerialNumbersList(holder.binding.btnAddSerial.tag as Int)
        }
//
//        holder.binding.txtQty.text =
//            if ((outGoingProducts?.get(position)?.productBatchList?.size ?: 0) > 0) {
//                outGoingProducts?.get(position)?.productBatchList?.size.toString()
//            } else outGoingProducts?.get(position)?.qty.toString()


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
                    outGoingProducts?.get(position)?.productId,
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
                    outGoingProducts?.get(position)?.getPreSelectedSerialNumbers(serialNumbers)
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
                        outGoingProducts?.get(position)?.productBatchList = batchList
                        outGoingProducts?.get(position)?.qty = batchList.size
                        notifyItemChanged(position)
                        if (::onItemUpdate.isInitialized) {
                            outGoingProducts?.get(position)?.let { onItemUpdate.OnItemUpdated(it) }
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
        outGoingProducts?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OutGoingStockItemClick {
        fun onOutGoingStockItemClick()
    }

    interface OnItemUpdated {
        fun OnItemUpdated(outGoingProduct: OutGoingProduct)
    }

}