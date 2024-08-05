package com.lfsolutions.retail.ui.documents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemDocumentBinding
import com.lfsolutions.retail.model.Documents

class DocumentAdapter(
    val documentList: ArrayList<DocumentType>,
    val onDocumentClickedListener: OnDocumentClickedListener
) :
    RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemDocumentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = documentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = documentList[position]
        holder.binding.txtDocumentName.text = holder.itemView.context.getString(document.label)
        holder.binding.icoDocument.setImageResource(document.iconResId)
        holder.binding.root.tag = document
        holder.binding.root.setOnClickListener {
            onDocumentClickedListener.onDocumentClicked(it.tag as DocumentType)
        }
    }

    interface OnDocumentClickedListener {
        fun onDocumentClicked(documents: DocumentType)
    }
}