package com.example.coplanning.fragments

import android.app.Application
import android.app.TimePickerDialog
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.example.coplanning.R
import com.example.coplanning.databinding.FragmentProfileBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.MenuOperations
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.viewmodels.ProfileViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), InitViewModel {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private var menuOperations = MenuOperations()
    private lateinit var fragmentOperations: FragmentOperations


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val application = requireNotNull(this.activity).application
        initViewModel(application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initFragmentOperations()
        initBinding(inflater, container)
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewModel = viewModel
        binding.username.paintFlags = binding.username.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.defaultUnavailableTimeFromTv.setOnClickListener {
            showSetTimeFromDialog()
        }
        binding.defaultUnavailableTimeToTv.setOnClickListener {
            showSetTimeToDialog()
        }
        binding.logout.setOnClickListener {
            viewModel.logout()
            menuOperations.hideBottomNavigationView()
            fragmentOperations.loadFragment(LoginFragment())
        }
        binding.lifecycleOwner = this
    }

    private fun initFragmentOperations() {
        fragmentOperations = FragmentOperations(fragmentManager)
    }

    override fun initViewModel(application: Application) {
        if (ScreensDataStorage.curProfileScreenData!=null) {
            val data = ScreensDataStorage.curProfileScreenData as ProfileFragment
            viewModel = data.viewModel
        } else {
            viewModel = ProfileViewModel(application)
            ScreensDataStorage.curProfileScreenData = this
        }
    }

    private fun showSetTimeFromDialog() {
        TimePickerDialog(
            context,
            tFrom,
            DateAndTimeConverter.getHourFromISOStringTime(viewModel.timeFromString.value.toString()),
            DateAndTimeConverter.getMinutesFromISOStringTime(viewModel.timeFromString.value.toString()),
            true
        )
            .show()
    }

    private var tFrom =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.setTimeFromCommand(hourOfDay, minute)
        }

    private fun showSetTimeToDialog() {
        TimePickerDialog(
            context,
            tTo,
            DateAndTimeConverter.getHourFromISOStringTime(viewModel.timeToString.value.toString()),
            DateAndTimeConverter.getMinutesFromISOStringTime(viewModel.timeToString.value.toString()),
            true
        )
            .show()
    }

    private var tTo =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.setTimeToCommand(hourOfDay, minute)
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
