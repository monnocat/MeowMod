// Copyright (c) 2026 monnocat

package dev.monnocat.client;

import static dev.monnocat.MeowMod.LOGGER;

import java.util.Random;

import org.jspecify.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.feline.CatSoundVariant;
import net.minecraft.world.entity.animal.feline.CatSoundVariant.CatSoundSet;
import net.minecraft.world.entity.animal.feline.CatSoundVariants.SoundSet;

public class MeowModClient implements ClientModInitializer {
    private static final Minecraft mc = Minecraft.getInstance();

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            if (message.getString().contains("meow"))
                sayMeow(sender);
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("meow"))
                sayMeow(null);
        });
    }

    // Minecraft.level and Minecraft.player should be non-null when this is called
    @SuppressWarnings("null")
    private void sayMeow(@Nullable GameProfile sender) {
        LOGGER.info("Playing cat sound");

        if (sender != null) {
            for (AbstractClientPlayer player : mc.level.players()) {
                if (player.getUUID().equals(sender.id())) {
                    mc.level.playLocalSound(player,
                            selectSound(),
                            SoundSource.MASTER, 1, 1);
                    return;
                }
            }
        }

        mc.player.playSound(selectSound());
    }

    // Minecraft.level should be non-null when this is called
    @SuppressWarnings("null")
    private SoundEvent selectSound() {
        Random rng = new Random(mc.level.getGameTime());
        boolean royal = rng.nextBoolean(), baby = rng.nextBoolean();

        CatSoundVariant catSoundVariant;
        if (royal) {
            catSoundVariant = SoundEvents.CAT_SOUNDS.get(SoundSet.ROYAL);
        } else {
            catSoundVariant = SoundEvents.CAT_SOUNDS.get(SoundSet.CLASSIC);
        }

        CatSoundSet catSoundSet;
        if (baby) {
            catSoundSet = catSoundVariant.babySounds();
        } else {
            catSoundSet = catSoundVariant.adultSounds();
        }

        LOGGER.info("Selected cat sound: royal=" + royal + ", baby=" + baby);

        return catSoundSet.ambientSound().value();
    }
}
