import java.util.*

/**
 * Created by admin on 21.11.2016.
 */
class Controller {
    private val cores: Array<Entity> = Array(8, {Entity()})
    private val memory: Array<Entity> = Array(16, {Entity()})
    private val devices: Array<Entity> = Array(4, {Entity()})
    private val taskList: ArrayList<Task> = ArrayList()
    private val tasksRemoved: ArrayList<Task> = ArrayList()

    fun run(queueTask: Array<TaskInfo>) {
        var index = 0
        var time = 0
        while (index < queueTask.size || taskList.isNotEmpty()) {
            time++
            var flagClose = index < queueTask.size
            while (flagClose && index < queueTask.size) {
                val taskInfo = queueTask[index]
                if (checkRes(taskInfo)) {
                    addTask(taskInfo)
                    index++
                    flagClose = true
                } else {
                    flagClose = false
                }
            }
            for (task in taskList)
                task.process()
            taskList.removeAll(tasksRemoved)
            tasksRemoved.clear()
            println("Cores: ${cores.joinToString { it.state.toString() }}")
            println("Memory: ${memory.joinToString { it.state.toString() }}")
            println("Devices: ${devices.joinToString { it.state.toString() }}")
            println("Count tasks: ${taskList.size}")
            println("Time $time")
        }
    }

    fun run1(queueTask: Array<TaskInfo>) {
        var time = 0
        val allTasks = ArrayList(queueTask.toList())
        while (allTasks.isNotEmpty() || taskList.isNotEmpty()) {
            time++
            val removed = ArrayList<TaskInfo>()
            for (task in allTasks) {
                if(checkRes(task)) {
                    addTask(task)
                    removed += task
                }
            }
            allTasks.removeAll(removed)
            for (task in taskList)
                task.process()
            taskList.removeAll(tasksRemoved)
            tasksRemoved.clear()
            println("Cores: ${cores.joinToString { it.state.toString() }}")
            println("Memory: ${memory.joinToString { it.state.toString() }}")
            println("Devices: ${devices.joinToString { it.state.toString() }}")
            println("Count tasks: ${taskList.size}")
            println("Time: $time")
        }
    }

    private fun addTask(taskInfo: TaskInfo) {
        var availableMemory = arrayOf<Entity>()
        for (cell in memory) {
            if (availableMemory.size >= taskInfo.memory)
                break
            if(!cell.state) {
                availableMemory += cell
            }
        }
        var availableCores = arrayOf<Entity>()
        for (cell in cores) {
            if (availableCores.size >= taskInfo.threads)
                break
            if(!cell.state) {
                availableCores += cell
            }
        }
        var availableDevices = mapOf<Int, Entity>()
        for(index in taskInfo.devices.map { it.first }) {
            availableDevices += Pair(index, devices[index])
        }
        taskList.add(Task(taskInfo, availableMemory, availableCores, availableDevices, {task: Task -> tasksRemoved.add(task) }))
    }

    private fun checkRes(taskInfo: TaskInfo): Boolean {
        return taskInfo.threads <= cores.filter { !it.state }.size &&
        taskInfo.memory <= memory.filter { !it.state }.size &&
        !taskInfo.devices.fold(false) {res, pair -> res || devices[pair.first].state}
    }
}