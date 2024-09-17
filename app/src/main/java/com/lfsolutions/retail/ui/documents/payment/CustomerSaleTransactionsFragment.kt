package com.lfsolutions.retail.ui.documents.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentCustomerSaleTransactionsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerSaleTransaction
import com.lfsolutions.retail.model.PaymentRequest
import com.lfsolutions.retail.model.PaymentTermsResult
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SaleTransactionRequestBody
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.widgets.payment.OnPaymentOptionSelected
import com.lfsolutions.retail.ui.widgets.payment.PaymentOptionsView
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.maltaisn.calcdialog.CalcDialog
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal

class CustomerSaleTransactionsFragment : Fragment(), OnNetworkResponse,
    CalcDialog.CalcDialogCallback {

    private var amountEditTransaction: CustomerSaleTransaction? = null
    private var paymentTypes: ArrayList<PaymentType> = ArrayList()
    private var transactions: ArrayList<CustomerSaleTransaction> = ArrayList()
    private lateinit var binding: FragmentCustomerSaleTransactionsBinding
    private lateinit var saleTransactionAdapter: CustomerTransactionAdapter
    private val args by navArgs<CustomerSaleTransactionsFragmentArgs>()
    private lateinit var customer: Customer


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentCustomerSaleTransactionsBinding.inflate(inflater, container, false)
            setHeaderData()
            setAdapters()
            addVerticalItemDecoration(binding.customers, requireContext())
            customer = Gson().fromJson(args.customer, Customer::class.java)

            binding.remarks.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onPayClickListener()
                }
                true
            }
        }
        return binding.root

    }

    private fun onPayClickListener() {
        if (isTransactionSelected().not()) {
            Notify.toastLong("Please select an item first")
            return
        }
        if (paymentTypes.isEmpty()) getPaymentTypes()
        else showPaymentTypes()
    }

    private fun isTransactionSelected(): Boolean {
        var selected = false
        transactions.forEach {
            if (it.applied == true) {
                selected = true
            }
        }
        return selected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCustomerTransactionData()
    }

    private fun setHeaderData() {
        binding.header.setBackText("Customer Transactions")
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
        binding.header.binding?.icoAccount?.visibility = View.GONE
    }

    fun updateSelectedAmount() {
        var amount = getSelectedItemsAmount()
        binding.header.binding?.txtName?.setText("Pay " + Main.app.getSession().currencySymbol + amount.formatDecimalSeparator())
        binding.header.binding?.txtName?.setOnClickListener {
            onPayClickListener()
        }
    }

    private fun getSelectedItemsAmount(): Double {
        var amount = 0.0
        transactions.forEach {
            if (it.applied == true) {
                amount += if (it.appliedAmount == null || it.appliedAmount == 0.0) {
                    it.amount ?: 0.0
                } else {
                    it.appliedAmount ?: 0.0
                }
            }
        }
        return amount
    }

    private fun getCustomerTransactionData() {
        if (transactions.isEmpty()) NetworkCall.make().setCallback(this)
            .autoLoadigCancel(Loading().forApi(requireActivity())).enque(
                Network.api()
                    ?.getSalesTransactions(SaleTransactionRequestBody(customerId = customer.id.toString()))
            ).execute()
    }

    private fun getPaymentTypes() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val result = response?.body() as BaseResponse<PaymentTermsResult>
                result.result?.items?.let {
                    paymentTypes.addAll(it)
                }
                showPaymentTypes()
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to load payment types")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getPaymentTerms()).execute()
    }

    private fun showPaymentTypes() {
        val modal = PaymentOptionsView()
        modal.options.addAll(paymentTypes)
        modal.setOnPaymentOptionSelected(object : OnPaymentOptionSelected {
            override fun onPaymentOptionSelected(paymentType: PaymentType) {
                pay(paymentType)
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, PaymentOptionsView.TAG) }
    }

    private fun pay(paymentType: PaymentType) {
        val session = Main.app.getSession()
        transactions.forEach {
            if (it.appliedAmount == null || it.appliedAmount == 0.0) {
                it.appliedAmount = it.balanceAmount
            }
        }
        val request = PaymentRequest(
            locationId = session.defaultLocationId,
            receiptDate = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                .replace(" ", "T").plus("Z"),
            customerId = customer.id,
            amount = getSelectedItemsAmount(),
            remarks = binding.remarks.text.toString(),
            paymentTypeId = paymentType.value?.toInt(),
            items = transactions
        )


        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val result = response?.body() as BaseResponse<String>
                if (result.success == true) {
                    Notify.toastLong("Payment Successful ${result.result}")
                    findNavController().popBackStack()
                } else {
                    Notify.toastLong("Payment Failed")
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to create payment request")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.createSaleReceipt(request)).execute()
    }


    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        try {
            val customerResponse =
                response?.body() as RetailResponse<ArrayList<CustomerSaleTransaction>>
            transactions = customerResponse.result?.filter {
                it.balanceAmount != null && it.balanceAmount!! > 0.0
            } as ArrayList<CustomerSaleTransaction>
            setAdapters()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setAdapters() {
        saleTransactionAdapter = CustomerTransactionAdapter(transactions)
        binding.customers.adapter = saleTransactionAdapter
        saleTransactionAdapter.setListener(object :
            CustomerTransactionAdapter.OnTransactionCallback {
            override fun onAmountEdit(transaction: CustomerSaleTransaction) {
                amountEditTransaction = transaction
                Calculator.show(this@CustomerSaleTransactionsFragment)
            }

            override fun onTransactionSelected(
                transaction: CustomerSaleTransaction, isChecked: Boolean
            ) {
                updateSelectedAmount()
            }

        })
    }

    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
        Notify.toastLong("Unable to load list")
    }

    fun addVerticalItemDecoration(recyclerView: RecyclerView, context: Context) {
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        if (amountEditTransaction != null) {
            val index = transactions.indexOf(amountEditTransaction)
            if (index > -1) {
                transactions[index].appliedAmount =
                    value?.toDouble()?.formatDecimalSeparator()?.replace(",", "")?.toDouble()
                transactions[index].applied = true
                saleTransactionAdapter.notifyItemChanged(index)
            }
            amountEditTransaction = null
        }
        updateSelectedAmount()
    }

}