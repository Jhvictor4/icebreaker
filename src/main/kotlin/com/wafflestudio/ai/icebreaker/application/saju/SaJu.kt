package com.wafflestudio.ai.icebreaker.application.saju

import java.time.LocalDate
import java.time.LocalDateTime

data class SaJu(
    val yearly: GanJi,
    val monthly: GanJi,
    val daily: GanJi,
    val hourly: GanJi,
) {
    val 음양오행_8가지 = listOf(
        yearly.천간_음양오행,
        yearly.지지_음양오행,
        monthly.천간_음양오행,
        monthly.지지_음양오행,
        daily.천간_음양오행,
        daily.지지_음양오행,
        hourly.천간_음양오행,
        hourly.지지_음양오행,
    )

    companion object {
        fun from(birth: LocalDateTime): SaJu {
            val birthDate = birth.toLocalDate()
            val yearly = year(birthDate)
            val monthly = month(birthDate, yearly)
            val daily = daily(birthDate)
            val hourly = hourly(birth, daily)
            return SaJu(yearly, monthly, daily, hourly)
        }

        private fun year(birth: LocalDate): GanJi {
            val yearOffset = birth.year - 1900
            val base1900 = GanJi.find("경자")
            return GanJi(
                base1900.천간.next(yearOffset),
                base1900.지지.next(yearOffset)
            )
        }

        private fun month(birth: LocalDate, yearly: GanJi): GanJi {
            val season = 절기.findByMonth(birth.month.value)
            val baseHeaven = when (yearly.천간) {
                천간.갑, 천간.기 -> 천간.병
                천간.을, 천간.경 -> 천간.무
                천간.병, 천간.신 -> 천간.경
                천간.정, 천간.임 -> 천간.임
                천간.무, 천간.계 -> 천간.갑
            }
            return GanJi(
                baseHeaven.next(season.month - 1),
                season.지지
            )
        }

        private fun daily(birth: LocalDate): GanJi {
            val baseDate = LocalDate.of(1925, 2, 9)
            val index = birth.toEpochDay() - baseDate.toEpochDay()
            return GanJi.idxAt(index.toInt())
        }

        private fun hourly(birth: LocalDateTime, daily: GanJi): GanJi {
            val 십이시 = 십이시.from(birth.toLocalTime())
            val baseHeaven = when (daily.천간) {
                천간.갑, 천간.기 -> 천간.갑
                천간.을, 천간.경 -> 천간.병
                천간.병, 천간.신 -> 천간.무
                천간.정, 천간.임 -> 천간.경
                천간.무, 천간.계 -> 천간.임
            }

            return GanJi(
                baseHeaven.next(십이시.ordinal % 12),
                지지.of(십이시)
            )
        }
    }
}
