package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.mapping.GroupedMappingResultsCollection
import com.example.coplanning.models.mapping.MappingResultItem
import com.example.coplanning.models.mapping.MappingResultsByGroups
import com.github.nkzawa.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MappingResultsViewModel(
    val application: Application,
    private val jsonMappingData: String
) : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    private val _allParticipantsString = MutableLiveData<String>()
    val allParticipantsString: LiveData<String>
        get() = _allParticipantsString

    private val _allParticipantsMinusOneString = MutableLiveData<String>()
    val allParticipantsMinusOneString: LiveData<String>
        get() = _allParticipantsMinusOneString

    private val _otherString = MutableLiveData<String>()
    val otherString: LiveData<String>
        get() = _otherString

    val _isAllParticipants = MutableLiveData<Boolean>()
    val isAllParticipants: LiveData<Boolean>
        get() = _isAllParticipants

    val _isAllParticipantsMinusOne = MutableLiveData<Boolean>()
    val isAllParticipantsMinusOne: LiveData<Boolean>
        get() = _isAllParticipantsMinusOne

    val _isOther = MutableLiveData<Boolean>()
    val isOther: LiveData<Boolean>
        get() = _isOther

    private val _allMappingResultElements = MutableLiveData<List<MappingResultItem>>()
    private val allMappingResultElements: LiveData<List<MappingResultItem>>
        get() = _allMappingResultElements

    private val _allGroupedMappingResultElements = MutableLiveData<GroupedMappingResultsCollection>()
    val allGroupedMappingResultElements: LiveData<GroupedMappingResultsCollection>
        get() = _allGroupedMappingResultElements

    init {
        getMappingResult()
    }

    fun updateResultsList() {
        val filteredResults = ArrayList<MappingResultItem>()
        val isAll = _isAllParticipants.value
        val isAllWithoutOne = _isAllParticipantsMinusOne.value
        val isOther = _isOther.value
        val maxParticipants = _allMappingResultElements.value?.let { getMaxAmount(it) }
        for (element in allMappingResultElements.value!!) {
            if (element.amount === maxParticipants && isAll!!) filteredResults.add(element)
            if (maxParticipants != null) {
                if (element.amount === maxParticipants - 1 && isAllWithoutOne!!) filteredResults.add(element)
            }
            if (maxParticipants != null) {
                if (element.amount < maxParticipants - 1 && isOther!!) filteredResults.add(element)
            }
        }
        _allGroupedMappingResultElements.value = groupMappingVisibleResultsElements(filteredResults)
    }

    private fun getMaxAmount(mappingsResults: List<MappingResultItem>): Int {
        var maxResult = 0
        for (element in mappingsResults) {
            val curMax: Int = element.amount
            if (curMax > maxResult) maxResult = curMax
        }
        return maxResult
    }

    private fun getGroupsList(
        mappingVisibleResultElements: ArrayList<MappingResultItem>
    ): ArrayList<String>? {
        val resultsList = ArrayList<String>()
        Collections.sort<MappingResultItem>(mappingVisibleResultElements, MappingResultItem.dateTimeFromComparator)
        for (mappingResultElement in mappingVisibleResultElements) {
            val dateTimeFrom = mappingResultElement.dateTimeFrom
            val dateFrom = dateTimeFrom.let {
                DateAndTimeConverter.getStringDateFromCalendar(it)
            }
            val dateTimeTo = mappingResultElement.dateTimeTo
            val dateTo = dateTimeTo.let {
                DateAndTimeConverter.getStringDateFromCalendar(it)
            }
            if (!resultsList.contains(dateFrom)) dateFrom?.let { resultsList.add(it) }
        }
        return resultsList
    }

    private fun groupMappingVisibleResultsElements(
        mappingResultElements: ArrayList<MappingResultItem>
    ): GroupedMappingResultsCollection {
        val intervalsGroupsList = getGroupsList(mappingResultElements)
        val groupedByDateMappingResultsList = ArrayList<MappingResultsByGroups>()
        for (group in intervalsGroupsList!!) {
            val curDateElements
                    = getCurDateMappingResultsList(mappingResultElements, group)
            groupedByDateMappingResultsList.add(
                MappingResultsByGroups(
                    group,
                    curDateElements
                )
            )
        }

        return GroupedMappingResultsCollection(
            groupedByDateMappingResultsList
        )
    }

    private fun getCurDateMappingResultsList(
        mappingResultElements: ArrayList<MappingResultItem>,
        date: String
    ): ArrayList<MappingResultItem> {
        val resultsList: ArrayList<MappingResultItem> = ArrayList()
        for (mappingVisibleResultElement in mappingResultElements) {
            val dateTimeFrom = mappingVisibleResultElement.dateTimeFrom
            val dateFrom = dateTimeFrom.let {
                DateAndTimeConverter.getStringDateFromCalendar(it)
            }
            val dateTimeTo = mappingVisibleResultElement.dateTimeTo
            val dateTo = dateTimeTo.let {
                DateAndTimeConverter.getStringDateFromCalendar(it)
            }
            if (dateFrom == date && dateTo == date) resultsList.add(mappingVisibleResultElement)
        }
        return resultsList
    }

    private fun getCurUserName(): String? { return sharedPrefs.login}

    private fun getMappingResult() {
        val jsonMappingData = JSONObject(jsonMappingData)

        val username = getCurUserName()
        socketClient.setMappingAnswerListener(onMappingAnswer)
        socketClient.getMappingsResult(jsonMappingData, username)
    }

    private val onMappingAnswer = Emitter.Listener {args->
        uiScope.launch {

            val data = args[0] as JSONArray

            val allMappingResultElements = ArrayList<MappingResultItem>()
            for (i in 0 until data.length()) {
                var jsonObject: JSONObject
                try {
                    jsonObject = data.getJSONObject(i)
                    val from = jsonObject.getString("from")
                    val dtFrom: Calendar =
                        DateAndTimeConverter.getCalendarDateTimeFromISOString(from)
                    val to = jsonObject.getString("to")
                    val dtTo: Calendar =
                        DateAndTimeConverter.getCalendarDateTimeFromISOString(to)
                    val amount = jsonObject.getInt("amount")
                    val array = jsonObject.getJSONArray("freeUsers")
                    val size = array.length()
                    val users = ArrayList<String>()
                    for (j in 0 until size) {
                        val user = array[j] as String
                        users.add(user)
                    }
                    allMappingResultElements.add(
                        MappingResultItem(
                            users,
                            dtFrom,
                            dtTo,
                            amount
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            val maxParticipants: Int = allMappingResultElements[0].amount
            val maxParticipantsStr = maxParticipants.toString()
            val maxParticipantsWithoutOneStr =
                (maxParticipants - 1).toString()

            _allParticipantsString.value = "All($maxParticipantsStr) persons"
            _allParticipantsMinusOneString.value = "$maxParticipantsWithoutOneStr person(s)"
            _otherString.value = "Other"

            _isAllParticipants.value = true
            _isAllParticipantsMinusOne.value = true
            _isOther.value = true

            _allMappingResultElements.value = allMappingResultElements

            _allGroupedMappingResultElements.value = groupMappingVisibleResultsElements(
                allMappingResultElements
            )
        }
    }

}