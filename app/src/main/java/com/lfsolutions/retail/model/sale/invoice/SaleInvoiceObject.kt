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
        salesInvoice?.invoiceNetDiscount?.let { updatePriceAndQty(it) }
    }

    fun updatePriceAndQty(discountProvided: Double = 0.0) {
        var appliedDiscount = discountProvided
        if (discountProvided == 0.0) {
            appliedDiscount = salesInvoice?.invoiceNetDiscount!!
        }
        var qty = 0.0
        var netTotal = 0.0
        var netDiscount = 0.0
        var subTotal = 0.0
        var taxAmount = 0.0
        var total = 0.0
        var itemDiscounts = 0.0
        val productPrices = arrayListOf<Double>()
        salesInvoiceDetail.forEach {
            productPrices.add((it.totalValue ?: 0.0))
        }
        val discountsPerItem = distributeDiscount(productPrices, appliedDiscount)
        var index = 0
        salesInvoiceDetail.forEach {
            it.netDiscount = discountsPerItem[index]
            var discountValueBasedOnPercentage = 0.0
            if (it.itemDiscountPerc != null && it.itemDiscountPerc!! > 0) {
                val discountFactor = it.itemDiscountPerc!!.div(100)
                discountValueBasedOnPercentage =
                    it.qty?.times(it.costWithoutTax)?.times(discountFactor) ?: 0.0
            }
            qty = qty.plus(it.qty ?: 0.0)
            if (it.isFOC == true) {
                it.subTotal = 0.0
                it.tax = 0.0
                it.netTotal = 0.0
                it.netTotal = 0.0
                it.totalValue = 0.0
            } else {
                it.subTotal = it.qty?.times(it.costWithoutTax)?.minus((it.itemDiscount ?: 0.0))
                    ?.minus(it.netDiscount ?: 0.0)?.minus(discountValueBasedOnPercentage)
                if (salesInvoice?.isTaxInclusive == true) {
                    it.tax = (it.subTotal ?: 0.0) * it.taxRate / (it.taxRate + 100)
                    it.subTotal = it.subTotal?.minus(it.tax ?: 0.0)
                } else {
                    it.tax = (it.subTotal ?: 0.0) * it.taxRate / 100
                }
                it.netTotal = it.subTotal
                it.netTotal = (it.totalValue ?: 0.0)
            }
            netTotal = netTotal.plus(it.netTotal ?: 0.0)
            netDiscount = netDiscount.plus(it.netDiscount ?: 0.0)
            itemDiscounts = itemDiscounts.plus(it.itemDiscount ?: 0.0)
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
        salesInvoice?.netDiscount = netDiscount
        salesInvoice?.invoiceNetDiscount = netDiscount
        salesInvoice?.invoiceItemDiscount = itemDiscounts
        salesInvoice?.invoiceGrandTotal = subTotal.plus(taxAmount)
        salesInvoice?.balance = salesInvoice?.invoiceGrandTotal

        if (netDiscount == 0.0) salesInvoice?.invoiceNetDiscountPerc = 0.0
        else salesInvoice?.invoiceNetDiscountPerc = (salesInvoice?.invoiceGrandTotal?.let {
            netDiscount.div(it)
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

    fun clear() {
        salesInvoice?.invoiceQty = 0.0
        salesInvoice?.invoiceTotalValue = 0.0
        salesInvoice?.invoiceNetTotal = 0.0
        salesInvoice?.invoiceSubTotal = 0.0
        salesInvoice?.invoiceTax = 0.0
        salesInvoice?.netDiscount = 0.0
        salesInvoice?.invoiceNetDiscount = 0.0
        salesInvoice?.invoiceItemDiscount = 0.0
        salesInvoice?.invoiceGrandTotal = 0.0
        salesInvoice?.balance = 0.0
    }

}