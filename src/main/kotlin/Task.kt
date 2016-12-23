/**
 * Created by admin on 21.11.2016.
 */
class Task(
        val info: TaskInfo,
        val memoryCell: Array<Entity>,
        val cores: Array<Entity>,
        val devices: Map<Int, Entity>,
        val destroy: (Task) -> Unit
) {
    private var currentTime = 0

    init {
        for (cell in memoryCell)
            cell.state = true
        for (core in cores)
            core.state = true
        for (device in devices)
            device.value.state = true
    }

    fun process() {
        currentTime++
        for (device in info.devices) {
            if(currentTime == device.second) {
                freeDevice(device.first)
            }
        }
        if(currentTime == info.time) {
            free()
        }
    }

    private fun freeDevice(first: Int) {
        devices[first]?.state = false
    }

    private fun free() {
        for (cell in memoryCell)
            cell.state = false
        for (core in cores)
            core.state = false
        destroy(this)
    }
}

data class TaskInfo(val timeCreated: Int, val threads: Int, val memory: Int, val time: Int, val devices: Array<Pair<Int, Int>>, var executed: Boolean = false) {
    init {
        for (device in devices) {
            if(device.second > time)
                throw RuntimeException("Время использования устройства(${device.second}) не должно превышать время выполнения задачи($time).")
        }
    }
}