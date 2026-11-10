package com.mx.bettersurvival.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;

/**
 * Tells a real melee swing apart from other damage (projectiles, magic, effect ticks, chain lightning).
 *
 * RLCombat replaces the vanilla player_attack / mob_attack damage type with a per-weapon one
 * (rlcombat:slashing for every sword-class item, which all Better Survival weapons are), so checking
 * only for player_attack / mob_attack misses every hit in a pack that has RLCombat.
 */
public final class MeleeHits {

    private MeleeHits() {
    }

    public static boolean isMeleeHit(DamageSource source) {
        if (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK))
            return true;
        Entity attacker = source.getEntity();
        return attacker != null
                && source.getDirectEntity() == attacker
                && source.typeHolder().unwrapKey()
                        .map(key -> key.location().getNamespace().equals("rlcombat"))
                        .orElse(false);
    }
}
