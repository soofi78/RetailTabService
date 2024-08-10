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
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.makeTextBold
import com.lfsolutions.retail.util.DateTime
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
        binding.header.setBackText("Sale Order")
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun setCustomerData() {
        binding.txtGroup.text =
            makeTextBold(
                text = getString(R.string.prefix_group, customer.group),
                startIndex = 8
            )
        binding.txtCustomerName.text = customer.name
        binding.txtAddress.text = customer.address1
        binding.txtAccountNo.text =
            makeTextBold(
                text = getString(R.string.prefix_account_no, customer.customerCode),
                startIndex = 8
            )

        binding.txtArea.text =
            makeTextBold(text = getString(R.string.prefix_area, customer.area), startIndex = 7)

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
            if (Main.app.getSaleOrder()?.SalesOrderDetail?.size == 0) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }

            Main.app.getSaleOrder()?.serializeItems()
            Main.app.getSaleOrder()?.SalesOrder?.CustomerName =
                binding.inputCustomerName.text.toString()
            Main.app.getSaleOrder()?.SalesOrder?.PaymentTermId = customer.paymentTermId
            uploadSignature()
        }

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