package magwer.dolphin.game

import kotlin.math.max

class Health(maxHealth: Double) {

    var maxHealth = maxHealth
    set(value) {
        if (health > value)
            health = value
    }
    var health = maxHealth

    val isDead
    get() = health == 0.0

    fun damage(amount: Double) {
        health = max(health - amount, 0.0)
    }



}