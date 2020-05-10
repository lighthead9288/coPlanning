package com.example.coplanning.fragments

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.coplanning.R
import com.example.coplanning.adapters.MappingElementsAdapter
import com.example.coplanning.adapters.SavedMappingClickListener
import com.example.coplanning.adapters.SavedMappingsAdapter
import com.example.coplanning.databinding.FragmentMappingsBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.viewmodels.MappingsViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MappingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MappingsFragment : Fragment() {
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

    lateinit var viewModel: MappingsViewModel
    lateinit var fragmentOperations: FragmentOperations


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMappingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mappings, container, false)
        val application = requireNotNull(this.activity).application
        fragmentOperations = FragmentOperations(fragmentManager)

        binding.viewModel = viewModel
        binding.mappingDateFrom.setOnClickListener {
            showSetDateFromDialog()
        }
        binding.mappingTimeFrom.setOnClickListener {
            showSetTimeFromDialog()
        }
        binding.mappingDateTo.setOnClickListener {
            showSetDateToDialog()
        }
        binding.mappingTimeTo.setOnClickListener {
            showSetTimeToDialog()
        }

        viewModel.UpdateMappingPanelState()

        viewModel.mappingParticipants.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val mapAdapter = MappingElementsAdapter(application, R.layout.mapping_element_layout,
                it
            ) {
                viewModel.RemoveUserFromMapping(it)
            }
            binding.mapElementsGv.adapter = mapAdapter

        })

        val savedMappingsAdapter = SavedMappingsAdapter(SavedMappingClickListener {
            viewModel.SetMappingParameters(it)
        })
        binding.savedMappingsRv.adapter = savedMappingsAdapter

        viewModel.savedMappings.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            savedMappingsAdapter.submitList(it)
        })

        binding.runMappingButton.setOnClickListener {
            val mappingData = viewModel.GetJSONMappingData()
            val fragment = MappingResultsFragment()

            val bundle = Bundle()
            bundle.putString("mappingData", mappingData.toString())
            fragment.arguments = bundle

            ScreensDataStorage.curMappingsScreenData = null
            fragmentOperations.LoadFragment(fragment)
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    private fun showSetDateFromDialog() {
        DatePickerDialog(
            context!!, dFrom,
            viewModel.dateAndTimeFrom.get(Calendar.YEAR),
            viewModel.dateAndTimeFrom.get(Calendar.MONTH),
            viewModel.dateAndTimeFrom.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    var dFrom =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.SetDateFromCommand(year, monthOfYear, dayOfMonth)
        }

    fun showSetTimeFromDialog() {
        TimePickerDialog(
            context,
            tFrom,
            viewModel.dateAndTimeFrom.get(Calendar.HOUR_OF_DAY),
            viewModel.dateAndTimeFrom.get(Calendar.MINUTE),
            true
        ).show()
    }

    var tFrom =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.SetTimeFromCommand(hourOfDay, minute)
        }


    private fun showSetDateToDialog() {
        DatePickerDialog(
            context!!, dTo,
            viewModel.dateAndTimeTo.get(Calendar.YEAR),
            viewModel.dateAndTimeTo.get(Calendar.MONTH),
            viewModel.dateAndTimeTo.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    var dTo =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.SetDateToCommand(year, monthOfYear, dayOfMonth)
        }

    fun showSetTimeToDialog() {
        TimePickerDialog(
            context,
            tTo,
            viewModel.dateAndTimeTo.get(Calendar.HOUR_OF_DAY),
            viewModel.dateAndTimeTo.get(Calendar.MINUTE),
            true
        ).show()
    }

    var tTo =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.SetTimeToCommand(hourOfDay, minute)
        }

    fun InitViewModel(application: Application) {

        if (ScreensDataStorage.curMappingsScreenData!=null) {
            val data = ScreensDataStorage.curMappingsScreenData as MappingsFragment
            viewModel = data.viewModel
        }

        else {
            viewModel = MappingsViewModel(application)
            ScreensDataStorage.curMappingsScreenData = this

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MappingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MappingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
