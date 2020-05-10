package com.example.coplanning.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.example.coplanning.R
import com.example.coplanning.adapters.NotificationsAdapter
import com.example.coplanning.databinding.FragmentNotificationsBinding
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.viewmodels.NotificationsViewModel
import com.example.coplanning.viewmodels.ProfileViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val application = requireNotNull(this.activity).application
        InitViewModel(application)
    }

    lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentNotificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        binding.viewModel = viewModel

        val adapter = NotificationsAdapter()
        binding.notificationsListRv.adapter = adapter

        viewModel.notifications.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.lifecycleOwner = this

        return binding.root
    }

    fun InitViewModel(application: Application) {
        if (ScreensDataStorage.curNotificationsScreenData!=null) {
            val data = ScreensDataStorage.curNotificationsScreenData as NotificationsFragment
            viewModel = data.viewModel
        }

        else {
            viewModel = NotificationsViewModel(application)
            ScreensDataStorage.curProfileScreenData = this
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
