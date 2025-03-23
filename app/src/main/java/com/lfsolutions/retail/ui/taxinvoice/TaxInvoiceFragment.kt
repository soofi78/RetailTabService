package com.lfsolutions.retail.ui.taxinvoice

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.setDebouncedClickListener
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

    //    private lateinit var paymentTypes: ArrayList<PaymentType>
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
            if (Main.app.getTaxInvoice()?.salesInvoice?.salesOrderId == null) Main.app.clearTaxInvoice()
            Main.app.getTaxInvoice()?.salesInvoice?.creatorUserId = Main.app.getSession().userId
            Main.app.getTaxInvoice()?.salesInvoice?.customerId = customer.id
            Main.app.getTaxInvoice()?.salesInvoice?.locationId =
                Main.app.getSession().defaultLocationId
            Main.app.getTaxInvoice()?.salesInvoice?.salespersonId =
                Main.app.getSession().salesPersonId
            Main.app.getTaxInvoice()?.salesInvoice?.isTaxInclusive = customer.isTaxInclusive
            if (customer.isTaxInclusive == true) {
                Main.app.getTaxInvoice()?.salesInvoice?.type = "I"
            } else {
                Main.app.getTaxInvoice()?.salesInvoice?.type = "N"
            }
            Main.app.getTaxInvoice()?.salesInvoice?.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.salesInvoice?.deliveryOrderDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.salesInvoice?.invoiceDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getTaxInvoice()?.salesInvoice?.invoiceDueDate =
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
        binding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.date.tag = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.dateSelectionView.setDebouncedClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    binding.date.text = "$day-$month-$year"
                    Main.app.getTaxInvoice()?.salesInvoice?.invoiceDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

//    private fun getPaymentTerms() {
//        NetworkCall.make().setCallback(object : OnNetworkResponse {
//            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
//                val result = response?.body() as BaseResponse<PaymentTermsResult>
//                result.result?.items?.let {
//                    paymentTypes.addAll(it)
//                }
//                setPaymentTermsAdapter()
//            }
//
//            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
//                Notify.toastLong("Unable to load payment types")
//            }
//        }).autoLoadigCancel(Loading().forApi(requireActivity()))
//            .enque(Network.api()?.getPaymentTerms()).execute()
//    }

    private fun setHeaderData() {
        binding.header.setBackText("Sale Invoice")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick { findNavController().popBackStack() }
        binding.btnCancel.setDebouncedClickListener { findNavController().popBackStack() }
    }

    private fun setCustomerData() {
        customer.let { binding.customerView.setCustomer(it) }
        binding.customerView.setDebouncedClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager, customer)
        }
    }

