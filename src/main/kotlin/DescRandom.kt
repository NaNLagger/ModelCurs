/**
 * Created by admin on 28.11.2016.
 */
object DescRandom : Random() {
    fun rand(p: Array<Pair<Int, Double>>): Int {
        val arrayP = normalize(p)
        val x = generator.nextDouble()
        var current = 0.0
        for (pair in arrayP) {
            current += pair.second
            if(current > x)
                return pair.first
        }
        return 0
    }

    private fun normalize(p: Array<Pair<Int, Double>>): Array<Pair<Int, Double>> {
        val sum = p.sumByDouble { it.second }
        return p.map { Pair(it.first, it.second/sum) }.toTypedArray()
    }
}