package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.GroupedMappingResultsCollection
import com.example.coplanning.models.MappingResultItem
import com.example.coplanning.models.MappingResultsByGroups
import com.github.nkzawa.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MappingResultsViewModel(val application: Application, val jsonMappingData: String): ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    val _allParticipantsString = MutableLiveData<String>()
    val allParticipantsString: LiveData<String>
        get() = _allParticipantsString

    val _allParticipantsMinusOneString = MutableLiveData<String>()
    val allParticipantsMinusOneString: LiveData<String>
        get() = _allParticipantsMinusOneString

    val _otherString = MutableLiveData<String>()
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

    val _allMappingResultElements = MutableLiveData<List<MappingResultItem>>()
    val allMappingResultElements: LiveData<List<MappingResultItem>>
        get() = _allMappingResultElements

    val _allGroupedMappingResultElements = MutableLiveData<GroupedMappingResultsCollection>()
    val allGroupedMappingResultElements: LiveData<GroupedMappingResultsCollection>
        get() = _allGroupedMappingResultElements


    fun UpdateResultsList() {
        val filteredResults = ArrayList<MappingResultItem>()
        val isAll = _isAllParticipants.value
        val isAllWithoutOne = _isAllParticipantsMinusOne.value
        val isOther = _isOther.value
        val maxParticipants = _allMappingResultElements.value?.let { GetMaxAmount(it) }
        for (element in allMappingResultElements.value!!) {
            if (element.GetAmount() === maxParticipants && isAll!!) filteredResults.add(element)
            if (maxParticipants != null) {
                if (element.GetAmount() === maxParticipants - 1 && isAllWithoutOne!!) filteredResults.add(element)
            }
            if (maxParticipants != null) {
                if (element.GetAmount() < maxParticipants - 1 && isOther!!) filteredResults.add(element)
            }
        }
        //_allMappingResultElements.value = filteredResults
        _allGroupedMappingResultElements.value = GroupMappingVisibleResultsElements(filteredResults)
    }

    private fun GetMaxAmount(mappingsResults: List<MappingResultItem>): Int {
        var maxResult = 0
        for (element in mappingsResults) {
            val curMax: Int = element.GetAmount()
            if (curMax > maxResult) maxResult = curMax
        }
        return maxResult
    }

    private fun GetGroupsList(mappingVisibleResultElements: ArrayList<MappingResultItem>): ArrayList<String>? {
        val resultsList = ArrayList<String>()
        Collections.sort<MappingResultItem>(mappingVisibleResultElements, MappingResultItem.DateTimeFromComparator)
        for (mappingResultElement in mappingVisibleResultElements) {
            val dateTimeFrom = mappingResultElement.GetDateTimeFrom()
            val dateFrom = dateTimeFrom?.let { DateAndTimeConverter.GetStringDateFromCalendar(it) }
            val dateTimeTo = mappingResultElement.GetDateTimeTo()
            val dateTo = dateTimeTo?.let { DateAndTimeConverter.GetStringDateFromCalendar(it) }
            if (!resultsList.contains(dateFrom)) dateFrom?.let { resultsList.add(it) }
        }
        return resultsList
    }

    private fun GroupMappingVisibleResultsElements(mappingResultElements: ArrayList<MappingResultItem>): GroupedMappingResultsCollection {
        val intervalsGroupsList = GetGroupsList(mappingResultElements)
        val groupedByDateMappingResultsList = ArrayList<MappingResultsByGroups>()
        for (group in intervalsGroupsList!!) {
            val curDateElements = GetCurDateMappingResultsList(mappingResultElements, group)
            groupedByDateMappingResultsList.add(MappingResultsByGroups(group, curDateElements))
        }

        val result = GroupedMappingResultsCollection(groupedByDateMappingResultsList)
        return result
    }

    private fun GetCurDateMappingResultsList(mappingResultElements: ArrayList<MappingResultItem>, date: String): ArrayList<MappingResultItem> {
        val resultsList: ArrayList<MappingResultItem> = ArrayList<MappingResultItem>()
        for (mappingVisibleResultElement in mappingResultElements) {
            val dateTimeFrom = mappingVisibleResultElement.GetDateTimeFrom()
            val dateFrom = dateTimeFrom?.let { DateAndTimeConverter.GetStringDateFromCalendar(it) }
            val dateTimeTo = mappingVisibleResultElement.GetDateTimeTo()
            val dateTo = dateTimeTo?.let { DateAndTimeConverter.GetStringDateFromCalendar(it) }
            if (dateFrom == date && dateTo == date) resultsList.add(mappingVisibleResultElement)
        }
        return resultsList
    }

    fun GetCurUserName(): String? { return sharedPrefs.login}

    fun GetMappingResult() {
        val jsonMappingData = JSONObject(jsonMappingData)

        val username = GetCurUserName()
        socketClient.SetMappingAnswerListener(onMappingAnswer)
        socketClient.GetMappingsResult(jsonMappingData, username)
    }

    private val onMappingAnswer = Emitter.Listener {args->
        uiScope.launch {

            val data = args[0] as JSONArray

            val allMappingResultElements = ArrayList<MappingResultItem>()
            for (i in 0 until data.length()) {
                var jsonobject: JSONObject
                try {
                    val mappingResultElement = MappingResultItem()
                    jsonobject = data.getJSONObject(i)
                    val from = jsonobject.getString("from")
                    val dtFrom: Calendar =
                        DateAndTimeConverter.GetCalendarDateTimeFromISOString(from)
                    val to = jsonobject.getString("to")
                    val dtTo: Calendar =
                        DateAndTimeConverter.GetCalendarDateTimeFromISOString(to)
                    mappingResultElement.SetDateTimeFrom(dtFrom)
                    mappingResultElement.SetDateTimeTo(dtTo)
                    val amount = jsonobject.getInt("amount")
                    mappingResultElement.SetAmount(amount)
                    val array = jsonobject.getJSONArray("freeUsers")
                    val size = array.length()
                    val users = ArrayList<String>()
                    for (j in 0 until size) {
                        val user = array[j] as String
                        users.add(user)
                    }
                    mappingResultElement.SetUserList(users)
                    allMappingResultElements.add(mappingResultElement)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            val maxParticipants: Int = allMappingResultElements.get(0).GetAmount()
            val maxParticipantsStr = Integer.toString(maxParticipants)
            val maxParticipantsWithoutOneStr =
                Integer.toString(maxParticipants - 1)

            _allParticipantsString.value = "All($maxParticipantsStr) persons"
            _allParticipantsMinusOneString.value = "$maxParticipantsWithoutOneStr person(s)"
            _otherString.value = "Other"

            _isAllParticipants.value = true
            _isAllParticipantsMinusOne.value = true
            _isOther.value = true

            _allMappingResultElements.value = allMappingResultElements

            _allGroupedMappingResultElements.value = GroupMappingVisibleResultsElements(allMappingResultElements)

        }

    }

    init {
        GetMappingResult()

    }
}