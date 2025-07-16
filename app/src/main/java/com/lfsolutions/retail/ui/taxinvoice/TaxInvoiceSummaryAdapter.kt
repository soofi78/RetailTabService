package com.lfsolutions.retail.ui.taxinvoice



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.stocktransfer.incoming.InComingStockSummaryAdapter.OnItemUpdated
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.disableQtyBox
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

class TaxInvoiceSummaryAdapter(
    val salveInvoiceDetails: ArrayList<SalesInvoiceDetail>?,
    val activity: FragmentActivity) :
    RecyclerView.Adapter<TaxInvoiceSummaryAdapter.ViewHolder>() {

    private lateinit var onItemUpdate: OnItemUpdated
    private var mListener: OnOrderSummarySelectListener? = null
    private lateinit var serialNumbers: ArrayList<SerialNumber>

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
            return binding.swipeAble
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = salveInvoiceDetails?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = salveInvoiceDetails?.get(position)?.qty.toString()
        if (salveInvoiceDetails?.get(position)?.isFOC == true)
            holder.binding.txtPrice.text =
                """${Main.app.getSession().currencySymbol}0"""
        else
            holder.binding.txtPrice.text =
                """${Main.app.getSession().currencySymbol}${salveInvoiceDetails?.get(position)?.totalValue?.formatDecimalSeparator()}"""
        holder.binding.txtProductName.text = salveInvoiceDetails?.get(position)?.productName
        Glide.with(holder.binding.imgProduct.context)
            .load(Main.app.getBaseUrl() + salveInvoiceDetails?.get(position)?.productImage)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(holder.binding.imgProduct)
        holder.binding.txtTag.text =
            if (salveInvoiceDetails?.get(position)?.isFOC == true) "FOC" else if (salveInvoiceDetails?.get(
                    position
                )?.isExchange == true
            ) "Exchange" else if (salveInvoiceDetails?.get(position)?.isExpire == true) "Return" else "None"

        holder.binding.txtTag.visibility =
            if (holder.binding.txtTag.text.equals("None")) View.GONE else View.VISIBLE

        holder.itemView.tag = salveInvoiceDetails?.get(position)
        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect(it.tag as SalesInvoiceDetail)
        }


        val batchList = salveInvoiceDetails?.get(position)?.productBatchList ?: emptyList()
        holder.binding.serialNoView.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()
        batchList.disableQtyBox(
            holder.binding.txtQty,
            holder.itemView
        )

        if(salveInvoiceDetails?.get(position)?.isAddSerialButtonVisible() == true){
            holder.binding.serialNoView.visibility=View.VISIBLE
            holder.binding.btnAddSerialNumber.visibility=View.VISIBLE
        }

        holder.binding.btnAddSerialNumber.tag = position
        holder.binding.btnAddSerialNumber.setOnClickListener {
            getSerialNumbersList(holder.binding.btnAddSerialNumber.tag as Int)
        }

        holder.binding.btnAddSerialNumber.setBackgroundResource(
            if ((salveInvoiceDetails?.get(position)?.productBatchList?.size ?: 0) > 0)
                R.drawable.round_green_background else R.drawable.round_red_background
        )
    }


    fun remove(position: Int) {
        salveInvoiceDetails?.removeAt(position)
        notifyItemRemoved(position)
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
                    salveInvoiceDetails?.get(position)?.productId?.toLong(),
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
                    salveInvoiceDetails?.get(position)?.getPreSelectedSerialNumbers(serialNumbers)
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
                        salveInvoiceDetails?.get(position)?.productBatchList = batchList
                        salveInvoiceDetails?.get(position)?.qty = batchList.size.toDouble()
                        notifyItemChanged(position)
                        if (::onItemUpdate.isInitialized) {
                            salveInvoiceDetails?.get(position)
                                ?.let { onItemUpdate.OnItemUpdated(it) }
                        }
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(activity.supportFragmentManager, "serialNumber")
    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun setOnItemUpdateListener(param: OnItemUpdated) {
        onItemUpdate = param
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect(salesInvoiceDetail: SalesInvoiceDetail)
    }

    interface OnItemUpdated {
        fun OnItemUpdated(salesInvoiceDetail: SalesInvoiceDetail)
    }
}