//    private fun setPaymentTermsAdapter() {
//        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_text_item, paymentTypes)
//        binding.spinnerPaymentTerms.adapter = adapter
//    }

    private fun addOnClickListener() {
        binding.btnLoadProducts.setDebouncedClickListener {
            if (Main.app.getTaxInvoice()?.salesInvoiceDetail.isNullOrEmpty().not()) {
                Notify.toastLong("Please clear the cart first!")
                return@setDebouncedClickListener
            }
            loadProducts()
        }
        binding.btnOpenEquipmentList.setDebouncedClickListener {
            findNavController().navigate(
                R.id.action_navigation_tax_invoice_to_open_tax_invoice_product_list,
                bundleOf(Constants.Customer to Gson().toJson(customer))
            )
        }

        binding.btnViewOrder.setDebouncedClickListener {
            findNavController().navigate(
                R.id.action_navigation_tax_invoice_to_open_products_summary
            )
        }

        binding.btnClearSign.setDebouncedClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSave.setDebouncedClickListener {
            if (Main.app.getTaxInvoice()?.salesInvoiceDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setDebouncedClickListener
            }

            if (binding.signaturePad.isEmpty) {
                Notify.toastLong("Please add your signature")
                return@setDebouncedClickListener
            }

            Main.app.getTaxInvoice()?.serializeItems()
            Main.app.getTaxInvoice()?.salesInvoice?.customerName =
                binding.inputCustomerName.text.toString()
            if (Main.app.getTaxInvoice()?.salesInvoice?.paymentTermId == null) Main.app.getTaxInvoice()?.salesInvoice?.paymentTermId =
                customer.paymentTermId
            if (Main.app.getTaxInvoice()?.salesInvoice?.paymentTermName == null) Main.app.getTaxInvoice()?.salesInvoice?.paymentTermName =
                customer.paymentTerm
            uploadSignature()
        }
    }

    private fun loadProducts() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Products..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<EquipmentListResult>)
                    Notify.toastLong("Product List Updated")
                    result.result?.items?.let { updateSaleInvoiceDetails(it) }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to load products")
                }
            }).enque(
                Network.api()?.getCustomerProduct(
                    LocationIdCustomerIdRequestObject(
                        Main.app.getSession().defaultLocationId, customer.id, true
                    )
                )
            ).execute()
    }

    private fun updateSaleInvoiceDetails(result: ArrayList<Product>) {
        result.forEach { product ->
            val qty = product.qty ?: 0.0
            val subTotal = (qty * (product.cost ?: 0.0))
            val discount = 0.0
            val taxAmount = subTotal * (product.getApplicableTaxRate().toDouble() / 100.0)
            val netTotal = (subTotal - discount) + taxAmount
            val total = (subTotal + taxAmount)
            Main.app.getTaxInvoice()?.addEquipment(
                SalesInvoiceDetail(
                    productId = product.productId?.toInt() ?: 0,
                    inventoryCode = product.inventoryCode,
                    productName = product.productName,
                    productImage = product.imagePath,
                    unitId = product.unitId,
                    unitName = product.unitName,
                    qty = qty,
                    qtyStock = product.qtyOnHand,
                    price = subTotal,
                    netCost = total,
                    costWithoutTax = product.cost ?: 0.0,
                    taxRate = product.getApplicableTaxRate().toDouble(),
                    departmentId = 0,
                    lastPurchasePrice = 0.0,
                    sellingPrice = 0.0,
                    mrp = 0,
                    isBatch = false,
                    itemDiscount = 0.0,
                    itemDiscountPerc = 0.0,
                    averageCost = 0.0,
                    netDiscount = 0.0,
                    subTotal = subTotal,
                    netTotal = netTotal,
                    tax = taxAmount,
                    totalValue = subTotal,
                    isFOC = false,
                    isExchange = false,
                    isExpire = false,
                    creationTime = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                        .replace(" ", "T").plus("Z"),
                    creatorUserId = Main.app.getSession().userId.toString()
                ).apply {
                    product.applicableTaxes?.let {
                        applicableTaxes = it
                        taxForProduct = it
                    }
                })
        }
    }


    private fun uploadSignature() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                    val signature = result.result
                    Main.app.getTaxInvoice()?.salesInvoice?.signature = signature?.filePath
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
                    val result = response?.body() as BaseResponse<Invoice>
                    if (result.success == true) {
                        Main.app.clearTaxInvoice()
                        Notify.toastLong("Sale Invoice: " + result.result?.transactionNo)
                        Printer.askForPrint(requireActivity(), {
                            result.result?.id?.let { getSaleInvoiceDetail(it) }
                        }, {
                            findNavController().popBackStack()
                        })
                    } else {
                        Notify.toastLong("Unable create Sale invoice: ${result.result}")
                    }
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable create sale invoice")
                }
            }).enque(Network.api()?.createUpdateSaleInvoice(Main.app.getTaxInvoice()!!)).execute()
    }

    private fun getSaleInvoiceDetail(id: Int) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Invoice Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val invoice = (response?.body() as BaseResponse<SaleInvoiceObject>).result
                    Printer.printInvoice(requireActivity(), invoice)
                    findNavController().popBackStack()
                    findNavController().navigate(
                        R.id.action_navigation_tax_invoice_to_invoice_details_payment,
                        bundleOf("id" to invoice?.salesInvoice?.id.toString(), "pay" to true)
                    )
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to print, Please go to details and try again!")
                    findNavController().popBackStack()
                }
            }).enque(
                Network.api()?.getSaleInvoiceDetail(IdRequest(id))
            ).execute()
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

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearTaxInvoice()
    }

}