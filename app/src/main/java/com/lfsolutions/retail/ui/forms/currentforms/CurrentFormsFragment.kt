package com.lfsolutions.retail.ui.forms.currentforms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCurrentFormsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Form
import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailsBottomSheet
import com.lfsolutions.retail.ui.forms.FormAdapter
import com.lfsolutions.retail.ui.forms.FormType
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CurrentFormsFragment : Fragment(), OnNetworkResponse {

    private var _binding: FragmentCurrentFormsBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mAdapter: FormAdapter
    private var customer: Customer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentFormsBinding.inflate(inflater, container, false)
        setCustomer()
        return mBinding.root
    }

    private fun setCustomer() {
        customer = (requireActivity() as FormsActivity).customer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setCustomerData()
        getFormsData()
        addOnClickListener()
        setAdapter(null)
    }

    private fun setupHeader() {
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        mBinding.header.setOnBackClick { requireActivity().finish() }
        mBinding.header.setBackText("Customer Forms")
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
    }

    private fun setCustomerData() {
        customer?.let { mBinding.customerView.setCustomer(it) }
        mBinding.customerView.setOnClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager, customer)
        }
    }


    private fun openSelectedForm(formType: FormType?) {
        when (formType) {
            FormType.AgreementMemo -> openAgreementMemo()
            FormType.ServiceForm -> openServiceForm()
            FormType.InvoiceForm -> openSaleInvoice()
            FormType.SaleOrder -> openSaleOrder()
            FormType.DeliveryOrder -> openDeliveryOrder()
            null -> Notify.toastLong("Invalid form requested")
        }
    }

    private fun openDeliveryOrder() {
        mBinding.root.findNavController()
            .navigate(
                R.id.action_navigation_current_forms_to_navigation_delivery_order,
                Bundle().apply {
                    putString(Constants.Customer, Gson().toJson(customer))
                })
    }

    private fun openSaleInvoice() {
        mBinding.root.findNavController()
            .navigate(
                R.id.action_navigation_current_forms_to_navigation_tax_invoice,
                Bundle().apply {
                    putString(Constants.Customer, Gson().toJson(customer))
                })
    }

    private fun openSaleOrder() {
        mBinding.root.findNavController()
            .navigate(
                R.id.action_navigation_current_forms_to_sale_order,
                Bundle().apply {
                    putString(Constants.Customer, Gson().toJson(customer))
                })
    }

    private fun openServiceForm() {
        mBinding.root.findNavController()
            .navigate(R.id.action_navigation_current_forms_to_serviceFormFragment, Bundle().apply {
                putString(Constants.Customer, Gson().toJson(customer))
            })
    }

    private fun openAgreementMemo() {
        findNavController().navigate(R.id.action_navigation_current_forms_to_navigation_agreement_memo,
            Bundle().apply {
                putString(Constants.Customer, Gson().toJson(customer))
            })
    }

    private fun setAdapter(forms: ArrayList<Form>?) {
        var filtered = arrayListOf<Form>()
        if (Main.app.getSession().isSuperVisor == true) {
            forms?.let { filtered.addAll(it) }
        } else {
            forms?.filter {
                (it.getType() != FormType.AgreementMemo
                        && it.getType() != FormType.ServiceForm
                        && it.getType() != FormType.SaleOrder)
            }
                ?.let { filtered.addAll(it) }
        }
        mAdapter = FormAdapter(filtered)
        mAdapter.setListener(object : FormAdapter.OnFormSelectListener {
            override fun onFormSelected(form: Form) {
                openSelectedForm(form.getType())
            }
        })
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun getFormsData() {
        val loading = Loading().forApi(requireActivity())
        NetworkCall.make().setCallback(this).setTag("FROMS").autoLoadigCancel(loading)
            .enque(Network.api()?.getCustomerForm(FormsRequest(customer?.id))).execute()
    }

    private fun addOnClickListener() {
        mBinding.addNewForms.setOnClickListener {
            val modal = NewFormsBottomSheet()
            modal.setOnClickListener {
                openSelectedForm(FormType.find(it.tag.toString()))
            }
            requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        if (requireActivity().intent.getStringExtra(Constants.FormType) != null) {
            val form = FormType.find(requireActivity().intent.getStringExtra(Constants.FormType)!!)
            openSelectedForm(form)
            requireActivity().intent.removeExtra(Constants.FormType)
        }
        val forms = response?.body() as RetailResponse<FormResult>
        setAdapter(forms.result?.items)
    }

    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
        Notify.toastLong("Unable to load forms")
    }

}