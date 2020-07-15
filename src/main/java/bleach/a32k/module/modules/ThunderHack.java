package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.RuhamaLogger;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

public class ThunderHack extends Module
{
    private static final List<SettingBase> settings = Collections.singletonList(new SettingToggle(false, "Public Chat"));

    public ThunderHack()
    {
        super("ThunderHack", 0, Category.EXPLOITS, "Lightning exploit, probably patched idk", settings);
    }

    public boolean onPacketRead(Packet<?> packet)
    {
        BlockPos newPos = null;

        if (packet instanceof SPacketEffect)
        {
            SPacketEffect effect = (SPacketEffect) packet;
            newPos = effect.getSoundPos();

            if (this.mc.player.getPosition().getDistance(effect.getSoundPos().getX(), effect.getSoundPos().getY(), effect.getSoundPos().getZ()) > 500.0D + this.mc.player.posY)
            {
                newPos = effect.getSoundPos();
            }
        } else if (packet instanceof SPacketSoundEffect)
        {
            SPacketSoundEffect sound = (SPacketSoundEffect) packet;

            if (this.mc.player.getPosition().getDistance((int) sound.getX(), (int) sound.getY(), (int) sound.getZ()) > 500.0D + this.mc.player.posY)
            {
                newPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
            }
        } else if (packet instanceof SPacketSpawnGlobalEntity)
        {
            SPacketSpawnGlobalEntity lightning = (SPacketSpawnGlobalEntity) packet;

            newPos = new BlockPos(lightning.getX(), lightning.getY(), lightning.getZ());
        }

        if (newPos != null)
        {
            RuhamaLogger.log("Thunder struck at: " + TextFormatting.ITALIC + newPos.getX() + TextFormatting.WHITE + ", " + TextFormatting.ITALIC + newPos.getY() + TextFormatting.WHITE + ", " + TextFormatting.ITALIC + newPos.getZ());

            if (this.getSettings().get(0).toToggle().state && this.mc.player.getPosition().getDistance(newPos.getX(), newPos.getY(), newPos.getZ()) > 100.0D)
            {
                this.mc.player.sendChatMessage("> Thunder struck at: " + newPos.getX() + ", " + newPos.getY() + ", " + newPos.getZ());
            }
        }

        return false;
    }
}
