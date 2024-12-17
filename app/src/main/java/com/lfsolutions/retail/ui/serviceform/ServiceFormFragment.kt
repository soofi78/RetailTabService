package com.lfsolutions.retail.ui.serviceform

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
import com.lfsolutions.retail.databinding.FragmentServiceFormBinding
import com.lfsolutions.retail.model.ComplaintServiceResponse
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.service.ReportTypeResult
import com.lfsolutions.retail.model.service.ReportTypes
import com.lfsolutions.retail.model.service.ServiceTypeResult
import com.lfsolutions.retail.model.service.ServiceTypes
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailActivity
import com.lfsolutions.retail.ui.widgets.FeedbackItemView
import com.lfsolutions.retail.ui.widgets.options.OnOptionItemClick
import com.lfsolutions.retail.ui.widgets.options.OptionItem
import com.lfsolutions.retail.ui.widgets.options.OptionsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
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

class ServiceFormFragment : Fragment() {

    private val args by navArgs<ServiceFormFragmentArgs>()
    private lateinit var customer: Customer
    private val feedbacks = ArrayList<Feedback>()
    private lateinit var binding: FragmentServiceFormBinding
    private var serviceTypes = ArrayList<ServiceTypes>()
    private var reportTypes = ArrayList<ReportTypes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentServiceFormBinding.inflate(layoutInflater, container, false)
            Main.app.getComplaintService()
            Main.app.getComplaintService()?.complaintService?.locationId =
                Main.app.getSession().defaultLocationId
            Main.app.getComplaintService()?.complaintService?.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            Main.app.getComplaintService()?.complaintService?.creatorUserId =
                Main.app.getSession().userId.toString()
            Main.app.getComplaintService()?.complaintService?.csDate =
                Main.app.getComplaintService()?.complaintService?.creationTime
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
        getFeedbackData()
    }

    private fun getServiceTypeData() {
        if (serviceTypes.isEmpty()) {
            NetworkCall.make()
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        (response?.body() as RetailResponse<ServiceTypeResult>).result?.items?.let {
                            serviceTypes.addAll(it)
                        }
                        setServiceTypeAdapter()
                        getReportTypeData()
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

                    }
                })
                .autoLoadigCancel(Loading().forApi(requireActivity()))
                .enque(Network.api()?.getServiceType()).execute()
        } else {
            setServiceTypeAdapter()
        }
    }

    private fun getReportTypeData() {
        if (reportTypes.isEmpty()) {
            NetworkCall.make()
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        (response?.body() as RetailResponse<ReportTypeResult>).result?.items?.let {
                            reportTypes.addAll(it)
                        }
                        setReportTypeAdapter()
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

                    }
                })
                .autoLoadigCancel(Loading().forApi(requireActivity()))
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
        binding.btnCheckIn.setOnClickListener {
            val time = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                .plus("Z")
            val dateTimeObject =
                DateTime.getDateFromString(time, DateTime.ServerDateTimeFormat)
            val formattedDateTime =
                DateTime.format(dateTimeObject, DateTime.DateTimeRetailFrontEndFormate)
            binding.checkinTime.text = formattedDateTime
            binding.checkinTime.tag = time
            Main.app.getComplaintService()?.complaintService?.timeIn = time

        }


        binding.btnCheckOut.setOnClickListener {
            val time = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                .plus("Z")
            val dateTimeObject =
                DateTime.getDateFromString(time, DateTime.ServerDateTimeFormat)
            val formattedDateTime =
                DateTime.format(dateTimeObject, DateTime.DateTimeRetailFrontEndFormate)
            binding.checkoutTime.text = formattedDateTime
            binding.checkoutTime.tag = time
        }

        binding.complainantCard.setOnClickListener {
            ComplainantInformationDialog.make(
                requireActivity(), Main.app.getComplaintService()?.complaintService?.complaintBy,
                Main.app.getComplaintService()?.complaintService?.designation,
                Main.app.getComplaintService()?.complaintService?.mobileNo,
            )
                .show(object : ComplainantInformationDialog.OnConfirmListener {
                    override fun onConfirm(
                        name: String,
                        designation: String,
                        mobileNumber: String
                    ) {
                        Main.app.getComplaintService()?.complaintService?.complaintBy = name
                        Main.app.getComplaintService()?.complaintService?.designation = designation
                        Main.app.getComplaintService()?.complaintService?.mobileNo = mobileNumber
                        binding.txtCmplntName.text = name
                        binding.txtCmplntDes.text = designation
                    }
                })
        }


        binding.printAndSend.setOnClickListener { onPrintAndSave() }

        binding.btnClearSign.setOnClickListener { binding.signaturePad.clear() }

        binding.btnOpenEquipmentList.setOnClickListener {
            val bundle = bundleOf(
                "IsEquipment" to true,
                Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_service_form_to_service_form_bottom_navigation,
                bundle
            )
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
            Main.app.clearComplaintService()
        }

        binding.btnViewOrder.setOnClickListener {
            val bundle = bundleOf(
                "IsEquipment" to false,
                Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_service_form_to_service_form_bottom_navigation,
                bundle
            )
        }
    }

    private fun onPrintAndSave() {
        if (isAllFeedbackSelected().not()) {
            Notify.toastLong("Please select all feedbacks")
            return
        }

        if (Main.app.getComplaintService()?.complaintServiceDetails?.size == 0) {
            Notify.toastLong("Please add products")
            return
        }

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
            .setCallback(
                object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                        val signature = result.result
                        Main.app.getComplaintService()?.complaintService?.signature =
                            signature?.filePath
                        saveComplaint()
                    }

                    override fun onFailure(
                        call: Call<*>?,
                        response: BaseResponse<*>?,
                        tag: Any?
                    ) {
                        Notify.toastLong("Unable to upload signature")
                    }
                }).enque(Network.api()?.uploadSignature(getMultipartSignatureFile()))
            .execute()
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

        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(
                object : OnNetworkResponse {
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
                        call: Call<*>?,
                        response: BaseResponse<*>?,
                        tag: Any?
                    ) {
                        Notify.toastLong("Unable create or update memo")
                    }
                })
            .enque(Network.api()?.createUpdateComplaintService(Main.app.getComplaintService()!!))
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

    private fun setData() {
        setHeaderData()
        setCustomerData()
        val currentDateTime = DateTime.getCurrentDateTime(DateTime.DateTimeRetailFrontEndFormate)
        binding.checkinTime.text = currentDateTime
        binding.checkoutTime.text = currentDateTime
        binding.complaintDateTimeText.text = currentDateTime

    }

    private fun setFeedbackData() {
        feedbacks.forEach { feedback ->
            val item = FeedbackItemView(context, feedback)
            binding.feedbackViewHandler.addView(item)
            item.setup(false)
        }
    }

    private fun setCustomerData() {
        customer.let { binding.cardCustomerInfo.setCustomer(it) }
        binding.cardCustomerInfo.setOnClickListener {
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

    private fun getFeedbackData() {
        if (feedbacks.isEmpty().not())
            return

        val loading = Loading().forApi(requireActivity())
        NetworkCall.make()
            .setCallback(object : OnNetworkResponse {
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
            })
            .setTag("FEEDBACK")
            .autoLoadigCancel(loading)
            .enque(Network.api()?.getFeedback()).execute()
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