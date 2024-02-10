package com.wafflestudio.ai.icebreaker.application.saju

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class SaJu(
    val yearly: GanJi,
    val monthly: GanJi,
    val daily: GanJi,
    val hourly: GanJi
) {
    val 음양오행_8가지 = listOf(
        yearly.천간_음양오행,
        yearly.지지_음양오행,
        monthly.천간_음양오행,
        monthly.지지_음양오행,
        daily.천간_음양오행,
        daily.지지_음양오행,
        hourly.천간_음양오행,
        hourly.지지_음양오행
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

data class GanJi(
    val 천간: 천간,
    val 지지: 지지
) {
    val twoLetters = 천간.name + 지지.name

    val 천간_음양오행 = 음양오행(천간.yy, 천간.oh)
    val 지지_음양오행 = 음양오행(지지.yy, 지지.oh)

    companion object {

        fun idxAt(index: Int): GanJi {
            return cached[index % cached.size]
        }

        fun find(천간: 천간, 지지: 지지): GanJi {
            return mapByStemAndBranch[천간 to 지지]!!
        }

        fun find(a: String, b: String) =
            find(천간.find(a), 지지.find(b))

        fun find(text: String) =
            find(text.substring(0, 1), text.substring(1, 2))

        fun getAvailableList() = cached

        private val cached = (0..59)
            .map { GanJi(천간.idxAt(it % 10), 지지.idxAt(it % 12)) }
            .toTypedArray()

        private val mapByStemAndBranch = cached
            .associateBy { it.천간 to it.지지 }

        private val indexMap = cached
            .mapIndexed { idx, cycle -> cycle.twoLetters to idx }
            .toMap()
    }
}

enum class 천간(val chinese: String, val yy: 음양, val oh: 오행) {
    갑("甲", 음양.양, 오행.목),
    을("乙", 음양.음, 오행.목),
    병("丙", 음양.양, 오행.화),
    정("丁", 음양.음, 오행.화),
    무("戊", 음양.양, 오행.토),
    기("己", 음양.음, 오행.토),
    경("庚", 음양.양, 오행.금),
    신("辛", 음양.음, 오행.금),
    임("壬", 음양.양, 오행.수),
    계("癸", 음양.음, 오행.수);

    fun next(offset: Int): 천간 {
        return idxAt(ordinal + offset)
    }

    companion object {
        private val cached = values().associateBy { it.name }

        fun idxAt(index: Int): 천간 {
            return values()[index % values().size]
        }

        fun find(key: String): 천간 {
            return cached[key] ?: throw IllegalArgumentException("천간을 찾을 수 없습니다: $key")
        }
    }
}

enum class 지지(val chinese: String, val animal: String, val yy: 음양, val oh: 오행) {
    자("子", "쥐", 음양.양, 오행.수),
    축("丑", "소", 음양.음, 오행.토),
    인("寅", "범", 음양.양, 오행.목),
    묘("卯", "토끼", 음양.음, 오행.목),
    진("辰", "용", 음양.양, 오행.토),
    사("巳", "뱀", 음양.음, 오행.화),
    오("午", "말", 음양.양, 오행.화),
    미("未", "양", 음양.음, 오행.토),
    신("申", "원숭이", 음양.양, 오행.금),
    유("酉", "닭", 음양.음, 오행.금),
    술("戌", "개", 음양.양, 오행.토),
    해("亥", "돼지", 음양.음, 오행.수);

    fun next(offset: Int): 지지 {
        return idxAt(ordinal + offset)
    }

    companion object {
        private val cached = values().associateBy { it.name }

        fun idxAt(index: Int): 지지 {
            return values()[index % values().size]
        }

        fun find(key: String): 지지 {
            return cached[key] ?: throw IllegalArgumentException("지지를 찾을 수 없습니다: $key")
        }

        fun of(twlv: 십이시): 지지 {
            return when (twlv) {
                십이시.자시 -> 자
                십이시.축시 -> 축
                십이시.인시 -> 인
                십이시.묘시 -> 묘
                십이시.진시 -> 진
                십이시.사시 -> 사
                십이시.오시 -> 오
                십이시.미시 -> 미
                십이시.신시 -> 신
                십이시.유시 -> 유
                십이시.술시 -> 술
                십이시.해시 -> 해
                십이시.야자시 -> 해
            }
        }
    }
}

enum class 십이시(val range: ClosedRange<LocalTime>) {
    자시(LocalTime.of(0, 30)..LocalTime.of(1, 30)),
    축시(LocalTime.of(1, 30)..LocalTime.of(3, 30)),
    인시(LocalTime.of(3, 30)..LocalTime.of(5, 30)),
    묘시(LocalTime.of(5, 30)..LocalTime.of(7, 30)),
    진시(LocalTime.of(7, 30)..LocalTime.of(9, 30)),
    사시(LocalTime.of(9, 30)..LocalTime.of(11, 30)),
    오시(LocalTime.of(11, 30)..LocalTime.of(13, 30)),
    미시(LocalTime.of(13, 30)..LocalTime.of(15, 30)),
    신시(LocalTime.of(15, 30)..LocalTime.of(17, 30)),
    유시(LocalTime.of(17, 30)..LocalTime.of(19, 30)),
    술시(LocalTime.of(19, 30)..LocalTime.of(21, 30)),
    해시(LocalTime.of(21, 30)..LocalTime.of(23, 30)),
    야자시(LocalTime.of(23, 30)..LocalTime.of(0, 30));

    companion object {
        fun from(time: LocalTime): 십이시 {
            return values().first { it.range.contains(time) }
        }
    }
}

enum class 십신(val chinese: String) {
    겁재("劫財"),
    비견("比肩"),
    식신("食神"),
    상관("傷官"),
    편재("偏財"),
    정재("正財"),
    편관("偏官"),
    정관("正官"),
    편인("偏印"),
    정인("正印");

    companion object {
        /**
         * 종류	오행	음양
         * ============
         * 비견	동일	같음
         * 겁재	동일	다름
         * 식신	가생	같음
         * 상관	가생	다름
         * 편재	가극	같음
         * 정재	가극	다름
         * 편관	피극	같음
         * 정관	피극	다름
         * 편인	피생	같음
         * 정인	피생	다름
         */
        internal fun calculate(one: 음양오행, another: 음양오행): 십신 {
            return when (one.음양 == another.음양) {
                true -> when {
                    one.오행 == another.오행 -> 비견
                    one.오행 empowers another.오행 -> 식신
                    one.오행 weakens another.오행 -> 편재
                    another.오행 weakens one.오행 -> 편관
                    another.오행 empowers one.오행 -> 편인
                    else -> throw IllegalStateException("Unexpected state: $one, $another")
                }

                false -> when {
                    one.오행 == another.오행 -> 겁재
                    one.오행 empowers another.오행 -> 상관
                    one.오행 weakens another.오행 -> 정재
                    another.오행 weakens one.오행 -> 정관
                    another.오행 empowers one.오행 -> 정인
                    else -> throw IllegalStateException("Unexpected state: $one, $another")
                }
            }
        }
    }
}

data class 음양오행(
    val 음양: 음양,
    val 오행: 오행
)

enum class 음양(val chinese: String) {
    음("陰"), 양("陽")
}

enum class 오행(val chinese: String, val easyKorean: String, val color: String) {
    목("木", "나무", "푸른"),
    화("火", "불", "붉은"),
    토("土", "흙", "노란"),
    금("金", "철", "흰"),
    수("水", "물", "검은");

    infix fun empowers(other: 오행): Boolean {
        return Interactions[this].empowers == other
    }

    infix fun weakens(other: 오행): Boolean {
        return Interactions[this].weakens == other
    }

    private enum class Interactions(val empowers: 오행, val weakens: 오행) {
        목(오행.화, 오행.토),
        화(오행.토, 오행.금),
        토(오행.금, 오행.수),
        금(오행.수, 오행.목),
        수(오행.목, 오행.화)
        ;

        companion object {
            private val RELATIONSHIP_GRAPH = mapOf(
                오행.목 to Interactions.목,
                오행.화 to Interactions.화,
                오행.토 to Interactions.토,
                오행.금 to Interactions.금,
                오행.수 to Interactions.수
            )

            operator fun get(오행: 오행): Interactions {
                return RELATIONSHIP_GRAPH[오행]!!
            }
        }
    }
}

enum class 절기(val month: Int, val 지지: 지지) {
    입춘(1, 지지.인),
    우수(1, 지지.인),
    경칩(2, 지지.묘),
    춘분(2, 지지.묘),
    청명(3, 지지.진),
    곡우(3, 지지.진),
    입하(4, 지지.사),
    소만(4, 지지.사),
    망종(5, 지지.오),
    하지(5, 지지.오),
    소서(6, 지지.미),
    대서(6, 지지.미),
    입추(7, 지지.술),
    처서(7, 지지.술),
    백로(8, 지지.해),
    추분(8, 지지.해),
    한로(9, 지지.자),
    상강(9, 지지.자),
    입동(10, 지지.축),
    소설(10, 지지.축),
    대설(11, 지지.진),
    동지(11, 지지.진),
    소한(12, 지지.유),
    대한(12, 지지.유);

    companion object {
        private val cachedByMonth = values().associateBy { it.month }

        fun findByMonth(month: Int): 절기 {
            return cachedByMonth[month] ?: throw IllegalArgumentException("절기를 찾을 수 없습니다: $month")
        }
    }
}
