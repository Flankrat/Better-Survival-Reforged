package com.mx.bettersurvival.items;

import com.mx.bettersurvival.BetterSurvival;
import com.mx.bettersurvival.capability.ModCapabilities;
import com.mx.bettersurvival.init.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class NunchakuItem extends CustomWeaponItem {

    public NunchakuItem(Tier tier, float damageModifier, float speedModifier, Properties properties) {
        super(tier, damageModifier, speedModifier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        // (no knockback: nunchaku hits deal none, NunchakuKnockbackHandler cancels it so a combo keeps the target in
        // reach. The knockback call that was here did nothing anyway: a negative strength is ignored.)

        if (attacker instanceof Player player) {
            player.getCapability(ModCapabilities.NUNCHAKU_COMBO).ifPresent(combo -> {
                int comboLevel = ModEnchantments.level(ModEnchantments.COMBO, stack);
                combo.setComboPower(combo.getComboPower() + 0.1F + comboLevel / 20F);
            });
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // While spinning, let the hit through (the spin attacks via NunchakuClient);
        // otherwise cancel the normal left-click attack.
        return player.getCapability(ModCapabilities.NUNCHAKU_COMBO)
                .map(combo -> !combo.isSpinning())
                .orElse(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(BetterSurvival.MOD_ID + ".nunchaku.desc")
                .withStyle(ChatFormatting.AQUA));
    }
}
