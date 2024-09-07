package com.singularitycoder.learnit.subtopic.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.singularitycoder.learnit.databinding.ListItemAddSubTopicBinding
import com.singularitycoder.learnit.helpers.hideKeyboard
import com.singularitycoder.learnit.helpers.onCustomLongClick
import com.singularitycoder.learnit.helpers.onSafeClick
import com.singularitycoder.learnit.helpers.showKeyboard
import com.singularitycoder.learnit.subtopic.model.SubTopic

class AddSubTopicsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var subTopicList = mutableListOf<SubTopic?>()
    private var itemClickListener: (subTopic: SubTopic?, position: Int) -> Unit = { _, _ -> }
    private var itemLongClickListener: (subTopic: SubTopic?, view: View?, position: Int) -> Unit = { _, _, _ -> }
    private var itemApproveUpdateClickListener: (subTopic: SubTopic?, position: Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = ListItemAddSubTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThisViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ThisViewHolder).setData(subTopicList[position])
    }

    override fun getItemCount(): Int = subTopicList.size

    override fun getItemViewType(position: Int): Int = position

    fun moveItem(from: Int, to: Int) {
        val fromItem = subTopicList[from]
        subTopicList.removeAt(from)
        subTopicList[to] = fromItem
//        if (to < from) {
//            subTopicList[to] = fromItem
//        } else {
//            subTopicList[to-1] = fromItem
//        }
    }

    fun setOnItemClickListener(listener: (subTopic: SubTopic?, position: Int) -> Unit) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(
        listener: (
            subTopic: SubTopic?,
            view: View?,
            position: Int
        ) -> Unit
    ) {
        itemLongClickListener = listener
    }

    fun setOnApproveUpdateClickListener(listener: (subTopic: SubTopic?, position: Int) -> Unit) {
        itemApproveUpdateClickListener = listener
    }

    fun showEditView(
        recyclerView: RecyclerView,
        adapterPosition: Int,
    ) {
        val viewHolder = (recyclerView.findViewHolderForAdapterPosition(adapterPosition) as? ThisViewHolder) ?: return
        viewHolder.getRootView().apply {
            clTitle.isVisible = false
            layoutUpdateItem.apply {
                root.isVisible = true
                etUpdateItem.setText(subTopicList[adapterPosition]?.title)
                etUpdateItem.showKeyboard()
            }
        }
    }

    inner class ThisViewHolder(
        private val itemBinding: ListItemAddSubTopicBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun getRootView() = itemBinding

        fun setData(subTopic: SubTopic?) {
            itemBinding.apply {
                tvTitle.text = subTopic?.title
                tvStepNumber.text = "${bindingAdapterPosition + 1}"
                root.onSafeClick {
                    itemClickListener.invoke(subTopic, bindingAdapterPosition)
                }
                root.onCustomLongClick {
                    itemLongClickListener.invoke(subTopic, it, bindingAdapterPosition)
                }
                layoutUpdateItem.ibCancelUpdate.onSafeClick {
                    layoutUpdateItem.root.isVisible = false
                    clTitle.isVisible = true
                    layoutUpdateItem.etUpdateItem.hideKeyboard()
                }
                layoutUpdateItem.ibApproveUpdate.onSafeClick {
                    itemApproveUpdateClickListener.invoke(
                        subTopic?.copy(title = layoutUpdateItem.etUpdateItem.text.toString()),
                        bindingAdapterPosition
                    )
                    layoutUpdateItem.root.isVisible = false
                    clTitle.isVisible = true
                    layoutUpdateItem.etUpdateItem.hideKeyboard()
                }
            }
        }
    }
}