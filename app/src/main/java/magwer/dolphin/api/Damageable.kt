package magwer.dolphin.api

import magwer.dolphin.game.Health

interface Damageable {

    val health: Health

    fun onDamage(damage: Double) {

    }

    fun onHeal(heal: Double) {

    }

    fun onChangeMaxHealth(old: Double, new: Double) {

    }

}