package kr.hahaha98757.examplemod

import kr.hahaha98757.zombiestools.Tools
import kr.hahaha98757.zombiestools.events.SoundEvent
import kr.hahaha98757.zombiestools.events.TitleEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

const val MODID = "examplemod"
const val NAME = "Example Mod"
const val VERSION = "1.0.0"

@Mod(modid = MODID, name = NAME, version = VERSION)
open class ExampleMod {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {

    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {

    }

    @SubscribeEvent
    open fun onTitle(event: TitleEvent) {
        Tools.addChat("Detected title: ${event.getTitle()}")
    }

    @SubscribeEvent
    open fun onRender(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Test String", 10F, 10F, 0xffffff)
    }

    @SubscribeEvent
    fun onSound(event: SoundEvent) {
        println("Detected sound: ${event.getSoundName()}")
    }
}