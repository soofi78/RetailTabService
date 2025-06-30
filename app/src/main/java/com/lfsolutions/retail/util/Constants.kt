package com.lfsolutions.retail.util

object Constants {
    object Invoice {
        const val UOM = "{{uom}}"
        const val InvoiceSubTotal = "{{invoice.invoiceSubTotal}}"
        const val InvoiceDiscount = "{{invoice.invoiceDiscount}}"
        const val InvoiceTax = "{{invoice.tax}}"
        const val InvoiceNetTotal = "{{invoice.netTotal}}"
        const val InvoiceBalanceAmount = "{{customer.balanceAmount}}"
        const val InvoiceOuStandingBalanceAmount = "{{invoice.outStandingBalance}}"
        const val InvoicePaidAmount = "{{customer.paidAmount}}"
        const val InvoiceSignature = "@#@{{&invoice.signature}}"
        const val InvoiceQR = "###{{invoice.qrUrl}}"
        const val Qty = "{{qty}}"
        const val Price = "{{price}}"
        const val NetTotal = "{{netTotal}}"
        const val ProductName = "{{productName}}"
        const val InvoiceAddress1 = "{{customer.address1}}"
        const val InvoiceAddress2 = "{{customer.address2}}"
        const val InvoiceCustomerName = "{{&invoice.customerName}}"
        const val InvoiceCustomerCode = "{{&invoice.customerCode}}"
        const val InvoiceTerm = "{{invoice.terms}}"
        const val InvoiceDate = "{{invoice.invoiceDate}}"
        const val InvoiceNo = "{{invoice.invoiceNo}}"
        const val InvoiceQty = "{{invoice.qty}}"
        const val TotalAmount = "{{receipt.amount}}"
    }

    object Customers{
        const val CustomerName = "{{customer.name}}"
        const val CustomerCode = "{{customer.code}}"
        const val CustomerAddress1 = "{{customer.address1}}"
        const val CustomerAddress2 = "{{customer.address2}}"
    }

    object Common {
        const val Date = "{{date}}"
        const val Index = "{{index}}"
        const val Qty = "{{qty}}"
        const val Price = "{{price}}"
        const val UOM = "{{uom}}"
        const val ItemsStart = "{{#items}}"
        const val ItemsEnd = "{{/items}}"
        const val TotalAmount = "{{totalAmount}}"
        const val ProductName = "{{productName}}"
    }

    object Payment {
        const val Amount = "{{amount}}"
        const val TermStart = "{{#paymentTerm}}"
        const val Term = "{{paymentTerm}}"
        const val TermEnd = "{{/paymentTerm}}"
    }

    object Order {
        const val OrderSubTotal = "{{order.subTotal}}"
        const val OrderTax = "{{order.tax}}"
        const val OrderNetTotal = "{{order.netTotal}}"
        const val CustomerBalanceAmount = "{{customer.balanceAmount}}"
        const val OrderSignature = "@#@{{&order.signature}}"
        const val OrderQR = "###{{order.qrUrl}}"
        const val Qty = "{{qty}}"
        const val Price = "{{price}}"
        const val NetTotal = "{{netTotal}}"
        const val ProductName = "{{productName}}"
        const val CustomerAddress1 = "{{customer.address1}}"
        const val CustomerAddress2 = "{{customer.address2}}"
        const val CustomerCustomerName = "{{customer.customerName}}"
        const val CustomerCustomerCode = "{{customer.customerCode}}"
        const val OrderTerm = "{{order.terms}}"
        const val OrderDate = "{{order.soDate}}"
        const val OrderNo = "{{order.soNo}}"
        const val OrderQty = "{{order.qty}}"
    }

    object Delivery {
        const val DeliveredQty = "{{delivered.qty}}"
        const val DeliverySignature = "@#@{{&delivery.signature}}"
        const val DeliveryQR = "###{{delivery.qrUrl}}"
        const val Qty = "{{qty}}"
        const val UOM = "{{uom}}"
        const val DeliveryQty = "{{delivery.qty}}"
        const val ProductName = "{{productName}}"
        const val CustomerAddress1 = "{{customer.address1}}"
        const val CustomerAddress2 = "{{customer.address2}}"
        const val CustomerName = "{{customer.name}}"
        const val CustomerCode = "{{customer.code}}"
        const val Term = "{{order.terms}}"
        const val Date = "{{delivery.date}}"
        const val OrderNo = "{{delivery.No}}"
        const val ReferenceNo = "{{delivery.referenceNo}}"
        const val OrderQty = "{{order.qty}}"
    }

