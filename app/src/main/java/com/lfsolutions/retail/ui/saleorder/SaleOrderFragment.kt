package com.lfsolutions.retail.ui.saleorder

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.Customer
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
import com.videotel.digital.util.Notify
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class SaleOrderFragment : Fragment() {

    private lateinit var binding: FragmentSaleOrderTaxInvoiceBinding
    private val args by navArgs<SaleOrderFragmentArgs>()
    private lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::binding.isInitialized.not()) {
            binding = FragmentSaleOrderTaxInvoiceBinding.inflate(inflater, container, false)
            Main.app.clearSaleOrder()
            Main.app.getSaleOrder()
            Main.app.getSaleOrder()?.SalesOrder?.CreatorUserId = Main.app.getSession().userId
            Main.app.getSaleOrder()?.SalesOrder?.CustomerId = customer.id
            Main.app.getSaleOrder()?.SalesOrder?.LocationId =
                Main.app.getSession().defaultLocationId
            Main.app.getSaleOrder()?.SalesOrder?.SalespersonId = Main.app.getSession().salesPersonId
            Main.app.getSaleOrder()?.SalesOrder?.CreationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getSaleOrder()?.SalesOrder?.DeliveryDate =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getSaleOrder()?.SalesOrder?.SoDate =
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
        binding.dateLabel.text= getString(R.string.order_date)
        binding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.date.tag = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.dateSelectionView.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    binding.date.setText("$day-$month-$year")
                    Main.app.getSaleOrder()?.SalesOrder?.SoDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

    private fun setHeaderData() {
        binding.header.setBackText("Sale Order")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun setCustomerData() {
        customer?.let { binding.customerView.setCustomer(it) }
        binding.customerView.setOnClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager,customer)
        }
    }

    private fun addOnClickListener() {
        binding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_sale_order_to_product_list,
                Bundle().apply {
                    putString(Constants.Customer, Gson().toJson(customer))
                })
        }
        binding.btnLoadProducts.visibility = View.GONE
        binding.btnViewOrder.setText("View Sale Order")
        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_sale_order_to_product_summary)
        }
        binding.btnClearSign.setOnClickListener {
            binding.signaturePad.clear()
        }
        binding.btnSave.setOnClickListener {
            save(false)
        }

        binding.btnSaveApprove.setOnClickListener {
            save(true)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        if (Main.app.getSession().isSupervisor == true) {
            binding.btnSaveApprove.visibility = View.VISIBLE
        } else {
            binding.btnSaveApprove.visibility = View.GONE
        }


    }

    private fun save(approve: Boolean = false) {
        if (Main.app.getSaleOrder()?.SalesOrderDetail?.size == 0) {
            Notify.toastLong("Please add products")
            return
        }

        if (binding.signaturePad.isEmpty) {
            Notify.toastLong("Please add your signature")
            return
        }

        Main.app.getSaleOrder()?.serializeItems()
        Main.app.getSaleOrder()?.SalesOrder?.PoNo = binding.poNumber.text.toString()
        Main.app.getSaleOrder()?.SalesOrder?.CustomerName =
            binding.inputCustomerName.text.toString()
        Main.app.getSaleOrder()?.SalesOrder?.PaymentTermId = customer.paymentTermId
        Main.app.getSaleOrder()?.SalesOrder?.Status = if (approve) "A" else null
        uploadSignature()
    }

    private fun uploadSignature() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                    val signature = result.result
                    Main.app.getSaleOrder()?.SalesOrder?.Signature = signature?.filePath
                    saveSaleOrder()
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to upload signature")
                }
            }).enque(Network.api()?.uploadSignature(getMultipartSignatureFile())).execute()
    }

    private fun saveSaleOrder() {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<Any>
                    if (result.success == true) {
                        Main.app.clearSaleOrder()
                        Notify.toastLong("Sale Order Created")
                        findNavController().popBackStack()
                    } else {
                        Notify.toastLong("Unable create Sale Order: ${result.result}")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable create Sale Order")
                }
            }).enque(Network.api()?.createUpdateSaleOrder(Main.app.getSaleOrder()!!)).execute()
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