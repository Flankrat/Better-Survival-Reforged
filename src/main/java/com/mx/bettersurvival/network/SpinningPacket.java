package com.mx.bettersurvival.network;

import com.mx.bettersurvival.capability.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SpinningPacket {

    private final boolean spinning;

    public SpinningPacket(boolean spinning) {
        this.spinning = spinning;
    }

    public static void encode(SpinningPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.spinning);
    }

    public static SpinningPacket decode(FriendlyByteBuf buf) {
        return new SpinningPacket(buf.readBoolean());
    }

    public static void handle(SpinningPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            // only with nunchaku in hand: the spin flag lets left clicks through NunchakuItem.onLeftClickEntity, so
            // the server can't take a client's word for it (stopping is always allowed)
            if (msg.spinning && !(player.getMainHandItem().getItem() instanceof com.mx.bettersurvival.items.NunchakuItem))
                return;

            boolean changed = player.getCapability(ModCapabilities.NUNCHAKU_COMBO).map(combo -> {
                boolean was = combo.isSpinning();
                combo.setSpinning(msg.spinning);
                if (!msg.spinning) {
                    combo.setComboTime(0);
                }
                return was != msg.spinning;
            }).orElse(false);
            // Tell everyone tracking this player, so they see the spin animation too, but only when it changed:
            // re-sending every packet let a modified client flood the players around it
            if (changed) {
                ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
                        new SpinningSyncPacket(player.getId(), msg.spinning));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
