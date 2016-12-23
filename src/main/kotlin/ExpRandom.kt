/**
 * Created by admin on 28.11.2016.
 */
object ExpRandom : Random() {
    fun rand(scale: Double, offset: Double): Double {
        val x = generator.nextDouble()
        val n = -scale*Math.log(x/offset)
        return n
    }
}