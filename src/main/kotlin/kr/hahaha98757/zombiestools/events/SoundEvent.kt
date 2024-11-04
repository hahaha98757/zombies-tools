package kr.hahaha98757.zombiestools.events

import net.minecraft.client.audio.ISound
import net.minecraftforge.fml.common.eventhandler.Event

class SoundEvent(private var sound: ISound): Event() {


    fun getSoundName(): String {
        return try {
            sound.soundLocation.toString().split(":")[1]
        } catch (e: IndexOutOfBoundsException) {
            ""
        }
    }
}