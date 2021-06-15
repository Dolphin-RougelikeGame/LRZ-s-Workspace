package magwer.dolphin.game

import magwer.dolphin.api.clamp
import kotlin.math.max
import kotlin.math.min

class Accelerator(val min: Double, val max: Double, val acc: Double) {

    var current = 0.0

    fun onTick(deltaTime: Long, boostStrength: Double): Double {
        if (boostStrength == 0.0)
            if (current > 0)
                current = max(current - acc * deltaTime * 0.02, 0.0)
            else
                current = min(current + acc * deltaTime * 0.02, 0.0)
        else
            current = clamp(current + acc * deltaTime * 0.02 * boostStrength, min, max)
        return current
    }

}