package bleach.a32k.mixin.mixins;

import bleach.a32k.Ruhama;
import bleach.a32k.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetHandlerPlayClient.class})
public class MixinPacketSend
{
    @Inject(
            method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void sendPacket(Packet<?> packetIn, CallbackInfo info)
    {
        if (ModuleManager.onPacketSend(packetIn))
        {
            info.cancel();
        }

        if (packetIn instanceof CPacketPlayerTryUseItemOnBlock)
        {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) packetIn;

            if (Minecraft.getMinecraft().player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox || Minecraft.getMinecraft().player.getHeldItem(packet.getHand()).getItem() == Item.getItemFromBlock(Blocks.HOPPER))
            {
                BlockPos pos = packet.getPos().offset(packet.getDirection());
                System.out.println("Rightclicked at: " + System.currentTimeMillis());
                Ruhama.friendBlocks.put(pos, 300);
            }
        }
    }
}
