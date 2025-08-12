package com.lfsolutions.retail

import android.app.Activity
import android.util.Log
import android.widget.LinearLayout
import com.lfsolutions.retail.model.PrintTemplate
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.TypeRequest
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.StockTransferDetailItem
import com.lfsolutions.retail.model.sale.SaleReceipt
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.order.response.SaleOrderResponse
import com.lfsolutions.retail.model.service.ServiceFormBody
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.delivery.order.DeliveryOrderDTO
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.settings.printer.PrinterManager
import com.lfsolutions.retail.util.Alert
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_AGREEMENT_MEMO
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_CURRENT_STOCK
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_DELIVERY_ORDER
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_INCOMMING_STOCK
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_RECEIPT
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_SALE_ORDER
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_SERVICE_FORM
import com.lfsolutions.retail.util.DateTime.DateTimetRetailGSTFormat2
import com.lfsolutions.retail.util.DateTime.getCurrentDateTimeSingapore
import com.lfsolutions.retail.util.DateTime.getFormattedSGTDate
import com.lfsolutions.retail.util.DateTime.getFormattedSGTTime
import com.lfsolutions.retail.util.DateTime.getTimeOnlyFromCreationTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

object Printer {
    private val templateCache = mutableMapOf<Int, PrintTemplate?>()

    fun askForPrint(activity: Activity, print: () -> Unit, cancel: () -> Unit) {
        Alert.make(activity).setTitle("Confirm!")
            .setDescription("Do you want to print the current item you just created?")
            .setButtonOrientation(LinearLayout.HORIZONTAL).addButton("No") {
                it.dismiss()
                cancel.invoke()
            }.addButton("Yes") {
                print.invoke()
                it.dismiss()
            }.show(false)
    }

    fun askForPrint(
        activity: Activity,
        print: () -> Unit,
        cancel: () -> Unit,
        thirdButtonText: String,
        thirdButtonClick: () -> Unit
    ) {
        Alert.make(activity).setTitle("Confirm!")
            .setDescription("Do you want to print the current item you just created?")
            .setButtonOrientation(LinearLayout.HORIZONTAL).addButton("No") {
                it.dismiss()
                cancel.invoke()
            }.addButton("Yes") {
                print.invoke()
                it.dismiss()
            }.addButton(thirdButtonText) {
                thirdButtonClick.invoke()
                it.dismiss()
            }.show(false)
    }

