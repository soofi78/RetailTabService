package com.lfsolutions.retail.ui.forms.currentforms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCurrentFormsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Form
import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.forms.FormAdapter
import com.lfsolutions.retail.ui.forms.FormType
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import retrofit2.Call
import retrofit2.Response

class CurrentFormsFragment : Fragment(), OnNetworkResponse {

    private var _binding: FragmentCurrentFormsBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mAdapter: FormAdapter
    private var customer: Customer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCurrentFormsBinding.inflate(inflater, container, false)
        setCustomer()
        return mBinding.root

    }

    private fun setCustomer() {
        customer = Gson().fromJson(
            requireActivity().intent.getStringExtra(Constants.Customer),
            Customer::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomerData()
        getFormsData()
        addOnClickListener()
        setAdapter(null)

    }

    private fun setAdapter(forms: ArrayList<Form>?) {
        mAdapter = FormAdapter(forms)
        mAdapter.setListener(object : FormAdapter.OnFormSelectListener {
            override fun onFormSelected(form: Form) {
                when (form.getType()) {
                    FormType.AgreementMemo -> findNavController()
                        .navigate(
                            R.id.action_navigation_current_forms_to_navigation_agreement_memo,
                            Bundle().apply {
                                putString(Constants.Form, Gson().toJson(form))
                                putString(Constants.Customer,Gson().toJson(customer))
                            })

                    FormType.ServiceForm -> mBinding.root.findNavController()
                        .navigate(R.id.action_navigation_current_forms_to_serviceFormFragment)

                    FormType.InvoiceForm -> mBinding.root.findNavController()
                        .navigate(R.id.action_navigation_current_forms_to_navigation_product_list)

                    null -> {}
                }
            }
        })
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun getFormsData() {
        val loading = Loading().forApi(requireActivity())
        NetworkCall.make()
            .setCallback(this)
            .setTag("FROMS")
            .autoLoadigCancel(loading)
            .enque(Network.api()?.getCustomerForm(FormsRequest(customer?.id))).execute()
    }

    private fun setCustomerData() {
        mBinding.txtCustomerName.text = customer?.name
        mBinding.txtAddress.text = customer?.address1
    }

    private fun addOnClickListener() {
        mBinding.flowBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        val forms = response?.body() as RetailResponse<FormResult>
        setAdapter(forms.result?.items)
    }

    override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {

    }

}