package magwer.dolphin.physics

import magwer.dolphin.api.CollisionScene
import magwer.dolphin.game.sceneobject.GameObject

class Collider<T: CollisionBox>(val owner: GameObject, var scene: CollisionScene, var box: T, val channel: Int) {

    fun addToScene() {
        scene.internal_addCollisionObject(this)
    }

    fun removeFromScene() {
        scene.internal_removeCollisionObject(this)
    }

    fun changeScene(newScene: CollisionScene) {
        if (scene == newScene)
            return
        scene.internal_removeCollisionObject(this)
        scene = newScene
    }

    fun checkCollision(box: CollisionBox = this.box): Pair<ArrayList<Collider<*>>, ArrayList<Collider<*>>> {
        synchronized(box) {
            return scene.internal_checkCollision(this, box)
        }
    }

}