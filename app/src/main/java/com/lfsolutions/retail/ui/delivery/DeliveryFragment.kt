package com.lfsolutions.retail.ui.delivery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentDeliveryBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerIdsList
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.outgoingstock.OutGoingProduct
import com.lfsolutions.retail.model.outgoingstock.OutGoingStockProductsResults
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.outgoingstock.GenerateOutGoingStockBottomSheet
import com.lfsolutions.retail.ui.outgoingstock.OutGoingStockSummaryActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class DeliveryFragment : Fragment(), OnNetworkResponse {

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
        setAdapters(null)
        if (Main.app.getSession().isSupervisor == false) {
            mBinding.recyclerViewUrgent.visibility = View.GONE
            mBinding.cardUrgent.visibility = View.GONE
        }
        addVerticalItemDecoration(mBinding.recyclerViewToVisit, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewUrgent, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewSchedule, requireContext())
        mBinding.fabStockRecord.setOnClickListener { generateOutStock(mScheduleAdapter.getCheckedItemList()) }
        getCustomerDetails()

    }

    private fun generateOutStock(scheduledList: ArrayList<Customer>) {
        if (scheduledList.isEmpty()) {
            Notify.toastLong("Please select items first!")
            return
        }
        val modal = GenerateOutGoingStockBottomSheet()
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

    private fun openOutStockProductSummaryActivity(outGoingProducts: java.util.ArrayList<OutGoingProduct>?) {
        startActivity(Intent(requireActivity(), OutGoingStockSummaryActivity::class.java).apply {
            putExtra(Constants.OutGoingProducts, Gson().toJson(outGoingProducts))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        val getCustomersResponse = response?.body() as RetailResponse<CustomerResult>
        setAdapters(getCustomersResponse)
    }

    private fun setAdapters(getCustomersResponse: RetailResponse<CustomerResult>?) {
        mUrgentAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getUrgentCustomersList(),
            DeliveryItemAdapter.CustomerItemType.Urgent
        )
        mToVisitAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getToVisitsCustomersList(),
            DeliveryItemAdapter.CustomerItemType.ToVisit
        )
        mScheduleAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getScheduledCustomersList(),
            DeliveryItemAdapter.CustomerItemType.Scheduled
        )
        mBinding.recyclerViewUrgent.adapter = mUrgentAdapter
        mBinding.recyclerViewToVisit.adapter = mToVisitAdapter
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

}