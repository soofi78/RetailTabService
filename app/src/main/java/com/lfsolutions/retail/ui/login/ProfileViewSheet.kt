package com.lfsolutions.retail.ui.login

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentUserProfileBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.profile.UserProfile
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class ProfileViewSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater)
        setHeader()
        getData()
        return binding.root
    }

    private fun getData() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading profile information"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val userProfile = (response?.body() as BaseResponse<UserProfile>).result
                    setData(userProfile)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to load user info")
                }
            }).enque(
                Network.api()?.getUserDetails(IdRequest(id = Main.app.getSession().userId))
            ).execute()
    }

    private fun setHeader() {
        binding.header.setBackText("Profile")
        binding.header.setOnBackClick {
            this.dismiss()
        }
    }


    private fun setData(userProfile: UserProfile?) {
        binding.name.text = userProfile?.user?.name
        binding.name.text = userProfile?.user?.name
        binding.surname.text = userProfile?.user?.surname
        binding.username.text = userProfile?.user?.userName
        binding.phoneNumber.text = userProfile?.user?.phoneNumber
        binding.email.text = userProfile?.user?.emailAddress
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val layoutParams = bottomSheet.layoutParams

                val windowHeight = getWindowHeight()
                if (layoutParams != null) {
                    layoutParams.height = windowHeight
                }
                bottomSheet.layoutParams = layoutParams
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    companion object {
        fun show(activity: AppCompatActivity) {
            val printerSettings = ProfileViewSheet()
            activity.supportFragmentManager.let {
                printerSettings.show(
                    it, "Profile"
                )
            }
        }
    }
}