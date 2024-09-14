package com.lfsolutions.retail.ui.delivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemDeliveryBinding
import com.lfsolutions.retail.databinding.ItemScheduledBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.makeTextBold

class DeliveryItemAdapter(
    var customers: ArrayList<Customer>? = ArrayList(),
    val type: CustomerItemType,
) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    class SimpleCustomerHolder(val binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderScheduled(val binding: ItemScheduledBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View? {
            return if (binding.swipeAble.tag != null && binding.swipeAble.tag == true)
                binding.swipeAble
            else null
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        if (type == CustomerItemType.Scheduled) {
            ViewHolderScheduled(
                ItemScheduledBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            SimpleCustomerHolder(
                ItemDeliveryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val customer = customers?.get(position)
        if (type == CustomerItemType.Scheduled) {
            val binding = (holder as ViewHolderScheduled).binding
            setScheduleData(binding, customer)
        } else {
            val binding = (holder as SimpleCustomerHolder).binding
            setUergentToVisitData(binding, customer)
        }
    }

    private fun setScheduleData(binding: ItemScheduledBinding, customer: Customer?) {
        binding.selected.visibility = if (customer?.IsVisited == true) View.GONE else View.VISIBLE
        binding.selected.isEnabled = customer?.IsVisited == false
        binding.swipeAble.setBackgroundColor(binding.swipeAble.resources.getColor(if (customer?.IsVisited == true) R.color.light_red else R.color.white))
        binding.txtGroup.text =
            makeTextBold(
                text = binding.txtGroup.context.getString(
                    R.string.prefix_group,
                    customer?.group
                ), startIndex = 8
            )
        binding.txtName.text = customer?.name
        binding.txtAddress.text = customer?.address1
        binding.txtAccountNo.text =
            makeTextBold(
                text = binding.txtAccountNo.context.getString(
                    R.string.prefix_account_no,
                    customer?.customerCode
                ), startIndex = 8
            )

        binding.txtArea.text =
            makeTextBold(
                text = binding.txtArea.context.getString(R.string.prefix_area, customer?.area),
                startIndex = 6
            )

        binding.root.tag = customer
        binding.root.setOnClickListener {
            binding.selected.isChecked = customer?.isSelected?.not() == true
        }
        binding.selected.isChecked = customer?.isSelected == true
        binding.selected.setOnCheckedChangeListener { _, isChecked ->
            customer?.isSelected = isChecked
        }
        binding.swipeAble.tag = customer?.isVisitationSchedule ?: false
    }

    fun getCheckedItemList(): ArrayList<Customer> {
        val items = arrayListOf<Customer>()
        customers?.forEach {
            if (it.isSelected) items.add(it)
        }
        return items
    }

    private fun setUergentToVisitData(binding: ItemDeliveryBinding, customer: Customer?) {
        binding.txtGroup.text =
            makeTextBold(
                text = binding.txtGroup.context.getString(
                    R.string.prefix_group,
                    customer?.group
                ), startIndex = 8
            )
        binding.txtName.text = customer?.name
        binding.txtAddress.text = customer?.address1
        binding.txtAccountNo.text =
            makeTextBold(
                text = binding.txtAccountNo.context.getString(
                    R.string.prefix_account_no,
                    customer?.customerCode
                ), startIndex = 8
            )

        binding.txtArea.text =
            makeTextBold(
                text = binding.txtArea.context.getString(R.string.prefix_area, customer?.area),
                startIndex = 7
            )

        binding.root.tag = customer
        binding.root.setOnClickListener {
            mListener?.onItemClick(it.tag as Customer)
        }
    }


    override fun getItemCount(): Int = customers?.size ?: 0

    fun remove(position: Int) {
        customers?.removeAt(position)
        notifyItemRemoved(position)
    }


    fun setListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(customer: Customer)
    }

    enum class CustomerItemType {
        Scheduled,
        ToVisit,
        Urgent,
        All
    }

}