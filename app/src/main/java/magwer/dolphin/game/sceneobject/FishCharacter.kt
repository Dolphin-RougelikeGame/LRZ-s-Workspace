package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.Animation
import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.Coord
import magwer.dolphin.api.adjacents
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.Accelerator
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.Health
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.physics.CircleBox
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import magwer.dolphin.physics.RectangleBox
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max

class FishCharacter(scene: GameScene, x: Double, y: Double) :
    Character<RectangleBox>(scene, x, y, 0.9, 0.9, "texture/fish/fish-1.png", 1.5) {

    override val collider =
        Collider(
            this,
            scene,
            RectangleBox(x - width * 0.5, y - height * 0.5, width, height),
            CollisionRule.PHYSICAL_CHANNEL
        )
    override val health = Health(this, 20.0)

    val attacker = Collider(this, scene, CircleBox(x, y, attackRange), CollisionRule.VISION_CHANNEL)
    val scouter = Collider(this, scene, CircleBox(x, y, scoutRange), CollisionRule.VISION_CHANNEL)
    val scouterLose =
        Collider(this, scene, CircleBox(x, y, loseRange), CollisionRule.VISION_CHANNEL)

    private val moveAcc = Accelerator(0.0, 0.2, 0.13)

    var attackTarget: MainCharacter? = null
    var chaseFinder: RoomGrid.PathFinder? = null
    var chasePathOrigin = ArrayList<Coord>()
    var chasePath = ArrayList<Coord>()
    var patrolCoord: Coord? = null
    var patrolIdle = 0L
    var attackCooldown = 0L

    private fun doPatrol(deltaTime: Long) {
        if (patrolIdle > 0) {
            patrolIdle -= deltaTime
            move(0.0, 0.0)
            return
        }
        if (random.nextDouble() < 0.3) {
            patrolIdle = (random.nextDouble() * 4000).toLong()
            move(0.0, 0.0)
            return
        }
        val ccoord = scene.roomGrid.gameToRoom(x, y)
        if (patrolCoord == null || abs(patrolCoord!!.first + 0.5 - x) < 0.05 && abs(patrolCoord!!.second + 0.5 - y) < 0.05) {
            val adjs = ccoord.adjacents
            val availables = ArrayList<Coord>()
            for (c in adjs) {
                val objs = scene.roomGrid.getColliders(c)
                if (objs == null || objs.all {
                        !scene.collisionRule.blocksWith(
                            it.channel,
                            collider.channel
                        )
                    })
                    availables.add(c)
            }
            if (availables.isEmpty())
                move(0.0, 0.0)
            else {
                patrolCoord = availables.random()
                val strength = moveAcc.onTick(deltaTime, 1.0)
                var dx = patrolCoord!!.first + 0.5 - x
                var dy = patrolCoord!!.second + 0.5 - y
                if (dx > 0 && dx > strength)
                    dx = strength
                if (dx < 0 && -dx > strength)
                    dx = -strength
                if (dy > 0 && dy > strength)
                    dy = strength
                if (dy < 0 && -dy > strength)
                    dy = -strength
                move(dx, dy)
            }
        }
    }

    fun doChase(deltaTime: Long) {
        if (chaseFinder == null || chaseFinder!!.failed)
            if (chasePathOrigin.size == 0 || chasePath.size == 0 || chasePath.size <= chasePathOrigin.size + 2)
                chaseFinder = scene.roomGrid.findPath(
                    collider,
                    scene.roomGrid.gameToRoom(x, y),
                    attackTarget!!,
                    attackRange * 0.7
                )
        if (chaseFinder != null && !chaseFinder!!.done) {
            val done = !chaseFinder!!.computeForTick() && chaseFinder!!.done
            if (done) {
                chasePath = chaseFinder!!.getPath()
                chaseFinder = null
            }
        }
        if (chasePath.isNotEmpty()) {
            val strength = moveAcc.onTick(deltaTime, 1.0)
            var chasecoord = chasePath.first()
            var dx = chasecoord.first + 0.5 - x
            var dy = chasecoord.second + 0.5 - y
            if (abs(dx) < 0.05 && abs(dy) < 0.05) {
                chasePath.removeAt(0)
                if (chasePath.isEmpty()) {
                    move(0.0, 0.0)
                    return
                }
                chasecoord = chasePath.first()
                dx = chasecoord.first + 0.5 - x
                dy = chasecoord.second + 0.5 - y
            }
            if (dx > 0 && dx > strength)
                dx = strength
            if (dx < 0 && -dx > strength)
                dx = -strength
            if (dy > 0 && dy > strength)
                dy = strength
            if (dy < 0 && -dy > strength)
                dy = -strength
            move(dx, dy)
        }

    }

    fun tryAttack(deltaTime: Long) {
        attackCooldown = max(attackCooldown - deltaTime, 0L)
        if (attackCooldown != 0L)
            return
        if (attackTarget == null)
            return
        if (attacker.checkCollision().second.contains(attackTarget!!.collider)) {
            attackCooldown = attackInterval
            attackTarget!!.health.damage(attackDamage)
        }
    }

    override fun updateLoc() {
        super.updateLoc()
        glShape.screenX = gameToScreen(x - width * 0.5)
        glShape.screenY = gameToScreen(y - height * 0.5)
        attacker.box = attacker.box.copyTo(x, y)
        scouter.box = scouter.box.copyTo(x, y)
        scouterLose.box = scouterLose.box.copyTo(x, y)
    }

    override fun addToScene() {
        super.addToScene()
        scene.animationManager.startAnimation(
            glShape.texture, Animation(
                2000, true,
                loadBitmapAsset(scene.context, "texture/fish/fish-1.png"),
                loadBitmapAsset(scene.context, "texture/fish/fish-2.png"),
                loadBitmapAsset(scene.context, "texture/fish/fish-3.png")
            )
        )
    }

    override fun removeFromScene() {
        super.removeFromScene()
        scene.animationManager.stopAnimation(glShape.texture)
    }

    override fun onTick(deltaTime: Long) {
        if (attackTarget != null) {
            val players = scouterLose.checkCollision().second
            if (!players.contains(attackTarget!!.collider))
                attackTarget = null
        }
        if (attackTarget == null) {
            val players = scouter.checkCollision().second
            if (players.isNotEmpty())
                attackTarget = players.random().owner as MainCharacter
        }
        if (attackTarget == null)
            doPatrol(deltaTime)
        else
            doChase(deltaTime)
        tryAttack(deltaTime)
        if (System.currentTimeMillis() - health.lastDamageMil < 150)
            glShape.tintColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        else
            glShape.tintColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun copyCollision(x: Double, y: Double): RectangleBox {
        return collider.box.copyTo(x - width * 0.5, y - height * 0.5)
    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord>? {
        return arrayListOf(roomGrid.gameToRoom(x, y))
    }

    companion object {

        private const val attackRange = 1.0
        private const val attackInterval = 1000L
        private const val attackDamage = 5.0
        private const val scoutRange = 8.0
        private const val loseRange = 16.0
        private val random = Random()

    }

}