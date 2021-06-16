package magwer.dolphin.game

import magwer.dolphin.api.Damageable
import kotlin.math.max
import kotlin.math.min

class Health(private val owner: Damageable, maxHealth: Double) {

    var maxHealth = maxHealth
        set(value) {
            if (health > value)
                health = value
            val old = field
            field = value
            owner.onChangeMaxHealth(old, value)
        }
    var health = maxHealth
        private set
    var lastDamageMil = 0L
        private set

    val isDead
        get() = health == 0.0

    fun damage(amount: Double) {
        health = max(health - amount, 0.0)
        lastDamageMil = System.currentTimeMillis()
        owner.onDamage(amount)
    }

    fun heal(amount: Double) {
        health = min(health + amount, maxHealth)
        owner.onHeal(amount)
    }


}