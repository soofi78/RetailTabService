package com.lfsolutions.retail.ui.outgoingstock

import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivitySummaryBinding
import com.lfsolutions.retail.model.outgoingstock.OutGoingProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class OutGoingStockSummaryActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySummaryBinding
    private var itemSwipeHelper: ItemTouchHelper? = null
    private lateinit var mAdapter: OutGoingStockSummaryAdapter
    private val outGoingProducts = arrayListOf<OutGoingProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Main.app.getStockTransferRequestObject()
        getOutGoingProductsFromIntent()
        initiateView()
    }

    private fun getOutGoingProductsFromIntent() {
        val json = intent.getStringExtra(Constants.OutGoingProducts)
        val type = TypeToken.getParameterized(
            ArrayList::class.java, OutGoingProduct::class.java
        ).type
        outGoingProducts.addAll(Gson().fromJson(json, type))
    }

    private fun initiateView() {
        mAdapter = OutGoingStockSummaryAdapter(this, outGoingProducts)
        mAdapter.setOnItemUpdateListener(object : OutGoingStockSummaryAdapter.OnItemUpdated {
            override fun OnItemUpdated(outGoingProduct: OutGoingProduct) {
                updateSummaryAmountAndQty()
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Back")
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        addOnClickListener()
        mBinding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        Main.app.getStockTransferRequestObject()?.date =
            mBinding.date.text.toString() + "T00:00:00Z"
        mBinding.dateSelectionView.setOnClickListener {
            DateTime.showDatePicker(this, object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    mBinding.date.setText("$day-$month-$year")
                    Main.app.getStockTransferRequestObject()?.date =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }


    private fun updateSummaryAmountAndQty() {
        mBinding.txtQTY.text = getQty().toString()
        mBinding.txtTotal.text =
            Main.app.getSession().currencySymbol + getTotal().formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = getQty() != 0
    }

    private fun getTotal(): Int {
        var total = 0
        outGoingProducts.forEach {
            total += it.subTotal
        }
        return total
    }

    private fun getQty(): Int {
        var qty = 0
        outGoingProducts.forEach {
            qty += it.qty
        }
        return qty
    }

    private fun getSwipeToDeleteListener(): ItemTouchHelper.SimpleCallback {
        return object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                mAdapter.remove(position)
                updateSummaryAmountAndQty()
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is OutGoingStockSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView())
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                getDefaultUIUtil().onDraw(
                    c,
                    recyclerView,
                    (viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onChildDrawOver(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                getDefaultUIUtil().onDrawOver(
                    c,
                    recyclerView,
                    (viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun addOnClickListener() {
        mBinding.btnCancel.setOnClickListener {
            Notify.toastLong("Cleared all items")
            finish()
        }

        mBinding.header.setOnBackClick {
            finish()
        }

        mBinding.btnComplete.setOnClickListener {

            if (serialBatchVerified().not()) {
                Notify.toastLong("Please add serial numbers")
                return@setOnClickListener
            }


            if (Main.app.getStockTransferRequestObject().date == null ||
                Main.app.getStockTransferRequestObject().date?.isBlank() == true
            ) {
                Notify.toastLong("Please select date")
                return@setOnClickListener
            }



            Main.app.getStockTransferRequestObject().stockTransferDetails = outGoingProducts
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(this, "Requesting Stock Transfer"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        Notify.toastLong("Success")
                        finish()
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Stock transfer failed")
                    }
                }).enque(
                    Network.api()
                        ?.createUpdateStockTransfer(Main.app.getStockTransferRequestObject())
                ).execute()
        }
    }

    private fun serialBatchVerified(): Boolean {
        var verified = true
        outGoingProducts.forEach {
            val serial = it.isAsset == true || it.type.equals("S")
            val notBatched = it.productBatchList == null || it.productBatchList.size == 0
            val batchedAndQtyNotMatch =
                it.productBatchList != null && it.qty != it.productBatchList.size
            if (serial && (notBatched || batchedAndQtyNotMatch)) {
                verified = false
            }
        }
        return verified
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.cleaStockTransfer()
    }
}