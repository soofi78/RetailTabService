package com.lfsolutions.retail.ui.serviceform

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentServiceFormBinding
import com.lfsolutions.retail.model.ComplaintServiceResponse
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.product.Asset
import com.lfsolutions.retail.model.product.AssetResult
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.model.service.FeedbackTypeResult
import com.lfsolutions.retail.model.service.FeedbackTypes
import com.lfsolutions.retail.model.service.ReportTypeResult
import com.lfsolutions.retail.model.service.ReportTypes
import com.lfsolutions.retail.model.service.ServiceTypeResult
import com.lfsolutions.retail.model.service.ServiceTypes
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailsBottomSheet
import com.lfsolutions.retail.ui.widgets.FeedbackItemView
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DateTime.extractOnlyDate
import com.lfsolutions.retail.util.DateTime.getDateFromString
import com.lfsolutions.retail.util.DateTime.getFormattedDisplayDateTime
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

class ServiceFormFragment : Fragment() {

    private val allocatedAssets = ArrayList<Asset>()
    private val args by navArgs<ServiceFormFragmentArgs>()
    private lateinit var customer: Customer
    private val feedbacks = ArrayList<Feedback>()
    private val feedbacksTypes = ArrayList<FeedbackTypes>()
    private lateinit var binding: FragmentServiceFormBinding
    private var serviceTypes = ArrayList<ServiceTypes>()
    private var reportTypes = ArrayList<ReportTypes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentServiceFormBinding.inflate(layoutInflater, container, false)
            Main.app.getComplaintService()
            Main.app.getComplaintService()?.complaintService?.locationId =
                Main.app.getSession().defaultLocationId
            /*Main.app.getComplaintService()?.complaintService?.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")*/
            Main.app.getComplaintService()?.complaintService?.creatorUserId =
                Main.app.getSession().userId.toString()

