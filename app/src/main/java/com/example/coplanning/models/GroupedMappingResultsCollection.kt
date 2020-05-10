package com.example.coplanning.models

import java.util.*

data class GroupedMappingResultsCollection(val groupedMappingResults: List<MappingResultsByGroups>)

data class MappingResultsByGroups(val groupName: String, val mappingResults: List<MappingResultItem>)