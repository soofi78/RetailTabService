package com.lfsolutions.retail.ui.documents.dailysale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentDailySaleRecordBinding
import com.lfsolutions.retail.model.UserIdDateRequestBody
import com.lfsolutions.retail.model.dailysale.DailySaleRecord
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.widgets.SaleRecordItemView
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class DailySaleRecordFragment : Fragment() {

    private var selectedDate: String = ""
    private lateinit var binding: FragmentDailySaleRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDailySaleRecordBinding.inflate(inflater, container, false)
        }
        return binding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setData()
    }


    private fun setData() {
        binding.dateHolder.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    selectedDate = "$year-$month-$day"
                    binding.date.text = selectedDate
                    fetchDailySale()
                }
            })
        }
        selectedDate = (requireActivity() as DailySaleFlowActivity).getSelectedDate().toString()
        binding.date.text = selectedDate
        binding.header.setBackText("Daily Sale Record")
        fetchDailySale()
    }


    private fun addOnClickListener() {
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
    }

    private fun fetchDailySale() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val saleRecord = (response?.body() as BaseResponse<DailySaleRecord>).result
                    setDailySaleRecordData(saleRecord)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toast("Unable to load the sale record")
                }
            }).enque(
                Network.api()?.dailySaleRecord(
                    UserIdDateRequestBody(
                        userId = Main.app.getSession().userId,
                        date = selectedDate
                    )
                )
            )
            .execute()

    }

    private fun setDailySaleRecordData(saleRecord: DailySaleRecord?) {
        binding.recordItems.removeAllViews()
        saleRecord?.dailySalesItem?.forEach { dailySalesItem ->
            val view = SaleRecordItemView(requireContext())
            view.setData(dailySalesItem)
            binding.recordItems.addView(view)
        }

        binding.netTotal.text =
            Main.app.getSession().currencySymbol + saleRecord?.netTotal.toString()
                .formatDecimalSeparator()
    }

}