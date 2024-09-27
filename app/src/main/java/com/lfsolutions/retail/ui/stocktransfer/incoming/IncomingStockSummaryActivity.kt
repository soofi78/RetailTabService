package com.lfsolutions.retail.ui.stocktransfer.incoming

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
import com.lfsolutions.retail.databinding.FragmentOutGoingStockSummaryBinding
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class IncomingStockSummaryActivity : BaseActivity() {
    private lateinit var mBinding: FragmentOutGoingStockSummaryBinding
    private var itemSwipeHelper: ItemTouchHelper? = null
    private lateinit var mAdapter: InComingStockSummaryAdapter
    private val stockTransferProducts = arrayListOf<StockTransferProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = FragmentOutGoingStockSummaryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Main.app.getInComingStockTransferRequestObject()
        getOutGoingProductsFromIntent()
        initiateView()
    }

    private fun getOutGoingProductsFromIntent() {
        val json = intent.getStringExtra(Constants.OutGoingProducts)
        val type = TypeToken.getParameterized(
            ArrayList::class.java, StockTransferProduct::class.java
        ).type
        stockTransferProducts.addAll(Gson().fromJson(json, type))
    }

    private fun initiateView() {
        mAdapter = InComingStockSummaryAdapter(this, stockTransferProducts)
        mAdapter.setOnItemUpdateListener(object : InComingStockSummaryAdapter.OnItemUpdated {
            override fun OnItemUpdated(stockTransferProduct: StockTransferProduct) {
                updateSummaryAmountAndQty()
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Back")
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
        mBinding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        Main.app.getInComingStockTransferRequestObject()?.date =
            mBinding.date.text.toString() + "T00:00:00Z"
        mBinding.dateSelectionView.setOnClickListener {
            DateTime.showDatePicker(this, object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    mBinding.date.setText("$day-$month-$year")
                    Main.app.getInComingStockTransferRequestObject()?.date =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }


    private fun updateSummaryAmountAndQty() {
        mBinding.txtQTY.text = getQty().toString()
        mBinding.txtTotal.text =
            Main.app.getSession().currencySymbol + getTotal().formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = getQty() != 0.0
    }

    private fun getTotal(): Double {
        var total = 0.0
        stockTransferProducts.forEach {
            total += it.subTotal
        }
        return total
    }

    private fun getQty(): Double {
        var qty = 0.0
        stockTransferProducts.forEach {
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
                if (viewHolder is InComingStockSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView(),
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


            if (Main.app.getInComingStockTransferRequestObject().date == null ||
                Main.app.getInComingStockTransferRequestObject().date?.isBlank() == true
            ) {
                Notify.toastLong("Please select date")
                return@setOnClickListener
            }



            Main.app.getInComingStockTransferRequestObject().stockTransferDetails =
                stockTransferProducts
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
                        ?.createUpdateInComingStockTransfer(Main.app.getInComingStockTransferRequestObject())
                ).execute()
        }
    }

    private fun serialBatchVerified(): Boolean {
        var verified = true
        stockTransferProducts.forEach {
            val serial = it.isAsset == true || it.type.equals("S")
            val notBatched = it.productBatchList == null || it.productBatchList.size == 0
            val batchedAndQtyNotMatch =
                it.productBatchList != null && it.qty.toInt() != it.productBatchList.size
            if (serial && (notBatched || batchedAndQtyNotMatch)) {
                verified = false
            }
        }
        return verified
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearInComingStockTransfer()
    }
}