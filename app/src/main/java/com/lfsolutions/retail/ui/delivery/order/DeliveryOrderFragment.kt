package com.lfsolutions.retail.ui.delivery.order

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
import com.lfsolutions.retail.databinding.FragmentDeliveryOrderBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.customer.CustomerDetailActivity
import com.lfsolutions.retail.ui.widgets.options.OnOptionItemClick
import com.lfsolutions.retail.ui.widgets.options.OptionItem
import com.lfsolutions.retail.ui.widgets.options.OptionsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class DeliveryOrderFragment : Fragment() {

    private lateinit var paymentTypes: ArrayList<PaymentType>
    private lateinit var binding: FragmentDeliveryOrderBinding
    private val args by navArgs<DeliveryOrderFragmentArgs>()
    private lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDeliveryOrderBinding.inflate(inflater, container, false)
            Main.app.clearDeliveryOrder()
            Main.app.getDeliveryOrder()
            Main.app.getDeliveryOrder()?.deliveryOrder?.creatorUserId = Main.app.getSession().userId
            Main.app.getDeliveryOrder()?.deliveryOrder?.customerId = customer.id
            Main.app.getDeliveryOrder()?.deliveryOrder?.locationId =
                Main.app.getSession().defaultLocationId
            Main.app.getDeliveryOrder()?.deliveryOrder?.salesPersonId =
                Main.app.getSession().salesPersonId
            Main.app.getDeliveryOrder()?.deliveryOrder?.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getDeliveryOrder()?.deliveryOrder?.deliveryDate =
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
                    Main.app.getDeliveryOrder()?.deliveryOrder?.deliveryDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

    private fun setHeaderData() {
        binding.header.setBackText("Delivery Order")
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setCustomerData() {
        customer.let { binding.customerView.setCustomer(it) }
        binding.customerView.setOnClickListener {
            OptionsBottomSheet.show(
                requireActivity().supportFragmentManager,
                arrayListOf(OptionItem("View Customer", R.drawable.person_black)),
                object : OnOptionItemClick {
                    override fun onOptionItemClick(optionItem: OptionItem) {
                        customer.let { it1 ->
                            CustomerDetailActivity.start(
                                requireActivity(),
                                it1
                            )
                        }
                    }
                })
        }
    }

    private fun setPaymentTermsAdapter() {
        val adapter = ArrayAdapter(
            requireActivity(), R.layout.simple_text_item, paymentTypes
        )
        binding.spinnerPaymentTerms.adapter = adapter
    }

    private fun addOnClickListener() {
        binding.btnLoadProducts.setOnClickListener {
            if (Main.app.getDeliveryOrder()?.deliveryOrderDetail.isNullOrEmpty().not()) {
                Notify.toastLong("Please clear the cart first!")
                return@setOnClickListener
            }
            loadProducts()
        }
        binding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_delivery_order_to_delivery_order_product_list,
                bundleOf(Constants.Customer to Gson().toJson(customer))
            )
        }

        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_delivery_order_to_open_products_summary
            )
        }

        binding.btnClearSign.setOnClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSave.setOnClickListener {
            if (Main.app.getDeliveryOrder()?.deliveryOrderDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }

            if (binding.signaturePad.isEmpty) {
                Notify.toastLong("Please add your signature")
                return@setOnClickListener
            }

            Main.app.getDeliveryOrder()?.serializeItems()
            Main.app.getDeliveryOrder()?.deliveryOrder?.customerName =
                binding.inputCustomerName.text.toString()
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
            val qty = product.qty ?: 0.0
            val subTotal = (qty * (product.cost ?: 0.0))
            val discount = 0.0
            val taxAmount = (product.getApplicableTaxRate().toDouble() / 100.0)
            val netTotal = (subTotal - discount) + taxAmount
            val total = (subTotal + taxAmount)
            Main.app.getDeliveryOrder()?.addEquipment(
                deliveryOrderDetail(
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
                    CostWithoutTax = product.cost ?: 0.0,
                    DepartmentId = 0,
                    LastPurchasePrice = 0.0,
                    SellingPrice = 0.0,
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
                )
            )
        }
    }

    private fun deliveryOrderDetail(
        ProductId: Int,
        InventoryCode: String?,
        ProductName: String?,
        ProductImage: String?,
        UnitId: Int?,
        UnitName: String?,
        Qty: Double,
        QtyStock: Double?,
        Price: Double,
        NetCost: Double,
        CostWithoutTax: Double,
        DepartmentId: Int,
        LastPurchasePrice: Double,
        SellingPrice: Double,
        MRP: Int,
        IsBatch: Boolean,
        ItemDiscount: Double,
        ItemDiscountPerc: Double,
        AverageCost: Int,
        NetDiscount: Double,
        SubTotal: Double,
        NetTotal: Double,
        Tax: Double,
        TotalValue: Double,
        IsFOC: Boolean,
        IsExchange: Boolean,
        IsExpire: Boolean,
        CreationTime: String,
        CreatorUserId: Int?
    ): DeliveryOrderDetails {
        return DeliveryOrderDetails()
    }


    private fun uploadSignature() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                    val signature = result.result
                    Main.app.getDeliveryOrder()?.deliveryOrder?.signature = signature?.filePath
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
                    val result = response?.body() as BaseResponse<DeliverOrderDetail>
                    if (result.success == true) {
                        Main.app.clearDeliveryOrder()
                        Notify.toastLong("Delivery order Created " + result.result?.transactionNo)
                        findNavController().popBackStack()
                    } else {
                        Notify.toastLong("Unable create delivery order: ${result.result}")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable create delivery order")
                }
            }).enque(Network.api()?.createDeliveryOrder(Main.app.getDeliveryOrder()!!))
            .execute()
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