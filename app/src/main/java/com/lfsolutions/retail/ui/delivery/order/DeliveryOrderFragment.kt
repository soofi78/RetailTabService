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
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentDeliveryOrderBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
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

class DeliveryOrderFragment : Fragment() {

    private lateinit var paymentTypes: ArrayList<PaymentType>
    private lateinit var binding: FragmentDeliveryOrderBinding
    private val args by navArgs<DeliveryOrderFragmentArgs>()
    private lateinit var customer: Customer
    private var isSaveBtnClicked=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDeliveryOrderBinding.inflate(inflater, container, false)
            if (Main.app.getDeliveryOrder()?.deliveryOrder?.salesOrderId == null) Main.app.clearDeliveryOrder()
            Main.app.getDeliveryOrder()?.deliveryOrder?.creatorUserId = Main.app.getSession().userId
            Main.app.getDeliveryOrder()?.deliveryOrder?.customerId = customer.id
            Main.app.getDeliveryOrder()?.deliveryOrder?.customerServiceToVisitId = customer.customerServiceToVisitId
            Main.app.getDeliveryOrder()?.deliveryOrder?.locationId =
                Main.app.getSession().defaultLocationId
            Main.app.getDeliveryOrder()?.deliveryOrder?.salesPersonId =
                Main.app.getSession().salesPersonId
            /*Main.app.getDeliveryOrder()?.deliveryOrder?.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")*/
            Main.app.getDeliveryOrder()?.deliveryOrder?.deliveryDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")

            //println("DeliveryOrder: ${Main.app.getDeliveryOrder()?.deliveryOrder}")
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
                    Main.app.getDeliveryOrder()?.deliveryOrder?.deliveryDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

    private fun setHeaderData() {
        binding.header.setBackText("Delivery Order")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            Main.app.clearDeliveryOrder()
            close()
        }
        binding.btnCancel.setDebouncedClickListener {
            Main.app.clearDeliveryOrder()
            close()
        }
    }

    private fun setCustomerData() {
        customer.let { binding.customerView.setCustomer(it) }
        binding.customerView.setDebouncedClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager, customer)
        }
    }

    private fun setPaymentTermsAdapter() {
        val adapter = ArrayAdapter(
            requireActivity(), R.layout.simple_text_item, paymentTypes
        )
        binding.spinnerPaymentTerms.adapter = adapter
    }

    private fun addOnClickListener() {
        binding.btnLoadProducts.setDebouncedClickListener {
            if (Main.app.getDeliveryOrder()?.deliveryOrderDetail.isNullOrEmpty().not()) {
                Notify.toastLong("Please clear the cart first!")
                return@setDebouncedClickListener
            }
            loadProducts()
        }
        binding.btnOpenEquipmentList.setDebouncedClickListener {
            findNavController().navigate(
                R.id.action_navigation_delivery_order_to_delivery_order_product_list,
                bundleOf(Constants.Customer to Gson().toJson(customer))
            )
        }

        binding.btnViewOrder.setDebouncedClickListener {
            findNavController().navigate(
                R.id.action_navigation_delivery_order_to_open_products_summary
            )
        }

        binding.btnClearSign.setDebouncedClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSave.setDebouncedClickListener {

            if (Main.app.getDeliveryOrder()?.deliveryOrderDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setDebouncedClickListener
            }

            if (binding.signaturePad.isEmpty) {
                Notify.toastLong("Please add your signature")
                return@setDebouncedClickListener
            }
             if(!isSaveBtnClicked){
                 isSaveBtnClicked=true
                 Main.app.getDeliveryOrder()?.serializeItems()
                 Main.app.getDeliveryOrder()?.deliveryOrder?.customerName =
                     binding.inputCustomerName.text.toString()
                 uploadSignature()
             }
        }
    }

    private fun loadProducts() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Products..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<EquipmentListResult>)
                    Notify.toastLong("Product List Updated")
                    result.result?.items?.let { mapProduct(it) }
                }
                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to load products")
                }
            }).enque(
                Network.api()?.getCustomerProduct(
                    LocationIdCustomerIdRequestObject(
                        Main.app.getSession().defaultLocationId, customer.id
                    )
                )
            ).execute()
    }

    private fun mapProduct(result: ArrayList<Product>) {
        result.forEach { product ->
            val qty = product.qty ?: 0.0
            val subTotal = (qty * (product.cost ?: 0.0))
            val discount = 0.0
            val taxAmount = (product.getApplicableTaxRate().toDouble() / 100.0)
            val netTotal = (subTotal - discount) + taxAmount
            val total = (subTotal + taxAmount)
            Main.app.getDeliveryOrder()?.addEquipment(
                DeliveryOrderDetails(
                    productId = product.productId?.toInt() ?: 0,
                    inventoryCode = product.inventoryCode,
                    productName = product.productName,
                    productImage = product.imagePath,
                    unitId = product.unitId,
                    uom = product.unitName,
                    deliverQty = qty,
                    qty = 0.0,
                    cost = product.cost,
                    costWithoutTax = product.cost ?: 0.0,
                    /*creationTime = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                        .replace(" ", "T").plus("Z"),*/
                    creatorUserId = Main.app.getSession().userId
                )
            )
        }
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
                        Printer.askForPrint(requireActivity(), {
                            result.result?.id?.let { getSaleOrderDetail(it) }
                        }, {
                            close()
                        })
                    } else {
                        Notify.toastLong("Unable create delivery order: ${result.result}")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable create delivery order")
                }
            }).enque(Network.api()?.createDeliveryOrder(Main.app.getDeliveryOrder()!!)).execute()
    }

    private fun close() {
        findNavController().popBackStack()
    }

    private fun getSaleOrderDetail(id: Int) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Order Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val order = (response?.body() as BaseResponse<DeliveryOrderDTO>).result
                    Printer.printDeliveryOrder(requireActivity(), order)
                    close()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to print delivery order")
                    close()
                }
            }).enque(
                Network.api()?.getDeliveryOrderDetails(IdRequest(id))
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
        Main.app.clearDeliveryOrder()
    }

}