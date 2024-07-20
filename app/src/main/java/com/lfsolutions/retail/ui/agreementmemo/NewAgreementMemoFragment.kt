package com.lfsolutions.retail.ui.agreementmemo

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentNewAgreementMemoBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Form
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
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


class NewAgreementMemoFragment : Fragment() {

    private var customer: Customer? = null
    private lateinit var _binding: FragmentNewAgreementMemoBinding
    private val args by navArgs<NewAgreementMemoFragmentArgs>()


    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentNewAgreementMemoBinding.inflate(inflater, container, false)
            Main.app.clearAgreementMemo()
            Main.app.getAgreementMemo()
            Main.app.getAgreementMemo()?.AgreementMemo?.LocationId =
                Main.app.getSession().defaultLocationId
            Main.app.getAgreementMemo()?.AgreementMemo?.CreationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
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
    }


    private fun setData() {
        val today = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        mBinding.dateText.text = today
        Main.app.getAgreementMemo()?.AgreementMemo?.AgreementDate = today + "T00:00:00Z"
        mBinding.dateText.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    mBinding.dateText.setText(day + "-" + month + "-" + year)
                    Main.app.getAgreementMemo()?.AgreementMemo?.AgreementDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
        mBinding.customerName.text = customer?.name
        mBinding.address.text = customer?.address1
        mBinding.header.setBackText("New Agreement Memo")
        Main.app.getSession().name?.let { mBinding.header.setName(it) }

    }


    private fun addOnClickListener() {
        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }
        mBinding.btnClearSign.setOnClickListener {
            mBinding.signaturePad.clear()
        }

        mBinding.btnOpenEquipmentList.setOnClickListener {
            val bundle = bundleOf(
                "IsEquipment" to true,
                Constants.Customer to args.customer
            )
            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )

        }

        mBinding.btnViewOrder.setOnClickListener {
            val bundle = bundleOf("IsEquipment" to false)
            it.findNavController().navigate(
                R.id.action_navigation_agreement_memo_to_navigation_agreement_memo_bottom_navigation,
                bundle
            )
        }

        mBinding.btnSave.setOnClickListener {

            if (mBinding.agressTermsAndService.isChecked.not()) {
                Notify.toastLong("Please agree terms & conditions")
                return@setOnClickListener
            }

            if (Main.app.getAgreementMemo()?.AgreementMemoDetail?.size == 0) {
                Notify.toastLong("No Product added into the list")
                return@setOnClickListener
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
                            response: ErrorResponse?,
                            tag: Any?
                        ) {
                            Notify.toastLong("Unable to upload signature")
                        }
                    }).enque(Network.api()?.uploadSignature(getMultipartSignatureFile()))
                .execute()
        }

    }

    private fun saveAgreement() {
        Main.app.getAgreementMemo()?.serializeItems()
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(
                object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        Notify.toastLong("success")
                    }

                    override fun onFailure(
                        call: Call<*>?,
                        response: ErrorResponse?,
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

}