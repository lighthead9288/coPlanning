package com.example.coplanning.fragments

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.coplanning.R
import com.example.coplanning.adapters.ExpandableTaskListAdapter
import com.example.coplanning.databinding.FragmentScheduleBinding
import com.example.coplanning.globals.FragmentOperations
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.models.ITaskListOperations
import com.example.coplanning.viewmodels.ScheduleViewModel
import com.example.coplanning.viewmodels.TaskEditorViewModel
import kotlinx.android.synthetic.main.delete_task_dialog.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class ScheduleFragment : Fragment(), ISchedule {
    // TODO: Rename and change types of parameters
    private var username: String? = null

    private lateinit var sharedPrefs: SharedPreferencesOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this.activity).application
        sharedPrefs = SharedPreferencesOperations(application)

        if (arguments!=null) {
            username = arguments?.getString(USERNAME)
        }

        InitViewModel(application)
    }


   // val viewModel = ViewModelProvider(this, SavedStateVMFactory(this)).get(ScheduleViewModel::class.java)
  //  val viewModel:ScheduleViewModel by viewModels { SavedStateVMFactory(this) }
    lateinit var viewModel: ScheduleViewModel
    lateinit var fragmentOperations: FragmentOperations



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)
        val application = requireNotNull(this.activity).application
        fragmentOperations = FragmentOperations(fragmentManager)

        binding.viewModel = viewModel

        binding.calendarView.SetICalendarCellClick { calendar: Calendar ->
            viewModel.SetIntervalCommand(calendar, calendar)
        }

        binding.dateFrom.setOnClickListener { showSetDateFromDialog() }
        binding.dateTo.setOnClickListener { showSetDateToDialog() }


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


        viewModel.groupedTaskList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val groupsList = it.tasksDays.map { it.day }
            val groupedTasks = it.tasksDays

            val adapter = ExpandableTaskListAdapter(application, groupsList, groupedTasks, R.layout.task_group_layout, GetTaskLayout(), sharedPrefs.login!!.toString(),
                {task->viewModel.SetTaskCompleted(task)},
                {task->
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.delete_task_dialog,null)

                    val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("")
                    val alertDialog = builder.show()

                    dialogView.confirmDelete.setOnClickListener {
                        alertDialog.dismiss()
                        viewModel.DeleteTask(task)
                    }

                    dialogView.cancelDelete.setOnClickListener {
                        alertDialog.dismiss()
                    }

                },
                { task, direction, callback ->
                    viewModel.SubscribeOnUserTask(task, direction, callback)
                }
            )
            binding.expandableTaskListLv.setAdapter(adapter)
            binding.expandableTaskListLv.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                val task = groupedTasks[groupPosition].tasks[childPosition]
                val taskEditorFragment = TaskEditorFragment()
                val arguments = Bundle()
                arguments.putSerializable("task", task)
                taskEditorFragment.arguments = arguments
                ScreensDataStorage.curScheduleScreenData = null
                fragmentOperations.LoadFragment(taskEditorFragment)

                false
            }
        })



        binding.taskFilterSv.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.GetTasks(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.GetTasks(newText)
                return false
            }
        })

        binding.addTaskButton.setOnClickListener {
            val taskEditorFragment = TaskEditorFragment()
            ScreensDataStorage.curScheduleScreenData = null
            fragmentOperations.LoadFragment(taskEditorFragment)
        }

        binding.lifecycleOwner = this



        return binding.root
    }

    //endregion
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
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.SetFromDateCommand(GregorianCalendar(year, monthOfYear, dayOfMonth))
            viewModel.GetTasks()
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
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.SetToDateCommand(GregorianCalendar(year, monthOfYear, dayOfMonth))
            viewModel.GetTasks()
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(USERNAME, username)
                }
            }
    }

    override fun GetTaskLayout(): Int {
        return R.layout.task_layout
    }

    override fun InitViewModel(application: Application) {
        if (ScreensDataStorage.curScheduleScreenData!=null) {
            val data = ScreensDataStorage.curScheduleScreenData as ScheduleFragment
            viewModel = data.viewModel
        }

        else {
            viewModel = ScheduleViewModel(application, sharedPrefs.login.toString())
            ScreensDataStorage.curScheduleScreenData = this
        }
    }
}

interface ISchedule {
    fun GetTaskLayout():Int
    fun InitViewModel(application: Application)
}