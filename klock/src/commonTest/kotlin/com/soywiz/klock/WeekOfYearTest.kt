import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import com.soywiz.klock.weekOfYear0
import kotlin.test.Test
import kotlin.test.assertEquals

class WeekOfYearTest {

    @Test
    fun testFirstWeekOfTheYearWithA52WeekYear() {
        assertEquals(51, DateTime(2022, Month.January, 1).weekOfYear0)
        assertEquals(51, DateTime(2022, Month.January, 2).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 3).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 4).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 5).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 6).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 7).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 8).weekOfYear0)
        assertEquals(0, DateTime(2022, Month.January, 9).weekOfYear0)
        assertEquals(1, DateTime(2022, Month.January, 10).weekOfYear0)
        assertEquals(1, DateTime(2022, Month.January, 11).weekOfYear0)
    }

    @Test
    fun testFirstWeekOfTheYearWithA53WeekYear() {
        assertEquals(52, DateTime(2021, Month.January, 1).weekOfYear0)
        assertEquals(52, DateTime(2021, Month.January, 2).weekOfYear0)
        assertEquals(52, DateTime(2021, Month.January, 3).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 4).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 5).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 6).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 7).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 8).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 9).weekOfYear0)
        assertEquals(0, DateTime(2021, Month.January, 10).weekOfYear0)
        assertEquals(1, DateTime(2021, Month.January, 11).weekOfYear0)
        assertEquals(1, DateTime(2021, Month.January, 12).weekOfYear0)
    }
}
