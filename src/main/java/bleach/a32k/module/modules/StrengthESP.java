package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.utils.ReflectUtils;
import bleach.a32k.utils.RenderUtils;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.Map.Entry;

public class StrengthESP extends Module
{
    private final List<List<Float>> effects = Arrays.asList(Arrays.asList(0.5764706F, 0.14117648F, 0.13725491F), Arrays.asList(0.48235294F, 0.3137255F, 0.4627451F), Arrays.asList(0.4F, 0.3019608F, 0.4117647F), Arrays.asList(0.65882355F, 0.28627452F, 0.18039216F), Arrays.asList(0.6666667F, 0.34509805F, 0.1764706F), Arrays.asList(0.7137255F, 0.44705883F, 0.2F), Arrays.asList(0.5411765F, 0.3882353F, 0.43137255F), Arrays.asList(0.49019608F, 0.39215687F, 0.38431373F), Arrays.asList(0.72156864F, 0.44313726F, 0.20784314F), Arrays.asList(0.5137255F, 0.3529412F, 0.44705883F), Arrays.asList(0.4509804F, 0.3529412F, 0.39607844F), Arrays.asList(0.69803923F, 0.38039216F, 0.19607843F));
    private final HashMap<Entity, Integer> players = new HashMap();

    public StrengthESP()
    {
        super("StrengthESP", 0, Category.RENDER, "Shows people with strength (only works with particles on)", (List) null);
    }

    public void onUpdate()
    {
        if (this.mc.currentScreen == null)
        {
            Iterator var1 = (new HashMap(this.players)).entrySet().iterator();

            while (var1.hasNext())
            {
                Entry<Entity, Integer> e = (Entry) var1.next();
                if (e.getValue() <= 0)
                {
                    this.players.remove(e.getKey());
                } else
                {
                    this.players.replace(e.getKey(), e.getValue() - 1);
                }
            }

            if (this.mc.world.playerEntities.size() > 1)
            {
                try
                {
                    int count = 0;
                    int playerCount = 0;
                    ArrayDeque[][] var3 = (ArrayDeque[][]) ReflectUtils.getField(ParticleManager.class, "fxLayers", "fxLayers").get(this.mc.effectRenderer);
                    int var4 = var3.length;

                    for (int var5 = 0; var5 < var4; ++var5)
                    {
                        ArrayDeque<Particle>[] p2 = var3[var5];
                        ArrayDeque[] var7 = p2;
                        int var8 = p2.length;

                        label82:
                        for (int var9 = 0; var9 < var8; ++var9)
                        {
                            ArrayDeque<Particle> p1 = var7[var9];
                            Iterator var11 = p1.iterator();

                            label79:
                            do
                            {
                                while (true)
                                {
                                    Particle p;
                                    do
                                    {
                                        do
                                        {
                                            if (!var11.hasNext())
                                            {
                                                continue label82;
                                            }

                                            p = (Particle) var11.next();
                                        } while (p == null);
                                    } while (!(p instanceof ParticleSpell));

                                    if (count > 250)
                                    {
                                        return;
                                    }

                                    ++count;
                                    Vec3d pos = new Vec3d((Double) ReflectUtils.getField(Particle.class, "posX", "posX").get(p), (Double) ReflectUtils.getField(Particle.class, "posY", "posY").get(p), (Double) ReflectUtils.getField(Particle.class, "posZ", "posZ").get(p));
                                    Iterator var14 = this.mc.world.playerEntities.iterator();

                                    while (var14.hasNext())
                                    {
                                        Entity e = (Entity) var14.next();
                                        if (e != this.mc.player && !this.players.containsKey(e) && pos.distanceTo(e.getPositionVector()) < 2.0D && this.effects.contains(Arrays.asList(p.getRedColorF(), p.getGreenColorF(), p.getBlueColorF())))
                                        {
                                            this.players.put(e, 10);
                                            ++playerCount;
                                            continue label79;
                                        }
                                    }
                                }
                            } while (playerCount < this.mc.world.playerEntities.size() - 1);

                            return;
                        }
                    }
                } catch (Exception var16)
                {
                    var16.printStackTrace();
                }

            }
        }
    }

    public void onOverlay()
    {
        int c = Gui.arrayListEnd + 15;

        for (Iterator var2 = this.players.entrySet().iterator(); var2.hasNext(); c += 10)
        {
            Entry<Entity, Integer> e = (Entry) var2.next();
            this.mc.fontRenderer.drawStringWithShadow(((Entity) e.getKey()).getName(), 2.0F, (float) c, 12591136);
        }

        if (c != Gui.arrayListEnd + 15)
        {
            this.mc.fontRenderer.drawStringWithShadow("Strength Players: ", 2.0F, (float) (Gui.arrayListEnd + 5), 14688288);
        }

        Gui.arrayListEnd = c;
    }

    public void onRender()
    {
        Iterator var1 = this.players.entrySet().iterator();

        while (var1.hasNext())
        {
            Entry<Entity, Integer> e = (Entry) var1.next();
            RenderUtils.drawFilledBlockBox(((Entity) e.getKey()).getEntityBoundingBox(), 1.0F, 0.0F, 0.0F, 0.3F);
        }

    }
}