    private fun updateTemplateforInvoiceAndPrint(
        template: PrintTemplate?, invoice: SaleInvoiceObject?
    ) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Invoice.InvoiceNo, invoice?.salesInvoice?.invoiceNo?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceDate, getFormattedSGTDate(invoice?.salesInvoice?.invoiceDate) //getFormattedSGTDate(invoice?.salesInvoice?.invoiceDate
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCreationTime, getTimeOnlyFromCreationTime(invoice?.salesInvoice?.creationTime)
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceTerm, invoice?.salesInvoice?.paymentTermName?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCustomerName, invoice?.salesInvoice?.customerName?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCustomerCode, invoice?.salesInvoice?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress1, invoice?.salesInvoice?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress2, invoice?.salesInvoice?.address2 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoicePONumber, invoice?.salesInvoice?.getPONo()?:""
        )

        val itemTemplate = try {
            templateText?.substring(
                templateText.indexOf(Constants.Common.ItemsStart),
                templateText.indexOf(Constants.Common.ItemsEnd) + 10
            )
        } catch (ex: Exception) {
            templateText
        }

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0
        invoice?.salesInvoiceDetail?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, it.slNo?.toString()?:"")
                ?.replace(Constants.Invoice.ProductName, it.productName?:"")
                ?.replace(Constants.Invoice.Qty, it.qty?.toString()?:"")
                ?.replace(Constants.Invoice.UOM, it.unitName?:"")
                ?.replace(Constants.Invoice.Price, it.price.toString())?.replace(
                    Constants.Invoice.NetTotal, it.getAmount()
                ).toString()
            count += 1
            if (count < invoice.salesInvoiceDetail.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceSubTotal,
            invoice?.salesInvoice?.InvoiceSubTotalFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceDiscount,
            invoice?.salesInvoice?.InvoiceDiscountFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceTax, invoice?.salesInvoice?.InvoiceTaxFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceNetTotal,
            invoice?.salesInvoice?.InvoiceNetTotalFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceRoundingAmount,
            invoice?.salesInvoice?.InvoiceRoundingAmountFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceGrandTotal,
            invoice?.salesInvoice?.InvoiceGrandTotalFromatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceQty, invoice?.salesInvoice?.invoiceQty?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceRemark, invoice?.salesInvoice?.remarks?:""
        )

        if (invoice?.salesInvoice?.type == "F") {
            templateText = templateText?.replace(
                Constants.Invoice.InvoiceBalanceAmount, "FOC"
            )

            templateText = templateText?.replace(
                Constants.Invoice.InvoiceOuStandingBalanceAmount, "FOC"
            )
            templateText = templateText?.replace(
                Constants.Invoice.InvoicePaidAmount, "FOC"
            )
        } else {
            templateText = templateText?.replace(
                Constants.Invoice.InvoiceBalanceAmount,
                invoice?.salesInvoice?.BalanceFormatted()?:""
            )


            templateText = templateText?.replace(
                Constants.Invoice.InvoiceOuStandingBalanceAmount,
                invoice?.salesInvoice?.OutstandingBalanceFormatted()?:""
            )

            templateText = templateText?.replace(
                Constants.Invoice.InvoicePaidAmount,
                invoice?.salesInvoice?.PaidAmountFormatted()?:""
            )
        }


        templateText = templateText?.replace(
            Constants.Invoice.InvoicePrintDateTime, getCurrentDateTimeSingapore()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceSignature,
            "@@@" + invoice?.salesInvoice?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceQR,
            Constants.QRTagStart + invoice?.salesInvoice?.zatcaQRCode.toString() + Constants.QRTagEnd
        )


        templateText?.let { PrinterManager.print(printableText = it, noOfCopies = template?.printDefault?:1) }

        Log.d("Sale Invoice Print", templateText.toString())
    }

    fun printInvoice(activity: Activity, invoice: SaleInvoiceObject?) {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    updateTemplateforInvoiceAndPrint(res.result?.get(0), invoice)
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get receipt template")
            }
        }).autoLoadigCancel(Loading().forApi(activity, "Loading receipt template..."))
            .enque(Network.api()?.getReceiptTemplatePrint(TypeRequest())).execute()
    }

    fun printDeliveryOrder(activity: Activity, order: DeliveryOrderDTO?) {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    prepareDeliveryOrderTemplateAndPrint(res.result?.get(0), order)
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get order template")
            }
        }).autoLoadigCancel(Loading().forApi(activity, "Loading order template...")).enque(
            Network.api()?.getReceiptTemplatePrint(TypeRequest(PRINT_TYPE_DELIVERY_ORDER))
        ).execute()
    }

    private fun prepareDeliveryOrderTemplateAndPrint(
        template: PrintTemplate?, order: DeliveryOrderDTO?
    ) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Delivery.OrderNo, order?.deliveryOrder?.deliveryNo?:""
        )

        templateText = templateText?.replace(
            Constants.Delivery.ReferenceNo, order?.deliveryOrder?.getPONo()?:""
        )

        templateText = templateText?.replace(
            Constants.Delivery.Date, order?.deliveryOrder?.DeliveryDateFormatted()?:""
        )

        templateText = templateText?.replace(
            Constants.Delivery.CustomerName, order?.deliveryOrder?.customerName?:""
        )

        templateText = templateText?.replace(
            Constants.Delivery.CustomerCode, order?.deliveryOrder?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Delivery.CustomerAddress1, order?.deliveryOrder?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Delivery.CustomerAddress2, order?.deliveryOrder?.address2 ?: ""
        )


        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0
        order?.deliveryOrderDetail?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, it.slNo?.toString()?:"")
                ?.replace(Constants.Delivery.ProductName, it.productName?:"")
                ?.replace(Constants.Delivery.Qty, it.deliveredQty?.toString()?:"")
                ?.replace(Constants.Delivery.UOM, it.uom?:"")
            count += 1
            if (count < order.deliveryOrderDetail.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText = templateText?.replace(
            Constants.Delivery.DeliveredQty, order?.deliveryOrder?.totalDeliveredQty?.toString()?:""
        )

//        templateText = templateText?.replace(
//            Constants.Delivery.DeliveryQty, order?.deliveryOrder?.totalDeliveredQty.toString()
//        )
//
        templateText = templateText?.replace(
            Constants.Delivery.DeliverySignature,
            "@@@" + order?.deliveryOrder?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.DeliveryQR,
            Constants.QRTagStart + order?.deliveryOrder?.zatcaQRCode.toString() + Constants.QRTagEnd
        )

        templateText?.let {
            PrinterManager.print(it,noOfCopies=template?.printDefault?:1)
        }
        Log.d("Print", templateText.toString())
    }

    fun printAgreementMemo(activity: Activity, memo: CreateUpdateAgreementMemoRequestBody?) {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    prepareAgreementMemoTemplateAndPrint(res.result?.get(0), memo)
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get order template")
            }
        }).autoLoadigCancel(Loading().forApi(activity, "Loading order template...")).enque(
            Network.api()?.getReceiptTemplatePrint(TypeRequest(PRINT_TYPE_AGREEMENT_MEMO))
        ).execute()
    }

    private fun prepareAgreementMemoTemplateAndPrint(template: PrintTemplate?, memo: CreateUpdateAgreementMemoRequestBody?){
        var templateText = template?.template
        val printDefaultNo = template?.printDefault

        val agreementMemo=memo?.AgreementMemo

        templateText = templateText?.replace(
            Constants.AgreementMemo.AgreementMemoNo, agreementMemo?.AgreementNo?:""
        )

        templateText = templateText?.replace(
            Constants.AgreementMemo.AgreementMemoDate, agreementMemo?.agreementDateFormatted()?:""
        )

        templateText = templateText?.replace(
            Constants.AgreementMemo.AgreementMemoQty, agreementMemo?.AgreementQty?.toString()?:""
        )


        templateText = templateText?.replace(
            Constants.AgreementMemo.AgreementMemoSignature, "@@@" + agreementMemo?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerName, agreementMemo?.CustomerName?:""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerCode, agreementMemo?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerAddress1, agreementMemo?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerAddress2, agreementMemo?.address2 ?: ""
        )

        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        memo?.AgreementMemoDetail?.forEachIndexed{count,item->
            items += itemTemplateClean?.replace(Constants.Common.Index, "${count+1}")
                ?.replace(Constants.Common.ProductName, item.ProductName?:"")
                ?.replace(Constants.Common.Qty, item.Qty?.toString()?:"")
                ?.replace(Constants.Common.UOM, item.UnitName?:"")
                ?.replace(Constants.AgreementMemo.AgreementMemoType, item.AgreementTypeDisplayText?:"")
            if (count <memo.AgreementMemoDetail.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText?.let { printableText -> PrinterManager.print(printableText = printableText, noOfCopies = printDefaultNo?.takeIf { it>0 }?:1) }
        Log.d("Print", templateText.toString())
    }

    fun printServiceForm(activity: Activity, service:ServiceFormBody?) {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    prepareServiceFormTemplateAndPrint(res.result?.get(0), service)
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get order template")
            }
        }).autoLoadigCancel(Loading().forApi(activity, "Loading order template...")).enque(
            Network.api()?.getReceiptTemplatePrint(TypeRequest(PRINT_TYPE_SERVICE_FORM))
        ).execute()
    }

    private fun prepareServiceFormTemplateAndPrint(
        template: PrintTemplate?, service: ServiceFormBody?
    ) {
        var templateText = template?.template
        val printDefaultNo = template?.printDefault

        val complaintService=service?.complaintService

        templateText = templateText?.replace(
            Constants.ComplaintService.CSNo, complaintService?.csNo?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSDate, complaintService?.csDate?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSDeliveryDate, complaintService?.type?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSReportTypeDeliveryDate, complaintService?.reportType?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSQty, complaintService?.totalQty?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSTimeIn, complaintService?.getCheckInTime()?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSTimeOut, complaintService?.getCheckOutTime()?:""
        )

        templateText = templateText?.replace(
            Constants.ComplaintService.CSRemarks, complaintService?.remarks?:""
        )


        templateText = templateText?.replace(
            Constants.ComplaintService.CSSignature, "@@@" + complaintService?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerName, complaintService?.customerName?:""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerCode, complaintService?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerAddress1, complaintService?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Customers.CustomerAddress2, complaintService?.address2 ?: ""
        )

        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        service?.complaintServiceDetails?.forEachIndexed{count,item->
            items += itemTemplateClean?.replace(Constants.Common.Index, "${count+1}")
                ?.replace(Constants.Common.ProductName, item.productName?:"")
                ?.replace(Constants.Common.Qty, item.qty?:"")
                ?.replace(Constants.Common.UOM, item.unitName?:"")
                ?.replace(Constants.ComplaintService.CSTransTypeString, item.transTypeDisplayText?:"")
                ?.replace(Constants.ComplaintService.CSActionTypeString, item.actionTypeString?:"")
                .toString()
            if (count <service.complaintServiceDetails.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText?.let { printableText -> PrinterManager.print(printableText = printableText, noOfCopies = printDefaultNo?.takeIf { it>0 }?:1) }
        Log.d("Print", templateText.toString())
    }


    fun printSaleOrder(activity: Activity, order: SaleOrderResponse?) {
        getTemplate(activity, PRINT_TYPE_SALE_ORDER) { template ->
            template?.let {
                prepareOrderTemplateAndPrint(it, order)
            }
        }
    }

    private fun prepareOrderTemplateAndPrint(template: PrintTemplate?, order: SaleOrderResponse?) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Order.OrderNo, order?.salesOrder?.soNo?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderDate, order?.salesOrder?.soDate?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderTerm, order?.salesOrder?.paymentReference?:""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerCustomerName, order?.salesOrder?.customerName?:""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerCustomerCode, order?.salesOrder?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerAddress1, order?.salesOrder?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerAddress2, order?.salesOrder?.address2 ?: ""
        )


        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0
        order?.salesOrderDetail?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, it.slNo?.toString()?:"")
                ?.replace(Constants.Invoice.ProductName, it.productName?:"")
                ?.replace(Constants.Invoice.Qty, it.qty?.toString()?:"")
                ?.replace(Constants.Invoice.UOM, it.unitName?:"")
                ?.replace(Constants.Invoice.Price, it.price?.toString()?:"")
                ?.replace(Constants.Invoice.NetTotal, it.netTotal?.toString()?:"").toString()
            count += 1
            if (count < order.salesOrderDetail.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText = templateText?.replace(
            Constants.Order.OrderSubTotal, order?.salesOrder?.soSubTotal?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderTax, order?.salesOrder?.soTax?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderNetTotal, order?.salesOrder?.soNetTotal?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderQty, order?.salesOrder?.orderedQty?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerBalanceAmount, order?.salesOrder?.balance?.toString()?:""
        )

        templateText = templateText?.replace(
            Constants.Order.OrderSignature, "@@@" + order?.salesOrder?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Order.OrderQR,
            Constants.QRTagStart + order?.salesOrder?.zatcaQRCode.toString() + Constants.QRTagEnd
        )

        templateText?.let { PrinterManager.print(printableText = it, noOfCopies = template?.printDefault?:1) }

        Log.d("Print", templateText.toString())
    }

    fun printReceipt(activity: Activity, saleReceipt: SaleReceipt?) {
        getTemplate(activity, PRINT_TYPE_RECEIPT) { template ->
            template?.let {
                prepareReceiptTemplateAndPrint(it, saleReceipt)
            }
        }
    }


    private fun prepareReceiptTemplateAndPrint(template: PrintTemplate?, receipt: SaleReceipt?) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Invoice.InvoiceNo, receipt?.receiptNo?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceDate, receipt?.receiptDate?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceTerm, receipt?.paymentTypeName?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCustomerName, receipt?.customerName?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCustomerCode, receipt?.customerCode?:""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress1, receipt?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress2, receipt?.address2 ?: ""
        )


        templateText = templateText?.replace(
            Constants.Invoice.TotalAmount, "${receipt?.getAmount()?:0.0}"
        )


        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0
        receipt?.items?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, count.toString())
                ?.replace(Constants.Receipt.TransactionNo, it.transactionNo?:"")
                ?.replace(Constants.Receipt.TransactionDate, it.transactionDate?:"")
                ?.replace(Constants.Receipt.TransactionAmount, it.getAppliedAmount())
            count += 1
            if (count < (receipt.items?.size ?: 0)) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

