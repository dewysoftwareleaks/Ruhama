package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.utils.RenderUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TunnelESP extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingMode("Mode: ", "1x2", "All (slow)"), new SettingSlider(2.0D, 20.0D, 10.0D, 0, "All Max: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Red: "), new SettingSlider(0.0D, 255.0D, 255.0D, 0, "Green: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Blue: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Alpha: "));
    private final List<TunnelESP.Space> spaces = new ArrayList();
    private final ConcurrentLinkedQueue<Chunk> scanQueue = new ConcurrentLinkedQueue();
    private final List<Chunk> addChunkQueue = new ArrayList();
    private final Thread scanThread = new Thread(() ->
    {
        while (true)
        {
            if (!this.scanQueue.isEmpty())
            {
                try
                {
                    this.scan(this.scanQueue.poll());
                } catch (Exception var2)
                {
                    var2.printStackTrace();
                }

                System.out.println("Done Scanning Chunk! (Queue: " + this.scanQueue.size() + ")");
            }
        }
    });

    public TunnelESP()
    {
        super("TunnelESP", 0, Category.RENDER, "Shows 1x2/small tunnels (toggle when changing mode)", settings);
        this.scanThread.start();
    }

    public void onDisable()
    {
        this.spaces.clear();
        this.scanQueue.clear();
    }

    public void onEnable()
    {
        for (int x = (int) (this.mc.player.posX - 120.0D); x < (int) this.mc.player.posX + 120; x += 16)
        {
            for (int z = (int) (this.mc.player.posZ - 120.0D); z < (int) this.mc.player.posZ + 120; z += 16)
            {
                BlockPos b = new BlockPos(x, 0, z);
                if (this.mc.world.isBlockLoaded(b, false))
                {
                    this.scanQueue.add(this.mc.world.getChunk(b));
                }
            }
        }

    }

    public boolean onPacketRead(Packet<?> packet)
    {
        if (packet instanceof SPacketChunkData)
        {
            SPacketChunkData chunkPack = (SPacketChunkData) packet;
            Chunk chunk = new Chunk(this.mc.world, chunkPack.getChunkX(), chunkPack.getChunkZ());
            chunk.read(chunkPack.getReadBuffer(), chunkPack.getExtractedSize(), chunkPack.isFullChunk());
            this.addChunkQueue.add(chunk);
        }

        return false;
    }

    public void onUpdate()
    {
        Iterator var1 = this.addChunkQueue.iterator();

        while (var1.hasNext())
        {
            Chunk c = (Chunk) var1.next();
            this.scanQueue.add(c);
        }

        this.addChunkQueue.clear();
        var1 = (new ArrayList(this.spaces)).iterator();

        while (var1.hasNext())
        {
            TunnelESP.Space s = (TunnelESP.Space) var1.next();
            if (this.mc.player.getPosition().add(0, -((int) this.mc.player.posY), 0).getDistance(s.pos.getX(), 0, s.pos.getZ()) > 160.0D)
            {
                this.spaces.remove(s);
            }
        }

    }

    public void onRender()
    {
        float r = (float) (this.getSettings().get(2).toSlider().getValue() / 255.0D);
        float g = (float) (this.getSettings().get(3).toSlider().getValue() / 255.0D);
        float b = (float) (this.getSettings().get(4).toSlider().getValue() / 255.0D);
        float a = (float) (this.getSettings().get(5).toSlider().getValue() / 255.0D);
        Iterator var5 = (new ArrayList(this.spaces)).iterator();

        while (var5.hasNext())
        {
            TunnelESP.Space s = (TunnelESP.Space) var5.next();
            if (s.xpos)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX() + 1, s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }

            if (s.xneg)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX(), s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }

            if (s.ypos)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY() + 1, s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }

            if (s.yneg)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY(), s.pos.getZ() + 1), r, g, b, a);
            }

            if (s.zpos)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ() + 1, s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }

            if (s.zneg)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ()), r, g, b, a);
            }
        }

    }

    private void scan(Chunk c)
    {
        if (this.isToggled())
        {
            List<BlockPos> air = new ArrayList();
            BlockPos start = new BlockPos(c.x * 16, 0, c.z * 16);

            int max;
            for (int x = 0; x < 16; ++x)
            {
                for (max = 0; max < 16; ++max)
                {
                    for (int y = 0; y < this.mc.world.getTopSolidOrLiquidBlock(start.add(x, 0, max)).getY() - 1; ++y)
                    {
                        if (c.getBlockState(x, y, max).getBlock() == Blocks.AIR)
                        {
                            air.add(start.add(x, y, max));
                        }
                    }
                }
            }

            List<List<BlockPos>> rotations = Arrays.asList(Arrays.asList(new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0)), Arrays.asList(new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0)));
            if (this.getSettings().get(0).toMode().mode == 0)
            {
                Iterator var20 = air.iterator();

                while (true)
                {
                    while (true)
                    {
                        while (true)
                        {
                            BlockPos b;
                            do
                            {
                                do
                                {
                                    do
                                    {
                                        if (!var20.hasNext())
                                        {
                                            return;
                                        }

                                        b = (BlockPos) var20.next();
                                    } while (!air.contains(b.down()));
                                } while (air.contains(b.up()));
                            } while (air.contains(b.down(2)));

                            if (this.isOnEdge(b))
                            {
                                if (this.mc.world.getBlockState(b.east()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.east().down()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.west()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.west().down()).getBlock() != Blocks.AIR)
                                {
                                    this.spaces.add(new TunnelESP.Space(b, true, true, true, false, false, false));
                                    this.spaces.add(new TunnelESP.Space(b.down(), true, true, false, true, false, false));
                                } else if (!air.contains(b.north()) && !air.contains(b.north().down()) && !air.contains(b.south()) && !air.contains(b.south().down()))
                                {
                                    this.spaces.add(new TunnelESP.Space(b, false, false, true, false, true, true));
                                    this.spaces.add(new TunnelESP.Space(b.down(), false, false, false, true, true, true));
                                }
                            } else if (!air.contains(b.east()) && !air.contains(b.east().down()) && !air.contains(b.west()) && !air.contains(b.west().down()))
                            {
                                this.spaces.add(new TunnelESP.Space(b, true, true, true, false, false, false));
                                this.spaces.add(new TunnelESP.Space(b.down(), true, true, false, true, false, false));
                            } else if (!air.contains(b.north()) && !air.contains(b.north().down()) && !air.contains(b.south()) && !air.contains(b.south().down()))
                            {
                                this.spaces.add(new TunnelESP.Space(b, false, false, true, false, true, true));
                                this.spaces.add(new TunnelESP.Space(b.down(), false, false, false, true, true, true));
                            }
                        }
                    }
                }
            } else
            {
                max = (int) this.getSettings().get(1).toSlider().getValue();
                Iterator var22 = rotations.iterator();

                label131:
                while (var22.hasNext())
                {
                    List<BlockPos> rot = (List) var22.next();
                    ArrayList toExplore = new ArrayList(air);

                    while (true)
                    {
                        ArrayList toSpacesAdd;
                        boolean shouldExit;
                        do
                        {
                            if (toExplore.isEmpty())
                            {
                                continue label131;
                            }

                            int found = 1;
                            List<BlockPos> explored = new ArrayList();
                            List<BlockPos> exploring = new ArrayList(Arrays.asList((BlockPos) toExplore.get(0)));
                            toSpacesAdd = new ArrayList(Arrays.asList(new TunnelESP.Space(exploring.get(0), true, true, true, true, true, true)));
                            shouldExit = false;

                            while (!exploring.isEmpty())
                            {
                                Iterator var14 = (new ArrayList(exploring)).iterator();

                                while (var14.hasNext())
                                {
                                    BlockPos b = (BlockPos) var14.next();
                                    Iterator var16 = rot.iterator();

                                    while (var16.hasNext())
                                    {
                                        BlockPos r = (BlockPos) var16.next();
                                        BlockPos next = b.add(r);
                                        if (!explored.contains(next) && !exploring.contains(next) && toExplore.contains(next))
                                        {
                                            toSpacesAdd.add(new TunnelESP.Space(next, true, true, true, true, true, true));
                                            exploring.add(next);
                                            ++found;
                                            if (found > max)
                                            {
                                                shouldExit = true;
                                            }
                                        }
                                    }

                                    explored.add(b);
                                    exploring.remove(b);
                                }
                            }

                            toExplore.removeAll(explored);
                        } while (shouldExit);

                        int y = -1;
                        Iterator var24 = toSpacesAdd.iterator();

                        while (var24.hasNext())
                        {
                            TunnelESP.Space s = (TunnelESP.Space) var24.next();
                            if (y == -1)
                            {
                                y = s.pos.getY();
                            } else if (s.pos.getY() != y)
                            {
                                shouldExit = true;
                                break;
                            }
                        }

                        if (shouldExit)
                        {
                            this.spaces.addAll(toSpacesAdd);
                        }
                    }
                }

            }
        }
    }

    private boolean isOnEdge(BlockPos b)
    {
        return b.getX() % 16 == 0 || b.getX() % 16 == 15 || b.getZ() % 16 == 0 || b.getZ() % 16 == 15;
    }

    class Space
    {
        public BlockPos pos;
        public boolean xpos;
        public boolean xneg;
        public boolean ypos;
        public boolean yneg;
        public boolean zpos;
        public boolean zneg;

        public Space(BlockPos pos, boolean xpos, boolean xneg, boolean ypos, boolean yneg, boolean zpos, boolean zneg)
        {
            this.pos = pos;
            this.xpos = xpos;
            this.xneg = xneg;
            this.ypos = ypos;
            this.yneg = yneg;
            this.zpos = zpos;
            this.zneg = zneg;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof TunnelESP.Space))
            {
                return false;
            } else
            {
                TunnelESP.Space s = (TunnelESP.Space) o;
                return s.pos.equals(this.pos) && s.xpos == this.xpos && s.xneg == this.xneg && s.pos == this.pos && s.ypos == this.ypos && s.zpos == this.zpos && s.zneg == this.zneg;
            }
        }
    }
}
