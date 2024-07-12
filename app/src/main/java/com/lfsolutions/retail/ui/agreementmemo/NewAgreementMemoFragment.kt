package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentNewAgreementMemoBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.EquipmentType
import com.lfsolutions.retail.model.Form
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class NewAgreementMemoFragment : Fragment() {

    private var customer: Customer? = null
    private var form: Form? = null
    private var isSignOn: Boolean = true
    private lateinit var _binding: FragmentNewAgreementMemoBinding
    private val args by navArgs<NewAgreementMemoFragmentArgs>()
    private var equipmentTypes: List<EquipmentType>? = null


    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentNewAgreementMemoBinding.inflate(inflater, container, false)
        }
        return mBinding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        form = Gson().fromJson(args.form, Form::class.java)
        customer = Gson().fromJson(args.customer, Customer::class.java)
        addOnClickListener()
        updateSignOnOff()
        setData()
        setEquipmentTypes()
    }

    private fun setEquipmentTypes() {
        if (equipmentTypes == null)
            NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity()))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        equipmentTypes =
                            (response?.body() as RetailResponse<EquipmentTypeResult>).result?.items?.toList()
                        setEquipmentTypesAdapter()
                    }

                    override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {
                        Notify.toastLong("Unable to get equipment list")
                    }
                }).enque(Network.api()?.getEquipmentType()).execute()
    }

    private fun setEquipmentTypesAdapter() {
        val adapter = equipmentTypes?.let {
            ArrayAdapter(
                requireActivity(),
                R.layout.simple_text_item, it
            )
        }
        mBinding.spinnerEquipmentType.adapter = adapter
    }

    private fun setData() {
        mBinding.serialViewHolder.visibility =
            if (form?.serialNumberAvailable() == true) View.VISIBLE else View.GONE
        mBinding.dateText.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onSelected(date: String?) {
                    mBinding.dateText.setText(date)
                }
            })
        }
        mBinding.customerName.text = customer?.name
        mBinding.address.text = customer?.address1

    }

    private fun updateSignOnOff() {
        if (isSignOn) {
            mBinding.btnSignOn.text = getString(R.string.label_sign_off)
            mBinding.btnSignOn.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.rounded_corner_white_background
                )
            )

        } else {
            mBinding.btnSignOn.text = getString(R.string.label_sign_on)
            mBinding.btnSignOn.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.rounded_corner_yellow_background
                )
            )
        }

    }


    private fun addOnClickListener() {

        mBinding.btnSignOn.setOnClickListener {
            isSignOn = isSignOn.not()
            updateSignOnOff()
        }

        mBinding.btnClearSign.setOnClickListener {
            mBinding.signaturePad.clear()
        }

        mBinding.btnOpenEquipmentList.setOnClickListener {
            val bundle = bundleOf(
                "IsEquipment" to true,
                Constants.Form to args.form, Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )

        }

        mBinding.btnViewOrder.setOnClickListener {

            val bundle = bundleOf("IsEquipment" to false)

            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )

        }

        mBinding.btnSave.setOnClickListener {

            it.findNavController().popBackStack(R.id.navigation_current_forms, false)

        }

    }

}