/**
 * Created by admin on 05.12.2016.
 */
object IntervalRandom : Random() {
    fun rand(left: Int, right: Int): Int {
        var res = generator.nextInt()
        if(res < 0)
            res = -res
        return res % (right - left) + left
    }
}