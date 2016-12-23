import java.util.*

/**
 * Created by admin on 02.12.2016.
 */
class ResourceManager(val memorySize: Int, val cores: Int, val deviceSize: Int) {
    private var freeCores: Int = cores
    private var freeMemory: Int = memorySize
    private val devices: Array<Entity> = Array(deviceSize, {Entity()})

    fun checkRes(taskInfo: TaskInfo): Boolean {
        return taskInfo.threads <= freeCores &&
                taskInfo.memory <= freeMemory &&
                !taskInfo.devices.fold(false) {res, pair -> res || devices[pair.first].state}
    }

    fun addTask(taskInfo: TaskInfo): Boolean {
        if(!checkRes(taskInfo))
            return false
        freeCores -= taskInfo.threads
        freeMemory -= taskInfo.memory
        for(index in taskInfo.devices.map { it.first }) {
            devices[index].state = true
        }
        return true
    }

    fun removeTask(taskInfo: TaskInfo) {
        freeCores += taskInfo.threads
        freeMemory += taskInfo.memory
    }

    fun freeDevice(index: Int) {
        devices[index].state = false
    }
}