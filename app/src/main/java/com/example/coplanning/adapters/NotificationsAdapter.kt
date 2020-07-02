package com.example.coplanning.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coplanning.databinding.NotificationLayoutBinding
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.notification.Notification

class NotificationsAdapter
    : ListAdapter<Notification, NotificationsAdapter.ViewHolder>(NotificationsListDiffCallback()) {

    class ViewHolder private constructor(
        val binding: NotificationLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            val date = DateAndTimeConverter.getISOStringDateFromISOString(item.dateTime)
            val time = DateAndTimeConverter.getISOStringTimeFromISOString(item.dateTime)
            binding.dateTime = "$date, $time"
            binding.senderTv.paintFlags = binding.senderTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.sender = item.sender
            binding.description = item.description

            val nameChanges =
                if (item.additionalInfo.name.isNullOrEmpty()) ""
                else "Name: " + item.additionalInfo.name + "\n"
            val commentChanges =
                if (item.additionalInfo.comment.isNullOrEmpty()) ""
                else """ Comment: ${item.additionalInfo.comment}"""
            val dateTimeFromChanges =
                if (item.additionalInfo.dateTimeFrom.isNullOrEmpty()) ""
                else """ Start date: ${item.additionalInfo.dateTimeFrom}"""
            val dateTimeToChanges =
                if (item.additionalInfo.dateTimeTo.isNullOrEmpty()) ""
                else """ Finish date: ${item.additionalInfo.dateTimeTo}"""
            binding.additionalInfo =
                nameChanges + commentChanges + dateTimeFromChanges + dateTimeToChanges
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                 val layoutInflater = LayoutInflater.from(parent.context)
                 val binding
                         = NotificationLayoutBinding.inflate(
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
        holder.bind(item)
    }

}

class NotificationsListDiffCallback: DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem==newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem==newItem
    }
}