            binding.txtRcpntName.text = Main.app.getSession().userName
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Main.app.getComplaintService()?.complaintService?.customerId = customer.id
        setData()
        getServiceTypeData()
        setClickListener()
        getFeedbackTypeData()
        getAllocatedAssets()
    }

    private fun setData() {
        setHeaderData()
        setCustomerData()

        val currentDateTime = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T").plus("Z")
        val complaintService = Main.app.getComplaintService()?.complaintService
        val checkInTime = complaintService?.timeIn
        if (!checkInTime.isNullOrEmpty()) {
            binding.checkinTime.text = checkInTime.getFormattedDisplayDateTime()
            binding.checkinTime.tag = checkInTime
        } else {
            Main.app.getComplaintService()?.complaintService?.timeIn = currentDateTime
            binding.checkinTime.text = currentDateTime.getFormattedDisplayDateTime()
            binding.checkinTime.tag = currentDateTime

        }

        val checkOutTime = complaintService?.timeOut
        if (!checkOutTime.isNullOrEmpty()) {
            binding.checkoutTime.text = checkOutTime.getFormattedDisplayDateTime()
            binding.checkoutTime.tag = checkOutTime
        } else {
            Main.app.getComplaintService()?.complaintService?.timeOut = currentDateTime
            binding.checkoutTime.text = currentDateTime.getFormattedDisplayDateTime()
            binding.checkoutTime.tag = currentDateTime

        }
        val csDateTime = complaintService?.csDateTime
        //Main.app.getComplaintService()?.complaintService?.csDate=DateTime.getCurrentDateTime(DateTime.DateRetailApiFormate)
        if (!csDateTime.isNullOrEmpty()) {
            Main.app.getComplaintService()?.complaintService?.csDate=csDateTime.extractOnlyDate()
            binding.complaintDateTimeText.text = csDateTime.getFormattedDisplayDateTime()
            binding.complaintDateTimeText.tag = csDateTime.extractOnlyDate()
        } else {
            Main.app.getComplaintService()?.complaintService?.csDateTime = currentDateTime
            Main.app.getComplaintService()?.complaintService?.csDate = currentDateTime.extractOnlyDate() /*DateTime.getCurrentDateTime(DateTime.DateRetailApiFormate)*/
            binding.complaintDateTimeText.text = currentDateTime.getFormattedDisplayDateTime()
            binding.complaintDateTimeText.tag = currentDateTime.extractOnlyDate()
        }

    }

    private fun getFeedbackTypeData() {
        if (feedbacksTypes.isEmpty().not()) return

        val loading = Loading().forApi(requireActivity())
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                feedbacksTypes.clear()
                (response?.body() as RetailResponse<FeedbackTypeResult>).result?.items?.let {
                    feedbacksTypes.addAll(it)
                }
                getFeedbackData()
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

            }
        }).setTag("FEEDBACK").autoLoadigCancel(loading).enque(Network.api()?.getFeedbackTypes())
            .execute()
    }

    private fun getAllocatedAssets() {
        if (allocatedAssets.isEmpty().not()) {
            setAssetData()
            return
        }

        val loading = Loading().forApi(requireActivity())
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                allocatedAssets.clear()
                (response?.body() as BaseResponse<AssetResult>).result?.items?.let {
                    allocatedAssets.addAll(it)
                }
                setAssetData()
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

            }
        }).setTag("Allocated Assets").autoLoadigCancel(loading).enque(
            Network.api()?.getAllocatedAssets(
                HistoryRequest(
                    sorting = "Id ASC",
                    status = null,
                    invoiceType = null,
                    maxResultCount = 1000,
                    locationId = 0,
                    filter = null,
                    customerId = customer.id.toString()
                )
            )
        ).execute()
    }

    private fun setAssetData() {
        binding.allocatedAssets.visibility =
            if (allocatedAssets.isEmpty()) View.GONE else View.VISIBLE
        binding.allocatedAssetsList.adapter = AllocatedAssetsAdapter(allocatedAssets)
    }

    private fun getServiceTypeData() {
        if (serviceTypes.isEmpty()) {
            NetworkCall.make().setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ServiceTypeResult>).result?.items?.let {
                        serviceTypes.addAll(it)
                    }
                    setServiceTypeAdapter()
                    getReportTypeData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
                .enque(Network.api()?.getServiceType()).execute()
        } else {
            setServiceTypeAdapter()
        }
    }

    private fun getReportTypeData() {
        if (reportTypes.isEmpty()) {
            NetworkCall.make().setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ReportTypeResult>).result?.items?.let {
                        reportTypes.addAll(it)
                    }
                    setReportTypeAdapter()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
                .enque(Network.api()?.getReportType()).execute()
        } else {
            setReportTypeAdapter()
        }
    }

    private fun setServiceTypeAdapter() {
        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_text_item, serviceTypes)
        binding.spinnerType.adapter = adapter
    }

    private fun setReportTypeAdapter() {
        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_text_item, reportTypes)
        binding.reportTypeSpinner.adapter = adapter
    }

    private fun setClickListener() {

        binding.btnCheckIn.setDebouncedClickListener {
            DateTime.showDateTimePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateTimeSelected(
                    isoDateTime: String,
                    displayDateTime: String,
                    date: String
                ) {
                    binding.checkinTime.text = displayDateTime
                    binding.checkinTime.tag = isoDateTime
                    Main.app.getComplaintService()?.complaintService?.timeIn = isoDateTime
                    //println("complaintService:${Main.app.getComplaintService()?.complaintService}")
                }
            })
        }

        binding.btnCheckOut.setDebouncedClickListener {
            DateTime.showDateTimePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateTimeSelected(
                    isoDateTime: String,
                    displayDateTime: String,
                    date: String
                ) {
                    binding.checkoutTime.text = displayDateTime
                    binding.checkoutTime.tag = isoDateTime
                    Main.app.getComplaintService()?.complaintService?.timeOut = isoDateTime
                    //println("complaintService:${Main.app.getComplaintService()?.complaintService}")
                }
            })
        }

        binding.complaintDateTimeText.setDebouncedClickListener {
            DateTime.showDateTimePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateTimeSelected(
                    isoDateTime: String,
                    displayDateTime: String,
                    date: String
                ) {
                    binding.complaintDateTimeText.text = displayDateTime
                    binding.complaintDateTimeText.tag = isoDateTime
                    Main.app.getComplaintService()?.complaintService?.csDateTime = isoDateTime
                    Main.app.getComplaintService()?.complaintService?.csDate = isoDateTime.extractOnlyDate()
                    println("complaintService:${Main.app.getComplaintService()?.complaintService}")
                }
            })
        }

       /* binding.btnCheckIn.setDebouncedClickListener {
            val time = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T").plus("Z")
            val dateTimeObject = DateTime.getDateFromString(time, DateTime.ServerDateTimeFormat)
            val formattedDateTime = DateTime.format(dateTimeObject, DateTime.DateTimeRetailFrontEndFormate)
            binding.checkinTime.text = formattedDateTime
            binding.checkinTime.tag = time
            Main.app.getComplaintService()?.complaintService?.timeIn = time
        }*/

        /*binding.btnCheckOut.setDebouncedClickListener {
            val time = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                .plus("Z")
            val dateTimeObject = DateTime.getDateFromString(time, DateTime.ServerDateTimeFormat)
            val formattedDateTime =
                DateTime.format(dateTimeObject, DateTime.DateTimeRetailFrontEndFormate)
            binding.checkoutTime.text = formattedDateTime
            binding.checkoutTime.tag = time
        }*/

        binding.complainantCard.setDebouncedClickListener {
            ComplainantInformationDialog.make(
                requireActivity(), Main.app.getComplaintService()?.complaintService?.complaintBy,
                Main.app.getComplaintService()?.complaintService?.designation,
                Main.app.getComplaintService()?.complaintService?.mobileNo,
            ).show(object : ComplainantInformationDialog.OnConfirmListener {
                override fun onConfirm(
                    name: String, designation: String, mobileNumber: String
                ) {
                    Main.app.getComplaintService()?.complaintService?.complaintBy = name
                    Main.app.getComplaintService()?.complaintService?.designation = designation
                    Main.app.getComplaintService()?.complaintService?.mobileNo = mobileNumber
                    binding.txtCmplntName.text = name
                    binding.txtCmplntDes.text = designation
                }
            })
        }


        binding.printAndSend.setDebouncedClickListener { onPrintAndSave() }

        binding.btnClearSign.setDebouncedClickListener { binding.signaturePad.clear() }

        binding.btnOpenEquipmentList.setDebouncedClickListener {
            val bundle = bundleOf(
                "IsEquipment" to true, Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_service_form_to_service_form_bottom_navigation, bundle
            )
        }

        binding.btnCancel.setDebouncedClickListener {
            findNavController().popBackStack()
            Main.app.clearComplaintService()
        }

        binding.btnViewOrder.setDebouncedClickListener {
            val bundle = bundleOf(
                "IsEquipment" to false, Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_service_form_to_service_form_bottom_navigation, bundle
            )
        }
    }

    private fun onPrintAndSave() {
        if (binding.signaturePad.isEmpty) {
            Notify.toastLong("Please add your signature")
            return
        }

        Main.app.getComplaintService()?.complaintService?.remarks = binding.remarks.text.toString()
        uploadSignature()
    }

    private fun uploadSignature() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                    val signature = result.result
                    Main.app.getComplaintService()?.complaintService?.signature =
                        signature?.filePath
                    saveComplaint()
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to upload signature")
                }
            }).enque(Network.api()?.uploadSignature(getMultipartSignatureFile())).execute()
    }

    private fun saveComplaint() {
        Main.app.getComplaintService()?.serializeItems()
        Main.app.getComplaintService()?.complaintService?.customerFeedbackList = feedbacks
        if (binding.spinnerType.selectedItemPosition > -1) {
            Main.app.getComplaintService()?.complaintService?.type =
                serviceTypes.get(binding.spinnerType.selectedItemPosition).value
        }

        if (binding.reportTypeSpinner.selectedItemPosition > -1) {
            Main.app.getComplaintService()?.complaintService?.reportType =
                reportTypes.get(binding.reportTypeSpinner.selectedItemPosition).value
        }

        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<ComplaintServiceResponse>
                    if (result.success == true) {
                        Notify.toastLong("Service Form Created: ${result.result?.id}")
                        findNavController().popBackStack()
                        Main.app.clearComplaintService()
                    } else {
                        Notify.toastLong("Unable create or update memo")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable create or update memo")
                }
            }).enque(Network.api()?.createUpdateComplaintService(Main.app.getComplaintService()!!))
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

    private fun isAllFeedbackSelected(): Boolean {
        var feedbackSelected = true
        feedbacks.forEach { feedback ->
            if (feedback.selected.isNullOrBlank()) {
                feedbackSelected = false
            }
        }
        return feedbackSelected
    }



    private fun setFeedbackData() {
        feedbacksTypes.forEach { feedbackType ->
            val feedbackTypeTitle = TextView(this@ServiceFormFragment.requireActivity()).apply {
                id = View.generateViewId()
                text = feedbackType.displayText
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(5, 5, 5, 5)
                setTextColor(Color.BLACK)
            }
            binding.feedbackViewHandler.addView(feedbackTypeTitle)
            feedbacks.forEach { feedback ->
                if (feedback.type == feedbackType.displayText) {
                    val item = FeedbackItemView(context, feedback)
                    binding.feedbackViewHandler.addView(item)
                    item.setup(false)
                }
            }
        }
    }

    private fun setCustomerData() {
        customer.let { binding.cardCustomerInfo.setCustomer(it) }
        binding.cardCustomerInfo.setDebouncedClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager, customer)
        }
    }

    private fun getFeedbackData() {
        if (feedbacks.isEmpty().not()) return

        val loading = Loading().forApi(requireActivity())
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                feedbacks.clear()
                (response?.body() as RetailResponse<ArrayList<Feedback>>).result?.let {
                    feedbacks.addAll(
                        it
                    )
                }
                setFeedbackData()
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

            }
        }).setTag("FEEDBACK").autoLoadigCancel(loading).enque(Network.api()?.getFeedback())
            .execute()
    }

    private fun setHeaderData() {
        binding.header.setBackText("Service Form")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
            Main.app.clearComplaintService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearComplaintService()
    }
}