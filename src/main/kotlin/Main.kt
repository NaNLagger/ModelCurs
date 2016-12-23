/**
 * Created by admin on 21.11.2016.
 */

fun main(args: Array<String>) {
    val controller = Controller()
    val coresP = Array<Pair<Int, Double>>(8, {i -> Pair(i, 1.0/8.0)})
    val memoryP = Array<Pair<Int, Double>>(16, {i -> Pair(i, 1.0/16.0) })
    val devicesP = Array<Pair<Int, Double>>(4, {i -> Pair(i, 1.0/4.0) })
    val eRand = {ExpRandom.rand(5.0, 1.0)}
    val tasks = Array<TaskInfo>(20,
            {i ->
                var time = eRand.invoke().toInt() + 1
                var timeD = eRand.invoke().toInt() + 1
                if (timeD > time) {
                    val temp = time
                    time = timeD
                    timeD = temp
                }
                TaskInfo(
                        1,
                        DescRandom.rand(coresP) + 1,
                        DescRandom.rand(memoryP) + 1,
                        time,
                        arrayOf(Pair(DescRandom.rand(devicesP), timeD))
                )
            }
    )
    controller.run1(tasks)
    controller.run(tasks)
//    controller.run(arrayOf<TaskInfo>(
//            TaskInfo(1, 1, 15, arrayOf(Pair(1, 5))),
//            TaskInfo(1, 1, 28, arrayOf(Pair(2, 5))),
//            TaskInfo(1, 1, 45, arrayOf(Pair(2, 5))),
//            TaskInfo(1, 1, 95, arrayOf(Pair(3, 5))),
//            TaskInfo(1, 1, 105, arrayOf(Pair(0, 5))),
//            TaskInfo(1, 1, 6, arrayOf(Pair(1, 5)))
//    ))
}
