package gg.rsmod.plugins.content.skills.farming.logic.handler

import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.model.item.Item
import gg.rsmod.plugins.api.cfg.Items
import gg.rsmod.plugins.api.ext.message
import gg.rsmod.plugins.api.ext.playSound
import gg.rsmod.plugins.content.skills.farming.data.Patch
import gg.rsmod.plugins.content.skills.farming.logic.PatchState

class WaterHandler(private val state: PatchState, private val patch: Patch, private val player: Player) {
    fun water(inventorySlot: Int) {
        val wateringCan = player.inventory[inventorySlot]
        if (canWater(wateringCan)) {
            player.lockingQueue {
                player.animate(animation)
                player.playSound(sound)
                wait(4)
                if (player.inventory[inventorySlot] == wateringCan && canWater(wateringCan)) {
                    state.water()
                    if (player.inventory.remove(wateringCan!!, beginSlot = inventorySlot).hasSucceeded()) {
                        player.inventory.add(usedWateringCan(wateringCan.id), beginSlot = inventorySlot)
                    }
                }
            }
        }
    }

    private fun canWater(wateringCan: Item?): Boolean {
        if (wateringCan == null) {
            return false
        }

        if (wateringCan.id !in wateringCans) {
            return false
        }

        if (wateringCan.id == emptyWateringCan) {
            player.message("This watering can contains no water.")
            return false
        }

        if (state.seed == null) {
            player.message("You should grow something first.")
            return false
        }

        if (!state.seed!!.seedType.canBeWatered) {
            player.message("This patch doesn't need watering.")
            return false
        }

        if (state.isPlantFullyGrown) {
            player.message("This patch doesn't need watering.")
            return false
        }

        if (state.isDead) {
            player.message("Water isn't going to cure that!")
            return false
        }

        return true
    }

    companion object {
        private const val animation = 2293
        private const val sound = 2446

        private const val emptyWateringCan = Items.WATERING_CAN
        val wateringCans = listOf(
                emptyWateringCan,
                Items.WATERING_CAN_1,
                Items.WATERING_CAN_2,
                Items.WATERING_CAN_3,
                Items.WATERING_CAN_4,
                Items.WATERING_CAN_5,
                Items.WATERING_CAN_6,
                Items.WATERING_CAN_7,
                Items.WATERING_CAN_8,
        )

        private fun usedWateringCan(current: Int): Int {
            val index = (wateringCans.indexOf(current) - 1).coerceAtLeast(0)
            return wateringCans[index]
        }
    }
}