//
//        templateText = templateText?.replace(
//            Constants.Order.OrderSignature,
//            "@@@" + order?.salesOrder?.signatureUrl().toString()
//        )

        templateText = templateText?.replace(
            Constants.Order.OrderQR,
            Constants.QRTagStart + receipt?.zatcaQRCode.toString() + Constants.QRTagEnd
        )

        templateText?.let { PrinterManager.print(printableText = it, noOfCopies = template?.printDefault?:1) }

        Log.d("Print", templateText.toString())
    }

    /*-------------Print current stocks-----------------*/

    fun printInComingStock(activity: Activity, stockReceived: StockTransferDetailItem?){
        getTemplate(activity, PRINT_TYPE_INCOMMING_STOCK) { template ->
            template?.let {
                prepareInComingStockTemplateAndPrint(it, stockReceived)
            }
        }
    }

    private fun prepareInComingStockTemplateAndPrint(
        template: PrintTemplate?,
        stockReceived: StockTransferDetailItem?){
        val templateText = template?.template
        templateText?.let { PrinterManager.print(printableText=it, noOfCopies = template.printDefault ) }
        Log.d("Print", templateText.toString())
    }

    fun printCurrentStock(activity: Activity, currentStocks: ArrayList<HistoryItemInterface>) {
        getTemplate(activity, PRINT_TYPE_CURRENT_STOCK) { template ->
            template?.let {
                prepareStockTemplateAndPrint(it, currentStocks)
            }
        }
    }

    private fun prepareStockTemplateAndPrint(template: PrintTemplate?,currentStocks: ArrayList<HistoryItemInterface>) {
        var templateText = template?.template
        var items = ""
        //var count = 0

        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        currentStocks.forEachIndexed { index, historyItemInterface ->
            val stock = historyItemInterface as Product
            items += itemTemplateClean?.replace(Constants.Common.Index, "${index+1}")
                ?.replace(Constants.CurrentStock.ProductName, stock.getTitle())
                ?.replace(Constants.CurrentStock.Price, stock.getAmount())
                ?.replace(Constants.CurrentStock.QTY, stock.getPrintQty())
                ?.replace(Constants.CurrentStock.UOM, stock.getPrintUOM())
                ?.replace(Constants.CurrentStock.MinQty, stock.getPrintMinQty())
                ?.replace(Constants.CurrentStock.VarQty, stock.getPrintVarianceQty())
            if (index < currentStocks.size) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)
        templateText?.let { PrinterManager.print(printableText = it, noOfCopies = template?.printDefault?:1) }

        Log.d("Print", templateText.toString())
    }


    private fun getTemplate(
        activity: Activity,
        typeId: Int,
        onResult: (PrintTemplate?) -> Unit
    ) {
        val cached = templateCache[typeId]
        if (cached != null) {
            onResult(cached)
            return
        }

        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    val template = res.result?.getOrNull(0)
                    templateCache[typeId] = template
                    onResult(template)
                }else{
                    Notify.toastLong("Unable to get print template")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get print template")
                onResult(null)
            }

        }).autoLoadigCancel(Loading().forApi(activity, "Loading print template..."))
            .enque(Network.api()?.getReceiptTemplatePrint(TypeRequest(typeId)))
            .execute()
    }

    fun clearCache() {
        templateCache.clear()
    }
}