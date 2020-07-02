package com.example.coplanning.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coplanning.communication.SocketClient
import com.example.coplanning.globals.BadgesOperations
import com.example.coplanning.globals.MappingElementsManager
import com.example.coplanning.globals.SharedPreferencesOperations
import com.example.coplanning.helpers.DateAndTimeConverter
import com.example.coplanning.models.mapping.SavedMapping
import com.github.nkzawa.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MappingsViewModel(val application: Application): ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sharedPrefs = SharedPreferencesOperations(application)

    private val socketClient: SocketClient = SocketClient()

    private val badgesOperations: BadgesOperations = BadgesOperations()

    private val mappingElementsManager: MappingElementsManager = MappingElementsManager()

    private val _dateFromString = MutableLiveData<String>()
    val dateFromString: LiveData<String>
        get() = _dateFromString

    private val _timeFromString = MutableLiveData<String>()
    val timeFromString: LiveData<String>
        get() = _timeFromString

    private val _dateToString = MutableLiveData<String>()
    val dateToString: LiveData<String>
        get() = _dateToString

    private val _timeToString = MutableLiveData<String>()
    val timeToString: LiveData<String>
        get() = _timeToString

    private val _mappingPanelVisible = MutableLiveData<Boolean>()
    val mappingPanelVisible: LiveData<Boolean>
        get() = _mappingPanelVisible

    private val _mappingParticipants = MutableLiveData<List<String>>()
    val mappingParticipants: LiveData<List<String>>
        get() = _mappingParticipants

    private val _savedMappings = MutableLiveData<List<SavedMapping>>()
    val savedMappings: LiveData<List<SavedMapping>>
        get() = _savedMappings

    var dateAndTimeFrom: Calendar = Calendar.getInstance()
    var dateAndTimeTo: Calendar = Calendar.getInstance()

    private val onSearchesAnswer = Emitter.Listener { args ->
        uiScope.launch {
            val data = args[0] as JSONArray
            val savedMappingsResults: ArrayList<SavedMapping> = ArrayList()
            for (i in 0 until data.length()) {
                var jsonobject: JSONObject
                try {
                    jsonobject = data.getJSONObject(i)
                    val from = jsonobject.getString("dateTimeFrom")
                    val dtFrom: Calendar =
                        DateAndTimeConverter.getCalendarDateTimeFromISOString(from)
                    val to = jsonobject.getString("dateTimeTo")
                    val dtTo: Calendar =
                        DateAndTimeConverter.getCalendarDateTimeFromISOString(to)
                    val array = jsonobject.getJSONArray("participantsList")
                    val size = array.length()
                    val patricipants =
                        ArrayList<String>()
                    for (j in 0 until size) {
                        val participant = array[j] as String
                        patricipants.add(participant)
                    }
                    savedMappingsResults.add(
                        SavedMapping(
                            dtFrom,
                            dtTo,
                            patricipants
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            _savedMappings.value = savedMappingsResults
        }
    }


    init {
        initDates()
        updateMappingPanelState()
        socketClient.setSavedMappingsListener(onSearchesAnswer)
        getSavedMappings()
    }

    fun getJSONMappingData(): JSONObject {
        val isoMapDateFrom = DateAndTimeConverter.convertStringDateToISO(
            _dateFromString.value.toString()
        )
        val isoMapTimeFrom = DateAndTimeConverter.convertStringTimeToISO(
            _timeFromString.value.toString()
        )
        val isoMapDateTo = DateAndTimeConverter.convertStringDateToISO(
            _dateToString.value.toString()
        )
        val isoMapTimeTo = DateAndTimeConverter.convertStringTimeToISO(
            _timeToString.value.toString()
        )
        val mappingElements = mappingElementsManager.getMappingElements()
        val jsonMappingElements = JSONArray()
        for (mElement in mappingElements) {
            jsonMappingElements.put(mElement)
        }
        val jsonMappingData = JSONObject()
        try {
            jsonMappingData.put("dateFrom", isoMapDateFrom)
            jsonMappingData.put("timeFrom", isoMapTimeFrom)
            jsonMappingData.put("dateTo", isoMapDateTo)
            jsonMappingData.put("timeTo", isoMapTimeTo)
            jsonMappingData.put("users", jsonMappingElements)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonMappingData
    }

    private fun initDates() {
        setDateFromCommand(
            dateAndTimeFrom[Calendar.YEAR],
            dateAndTimeFrom[Calendar.MONTH],
            dateAndTimeFrom[Calendar.DAY_OF_MONTH]
        )
        setTimeFromCommand(
            dateAndTimeFrom[Calendar.HOUR_OF_DAY],
            dateAndTimeFrom[Calendar.MINUTE]
        )
        setDateToCommand(
            dateAndTimeTo[Calendar.YEAR],
            dateAndTimeTo[Calendar.MONTH],
            dateAndTimeTo[Calendar.DAY_OF_MONTH]
        )
        setTimeToCommand(
            dateAndTimeTo[Calendar.HOUR_OF_DAY],
            dateAndTimeTo[Calendar.MINUTE]
        )
    }

    fun setMappingParameters(mapping: SavedMapping) {
        dateAndTimeFrom = mapping.dateTimeFrom.clone() as Calendar
        dateAndTimeTo = mapping.dateTimeTo.clone() as Calendar
        initDates()

        mappingElementsManager.clearMappingElements()
        mapping.participants.forEach {
            mappingElementsManager.addMappingElement(it)
        }
        updateBadges()
        updateMappingPanelState()
    }

    private fun updateBadges() {
        val curMappingElements = mappingElementsManager.getMappingElements()
        if (curMappingElements.count()==0) {
            badgesOperations.clearMappingsAmount()
        } else {
            badgesOperations.setMappingsAmount(curMappingElements.count())
        }
    }

    fun removeUserFromMapping(username: String) {
        val curMappingElements = mappingElementsManager.getMappingElements()
        if (curMappingElements.contains(username)) {
            mappingElementsManager.removeMappingElement(username)
        }
        updateBadges()
        updateMappingPanelState()
    }

    fun updateMappingPanelState() {
        val participants = mappingElementsManager.getMappingElements()
        _mappingParticipants.value = participants
        _mappingPanelVisible.value = participants.count()>1
    }

    fun setDateFromCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateFromString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)
    }

    fun setTimeFromCommand(hours: Int, minutes: Int) {
        dateAndTimeFrom[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeFrom[Calendar.MINUTE] = minutes
        _timeFromString.value = DateAndTimeConverter.convertToStringTime(hours, minutes)
    }

    fun setDateToCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateToString.value = DateAndTimeConverter.convertToStringDate(year, dispMonth, date)
    }

    fun setTimeToCommand(hours: Int, minutes: Int) {
        dateAndTimeTo[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeTo[Calendar.MINUTE] = minutes
        _timeToString.value = DateAndTimeConverter.convertToStringTime(hours, minutes)
    }

    private fun getCurUserName(): String? { return sharedPrefs.login}

    private fun getSavedMappings() {
        val username = getCurUserName()
        socketClient.getSavedMappings(username)
    }

}