package com.example.coplanning.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coplanning.databinding.SavedMappingLayoutBinding
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.mapping.SavedMapping

class SavedMappingsAdapter(
    private val savedMappingClickListener: SavedMappingClickListener
) : ListAdapter<SavedMapping, SavedMappingsAdapter.ViewHolder>(SavedMappingsListDiffCallback()) {

    class ViewHolder private constructor(val binding: SavedMappingLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SavedMapping, savedMappingClickListener: SavedMappingClickListener) {
            binding.savedMapping = item
            binding.savedMappingClickListener = savedMappingClickListener

            val dateFrom
                    = DateAndTimeConverter.getISOStringDateFromCalendar(item.dateTimeFrom)
            val timeFrom
                    = DateAndTimeConverter.getISOStringTimeFromCalendar(item.dateTimeFrom)
            binding.dateTimeFrom = "$dateFrom, $timeFrom"

            val dateTo = DateAndTimeConverter.getISOStringDateFromCalendar(item.dateTimeTo)
            val timeTo = DateAndTimeConverter.getISOStringTimeFromCalendar(item.dateTimeTo)
            binding.dateTimeTo = "$dateTo, $timeTo"

            val participants = item.participants
            var participantsListStr = ""
            for (participant in participants) {
                participantsListStr += "$participant; "
            }
            binding.participantsList = participantsListStr

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SavedMappingLayoutBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, savedMappingClickListener)
    }
}

class SavedMappingsListDiffCallback: DiffUtil.ItemCallback<SavedMapping>() {
    override fun areItemsTheSame(oldItem: SavedMapping, newItem: SavedMapping): Boolean {
        return oldItem==newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SavedMapping, newItem: SavedMapping): Boolean {
        return oldItem==newItem
    }
}

class SavedMappingClickListener(val savedMappingClickListener: (savedMapping: SavedMapping)->Unit) {
    fun onClick(savedMapping: SavedMapping) = savedMappingClickListener(savedMapping)
}