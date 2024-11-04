package kr.hahaha98757.zombiestools.events

import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.Event

class TitleEvent(private var title: String): Event() {

    fun getTitle(): String {
        return EnumChatFormatting.getTextWithoutFormattingCodes(title)
    }
}