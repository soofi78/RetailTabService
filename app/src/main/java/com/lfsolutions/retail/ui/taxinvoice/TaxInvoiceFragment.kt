package com.lfsolutions.retail.ui.taxinvoice

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
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

    private lateinit var paymentTypes: ArrayList<PaymentType>
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

    private fun setPaymentTermsAdapter() {
        val adapter = ArrayAdapter(
            requireActivity(), R.layout.simple_text_item, paymentTypes
        )
        binding.spinnerPaymentTerms.adapter = adapter
    }

    private fun addOnClickListener() {
        binding.btnLoadProducts.setOnClickListener {
            if (Main.app.getTaxInvoice()?.SalesInvoiceDetail.isNullOrEmpty().not()) {
                Notify.toastLong("Please clear the cart first!")
                return@setOnClickListener
            }
            loadProducts()
        }
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
            if (Main.app.getTaxInvoice()?.SalesInvoiceDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }

            Main.app.getTaxInvoice()?.serializeItems()
            Main.app.getTaxInvoice()?.SalesInvoice?.CustomerName =
                binding.inputCustomerName.text.toString()
            Main.app.getTaxInvoice()?.SalesInvoice?.PaymentTermId = customer.paymentTermId
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
                Network.api()?.getProductForTaxInvoice(
                    LocationIdCustomerIdRequestObject(
                        Main.app.getSession().defaultLocationId,
                        customer.id
                    )
                )
            ).execute()
    }

    private fun updateSaleInvoiceDetails(result: ArrayList<Product>) {
        result.forEach { product ->
            val qty = 1
            val subTotal = (qty * (product?.cost ?: 0)).toDouble()
            val discount = 0.0
            val taxAmount = subTotal * (product.getApplicableTaxRate().toDouble() / 100.0)
            val netTotal = (subTotal - discount) + taxAmount
            val total = (subTotal + taxAmount)
            Main.app.getTaxInvoice()?.addEquipment(
                SalesInvoiceDetail(
                    ProductId = product.productId?.toInt() ?: 0,
                    InventoryCode = product.inventoryCode,
                    ProductName = product.productName,
                    ProductImage = product.imagePath,
                    UnitId = product.unitId,
                    UnitName = product.unitName,
                    Qty = qty,
                    QtyStock = product.qtyOnHand,
                    Price = subTotal,
                    NetCost = total,
                    CostWithoutTax = product.cost?.toDouble() ?: 0.0,
                    DepartmentId = 0,
                    LastPurchasePrice = 0,
                    SellingPrice = 0,
                    MRP = 0,
                    IsBatch = false,
                    ItemDiscount = 0.0,
                    ItemDiscountPerc = 0.0,
                    AverageCost = 0,
                    NetDiscount = 0.0,
                    SubTotal = subTotal,
                    NetTotal = netTotal,
                    Tax = taxAmount,
                    TotalValue = subTotal,
                    IsFOC = false,
                    IsExchange = false,
                    IsExpire = false,
                    CreationTime = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                        .replace(" ", "T").plus("Z"),
                    CreatorUserId = Main.app.getSession().userId
                ).apply {
                    product.applicableTaxes?.let {
                        ApplicableTaxes = it
                        TaxForProduct = it
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
                        Main.app.clearTaxInvoice()
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