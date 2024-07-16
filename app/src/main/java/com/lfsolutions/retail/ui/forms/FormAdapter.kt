package com.lfsolutions.retail.ui.forms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemFormBinding
import com.lfsolutions.retail.model.Form

class FormAdapter(val forms: ArrayList<Form>?) : RecyclerView.Adapter<FormAdapter.ViewHolder>() {

    private var mListener: OnFormSelectListener? = null

    class ViewHolder(val binding: ItemFormBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = forms?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val form = forms?.get(position)
        holder.binding.let { binding ->
            binding.txtName.text = form?.title ?: "N/A"
            binding.txtSerialNo.visibility = if (form?.serialNo == null) View.GONE else View.VISIBLE
            binding.txtSerialNo.text = "Serial no. " + form?.serialNo
            binding.txtEditedDate.visibility =
                if (form?.lastModificationTime == null) View.GONE else View.VISIBLE
            binding.txtEditedDate.text = "Edited on: " + form?.lastModificationTime
            binding.txtDate.visibility =
                if (form?.date == null) View.GONE else View.VISIBLE
            binding.txtDate.text = form?.date.toString()
            when (form?.getType()) {
                FormType.AgreementMemo -> binding.icoItem.setImageResource(R.drawable.agreement_memo)
                FormType.InvoiceForm -> binding.icoItem.setImageResource(R.drawable.service_form)
                FormType.ServiceForm -> binding.icoItem.setImageResource(R.drawable.service_form)
                FormType.TaxForm -> binding.icoItem.setImageResource(R.drawable.agreement_memo)
                null -> {}
            }
        }
        holder.itemView.tag = form
        holder.itemView.setOnClickListener {
            mListener?.onFormSelected(holder.itemView.tag as Form)
        }
    }

    fun setListener(listener: OnFormSelectListener) {
        mListener = listener
    }

    interface OnFormSelectListener {
        fun onFormSelected(form: Form)
    }

}