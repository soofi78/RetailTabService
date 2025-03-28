package com.lfsolutions.retail.util

object Constants {
    object Invoice {
        const val UOM = "{{uom}}"
        const val InvoiceSubTotal = "{{invoice.invoiceSubTotal}}"
        const val InvoiceDiscount = "{{invoice.invoiceDiscount}}"
        const val InvoiceTax = "{{invoice.tax}}"
        const val InvoiceNetTotal = "{{invoice.netTotal}}"
        const val InvoiceBalanceAmount = "{{customer.balanceAmount}}"
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
    }

    object Common {
        const val Date = "{{date}}"
        const val Index = "{{index}}"
        const val ItemsStart = "{{#items}}"
        const val ItemsEnd = "{{/items}}"
        const val TotalAmount = "{{totalAmount}}"
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
        const val OrderQty = "{{order.qty}}"
    }

    object Receipt {
        const val TransactionNo = "{{receipt.transactionNo}}"
        const val TransactionDate = "{{receipt.transactionDate}}"
        const val TransactionAmount = "{{receipt.transactionAmount}}"
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
}