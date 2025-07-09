package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator


data class SalesInvoice(
    @SerializedName("Id", alternate = arrayOf("id")) var id: Int? = null,
    @SerializedName("InvoiceNo", alternate = arrayOf("invoiceNo")) var invoiceNo: String? = null,
    @SerializedName(
        "InvoiceDate",
        alternate = arrayOf("invoiceDate")
    ) var invoiceDate: String? = null,
    @SerializedName("poNo", alternate = arrayOf("PoNo")) var poNo: String? = null,
    @SerializedName("LocationId", alternate = arrayOf("locationId")) var locationId: Int? = null,
    @SerializedName("CustomerId", alternate = arrayOf("customerId")) var customerId: Int? = null,
    @SerializedName(
        "CustomerName",
        alternate = arrayOf("customerName")
    ) var customerName: String? = null,
    @SerializedName(
        "CustomerCode",
        alternate = arrayOf("customerCode")
    ) var customerCode: String? = null,
    @SerializedName("Address1", alternate = arrayOf("address1")) var address1: String? = null,
    @SerializedName("Address2", alternate = arrayOf("address2")) var address2: String? = null,
    @SerializedName(
        "DeliveryTimingDescription",
        alternate = arrayOf("deliveryTimingDescription")
    ) var deliveryTimingDescription: String? = null,
    @SerializedName(
        "CustomerRemarks",
        alternate = arrayOf("customerRemarks")
    ) var customerRemarks: String? = null,
    @SerializedName(
        "CompanyRemarks",
        alternate = arrayOf("companyRemarks")
    ) var companyRemarks: String? = null,
    @SerializedName("Balance", alternate = arrayOf("balance")) var balance: Double? = 0.0,
    @SerializedName("outStandingBalance") var outStandingBalance: Double? = 0.0,
    @SerializedName("outstandingAmount") var outstandingAmount: Double? = 0.0,
    @SerializedName(
        "InvoiceItemDiscount",
        alternate = arrayOf("invoiceItemDiscount")
    ) var invoiceItemDiscount: Double? = 0.0,
    @SerializedName(
        "InvoiceNetDiscountPerc",
        alternate = arrayOf("invoiceNetDiscountPerc")
    ) var invoiceNetDiscountPerc: Double? = 0.0,
    @SerializedName(
        "InvoiceNetDiscount",
        alternate = arrayOf("invoiceNetDiscount")
    ) var invoiceNetDiscount: Double? = 0.0,
    @SerializedName(
        "InvoiceSubTotal",
        alternate = arrayOf("invoiceSubTotal")
    ) var invoiceSubTotal: Double? = 0.0,
    @SerializedName("InvoiceTax", alternate = arrayOf("invoiceTax")) var invoiceTax: Double? = 0.0,
    @SerializedName(
        "InvoiceQtyTotal",
        alternate = arrayOf("invoiceQtyTotal")
    ) var invoiceQty: Double? = 0.0,
    @SerializedName(
        "InvoiceNetTotal",
        alternate = arrayOf("invoiceNetTotal")
    ) var invoiceNetTotal: Double? = 0.0,
    @SerializedName(
        "InvoiceRoundingAmount",
        alternate = arrayOf("invoiceRoundingAmount")
    ) var invoiceRoundingAmount: Double? = 0.0,
    @SerializedName(
        "OtherCharges",
        alternate = arrayOf("otherCharges")
    ) var otherCharges: Double? = 0.0,
    @SerializedName(
        "NetDiscount",
        alternate = arrayOf("netDiscount")
    ) var netDiscount: Double? = 0.0,
    @SerializedName(
        "InvoiceGrandTotal",
        alternate = arrayOf("invoiceGrandTotal")
    ) var invoiceGrandTotal: Double? = 0.0,

    @SerializedName(
        "InvoiceTotalValue",
        alternate = arrayOf("invoiceTotalValue")
    ) var invoiceTotalValue: Double? = 0.0,
    @SerializedName("Remarks", alternate = arrayOf("remarks")) var remarks: String? = null,
    @SerializedName("CreditDays", alternate = arrayOf("creditDays")) var creditDays: Int? = 0,
    @SerializedName(
        "SalesOrderId",
        alternate = arrayOf("salesOrderId")
    ) var salesOrderId: Int? = null,
    @SerializedName("Status", alternate = arrayOf("status")) var status: String? = "I",
    @SerializedName("TenantId", alternate = arrayOf("tenantId")) var tenantId: Int? = 0,
    @SerializedName(
        "SalespersonId",
        alternate = arrayOf("salespersonId")
    ) var salespersonId: Int? = null,
    @SerializedName(
        "DeliveryPersonId",
        alternate = arrayOf("deliveryPersonId")
    ) var deliveryPersonId: Int? = null,
    @SerializedName(
        "isTaxInclusive"
    ) var isTaxInclusive: Boolean? = null,
    @SerializedName(
        "DeliveryPersonName",
        alternate = arrayOf("deliveryPersonName")
    ) var deliveryPersonName: String? = null,
    @SerializedName(
        "SalesPersonName",
        alternate = arrayOf("salesPersonName")
    ) var salesPersonName: String? = null,
    @SerializedName(
        "AmountInWords",
        alternate = arrayOf("amountInWords")
    ) var amountInWords: String? = null,
    @SerializedName(
        "PaymentTermId",
        alternate = arrayOf("paymentTermId")
    ) var paymentTermId: Int? = null,
    @SerializedName("BanchId", alternate = arrayOf("branchId")) var branchId: String? = null,
    @SerializedName(
        "PaymentTermName",
        alternate = arrayOf("paymentTermName")
    ) var paymentTermName: String? = null,
    @SerializedName(
        "FromLocationName",
        alternate = arrayOf("fromLocationName")
    ) var fromLocationName: String? = null,
    @SerializedName(
        "EmployeeName",
        alternate = arrayOf("employeeName")
    ) var employeeName: String? = null,
    @SerializedName("TokenId", alternate = arrayOf("tokenId")) var tokenId: String? = null,
    @SerializedName(
        "InvoiceDueDate",
        alternate = arrayOf("invoiceDueDate")
    ) var invoiceDueDate: String? = null,
    @SerializedName("Type", alternate = arrayOf("type")) var type: String? = "N",
    @SerializedName(
        "ProfitMargin",
        alternate = arrayOf("profitMargin")
    ) var profitMargin: Double? = 0.0,
    @SerializedName("TaxName", alternate = arrayOf("taxName")) var taxName: String? = null,
    @SerializedName("Signature", alternate = arrayOf("signature")) var signature: String? = null,
    @SerializedName(
        "DeliveryOrderList",
        alternate = arrayOf("deliveryOrderList")
    ) var deliveryOrderList: ArrayList<JsonObject> = arrayListOf(),
    @SerializedName(
        "DeliveryOrdered",
        alternate = arrayOf("deliveryOrdered")
    ) var deliveryOrdered: String? = null,
    @SerializedName(
        "DeliveryOrderDate",
        alternate = arrayOf("deliveryOrderDate")
    ) var deliveryOrderDate: String? = null,
    @SerializedName(
        "DeliveryTimingId",
        alternate = arrayOf("deliveryTimingId")
    ) var deliveryTimingId: String? = null,
    @SerializedName("MemberId", alternate = arrayOf("memberId")) var memberId: String? = null,
    @SerializedName("VendorId", alternate = arrayOf("vendorId")) var vendorId: Int? = 0,
    @SerializedName("PaidAmount", alternate = arrayOf("paidAmount")) var paidAmount: Double? = 0.0,
    @SerializedName("CurrencyId", alternate = arrayOf("currencyId")) var currencyId: Int? = null,
    @SerializedName(
        "CurrencyRate",
        alternate = arrayOf("currencyRate")
    ) var currencyRate: Int? = 1,
    @SerializedName(
        "ZatcaQRCode",
        alternate = arrayOf("zatcaQRCode")
    ) var zatcaQRCode: String? = null,
    @SerializedName(
        "ThirdPartyId",
        alternate = arrayOf("thirdPartyId")
    ) var thirdPartyId: String? = null,
    @SerializedName("AutoSync", alternate = arrayOf("autoSync")) var autoSync: Boolean? = false,
    @SerializedName("IsDeleted", alternate = arrayOf("isDeleted")) var isDeleted: Boolean? = false,
    @SerializedName(
        "DeleterUserId",
        alternate = arrayOf("deleterUserId")
    ) var deleterUserId: String? = null,
    @SerializedName(
        "DeletionTime",
        alternate = arrayOf("deletionTime")
    ) var deletionTime: String? = null,
    @SerializedName(
        "LastModificationTime",
        alternate = arrayOf("lastModificationTime")
    ) var lastModificationTime: String? = null,
    @SerializedName(
        "LastModifierUserId",
        alternate = arrayOf("lastModifierUserId")
    ) var lastModifierUserId: Int? = null,
    @SerializedName(
        "CreationTime",
        alternate = arrayOf("creationTime")
    ) var creationTime: String? = null,
    @SerializedName(
        "CreatorUserId",
        alternate = arrayOf("creatorUserId")
    ) var creatorUserId: Int? = null,
    @SerializedName(
        "reportName",
    ) var reportName: String? = null,

    @SerializedName("CustomerServiceToVisitId", alternate = arrayOf("customerServiceToVisitId"))
    var customerServiceToVisitId: Long= 0,

    ) {
    fun getPONo(): String {
        return poNo?:""
    }

    fun InvoiceDateFormatted(): String {
        val date = DateTime.getDateFromString(
            invoiceDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: invoiceDate ?: ""
    }

    fun StatusFormatted(): String {
        return when (status) {
            "I" -> "INVOICED"
            "P" -> "PAID"
            "D" -> "DRAFT"
            else -> status.toString()
        }
    }

    fun InvoiceSubTotalFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceSubTotal?.formatDecimalSeparator()
    }

    fun InvoiceDiscountFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceNetDiscount?.formatDecimalSeparator()
    }

    fun InvoiceTaxFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceTax?.formatDecimalSeparator()
    }

    fun InvoiceNetTotalFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceNetTotal?.formatDecimalSeparator()
    }

    fun InvoiceRoundingAmountFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceRoundingAmount?.formatDecimalSeparator()
    }

    fun InvoiceGrandTotalFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceGrandTotal?.formatDecimalSeparator()
    }

    fun BalanceFormatted(): String {
        return Main.app.getSession().currencySymbol + balance?.formatDecimalSeparator()
    }

    fun OutstandingBalanceFormatted(): String {
        return Main.app.getSession().currencySymbol + outStandingBalance?.formatDecimalSeparator()
    }

    fun PaidAmountFormatted(): String {
        return Main.app.getSession().currencySymbol + paidAmount?.formatDecimalSeparator()
    }


    fun signatureUrl(): String {
        return Main.app.getBaseUrl() + signature?.replace("\\\\", "\\").toString()
    }
}