package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName


data class SaleInvoiceObject(

    @SerializedName(
        "SalesInvoice", alternate = arrayOf("salesInvoice")
    ) var salesInvoice: SalesInvoice? = SalesInvoice(), @SerializedName(
        "SalesInvoiceDetail", alternate = arrayOf("salesInvoiceDetail")
    ) var salesInvoiceDetail: ArrayList<SalesInvoiceDetail> = arrayListOf()

) {
    fun addEquipment(product: SalesInvoiceDetail) {
        salesInvoiceDetail.add(product)
        updatePriceAndQty()
    }

    fun updatePriceAndQty(appliedDiscount: Double = 0.0) {
        var qty = 0.0
        var netTotal = 0.0
        var discount = 0.0
        var subTotal = 0.0
        var taxAmount = 0.0
        var total = 0.0
        var discounts = 0.0
        val productPrices = arrayListOf<Double>()
        salesInvoiceDetail.forEach {
            productPrices.add((it.totalValue ?: 0.0))
        }
        val discountsPerItem = distributeDiscount(productPrices, appliedDiscount)
        var index = 0
        salesInvoiceDetail.forEach {
            val itemDiscount = discountsPerItem[index]
            qty = qty.plus(it.qty ?: 0.0)
            it.netDiscount = itemDiscount
            it.itemDiscount = itemDiscount
            it.subTotal = it.qty?.times(it.costWithoutTax)?.minus(itemDiscount)
            it.netTotal = it.subTotal
            it.netTotal = (it.totalValue ?: 0.0)
            it.tax = (it.subTotal ?: 0.0) * it.taxRate / 100
            netTotal = netTotal.plus(it.netTotal ?: 0.0)
            discount = discount.plus(it.netDiscount ?: 0.0)
            subTotal = subTotal.plus(it.subTotal ?: 0.0)
            taxAmount = taxAmount.plus(it.tax ?: 0.0)
            total = total.plus(it.totalValue ?: 0.0)
            index++
        }
        salesInvoice?.invoiceQty = qty
        salesInvoice?.invoiceTotalValue = total
        salesInvoice?.invoiceNetTotal = subTotal.plus(taxAmount)
        salesInvoice?.invoiceSubTotal = subTotal
        salesInvoice?.invoiceTax = taxAmount
        salesInvoice?.netDiscount = discount
        salesInvoice?.invoiceNetDiscount = discount
        salesInvoice?.invoiceItemDiscount = discount
        salesInvoice?.invoiceGrandTotal = subTotal.plus(taxAmount)
        salesInvoice?.balance = salesInvoice?.invoiceGrandTotal

        if (discount == 0.0) salesInvoice?.invoiceNetDiscountPerc = 0.0
        else salesInvoice?.invoiceNetDiscountPerc = (salesInvoice?.invoiceGrandTotal?.let {
            discount.div(it)
        })?.times(100)


    }

    fun serializeItems() {
        var serial = 0
        salesInvoiceDetail.forEach {
            serial += 1
            it.slNo = serial
        }
    }

    fun distributeDiscount(productPrices: List<Double>, totalDiscount: Double): List<Double> {
        val discounts = MutableList(productPrices.size) { 0.0 }
        var remainingDiscount = totalDiscount

        // Calculate the total price of all products
        val totalPrice = productPrices.sum()

        if (totalPrice <= 0.toDouble() || totalDiscount <= 0) {
            for (i in productPrices.indices) {
                discounts[i] = 0.0
            }
            return discounts
        }

        // Step 1: Distribute the discount proportionally
        for (i in productPrices.indices) {
            discounts[i] = (productPrices[i] / totalPrice) * totalDiscount
        }

        // Step 2: Adjust discounts if they exceed product prices
        var adjusted = true
        while (adjusted) {
            adjusted = false
            for (i in productPrices.indices) {
                if (discounts[i] > productPrices[i]) {
                    remainingDiscount += discounts[i] - productPrices[i]
                    discounts[i] = productPrices[i]
                    adjusted = true
                }
            }

            // Redistribute remaining discount proportionally among eligible products
            if (adjusted && remainingDiscount > 0) {
                val eligibleProducts =
                    productPrices.indices.filter { discounts[it] < productPrices[it] }
                val eligibleTotalPrice =
                    eligibleProducts.sumOf { productPrices[it] - discounts[it] }

                for (i in eligibleProducts) {
                    val additionalDiscount =
                        (productPrices[i] - discounts[i]) / eligibleTotalPrice * remainingDiscount
                    if (discounts[i] + additionalDiscount > productPrices[i]) {
                        remainingDiscount -= productPrices[i] - discounts[i]
                        discounts[i] = productPrices[i]
                    } else {
                        discounts[i] += additionalDiscount
                        remainingDiscount -= additionalDiscount
                    }
                }
            }
        }

        return discounts
    }

}