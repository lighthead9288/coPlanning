package com.example.coplanning.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.coplanning.R
import com.example.coplanning.databinding.FragmentLoginBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.MenuOperations
import com.example.coplanning.viewmodels.LoginViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var fragmentOperations: FragmentOperations
    private var menuOperations = MenuOperations()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initFragmentOperations()
        initViewModel()
        initBinding(inflater, container)
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initFragmentOperations() {
        fragmentOperations = FragmentOperations(fragmentManager)

    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.registerButton.setOnClickListener {
            val registerFragment = RegisterFragment()
            fragmentOperations.loadFragment(registerFragment)
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initViewModel() {
        val application = requireNotNull(this.activity).application
        viewModel = LoginViewModel(application) {
            menuOperations.showBottomNavigationView()
            fragmentOperations.loadFragment(ProfileFragment())
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
