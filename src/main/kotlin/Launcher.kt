import java.util.*

/**
 * Created by admin on 02.12.2016.
 */

fun main(args: Array<String>) {
    val resManager = ResourceManager(16, 8, 4)
    val calendarEvents = ArrayList<Event>()
    val queue = ArrayList<TaskInfo>()
    var runTime = 0

    calendarEvents.add(NewTaskEvent(0, queue, resManager, 100))

    while (calendarEvents.isNotEmpty()) {
        val event = calendarEvents.minBy { it.time }
        event?.action()
        calendarEvents += event?.plan()?: emptyArray()
        runTime = calendarEvents.map { it.time }.max()?: 0
        //println(event)
        calendarEvents.remove(event)
    }

    println("Run time: $runTime")
}