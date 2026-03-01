package io.flavien.demo.testCore.comparator

import java.time.OffsetDateTime

class OffsetDateTimeTestComparator : Comparator<OffsetDateTime> {

    override fun compare(o1: OffsetDateTime, o2: OffsetDateTime): Int {
        val min = o1.minusMinutes(1)
        val max = o1.plusMinutes(1)

        if (o2.compareTo(min) >= 0 && o2.compareTo(max) <= 0) {
            return 0
        }

        return o1.compareTo(o2)
    }
}