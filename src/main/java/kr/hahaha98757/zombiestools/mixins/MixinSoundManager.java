package kr.hahaha98757.zombiestools.mixins;

import kr.hahaha98757.zombiestools.events.SoundEvent;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class MixinSoundManager {

    @Inject(method = "playSound", at = @At("HEAD"))
    private void playSound(ISound p_sound, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new SoundEvent(p_sound));
    }
}