package com.lfsolutions.retail.ui.delivery

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentDeliveryBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerIdsList
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.model.outgoingstock.OutGoingStockProductsResults
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.saleorder.SaleOrderSummaryAdapter
import com.lfsolutions.retail.ui.stocktransfer.incoming.GenerateInComingStockBottomSheet
import com.lfsolutions.retail.ui.stocktransfer.incoming.IncomingStockSummaryActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class DeliveryFragment : Fragment(), OnNetworkResponse {

    private lateinit var itemSwipeHelper: ItemTouchHelper
    private var getCustomersResponse: RetailResponse<CustomerResult>? = null
    private var _binding: FragmentDeliveryBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mUrgentAdapter: DeliveryItemAdapter

    private lateinit var mToVisitAdapter: DeliveryItemAdapter
    private lateinit var mScheduleAdapter: DeliveryItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters(null, "")
        if (Main.app.getSession().isSupervisor == false) {
            mBinding.recyclerViewUrgent.visibility = View.GONE
            mBinding.cardUrgent.visibility = View.GONE
        }
        addVerticalItemDecoration(mBinding.recyclerViewToVisit, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewUrgent, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewSchedule, requireContext())
        mBinding.fabStockRecord.setOnClickListener { generateOutStock(mScheduleAdapter.getCheckedItemList()) }


        mBinding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setAdapters(getCustomersResponse, newText ?: "")
                return true
            }
        })
    }

    private fun isCandidateForFilter(query: String, customer: Customer): Boolean {
        if (query.isNullOrEmpty())
            return true
        var contains = true
        query.split(" ").toSet().forEach {
            contains =
                contains && (customer.name?.lowercase()?.contains(it.lowercase()) == true
                        || customer.country?.lowercase()?.contains(it) == true
                        || customer.area?.lowercase()?.contains(it) == true
                        || customer.address1?.lowercase()?.contains(it) == true
                        || customer.address2?.lowercase()?.contains(it) == true
                        || customer.address3?.lowercase()?.contains(it) == true
                        || customer.customerCode?.lowercase()?.contains(it) == true
                        || customer.email?.lowercase()?.contains(it) == true
                        || customer.salespersonName?.lowercase()?.contains(it) == true
                        || customer.city?.lowercase()?.contains(it) == true)
        }
        return contains
    }

    private fun generateOutStock(scheduledList: ArrayList<Customer>) {
        if (scheduledList.isEmpty()) {
            Notify.toastLong("Please select schedule customer!")
            return
        }
        val modal = GenerateInComingStockBottomSheet()
        modal.setOnConfirmClickListener {
            outGoingStockConfirmClicked(scheduledList)
        }
        modal.setList(scheduledList)
        requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    private fun outGoingStockConfirmClicked(scheduledList: java.util.ArrayList<Customer>) {
        val ids = arrayListOf<Int>()
        scheduledList.forEach {
            it.id?.let { it1 -> ids.add(it1) }
        }
        NetworkCall.make().setCallback(this)
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading outgoing products"))
            .enque(Network.api()?.getOutGoingStockTransferProductList(CustomerIdsList(ids)))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val outGoingProducts =
                        (response?.body() as RetailResponse<OutGoingStockProductsResults>).result?.items
                    openOutStockProductSummaryActivity(outGoingProducts)
                    Notify.toastLong("Success")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to load products")
                }
            }).execute()
    }

    private fun openOutStockProductSummaryActivity(stockTransferProducts: java.util.ArrayList<StockTransferProduct>?) {
        startActivity(Intent(requireActivity(), IncomingStockSummaryActivity::class.java).apply {
            putExtra(Constants.OutGoingProducts, Gson().toJson(stockTransferProducts))
        })
    }

    private fun getCustomerDetails() {
        NetworkCall.make().setCallback(this).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getCustomers()).execute()
    }

    private fun displayItemDetails(customer: Customer) {
        startActivity(FormsActivity.getInstance(context = requireContext()).apply {
            putExtra(Constants.Customer, Gson().toJson(customer))
        })
    }

    override fun onResume() {
        super.onResume()
        getCustomerDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        getCustomersResponse = response?.body() as RetailResponse<CustomerResult>
        setAdapters(getCustomersResponse, "")
    }

    private fun setAdapters(getCustomersResponse: RetailResponse<CustomerResult>?, s: String) {
        mUrgentAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getUrgentCustomersList()
                ?.filter { isCandidateForFilter(s, it) } as ArrayList<Customer>?,
            DeliveryItemAdapter.CustomerItemType.Urgent
        )
        mToVisitAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getToVisitsCustomersList()
                ?.filter { isCandidateForFilter(s, it) } as ArrayList<Customer>?,
            DeliveryItemAdapter.CustomerItemType.ToVisit
        )
        mScheduleAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getScheduledCustomersList()
                ?.filter { isCandidateForFilter(s, it) } as ArrayList<Customer>?,
            DeliveryItemAdapter.CustomerItemType.Scheduled
        )
        mBinding.recyclerViewUrgent.adapter = mUrgentAdapter
        mBinding.recyclerViewToVisit.adapter = mToVisitAdapter
        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper.attachToRecyclerView(mBinding.recyclerViewSchedule)
        mBinding.recyclerViewSchedule.adapter = mScheduleAdapter
        mUrgentAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                displayItemDetails(customer)
            }
        })

        mToVisitAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                displayItemDetails(customer)
            }
        })
    }


    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

    }

    fun addVerticalItemDecoration(recyclerView: RecyclerView, context: Context) {
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun getSwipeToDeleteListener(): SimpleCallback {
        return object :
            SimpleCallback(
                0,
                LEFT or RIGHT
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
                deleteFromSchedule(position)
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is DeliveryItemAdapter.ViewHolderScheduled) {
                    val swipeFlags = START or END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as DeliveryItemAdapter.ViewHolderScheduled).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as DeliveryItemAdapter.ViewHolderScheduled).getSwipableView())
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
                    (viewHolder as DeliveryItemAdapter.ViewHolderScheduled).getSwipableView(),
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
                    (viewHolder as DeliveryItemAdapter.ViewHolderScheduled).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun deleteFromSchedule(position: Int) {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                mScheduleAdapter.remove(position)
                Notify.toastLong("Success")
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to delete")
                getCustomerDetails()
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(
                Network.api()?.deleteCustomerFromVisitationSchedule(
                    IdRequest(
                        mScheduleAdapter.customers?.get(position)?.id
                    )
                )
            ).execute()
    }


}