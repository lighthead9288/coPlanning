package com.example.coplanning.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.coplanning.R
import com.example.coplanning.adapters.ExpandableMappingIntervalsAdapter
import com.example.coplanning.databinding.FragmentMappingResultsBinding
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.viewmodels.MappingResultsViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val MAPPING_DATA = "mappingData"

/**
 * A simple [Fragment] subclass.
 * Use the [MappingResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MappingResultsFragment : Fragment(), InitViewModel {
    // TODO: Rename and change types of parameters
    private var mappingData: String? = null
    private lateinit var binding: FragmentMappingResultsBinding
    private lateinit var viewModel: MappingResultsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = requireNotNull(this.activity).application
        arguments?.let {
            mappingData = it.getString(MAPPING_DATA)
        }
        initViewModel(application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        initObservables()
        return binding.root
    }

    private fun initObservables() {
        val application = requireNotNull(this.activity).application
        viewModel.allGroupedMappingResultElements.observe(
            viewLifecycleOwner,
            Observer {
                val groups = it.groupedMappingResults.map { it.groupName }
                val adapter = ExpandableMappingIntervalsAdapter(
                    application,
                    groups,
                    it.groupedMappingResults,
                    R.layout.mapping_result_group_layout,
                    R.layout.mapping_interval_layout
                )
                binding.mappingIntervals.setAdapter(adapter)
            }
        )
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_mapping_results,
            container,
            false
        )
        binding.viewModel = viewModel

        binding.allParticapants.setOnClickListener {
            viewModel.updateResultsList()
        }
        binding.allParticapantsWithoutOne.setOnClickListener {
            viewModel.updateResultsList()
        }
        binding.other.setOnClickListener {
            viewModel.updateResultsList()
        }
        binding.lifecycleOwner = this

    }

    override fun initViewModel(application: Application) {
        if (ScreensDataStorage.curMappingsScreenData!=null) {
            val data
                    = ScreensDataStorage.curMappingsScreenData as MappingResultsFragment
            viewModel = data.viewModel
        } else {
            viewModel = MappingResultsViewModel(application, mappingData.toString())
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
         * @return A new instance of fragment MappingResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MappingResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(MAPPING_DATA, mappingData)
                }
            }
    }
}
