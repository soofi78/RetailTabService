package com.lfsolutions.retail.ui.delivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemDeliveryBinding
import com.lfsolutions.retail.databinding.ItemScheduledBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.theme.getAppColor
import com.lfsolutions.retail.util.makeTextBold
import com.lfsolutions.retail.util.setDebouncedClickListener

class DeliveryItemAdapter(
    var customers: ArrayList<Customer>? = ArrayList(),
    val type: CustomerItemType,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnItemClickListener? = null
    private var mProductInfoClick: OnItemClickListener? = null
    var enableProductInfo = false

    class SimpleCustomerHolder(val binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
            return binding.swipeAble
        }
    }

    class ViewHolderScheduled(val binding: ItemScheduledBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
            return binding.swipeAble
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
        if (customer?.IsVisited == true) {
            binding.swipeAble.setBackgroundColor(binding.swipeAble.resources.getColor(R.color.light_red))
        } else if (customer?.isVisitationSchedule == true) {
            binding.swipeAble.setBackgroundColor(binding.swipeAble.context.getAppColor(R.attr.tertiary))
        } else {
            binding.swipeAble.setBackgroundColor(binding.swipeAble.resources.getColor(R.color.white))
        }

        binding.saleOrderLabel.visibility =
            if ((customer?.saleOrderId ?: 0) > 0) View.VISIBLE else View.GONE

        customer?.group?.let {
            binding.txtGroup.text =
                makeTextBold(
                    text = binding.txtGroup.context.getString(
                        R.string.prefix_group,
                        customer.group
                    ), startIndex = 8
                )
        } ?: run {
            binding.txtGroup.text =
                makeTextBold(
                    text = binding.txtGroup.context.getString(
                        R.string.prefix_group,
                        ""
                    ), startIndex = 8
                )
        }

        binding.txtName.text = customer?.name
        binding.txtAddress.text = customer?.address1
        binding.txtAccountNo.text =
            makeTextBold(
                text = binding.txtAccountNo.context.getString(
                    R.string.prefix_account_no,
                    customer?.customerCode
                ), startIndex = 8
            )

        if (customer?.area != null) {
            binding.txtArea.text =
                makeTextBold(
                    text = binding.txtArea.context.getString(R.string.prefix_area, customer.area),
                    startIndex = 6
                )
        } else {
            binding.txtArea.text =
                makeTextBold(
                    text = binding.txtArea.context.getString(
                        R.string.prefix_area,
                        customer?.customerWorkArea
                    ),
                    startIndex = 6
                )
        }

        binding.root.tag = customer
        binding.root.setDebouncedClickListener {
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
        customer?.group?.let {
            binding.txtGroup.text =
                makeTextBold(
                    text = binding.txtGroup.context.getString(
                        R.string.prefix_group,
                        customer.group
                    ), startIndex = 8
                )
        } ?: run {
            binding.txtGroup.text =
                makeTextBold(
                    text = binding.txtGroup.context.getString(
                        R.string.prefix_group,
                        "-"
                    ), startIndex = 8
                )
        }

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
                text = binding.txtArea.context.getString(
                    R.string.prefix_area,
                    customer?.customerWorkArea
                ),
                startIndex = 7
            )

        binding.root.tag = customer
        binding.root.setDebouncedClickListener {
            mListener?.onItemClick(it.tag as Customer)
        }

        if (enableProductInfo) {
            binding.productInfo.visibility = View.VISIBLE
            binding.productInfo.tag = customer
            binding.productInfo.setDebouncedClickListener {
                mProductInfoClick?.onItemClick(it.tag as Customer)
            }
        }

        binding.saleOrderLabel.visibility =
            if ((customer?.saleOrderId ?: 0) > 0) View.VISIBLE else View.GONE
    }


    override fun getItemCount(): Int = customers?.size ?: 0

    fun remove(position: Int) {
        customers?.removeAt(position)
        notifyItemRemoved(position)
    }


    fun setListener(listener: OnItemClickListener) {
        mListener = listener
    }

    fun setProductInfoClick(listener: OnItemClickListener) {
        mProductInfoClick = listener
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