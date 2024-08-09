package com.lfsolutions.retail.ui.documents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentDocumentsBinding
import com.lfsolutions.retail.model.Documents
import com.lfsolutions.retail.ui.documents.history.HistoryFlowActivity
import com.lfsolutions.retail.ui.documents.payment.PaymentFlowActivity
import java.util.ArrayList

class DocumentsFragment : Fragment() {

    private lateinit var binding: FragmentDocumentsBinding
    private lateinit var mAdapter: DocumentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not())
            binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        mAdapter = DocumentAdapter(getDocumentsMenuList(),
            object : DocumentAdapter.OnDocumentClickedListener {
                override fun onDocumentClicked(documents: DocumentType) {
                    openDocument(documents)
                }
            })
        binding.recyclerViewDocuments.adapter = mAdapter
    }

    private fun openDocument(document: DocumentType) {
        if (document is DocumentType.Payment) {
            openPaymentView()
        }
        if (document is DocumentType.History) {
            openHistoryView()
        }
    }

    private fun openHistoryView() {
        startActivity(Intent(requireActivity(), HistoryFlowActivity::class.java))
    }

    private fun openPaymentView() {
        startActivity(Intent(requireActivity(), PaymentFlowActivity::class.java))
    }

    private fun getDocumentsMenuList(): ArrayList<DocumentType> {
        return arrayListOf(
            DocumentType.OutGoingStockRecord(
                R.string.label_outgoing_stock_record,
                R.drawable.outgoing_stock_record
            ),
            DocumentType.DailySalesRecord(
                R.string.label_daily_sales_record,
                R.drawable.daily_sales_record
            ),
            DocumentType.DriverMemo(
                R.string.label_driver_memo,
                R.drawable.driver_memo
            ),
            DocumentType.Payment(
                R.string.label_payment,
                R.drawable.ic_payment
            ),
            DocumentType.History(
                R.string.label_history,
                R.drawable.ic_history
            )
        )
    }

}