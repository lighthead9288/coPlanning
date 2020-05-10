package com.example.coplanning.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coplanning.databinding.SavedMappingLayoutBinding
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.SavedMapping

class SavedMappingsAdapter(val savedMappingClickListener: SavedMappingClickListener): ListAdapter<SavedMapping, SavedMappingsAdapter.ViewHolder>(SavedMappingsListDiffCallback()) {

    class ViewHolder private constructor(val binding: SavedMappingLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SavedMapping, savedMappingClickListener: SavedMappingClickListener) {
            binding.savedMapping = item
            binding.savedMappingClickListener = savedMappingClickListener

            val dateFrom= DateAndTimeConverter.GetISOStringDateFromCalendar(item.GetDateTimeFrom())
            val timeFrom= DateAndTimeConverter.GetISOStringTimeFromCalendar(item.GetDateTimeFrom())
            binding.dateTimeFrom = "$dateFrom, $timeFrom"

            val dateTo = DateAndTimeConverter.GetISOStringDateFromCalendar(item.GetDateTimeTo())
            val timeTo = DateAndTimeConverter.GetISOStringTimeFromCalendar(item.GetDateTimeTo())
            binding.dateTimeTo = "$dateTo, $timeTo"

            val participants = item.GetParticipants()
            var participantsListStr = ""
            for (participant in participants) {
                participantsListStr += "$participant; "
            }
            binding.participantsList = participantsListStr

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): SavedMappingsAdapter.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SavedMappingLayoutBinding.inflate(layoutInflater, parent, false)
                return SavedMappingsAdapter.ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return SavedMappingsAdapter.ViewHolder.from(
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