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
import com.example.coplanning.models.SavedMapping
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

    val _dateFromString = MutableLiveData<String>()
    val dateFromString: LiveData<String>
        get() = _dateFromString

    val _timeFromString = MutableLiveData<String>()
    val timeFromString: LiveData<String>
        get() = _timeFromString

    val _dateToString = MutableLiveData<String>()
    val dateToString: LiveData<String>
        get() = _dateToString

    val _timeToString = MutableLiveData<String>()
    val timeToString: LiveData<String>
        get() = _timeToString

    val _mappingPanelVisible = MutableLiveData<Boolean>()
    val mappingPanelVisible: LiveData<Boolean>
        get() = _mappingPanelVisible

    val _mappingParticipants = MutableLiveData<List<String>>()
    val mappingParticipants: LiveData<List<String>>
        get() = _mappingParticipants

    val _savedMappings = MutableLiveData<List<SavedMapping>>()
    val savedMappings: LiveData<List<SavedMapping>>
        get() = _savedMappings


    var dateAndTimeFrom = Calendar.getInstance()
    var dateAndTimeTo = Calendar.getInstance()

    private val onSearchesAnswer = Emitter.Listener { args ->
        uiScope.launch {
            val data = args[0] as JSONArray

            val savedMappingsResults: ArrayList<SavedMapping> =
                ArrayList<SavedMapping>()

            for (i in 0 until data.length()) {
                var jsonobject: JSONObject
                try {
                    jsonobject = data.getJSONObject(i)
                    val from = jsonobject.getString("dateTimeFrom")
                    val dtFrom: Calendar =
                        DateAndTimeConverter.GetCalendarDateTimeFromISOString(from)
                    val to = jsonobject.getString("dateTimeTo")
                    val dtTo: Calendar =
                        DateAndTimeConverter.GetCalendarDateTimeFromISOString(to)
                    val array = jsonobject.getJSONArray("participantsList")
                    val size = array.length()
                    val patricipants =
                        ArrayList<String>()
                    for (j in 0 until size) {
                        val participant = array[j] as String
                        patricipants.add(participant)
                    }
                    savedMappingsResults.add(SavedMapping(dtFrom, dtTo, patricipants))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            _savedMappings.value = savedMappingsResults
        }

    }


    init {

        InitDates()
        UpdateMappingPanelState()

        socketClient.SetSavedMappingsListener(onSearchesAnswer)
        GetSavedMappings()

    }

    fun GetJSONMappingData(): JSONObject {
        val isoMapDateFrom = DateAndTimeConverter.ConvertStringDateToISO(_dateFromString.value.toString())
        val isoMapTimeFrom = DateAndTimeConverter.ConvertStringTimeToISO(_timeFromString.value.toString())
        val isoMapDateTo = DateAndTimeConverter.ConvertStringDateToISO(_dateToString.value.toString())
        val isoMapTimeTo = DateAndTimeConverter.ConvertStringTimeToISO(_timeToString.value.toString())

        val mappingElements = mappingElementsManager.GetMappingElements()
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

    fun InitDates() {
        SetDateFromCommand(dateAndTimeFrom[Calendar.YEAR], dateAndTimeFrom[Calendar.MONTH], dateAndTimeFrom[Calendar.DAY_OF_MONTH])
        SetTimeFromCommand(dateAndTimeFrom[Calendar.HOUR_OF_DAY], dateAndTimeFrom[Calendar.MINUTE])
        SetDateToCommand(dateAndTimeTo[Calendar.YEAR], dateAndTimeTo[Calendar.MONTH], dateAndTimeTo[Calendar.DAY_OF_MONTH])
        SetTimeToCommand(dateAndTimeTo[Calendar.HOUR_OF_DAY], dateAndTimeTo[Calendar.MINUTE])
    }

    fun SetMappingParameters(mapping: SavedMapping) {
        dateAndTimeFrom = mapping.GetDateTimeFrom().clone() as Calendar?
        dateAndTimeTo = mapping.GetDateTimeTo().clone() as Calendar?
        InitDates()

        mappingElementsManager.ClearMappingElements()
        mapping.GetParticipants().forEach {
            mappingElementsManager.AddMappingElement(it)
        }
        UpdateBadges()
        UpdateMappingPanelState()
    }

    fun UpdateBadges() {
        val curMappingElements = mappingElementsManager.GetMappingElements()
        if (curMappingElements.count()==0)
            badgesOperations.ClearMappingsAmount()
        else
            badgesOperations.SetMappingsAmount(curMappingElements.count())
    }

    fun RemoveUserFromMapping(username: String) {
        val curMappingElements = mappingElementsManager.GetMappingElements()
        if (curMappingElements.contains(username))
            mappingElementsManager.RemoveMappingElement(username)

        UpdateBadges()
        UpdateMappingPanelState()
    }

    fun UpdateMappingPanelState() {
        val participants = mappingElementsManager.GetMappingElements()
        _mappingParticipants.value = participants
        _mappingPanelVisible.value = participants.count()>1
    }

    fun SetDateFromCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeFrom[year, month] = date
        _dateFromString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)
    }

    fun SetTimeFromCommand(hours: Int, minutes: Int) {
        dateAndTimeFrom[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeFrom[Calendar.MINUTE] = minutes
        _timeFromString.value = DateAndTimeConverter.ConvertToStringTime(hours, minutes)
    }

    fun SetDateToCommand(year: Int, month: Int, date: Int) {
        val dispMonth = month + 1
        dateAndTimeTo[year, month] = date
        _dateToString.value = DateAndTimeConverter.ConvertToStringDate(year, dispMonth, date)
    }

    fun SetTimeToCommand(hours: Int, minutes: Int) {
        dateAndTimeTo[Calendar.HOUR_OF_DAY] = hours
        dateAndTimeTo[Calendar.MINUTE] = minutes
        _timeToString.value = DateAndTimeConverter.ConvertToStringTime(hours, minutes)
    }

    fun GetCurUserName(): String? { return sharedPrefs.login}

    fun GetSavedMappings() {
        val username = GetCurUserName()
        socketClient.GetSavedMappings(username)
    }




}