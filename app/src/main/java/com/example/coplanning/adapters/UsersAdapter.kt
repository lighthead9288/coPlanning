package com.example.coplanning.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coplanning.R
import com.example.coplanning.databinding.UserLayoutBinding
import com.example.coplanning.globals.MappingElementsManager
import com.example.coplanning.models.user.User
import com.example.coplanning.viewmodels.MappingAddChangeViewListener
import com.example.coplanning.viewmodels.SubscribeChangeViewListener

class UsersAdapter(
    private val openUserClickListener: OpenUserClickListener,
    private val subscribeOnUserClickListener: SubscribeOnUserClickListener,
    private val addUserToMappingClickListener: AddUserToMappingClickListener,
    private val username: String
) : ListAdapter<User, UsersAdapter.ViewHolder>(UsersListDiffCallback()) {

    class ViewHolder private constructor(private var binding: UserLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: User,
            openUserClickListener: OpenUserClickListener,
            subscribeOnUserClickListener: SubscribeOnUserClickListener,
            addUserToMappingClickListener: AddUserToMappingClickListener,
            username: String
        ) {
            binding.user = item
            binding.isMe = item.username==username
            binding.subscibeDirection = !item.subscriberList.contains(username)

            changeUserSubscribeButton(item, username)
            changeUserMappingButton(item.username)

            binding.openUserClickListener = openUserClickListener
            binding.subscribeOnUserClickListener = subscribeOnUserClickListener
            binding.addUserToMappingClickListener = addUserToMappingClickListener

            binding.subscribeChangeViewListener = SubscribeChangeViewListener {
                if (it != null) {
                    binding.subscibeDirection = !it.subscriberList.contains(username)
                    changeUserSubscribeButton(it, username)
                }
            }

            binding.mappingAddChangeViewListener = MappingAddChangeViewListener {
                changeUserMappingButton(it)
            }

            binding.executePendingBindings()

        }

        private fun changeUserSubscribeButton(curUser: User, username: String) {
            if (curUser.subscriberList.contains(username)) {
                binding.subscribeButton.setImageResource(R.drawable.subscribe_off)
            } else {
                binding.subscribeButton.setImageResource(R.drawable.subscribe_on)
            }
        }

        private fun changeUserMappingButton(username: String) {
            val elements = mappingElementsManager.getMappingElements()
            if (elements.contains(username)) {
                binding.addToMappingButton.setImageResource(R.drawable.remove_from_mapping)
            } else {
                binding.addToMappingButton.setImageResource(R.drawable.add_to_mapping)
            }
        }

        private val mappingElementsManager: MappingElementsManager = MappingElementsManager()

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserLayoutBinding.inflate(layoutInflater, parent, false)
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
        holder.bind(item, openUserClickListener, subscribeOnUserClickListener, addUserToMappingClickListener, username)
    }

}

class UsersListDiffCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.username==newItem.username
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem==newItem
    }
}

class OpenUserClickListener(val openUserClickListener: (user: User)->Unit) {
    fun onClick(user: User) = openUserClickListener(user)
}

class SubscribeOnUserClickListener(val subscribeOnUserClickListener: (user: User, direction: Boolean, listener: SubscribeChangeViewListener)->Unit) {
    fun onClick(user: User, direction: Boolean, listener: SubscribeChangeViewListener) = subscribeOnUserClickListener(user, direction, listener)

}

class AddUserToMappingClickListener(val addUserToMappingClickListener: (user: User, listener: MappingAddChangeViewListener)->Unit) {
    fun onClick(user: User, listener: MappingAddChangeViewListener) = addUserToMappingClickListener(user, listener)
}

