package com.example.coplanning.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.example.coplanning.R
import com.example.coplanning.adapters.AddUserToMappingClickListener
import com.example.coplanning.adapters.OpenUserClickListener
import com.example.coplanning.adapters.SubscribeOnUserClickListener
import com.example.coplanning.adapters.UsersAdapter
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.databinding.FragmentSearchBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.viewmodels.SearchViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: SearchViewModel
    private lateinit var fragmentOperations: FragmentOperations

    private var socketClient: SocketClient = SocketClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val application = requireNotNull(this.activity).application


        if (ScreensDataStorage.curSearchScreenData!=null) {
            val data = ScreensDataStorage.curSearchScreenData as SearchFragment
            viewModel = data.viewModel
        }

        else {
            viewModel = SearchViewModel(application)
            ScreensDataStorage.curSearchScreenData = this

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentOperations = FragmentOperations(fragmentManager)
        val binding = DataBindingUtil.inflate<FragmentSearchBinding>(inflater, R.layout.fragment_search, container, false)
        binding.viewModel = viewModel

        binding.usersSv.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.SearchCommand(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.SearchCommand(newText)
                return false
            }
        })

        val adapter = UsersAdapter(OpenUserClickListener {
            val scheduleFragment = UserScheduleFragment()
            val arguments = Bundle()
            arguments.putSerializable("username", it.username)
            scheduleFragment.arguments = arguments
            ScreensDataStorage.curSearchScreenData = null
            fragmentOperations.LoadFragment(scheduleFragment)
        },
            SubscribeOnUserClickListener {user, direction, listener ->
            viewModel.Subscribe(user, direction, listener)
        },
            AddUserToMappingClickListener { user, listener ->
            viewModel.AddUserToMapping(user.username, listener)
        },
            viewModel.GetCurUserName().toString())

        binding.usersRv.adapter = adapter

        viewModel.usersList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.lifecycleOwner = this

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