    object AgreementMemo {
        const val AgreementMemoDate = "{{agreementMemo.date}}"
        const val AgreementMemoNo = "{{agreementMemo.No}}"
        const val AgreementMemoType = "{{agreementTypeString}}"
        const val AgreementMemoQty = "{{agreementMemo.qty}}"
        const val AgreementMemoSignature = "@#@{{&agreementMemo.signature}}"
    }

    object ComplaintService {
        const val CSDate = "{{complaintService.date}}"
        const val CSNo = "{{complaintService.No}}"
        const val CSDeliveryDate = "{{complaintService.typeString}}"
        const val CSReportTypeDeliveryDate = "{{complaintService.reportTypeString}}"
        const val CSActionTypeString = "{{actionTypeString}}"
        const val CSTransTypeString = "{{transTypeString}}"
        const val CSQty = "{{complaintService.qty}}"
        const val CSTimeIn = "{{complaintService.timein}}"
        const val CSTimeOut = "{{complaintService.timeout}}"
        const val CSRemarks = "{{complaintService.remark}}"
        const val CSSignature = "@#@{{&complaintService.signature}}"
    }

    object Receipt {
        const val TransactionNo = "{{receipt.transactionNo}}"
        const val TransactionDate = "{{receipt.transactionDate}}"
        const val TransactionAmount = "{{receipt.transactionAmount}}"
    }


    object CurrentStock {
        const val ProductName = "{{productName}}"
        const val Price = "{{price}}"
        const val QTY = "{{qty}}"
        const val UOM = "{{uom}}"
        const val MinQty = "{{minQty}}"
        const val VarQty = "{{varQty}}"
    }


    const val PrivacyPolicy = "Privacy Policy"
    const val OrderId: String = "order_id"
    const val FormType: String = "FormType"
    const val QRTagStart = "<qrcode>"
    const val QRTagEnd = "</qrcode>"
    const val PrinterSettings = "Select Printer"
    const val SELECTED_BLUETOOTH: String = "Selected Bluetooth"
    const val PRINTER_WIDTH: String = "printer_paper_width"
    const val CHARACTER_PER_LINE = "character_per_line"
    const val AppTheme: String = "App Theme"
    const val ASCENDING = "Ascending"
    const val DESCENDING = "Descending"
    const val Version = "Version"
    const val Memo = "memo"
    const val DownloadPdf: String = "Download PDF"
    const val Print: String = "Print"
    const val Delete: String = "Delete"
    const val Item: String = "item"
    const val OutGoingProducts: String = "out_going_products"
    const val InComingProducts: String = "in_coming_products"
    val Logout: CharSequence = "Logout"
    val ViewProfile: CharSequence = "View Profile"
    const val Product: String = "product"
    const val isTaxInclusive: String = "isTaxInclusive"
    const val Form: String = "form"
    const val Customer: String = "customer"
    const val SESSION: String = "session"
    const val IS_LOGGED_IN: String = "is_logged_in"
    const val baseUrl: String = "base_url"

    const val PRINT_TYPE_TAX_INVOICE: Int = 2
    const val PRINT_TYPE_RECEIPT: Int = 4
    const val PRINT_TYPE_SALE_ORDER: Int = 13
    const val PRINT_TYPE_DELIVERY_ORDER: Int = 14
    const val PRINT_TYPE_CURRENT_STOCK: Int = 15
    const val PRINT_TYPE_INCOMMING_STOCK: Int = 16
    const val PRINT_TYPE_AGREEMENT_MEMO: Int = 17
    const val PRINT_TYPE_SERVICE_FORM: Int = 18

    fun getCrystalReportEndPoint(
        id: Int?,
        invoiceNo: String?,
        fileName: String?,
        tag: String?,
        type: String?
    ): String {
        return "ReportsView/ReportViewer.aspx?Id=$id&InvoiceNo=$invoiceNo&ReportTag=$tag&filePath=$fileName&type=$type"
    }
}