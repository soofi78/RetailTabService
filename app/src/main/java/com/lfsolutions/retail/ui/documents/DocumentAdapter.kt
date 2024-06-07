package com.lfsolutions.retail.ui.documents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemDocumentBinding

class DocumentAdapter : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    private var documentList = listOf<DocumentType>()

    class ViewHolder(val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(documentList: List<DocumentType>) {

        this.documentList = documentList

        notifyDataSetChanged()

    }

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

        holder.binding.let { binding ->

            documentList[position].let { document ->

                binding.txtDocumentName.text = holder.itemView.context.getString(document.label)

                binding.icoDocument.setImageResource(document.iconResId)
            }

        }

    }
}