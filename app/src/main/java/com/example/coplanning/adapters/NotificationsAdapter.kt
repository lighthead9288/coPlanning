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
import com.example.coplanning.models.Notification

class NotificationsAdapter: ListAdapter<Notification, NotificationsAdapter.ViewHolder>(NotificationsListDiffCallback()) {

    class ViewHolder private constructor(val binding: NotificationLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {

            val date = DateAndTimeConverter.GetISOStringDateFromISOString(item.GetDateTime())
            val time = DateAndTimeConverter.GetISOStringTimeFromISOString(item.GetDateTime())
            binding.dateTime = "$date, $time"

            binding.senderTv.paintFlags = binding.senderTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.sender = item.GetSender()

            binding.description = item.GetDescription()

            val nameChanges =
                if (item.GetNameChanges().isNullOrEmpty()) "" else "Name: " + item.GetNameChanges() + "\n"
            val commentChanges =
                if (item.GetCommentChanges().isNullOrEmpty()) "" else """ Comment: ${item.GetCommentChanges()}"""
            val dateTimeFromChanges =
                if (item.GetDateTimeFromChanges().isNullOrEmpty()) "" else """ Start date: ${item.GetDateTimeFromChanges()}"""
            val dateTimeToChanges =
                if (item.GetDateTimeToChanges().isNullOrEmpty()) "" else """ Finish date: ${item.GetDateTimeToChanges()}"""
            binding.additionalInfo =
                nameChanges + commentChanges + dateTimeFromChanges + dateTimeToChanges
        }

        companion object {
            fun from(parent: ViewGroup): NotificationsAdapter.ViewHolder {
                 val layoutInflater = LayoutInflater.from(parent.context)
                 val binding = NotificationLayoutBinding.inflate(layoutInflater, parent, false)
                 return NotificationsAdapter.ViewHolder(binding)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return NotificationsAdapter.ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: NotificationsAdapter.ViewHolder, position: Int) {
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