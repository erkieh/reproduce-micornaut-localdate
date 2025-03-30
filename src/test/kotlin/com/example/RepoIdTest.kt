package com.example

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.management.timer.Timer.ONE_HOUR
import kotlin.jvm.optionals.getOrNull

@MicronautTest
class RepoIdTest(
    val documentRepository: DocumentRepository,
) : StringSpec({

    "test reading existing date" {
        val date = LocalDate.of(1001, 1, 1)
        val documentId = UUID.fromString("d9090160-8777-4b0f-8cc7-40390ec3d4ef")

        val document = documentRepository.findById(documentId).getOrNull()

        document shouldNotBe null
        document!!.startDate.toString() shouldBe date
    }

    "test saving and reading date" {
        val date = LocalDate.of(1111, 11, 11)
        val savedDocument = documentRepository.save(Document(null, date))

        savedDocument.startDate shouldBe date

        val document = documentRepository.findById(savedDocument.id).getOrNull()

        document shouldNotBe null
        document!!.startDate shouldBe date
    }

    val dates = listOf(
        Triple(1001, 1, 1),
        Triple(1582, 10, 3),
        Triple(1582, 10, 4),
        Triple(1582, 10, 15),
        Triple(1582, 10, 16),
        Triple(2024, 12, 31),
    )

    "test Date to LocalDate without calendar check" {
        assertSoftly {
            dates.forEach {
                val date = dateFormat.parse("${it.first}-${it.second}-${it.third}")
                val expectedDate = LocalDate.of(it.first, it.second, it.third)
                convertDateToLocalDate(date) shouldBe expectedDate
            }
        }
    }

    "test Date to LocalDate with calendar check" {
        assertSoftly {
            dates.forEach {
                val date = dateFormat.parse("${it.first}-${it.second}-${it.third}")
                val expectedDate = LocalDate.of(it.first, it.second, it.third)
                convertDateToLocalDateWithCalendar(date) shouldBe expectedDate
            }
        }
    }
}
) {
    companion object {
        //as in java.util.GregorianCalendar.DEFAULT_GREGORIAN_CUTOVER
        val DEFAULT_GREGORIAN_CUTOVER = -12219292800000L
        val MAX_GREGORIAN_CUTOVER = DEFAULT_GREGORIAN_CUTOVER + 14 * ONE_HOUR
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val fullDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

        fun isGregorian(utc: Long): Boolean {
            return utc >= MAX_GREGORIAN_CUTOVER || utc >= (DEFAULT_GREGORIAN_CUTOVER - TimeZone.getDefault()
                .getOffset(utc))
        }

        fun convertDateToLocalDateWithCalendar(date: Date): LocalDate {
            if (isGregorian(date.time)) {
                date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate()
            }
            return LocalDate.from(dateTimeFormatter.parse(fullDateFormat.format(date)))
        }

        fun convertDateToLocalDate(date: Date): LocalDate {
            return date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate()
        }
    }
}