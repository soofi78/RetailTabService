package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentEquipmentListBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Equipment
import com.lfsolutions.retail.model.EquipmentListRequest
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.Form
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class EquipmentListFragment : Fragment() {

    private var equipmentlist: List<Equipment>? = null
    private lateinit var form: Form
    private lateinit var customer: Customer
    private lateinit var _binding: FragmentEquipmentListBinding
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: EquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
            form = Gson().fromJson(arguments?.getString(Constants.Form), Form::class.java)
            customer =
                Gson().fromJson(arguments?.getString(Constants.Customer), Customer::class.java)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEquipmentList()
        updateEquipmentListView()
    }

    private fun getEquipmentList() {
        if (equipmentlist == null) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    equipmentlist =
                        (response?.body() as RetailResponse<EquipmentListResult>).result?.items?.toList()
                    updateEquipmentListView()
                }

                override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {
                    Notify.toastLong("Unable to get equipment list")
                }
            }).enque(
                Network.api()
                    ?.getEquipmentList(EquipmentListRequest(Main.app.getSession().defaultLocationId))
            ).execute()
    }

    private fun updateEquipmentListView() {
        mAdapter = EquipmentAdapter(equipmentlist)
        mAdapter.setListener(object : EquipmentAdapter.OnEquipmentClickListener {
            override fun onEquipmentClick(equipment: Equipment) {
                findNavController().navigate(
                        R.id.action_navigation_agreement_memo_bottom_navigation_to_navigation_add_equipment,
                        bundleOf(Constants.Equipment to Gson().toJson(equipment))
                    )
            }
        })
        mBinding.recyclerView.adapter = mAdapter
    }

}