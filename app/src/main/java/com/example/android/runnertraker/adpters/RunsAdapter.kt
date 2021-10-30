package com.example.android.runnertraker.adpters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.runnertraker.databinding.ItemViewBinding
import com.example.android.runnertraker.model.RunModel

class RunsAdapter : RecyclerView.Adapter<RunsAdapter.RunViwHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<RunModel>() {
        override fun areItemsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,differCallback)

    fun setList (list: List<RunModel>)=differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViwHolder {
        return RunViwHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RunViwHolder, position: Int) {
        val runModel = differ.currentList[position]
        holder.bind(runModel)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    inner class RunViwHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (runModel : RunModel){
            binding.runInfo=runModel
            binding.executePendingBindings()
        }
    }
}