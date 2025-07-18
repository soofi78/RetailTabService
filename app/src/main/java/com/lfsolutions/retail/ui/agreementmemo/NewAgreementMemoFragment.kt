package com.lfsolutions.retail.ui.agreementmemo

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentNewAgreementMemoBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.taxinvoice.Invoice
import com.lfsolutions.retail.ui.widgets.FeedbackItemView
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


class NewAgreementMemoFragment : Fragment() {

    private var customer: Customer? = null
    private lateinit var _binding: FragmentNewAgreementMemoBinding
    private val args by navArgs<NewAgreementMemoFragmentArgs>()
    private val feedbacks = ArrayList<Feedback>()

    private val mBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentNewAgreementMemoBinding.inflate(inflater, container, false)
            Main.app.getAgreementMemo()
            Main.app.getAgreementMemo()?.AgreementMemo?.LocationId =
                Main.app.getSession().defaultLocationId
           /* Main.app.getAgreementMemo()?.AgreementMemo?.CreationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")*/
            Main.app.getAgreementMemo()?.AgreementMemo?.CreatorUserId = Main.app.getSession().userId
        }
        return mBinding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
        Main.app.getAgreementMemo()?.AgreementMemo?.CustomerId = customer?.id
        addOnClickListener()
        setData()
        getFeedbackData()
    }

    private fun setData() {
        val today = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        mBinding.dateText.text = today
        Main.app.getAgreementMemo()?.AgreementMemo?.AgreementDate = today + "T00:00:00Z"
        mBinding.dateText.setDebouncedClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    mBinding.dateText.text = day + "-" + month + "-" + year
                    Main.app.getAgreementMemo()?.AgreementMemo?.AgreementDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
        mBinding.customerName.text = customer?.name
        mBinding.address.text = customer?.address1
        mBinding.header.setBackText("New Agreement Memo")
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
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
            .enque(Network.api()?.getFeedback(true)).execute()
    }

    private fun setFeedbackData() {
        mBinding.cardFeedbackViewHandler.visibility =
            if (feedbacks.isEmpty()) View.GONE else View.VISIBLE
        feedbacks.forEach { feedback ->
            val item = FeedbackItemView(context, feedback)
            mBinding.feedbackViewHandler.addView(item)
            item.setup(false)
        }
    }


    private fun addOnClickListener() {
        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
            Main.app.clearAgreementMemo()
        }
        mBinding.btnClearSign.setDebouncedClickListener {
            mBinding.signaturePad.clear()
        }

        mBinding.btnOpenEquipmentList.setDebouncedClickListener {
            val bundle = bundleOf(
                "IsEquipment" to true,
                Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )

        }

        mBinding.btnViewOrder.setDebouncedClickListener {
            val bundle = bundleOf(Constants.Customer to Gson().toJson(customer))
            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )
        }

        mBinding.btnSave.setDebouncedClickListener {

            if (isAllFeedbackSelected().not()) {
                Notify.toastLong("Please select all feedbacks")
                return@setDebouncedClickListener
            }

            if (mBinding.agressTermsAndService.isChecked.not()) {
                Notify.toastLong("Please agree terms & conditions")
                return@setDebouncedClickListener
            }

            if (Main.app.getAgreementMemo()?.AgreementMemoDetail?.size == 0) {
                Notify.toastLong("No Product added into the list")
                return@setDebouncedClickListener
            }

            if (mBinding.signaturePad.isEmpty) {
                Notify.toastLong("Please add your signature")
                return@setDebouncedClickListener
            }
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Uploading Signature..."))
                .setCallback(
                    object : OnNetworkResponse {
                        override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                            val result = (response?.body() as RetailResponse<SignatureUploadResult>)
                            val signature = result.result
                            Main.app.getAgreementMemo()?.AgreementMemo?.Signature =
                                signature?.filePath
                            saveAgreement()
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

        mBinding.btnCancel.setDebouncedClickListener {
            findNavController().popBackStack()
            Main.app.clearAgreementMemo()
        }
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


    private fun saveAgreement() {
        Main.app.getAgreementMemo()?.serializeItems()
        Main.app.getAgreementMemo()?.AgreementMemo?.customerFeedbackList = feedbacks
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(
                object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val result = response?.body() as BaseResponse<AgreementMemoResult>
                        if (result.success == true){
                            Notify.toastLong("Successfully Created Agreement Memo : " + result.result?.transactionNo)
                            Main.app.clearAgreementMemo()
                            Printer.askForPrint(requireActivity(), {
                                result.result?.id?.let {
                                    getMemoDetails(it)
                                }
                            }, {
                                findNavController().popBackStack()
                            })
                        }else{
                            Notify.toastLong("Unable to create Agreement Memo: ${result.result}")
                        }
                    }

                    override fun onFailure(
                        call: Call<*>?,
                        response: BaseResponse<*>?,
                        tag: Any?
                    ) {
                        Notify.toastLong("Unable create or update memo")
                    }
                }).enque(Network.api()?.createUpdateMemo(Main.app.getAgreementMemo()!!))
            .execute()
    }

    private fun getMultipartSignatureFile(): MultipartBody.Part {
        val file = File(requireActivity().cacheDir, Date().toString() + "Signature.jpeg")
        val fos = FileOutputStream(file)
        mBinding.signaturePad.signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        val filePart = MultipartBody.Part.createFormData(
            "file", file.getName(), RequestBody.create(
                MediaType.parse("image/jpeg"), file
            )
        )
        return filePart
    }

    private fun getMemoDetails(id: Int) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading agreement memo"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val memo = (response?.body() as BaseResponse<CreateUpdateAgreementMemoRequestBody>).result
                    Printer.printAgreementMemo(requireActivity(), memo)
                    findNavController().popBackStack()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get order details")
                }
            }).enque(
                Network.api()?.getAgreementMemoDetails(IdRequest(id = id))
            ).execute()
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearAgreementMemo()
    }

}