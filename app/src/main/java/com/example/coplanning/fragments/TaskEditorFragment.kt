package com.example.coplanning.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.coplanning.R
import com.example.coplanning.databinding.FragmentTaskEditorBinding
import com.example.coplanning.globals.ScreensDataStorage
import com.example.coplanning.models.TaskComparable
import com.example.coplanning.viewmodels.TaskEditorViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TASK = "task"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskEditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskEditorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var task: TaskComparable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getSerializable(TASK) as TaskComparable?
        }

        val application = requireNotNull(this.activity).application

        if (ScreensDataStorage.curScheduleScreenData!=null) {
            val data = ScreensDataStorage.curScheduleScreenData as TaskEditorFragment
            viewModel = data.viewModel
        }

        else {
            viewModel = task?.let {
                    TaskEditorViewModel(it, application) { fragmentManager?.popBackStack() }
            } ?:
                    TaskEditorViewModel(application) { fragmentManager?.popBackStack() }
            ScreensDataStorage.curScheduleScreenData = this

        }
    }

    lateinit var viewModel: TaskEditorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTaskEditorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_editor, container, false)
        binding.viewModel = viewModel
        binding.taskDateFrom.setOnClickListener {
            showSetDateFromDialog()
        }
        binding.taskTimeFrom.setOnClickListener {
            showSetTimeFromDialog()
        }
        binding.taskDateTo.setOnClickListener {
            showSetDateToDialog()
        }
        binding.taskTimeTo.setOnClickListener {
            showSetTimeToDialog()
        }

        binding.lifecycleOwner = this
        // Inflate the layout for this fragment
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
        OnTimeSetListener { view, hourOfDay, minute ->
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
        OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.SetTimeToCommand(hourOfDay, minute)
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskEditorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(task: TaskComparable) =
            TaskEditorFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TASK, task)
                }
            }
    }
}
