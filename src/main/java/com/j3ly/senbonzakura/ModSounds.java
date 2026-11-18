package com.j3ly.senbonzakura;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
//? if neoforge {
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
//?}

/**
 * Custom ability sounds, one per weapon.
 *
 * <p>To add real audio, drop an .ogg at
 * {@code assets/senbonzakura/sounds/<name>.ogg}. The files shipped now are
 * short synthetic placeholders so abilities still make a sound until you
 * replace them. Subtitle keys live in {@code lang/en_us.json}.
 */
public final class ModSounds {
    //? if neoforge {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, SenbonzakuraMod.MOD_ID);
    //?}

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        //? if neoforge {
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(SenbonzakuraMod.MOD_ID, name)));
        //?} else {
        /*throw new UnsupportedOperationException("Sound events are not ported to this loader yet");*/
        //?}
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> SENBONZAKURA_SWING = register("senbonzakura_swing");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRESCENT_BLADE_SLASH = register("crescent_blade_slash");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRYPT_BLADE_BLAST = register("crypt_blade_blast");
    public static final DeferredHolder<SoundEvent, SoundEvent> SAW_BLADE_AXE_SPIN = register("saw_blade_axe_spin");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITE_OF_COMMANDMENTS_THUNDER = register("rite_of_commandments_thunder");
    public static final DeferredHolder<SoundEvent, SoundEvent> PYRERIZER_FLAME = register("pyrerizer_flame");
    public static final DeferredHolder<SoundEvent, SoundEvent> YUKI_NO_AME_SWING = register("yuki_no_ame_swing");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAZOR_SHARK_LASER = register("razor_shark_laser");
    public static final DeferredHolder<SoundEvent, SoundEvent> SENTENCE_SWING = register("sentence_swing");
    public static final DeferredHolder<SoundEvent, SoundEvent> WENDIGO_ARROWS = register("wendigo_arrows");
    public static final DeferredHolder<SoundEvent, SoundEvent> EMPIRE_STAFF_CAST = register("empire_staff_cast");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOD_ROCK_PULSE = register("god_rock_pulse");
    public static final DeferredHolder<SoundEvent, SoundEvent> MATTHIAS_CURSEBLADE_BURST = register("matthias_curseblade_burst");
    public static final DeferredHolder<SoundEvent, SoundEvent> DUCKYS_WAND_DUCK = register("duckys_wand_duck");
    public static final DeferredHolder<SoundEvent, SoundEvent> DUCKYS_WAND_DOMAIN = register("duckys_wand_domain");

    private ModSounds() {
    }
}
