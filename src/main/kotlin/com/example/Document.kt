package com.example

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import java.time.LocalDate
import java.util.*

@MappedEntity
data class Document(
    @field:Id
    @GeneratedValue
    val id: UUID? = null,
    val startDate: LocalDate? = null
)