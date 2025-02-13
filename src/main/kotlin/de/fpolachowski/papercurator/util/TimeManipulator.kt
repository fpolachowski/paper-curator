package de.fpolachowski.papercurator.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TimeManipulator {
    companion object {
        fun dateToLocalDateTime(date : Date): LocalDateTime {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
}