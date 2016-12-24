import java.util.*

/**
 * Created by admin on 02.12.2016.
 */



//Дикий костыль
var getFrom = LightTasks

abstract class Event(val time: Int) {
    abstract fun action()
    abstract fun plan(): Array<Event>
    override fun toString(): String {
        return "${this.javaClass.simpleName}(time: $time)"
    }
}

class NewTaskEvent(time: Int, val queue: ArrayList<TaskInfo>, val resourceManager: ResourceManager, val countTasks: Int) : Event(time) {
    val eRand = {ExpRandom.rand(360.0, 10.0)}
    val memoryP = Array<Pair<Int, Double>>(resourceManager.memorySize, {i -> Pair(i, 1.0/resourceManager.memorySize) })
    val coreP = Array<Pair<Int, Double>>(resourceManager.cores, {i -> Pair(i, 1.0/resourceManager.cores) })
    val devicesP = Array<Pair<Int, Double>>(resourceManager.deviceSize + 1, {i -> Pair(i, 1.0/(resourceManager.deviceSize + 1)) })

    override fun action() {
        val time = eRand.invoke().toInt() + 1
        var devices = devicesP.copyOf()
        var device = DescRandom.rand(devices)
        var res = emptyArray<Pair<Int, Int>>()
        while (device < resourceManager.deviceSize) {
            val timeD = eRand.invoke().toInt() + 1
            res += Pair(device, timeD % time)
            devices = devices.filter { it.first != device }.toTypedArray()
            device = DescRandom.rand(devices)
        }
        queue.add(
                TaskInfo(
                        this.time,
                        DescRandom.rand(coreP) + 1,
                        DescRandom.rand(memoryP) + 1,
                        time,
                        res
                )
        )
        //println(queue.last())
    }

    override fun plan(): Array<Event> {
        var res: Array<Event> = emptyArray()
        res += getFrom.getFromQueue(time, queue, resourceManager)
        if(countTasks > 0)
            res += NewTaskEvent(time + ExpRandom.rand(60.0, 10.0).toInt() + 1, queue, resourceManager, countTasks - 1)
        return res
    }
}

class AddTaskEvent(time: Int, val task: TaskInfo, val resourceManager: ResourceManager, val queue: ArrayList<TaskInfo>): Event(time) {
    override fun action() {
        task.executed = true
        resourceManager.addTask(task)
    }

    override fun plan(): Array<Event> {
        var res: Array<Event> = emptyArray()
        res += RemoveTaskEvent(time + task.time, task, resourceManager, queue)
        for (device in task.devices) {
            res += FreeDeviceEvent(time + device.second, device.first, resourceManager, queue)
        }
        return res
    }

}

class RemoveTaskEvent(time: Int, val task: TaskInfo, val resourceManager: ResourceManager, val queue: ArrayList<TaskInfo>): Event(time) {
    override fun action() {
        resourceManager.removeTask(task)
        queue.remove(task)
        println(time - task.timeCreated)
    }

    override fun plan(): Array<Event> {
        return getFrom.getFromQueue(time, queue, resourceManager)
    }

}

class FreeDeviceEvent(time: Int, val index: Int, val resourceManager: ResourceManager, val queue: ArrayList<TaskInfo>): Event(time) {

    override fun action() {
        resourceManager.freeDevice(index)
    }

    override fun plan(): Array<Event> {
        return getFrom.getFromQueue(time, queue, resourceManager)
    }
}

interface GetFromQueue {
    fun getFromQueue(time: Int, queue: ArrayList<TaskInfo>, resourceManager: ResourceManager): Array<Event>
}

object Fifo : GetFromQueue {
    override fun getFromQueue(time: Int, queue: ArrayList<TaskInfo>, resourceManager: ResourceManager): Array<Event> {
        var res: Array<Event> = emptyArray()
        val queueWithoutExecuted = queue.filter { !it.executed }
        if (queueWithoutExecuted.isNotEmpty() && resourceManager.checkRes(queueWithoutExecuted.first())) {
            res += AddTaskEvent(time, queueWithoutExecuted.first(), resourceManager, queue)
        }
        return res
    }
}


object AvailableTasks : GetFromQueue {
    override fun getFromQueue(time: Int, queue: ArrayList<TaskInfo>, resourceManager: ResourceManager): Array<Event> {
        var res: Array<Event> = emptyArray()
        val queueWithoutExecuted = queue.filter { !it.executed }
        for (task in queueWithoutExecuted) {
            if (resourceManager.checkRes(task)) {
                res += AddTaskEvent(time, task, resourceManager, queue)
            }
        }
        return res
    }
}

object LightTasks : GetFromQueue {
    override fun getFromQueue(time: Int, queue: ArrayList<TaskInfo>, resourceManager: ResourceManager): Array<Event> {
        var res: Array<Event> = emptyArray()
        val queueWithoutExecuted = queue.filter { !it.executed }
        val sortedQueue = queueWithoutExecuted.sortedWith(Comparator {
            t, t1 ->
            val first = t.memory + t.threads + t.devices.size
            val second = t1.memory + t1.threads + t1.devices.size
            first - second
        })
        res += sortedQueue
                .takeWhile { resourceManager.checkRes(it) }
                .map { AddTaskEvent(time, it, resourceManager, queue) }
        return res
    }
}

object LightTasksAvailable : GetFromQueue {
    override fun getFromQueue(time: Int, queue: ArrayList<TaskInfo>, resourceManager: ResourceManager): Array<Event> {
        var res: Array<Event> = emptyArray()
        val queueWithoutExecuted = queue.filter { !it.executed }
        val sortedQueue = queueWithoutExecuted.sortedWith(Comparator {
            t, t1 ->
            val first = t.memory + t.threads + t.devices.size
            val second = t1.memory + t1.threads + t1.devices.size
            first - second
        })
        res += sortedQueue
                .filter { resourceManager.checkRes(it) }
                .map { AddTaskEvent(time, it, resourceManager, queue) }
        return res
    }
}