package com.example.coplanning.models.mapping

data class GroupedMappingResultsCollection(val groupedMappingResults: List<MappingResultsByGroups>)

data class MappingResultsByGroups(val groupName: String, val mappingResults: List<MappingResultItem>)