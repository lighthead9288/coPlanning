package com.example.coplanning.fragments

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.coplanning.R
import com.example.coplanning.adapters.ExpandableTaskListAdapter
import com.example.coplanning.databinding.FragmentScheduleBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.viewmodels.ScheduleViewModel
import kotlinx.android.synthetic.main.delete_task_dialog.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USERNAME = "username"


/**
 * A simple [Fragment] subclass.
 * Use the [UserScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserScheduleFragment : Fragment(), ISchedule, InitViewModel {
    // TODO: Rename and change types of parameters
    private var username: String? = null

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var fragmentOperations: FragmentOperations
    private lateinit var sharedPrefs: SharedPreferencesOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments!=null) {
            username = arguments?.getString(USERNAME)
        }
        val application = requireNotNull(this.activity).application
        initSharedPrefs(application)
        initViewModel(application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initFragmentOperations()
        initBinding(inflater, container)
        initObservables()

        return binding.root
    }

    private fun initSharedPrefs(application: Application) {
        sharedPrefs = SharedPreferencesOperations(application)
    }

    private fun initFragmentOperations() {
        fragmentOperations = FragmentOperations(fragmentManager)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        val binding: FragmentScheduleBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_schedule,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.calendarView.setICalendarCellClick { calendar: Calendar ->
            viewModel.setIntervalCommand(calendar, calendar)
        }
        binding.dateFrom.setOnClickListener { showSetDateFromDialog() }
        binding.dateTo.setOnClickListener { showSetDateToDialog() }
        binding.taskFilterSv.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.getTasks(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.getTasks(newText)
                return false
            }
        })

        if (isMe())
            binding.addTaskButton.setOnClickListener {
                val taskEditorFragment = TaskEditorFragment()
                ScreensDataStorage.curScheduleScreenData = null
                fragmentOperations.loadFragment(taskEditorFragment)
            }
        binding.lifecycleOwner = this
    }

    private fun initObservables() {
        val application = requireNotNull(this.activity).application
        viewModel.isCalendar.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                binding.calendarButton.setImageResource(R.drawable.calendar_selected)
            else
                binding.calendarButton.setImageResource(R.drawable.calendar)

        })
        viewModel.isParams.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                binding.filterButton.setImageResource(R.drawable.filter_selected)
            else
                binding.filterButton.setImageResource(R.drawable.filter)
        })
        viewModel.groupedTaskList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            val groupsList = it.tasksDays.map { it.day }
            val groupedTasks = it.tasksDays

            val adapter = ExpandableTaskListAdapter(
                application,
                groupsList,
                groupedTasks,
                R.layout.task_group_layout,
                getTaskLayout(),
                viewModel.getCurUser(),
                { task -> viewModel.setTaskCompleted(task)
                },
                { task ->
                    val dialogView
                            = LayoutInflater.from(context).inflate(R.layout.delete_task_dialog,null)
                    val builder
                            = AlertDialog.Builder(context).setView(dialogView).setTitle("")
                    val alertDialog = builder.show()
                    dialogView.confirmDelete.setOnClickListener {
                        alertDialog.dismiss()
                        viewModel.deleteTask(task)
                    }
                    dialogView.cancelDelete.setOnClickListener {
                        alertDialog.dismiss()
                    }
                },
                { task, direction, callback ->
                    viewModel.subscribeOnUserTask(task, direction, callback)
                }
            )
            binding.expandableTaskListLv.setAdapter(adapter)
            if (isMe())
                binding.expandableTaskListLv.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                    val task = groupedTasks[groupPosition].tasks[childPosition]
                    val taskEditorFragment = TaskEditorFragment()
                    val arguments = Bundle()
                    arguments.putSerializable("task", task)
                    taskEditorFragment.arguments = arguments
                    ScreensDataStorage.curScheduleScreenData = null
                    fragmentOperations.loadFragment(taskEditorFragment)

                    false
                }
        })
    }

    private fun isMe():Boolean {
        return username==sharedPrefs.login
    }

    override fun getTaskLayout(): Int {
        return if (isMe()) R.layout.task_layout else R.layout.user_task_layout
    }

    override fun initViewModel(application: Application) {
        if (ScreensDataStorage.curSearchScreenData!=null) {
            val data = ScreensDataStorage.curSearchScreenData as UserScheduleFragment
            viewModel = data.viewModel
        } else {
            viewModel = ScheduleViewModel(application, username.toString())
            ScreensDataStorage.curSearchScreenData = this
        }
    }

    private fun showSetDateFromDialog() {
        DatePickerDialog(
            requireContext(), dFrom,
            viewModel.dateAndTimeFrom.get(Calendar.YEAR),
            viewModel.dateAndTimeFrom.get(Calendar.MONTH),
            viewModel.dateAndTimeFrom.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    var dFrom =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.setFromDateCommand(GregorianCalendar(year, monthOfYear, dayOfMonth))
            viewModel.getTasks()
        }

    private fun showSetDateToDialog() {
        DatePickerDialog(
            requireContext(), dTo,
            viewModel.dateAndTimeTo.get(Calendar.YEAR),
            viewModel.dateAndTimeTo.get(Calendar.MONTH),
            viewModel.dateAndTimeTo.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    var dTo =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.setToDateCommand(GregorianCalendar(year, monthOfYear, dayOfMonth))
            viewModel.getTasks()
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(USERNAME, username)
                }
            }
    }


}
