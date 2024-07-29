package com.lfsolutions.retail.ui.taxinvoice

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.ComplaintServiceResponse
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.PaymentTerm
import com.lfsolutions.retail.model.PaymentTermsResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.makeTextBold
import com.videotel.digital.util.DateTime
import com.videotel.digital.util.Notify
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class TaxInvoiceFragment : Fragment() {

    private lateinit var paymentTerms: ArrayList<PaymentTerm>
    private lateinit var binding: FragmentSaleOrderTaxInvoiceBinding
    private val args by navArgs<TaxInvoiceFragmentArgs>()
    private lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentSaleOrderTaxInvoiceBinding.inflate(inflater, container, false)
            Main.app.clearTaxInvoice()
            Main.app.getTaxInvoice()
            Main.app.getTaxInvoice()?.SalesInvoice?.CreatorUserId = Main.app.getSession().userId
            Main.app.getTaxInvoice()?.SalesInvoice?.CustomerId = customer.id
            Main.app.getTaxInvoice()?.SalesInvoice?.LocationId =
                Main.app.getSession().defaultLocationId
            Main.app.getTaxInvoice()?.SalesInvoice?.SalespersonId =
                Main.app.getSession().salesPersonId
            Main.app.getTaxInvoice()?.SalesInvoice?.CreationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.SalesInvoice?.DeliveryOrderDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceDueDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setHeaderData()
        setCustomerData()
        getPaymentTerms()
        binding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.date.tag = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.dateSelectionView.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    binding.date.setText("$day-$month-$year")
                    Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

    private fun setHeaderData() {
        binding.header.setBackText("Tax Invoice")
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun setCustomerData() {
        binding.txtGroup.text = makeTextBold(
            text = getString(R.string.prefix_group, customer.group), startIndex = 8
        )
        binding.txtCustomerName.text = customer.name
        binding.txtAddress.text = customer.address1
        binding.txtAccountNo.text = makeTextBold(
            text = getString(R.string.prefix_account_no, customer.customerCode), startIndex = 8
        )

        binding.txtArea.text =
            makeTextBold(text = getString(R.string.prefix_area, customer.area), startIndex = 7)

    }

    private fun getPaymentTerms() {
        if (::paymentTerms.isInitialized.not())
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity()))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        (response?.body() as RetailResponse<PaymentTermsResult>).result?.items?.let {
                            paymentTerms = it
                            setEquipmentTypesAdapter()
                        }
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Unable to get equipment list")
                    }
                }).enque(Network.api()?.getPaymentTerms()).execute()
    }

    private fun setEquipmentTypesAdapter() {
        val adapter = ArrayAdapter(
            requireActivity(), R.layout.simple_text_item, paymentTerms
        )
        binding.spinnerPaymentTerms.adapter = adapter
    }

    private fun addOnClickListener() {
        binding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_tax_invoice_to_open_tax_invoice_product_list,
                bundleOf(Constants.Customer to Gson().toJson(customer))
            )
        }

        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_tax_invoice_to_open_products_summary
            )
        }

        binding.btnClearSign.setOnClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSave.setOnClickListener {
            if (binding.inputCustomerName.text.toString().isEmpty()) {
                Notify.toastLong("Please enter customer name")
                return@setOnClickListener
            }

            if (Main.app.getTaxInvoice()?.SalesInvoiceDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }

            Main.app.getTaxInvoice()?.serializeItems()
            Main.app.getTaxInvoice()?.SalesInvoice?.CustomerName =
                binding.inputCustomerName.text.toString()
            Main.app.getTaxInvoice()?.SalesInvoice?.PaymentTermName =
                getSelectedPaymentTerm().displayText
            Main.app.getTaxInvoice()?.SalesInvoice?.PaymentTermId = getSelectedPaymentTerm().value
            uploadSignature()
        }
    }

    private fun getSelectedPaymentTerm(): PaymentTerm {
        return paymentTerms[binding.spinnerPaymentTerms.selectedItemPosition]
    }


    private fun uploadSignature() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                    val signature = result.result
                    Main.app.getTaxInvoice()?.SalesInvoice?.Signature = signature?.filePath
                    saveTaxInvoice()
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to upload signature")
                }
            }).enque(Network.api()?.uploadSignature(getMultipartSignatureFile())).execute()
    }

    private fun saveTaxInvoice() {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<Any>
                    if (result.success == true) {
                        Notify.toastLong("Tax Invoice Created")
                        findNavController().popBackStack()
                    } else {
                        Notify.toastLong("Unable create tax invoice: ${result.result}")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable create tax invoice")
                }
            }).enque(Network.api()?.createUpdateSaleInvoice(Main.app.getTaxInvoice()!!)).execute()
    }

    private fun getMultipartSignatureFile(): MultipartBody.Part {
        val file = File(requireActivity().cacheDir, Date().toString() + "Signature.jpeg")
        val fos = FileOutputStream(file)
        binding.signaturePad.signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        val filePart = MultipartBody.Part.createFormData(
            "file", file.getName(), RequestBody.create(
                MediaType.parse("image/jpeg"), file
            )
        )
        return filePart
    }

}