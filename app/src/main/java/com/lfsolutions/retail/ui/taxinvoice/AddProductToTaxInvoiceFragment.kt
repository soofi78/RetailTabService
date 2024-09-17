package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentAddToCartBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class AddProductToTaxInvoiceFragment : Fragment() {

    private var _binding: FragmentAddToCartBinding? = null
    private val mBinding get() = _binding!!
    private var serialNumbers = ArrayList<SerialNumber>()
    private var selectedSerialNumbers = ArrayList<MultiSelectModelInterface>()
    private lateinit var serialNumberAdapter: MultiSelectListAdapter
    private val args by navArgs<AddProductToTaxInvoiceFragmentArgs>()
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToCartBinding.inflate(inflater, container, false)
        product = Gson().fromJson(args.product, Product::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeaderData()
        setData()
        setApplicableTaxAdapter()
        addOnClickListener()
        addOnCheckedChangeListener()
        addSerialNumberClick()
        updateTotal()
    }

    private fun setData() {
        mBinding.txtQty.text = "1"
        product?.qtyOnHand?.let {
            mBinding.txtQtyAvailable.text = it.toString()
        }
        mBinding.txtProductName.text = product?.productName
        mBinding.txtCategory.text = product?.categoryName
        mBinding.txtPrice.text =
            Main.app.getSession().currencySymbol + product?.cost?.formatDecimalSeparator()
        Glide.with(this).load(Main.app.getBaseUrl() + product?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(mBinding.imgProduct)
        mBinding.serialheader.visibility =
            if (product.isSerialEquipment()) View.VISIBLE else View.GONE
        mBinding.serialNumberRecyclerView.visibility =
            if (product.isSerialEquipment()) View.VISIBLE else View.GONE
        mBinding.lblTaxAsterik.visibility = View.GONE
        mBinding.lblApplicableTax.visibility = View.GONE
        mBinding.spinnerApplicableTax.visibility = View.GONE
    }

    private fun setHeaderData() {
        mBinding.header.setBackText("Tax Invoice")
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun setApplicableTaxAdapter() {
        val adapter = product?.applicableTaxes?.let {
            ArrayAdapter(
                requireActivity(), R.layout.simple_text_item, it
            )
        }
        mBinding.spinnerApplicableTax.adapter = adapter
    }

    private fun getApplicableTax(): Int {
        if (product.applicableTaxes == null || product.applicableTaxes?.isEmpty() == true)
            return 0

        var tax = 0
        product.applicableTaxes?.forEach {
            tax += it.taxRate ?: 0
        }
        return tax
    }

    private fun addOnCheckedChangeListener() {

        mBinding.spinnerApplicableTax.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                updateTotal()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateTotal()
            }

        }

        mBinding.checkboxFOC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxIsExpired.isChecked = false
                mBinding.checkboxExchange.isChecked = false
            }
        }

        mBinding.checkboxIsExpired.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxFOC.isChecked = false
                mBinding.checkboxExchange.isChecked = false
            }
        }

        mBinding.checkboxExchange.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxIsExpired.isChecked = false
                mBinding.checkboxFOC.isChecked = false
            }
        }
    }

    private fun addOnClickListener() {
        mBinding.btnSub.setOnClickListener {
            openQuantityUpdateDialog()
        }

        mBinding.btnAdd.setOnClickListener {
            openQuantityUpdateDialog()
        }

        mBinding.btnSave.setOnClickListener {
            if (mBinding.txtQty.text.toString() == "0") {
                Notify.toastLong("Can't add zero quantity!")
                return@setOnClickListener
            }

            if (product.isSerialEquipment() && selectedSerialNumbers.isEmpty()) {
                Notify.toastLong("Please add serial number")
                return@setOnClickListener
            }

            if (product.isSerialEquipment() && mBinding.txtQty.text.toString()
                    .toInt() != selectedSerialNumbers.size
            ) {
                Notify.toastLong("Serial Number and quantity should be equal")
                return@setOnClickListener
            }

            addToCart()
            it.findNavController().popBackStack()
        }

    }

    private fun openQuantityUpdateDialog() {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            product?.imagePath.toString(),
            product?.productName.toString(),
            mBinding.txtQty.text.toString().toDouble(),
            product?.cost ?: 0.0,
            product?.unitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                mBinding.txtQty.text = quantity.toString()
                updateTotal()
            }

            override fun onPriceChanged(price: Double) {
                product?.cost = price
                updateTotal()
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    private fun addToCart() {
        val batchList = arrayListOf<ProductBatchList>()
        if (selectedSerialNumbers != null && selectedSerialNumbers.size > 0) {
            selectedSerialNumbers.forEach { serialItem ->
                batchList.add(
                    ProductBatchList(
                        Id = serialItem.getId().toInt(),
                        SerialNumber = serialItem.getText(),
                        Price = 0,
                        ProductId = 0,
                        TenantId = 0,
                        UnitCost = 0
                    )
                )
            }
        }

        val qty = mBinding.txtQty.text.toString().toDouble()
        val subTotal =
            (mBinding.txtQty.text.toString().toDouble() * (product?.cost ?: 0.0)).toDouble()
        val discount = 0.0
        val taxAmount = subTotal * (product.getApplicableTaxRate().toDouble() / 100.0)
        val netTotal = (subTotal - discount) + taxAmount
        val total = (subTotal + taxAmount)
        Main.app.getTaxInvoice()?.addEquipment(SalesInvoiceDetail(
            ProductId = product.productId?.toInt() ?: 0,
            InventoryCode = product.inventoryCode,
            ProductName = product.productName,
            ProductImage = product.imagePath,
            UnitId = product.unitId,
            UnitName = product.unitName,
            Qty = qty,
            QtyStock = product.qtyOnHand,
            Price = subTotal,
            NetCost = total,
            CostWithoutTax = product?.cost?.toDouble() ?: 0.0,
            TaxRate = product.getApplicableTaxRate().toDouble(),
            DepartmentId = 0,
            LastPurchasePrice = 0.0,
            SellingPrice = 0.0,
            MRP = 0,
            IsBatch = false,
            ItemDiscount = 0.0,
            ItemDiscountPerc = 0.0,
            AverageCost = 0,
            NetDiscount = 0.0,
            SubTotal = subTotal,
            NetTotal = netTotal,
            Tax = taxAmount,
            TotalValue = subTotal,
            IsFOC = mBinding.checkboxFOC.isChecked,
            IsExchange = mBinding.checkboxExchange.isChecked,
            IsExpire = mBinding.checkboxIsExpired.isChecked,
            CreationTime = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                .replace(" ", "T").plus("Z"),
            CreatorUserId = Main.app.getSession().userId,
            ProductBatchList = batchList
        ).apply {
            product.applicableTaxes?.let {
                ApplicableTaxes = it
                TaxForProduct = it
            }
        })
    }

    private fun updateSerialNumbersAdapter() {
        serialNumberAdapter = MultiSelectListAdapter(selectedSerialNumbers)
        mBinding.serialNumberRecyclerView.adapter = serialNumberAdapter
    }

    private fun addSerialNumberClick() {
        mBinding.addSerialNumber.setOnClickListener {
            if (serialNumbers == null || serialNumbers.isEmpty()) getSerialNumbersList() else showSerialNumbersList()
        }
    }

    private fun showSerialNumbersList() {
        val multiSelectDialog =
            MultiSelectDialog().title("Select serial numbers").titleSize(25f).positiveText("Done")
                .negativeText("Cancel").setMinSelectionLimit(1)
                .setMaxSelectionLimit(serialNumbers.size).preSelectIDsList(selectedSerialNumbers)
                .multiSelectList(serialNumbers).onSubmit(object : SubmitCallbackListener {
                    override fun onSelected(
                        selectedIds: ArrayList<MultiSelectModelInterface>?,
                        selectedNames: ArrayList<String>?,
                        commonSeperatedData: String?
                    ) {
                        selectedSerialNumbers.clear()
                        selectedIds?.let { selectedSerialNumbers.addAll(it) }
                        updateSerialNumbersAdapter()
                        mBinding.txtQty.text = selectedSerialNumbers.size.toString()
                        updateTotal()
                        updateAddButtonForSerialNumber()
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(requireActivity().supportFragmentManager, "serialNumber")
    }

    private fun updateTotal() {
        val currency = Main.app.getSession().currencySymbol
        val totalAmount =
            (mBinding.txtQty.text.toString().toInt() * (product.cost ?: 0.0)).toDouble()
        val discount = 0.0
        val subTotal = totalAmount - discount
        val taxAmount = subTotal * (product.getApplicableTaxRate() / 100.0)
        val total = (subTotal + taxAmount)
        mBinding.txtTotalAmount.text =
            currency + " " + totalAmount.toString().formatDecimalSeparator()
        mBinding.txtDiscounts.text = currency + " " + discount.toString().formatDecimalSeparator()
        mBinding.txtSubTotal.text = currency + " " + subTotal.toString().formatDecimalSeparator()
        mBinding.lblTaxAmount.text = "Tax (" + product.getApplicableTaxRate() + "%)"
        mBinding.txtTaxAmount.text = currency + " " + taxAmount.toString().formatDecimalSeparator()
        mBinding.txtTotal.text = currency + " " + total.toString().formatDecimalSeparator()
    }

    private fun updateAddButtonForSerialNumber() {
        mBinding.addSerialNumber.setBackgroundResource(if (selectedSerialNumbers.size > 0) R.drawable.round_green_background else R.drawable.round_red_background)
    }

    private fun getSerialNumbersList() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading serial numbers"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ArrayList<SerialNumber>>).result?.let {
                        serialNumbers = it
                    }
                    showSerialNumbersList()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get serial numbers list")
                }
            }).enque(
                Network.api()?.getSerialNumbers(
                    product?.productId, Main.app.getSession().defaultLocationId?.toLong()
                )
            ).execute()
    }

}