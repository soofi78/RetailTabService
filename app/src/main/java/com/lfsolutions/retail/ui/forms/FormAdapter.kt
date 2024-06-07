package com.lfsolutions.retail.ui.forms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemFormBinding

class FormAdapter : RecyclerView.Adapter<FormAdapter.ViewHolder>() {

    private var mFormType = listOf<FormType>()

    private var mListener: OnFormSelectListener? = null

    fun setData(formType: List<FormType>) {

        mFormType = formType

        notifyDataSetChanged()

    }

    class ViewHolder(val binding: ItemFormBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = mFormType.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.let { binding ->

            when (mFormType[position]) {

                FormType.AgreementMemo -> {

                    binding.txtName.text = "Agreement Memo"

                    binding.icoItem.setImageResource(R.drawable.agreement_memo)

                }

                FormType.InvoiceForm -> {

                    binding.txtName.text = "Tax Invoice"

                    binding.icoItem.setImageResource(R.drawable.service_form)

                }

                FormType.ServiceForm -> {

                    binding.txtName.text = "Service Form"

                    binding.icoItem.setImageResource(R.drawable.service_form)

                }

            }

        }

        holder.itemView.setOnClickListener {

            when (mFormType[position]) {
                FormType.AgreementMemo -> mListener?.onAgreementMemoSelect()
                FormType.InvoiceForm -> mListener?.onTaxInvoiceSelect()
                FormType.ServiceForm -> mListener?.onServiceFormSelect()
            }

        }

    }

    fun setListener(listener: OnFormSelectListener) {

        mListener = listener

    }

    interface OnFormSelectListener {

        fun onAgreementMemoSelect()

        fun onServiceFormSelect()

        fun onTaxInvoiceSelect()

    }

}