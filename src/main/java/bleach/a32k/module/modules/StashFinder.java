package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.FileMang;
import bleach.a32k.utils.RenderUtils;
import bleach.a32k.utils.RuhamaLogger;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

public class StashFinder extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingMode("Mode: ", "Current", "0, 0"), new SettingSlider(0.0D, 10.0D, 4.0D, 0, "Fly Gap: "), new SettingToggle(false, "Debug"), new SettingToggle(true, "AutoReopen"), new SettingToggle(true, "Shulker Log"), new SettingToggle(true, "Dupe Log"), new SettingToggle(true, "Chest Log"), new SettingToggle(true, "Sign Log"), new SettingToggle(false, "Illegal Log"), new SettingSlider(0.0D, 50.0D, 20.0D, 0, "Min Chest: "), new SettingToggle(true, "Active"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public List<ChunkPos> chunks = new ArrayList<>();
    public List<ChunkPos> nextChunks = new ArrayList<>();

    public int range = 0;

    public ChunkPos nextChunk;
    public ChunkPos startChunk;

    public List<ChunkPos> chestList = new ArrayList<>();
    public List<UUID> dupeList = new ArrayList<>();
    public List<BlockPos> shulkers = new ArrayList<>();
    public List<BlockPos> signs = new ArrayList<>();

    public boolean elytraing;
    public int elytratime;

    public int timeout = 0;

    public StashFinder()
    {
        super("StashFinder", 0, Category.MISC, "Explores Chunks around you using the elytra and logs stashes (saves in \".minecraft/bleach/ruhama/\", /stashfinder x z to set a start point)", settings);
    }

    public void onDisable()
    {
        this.chunks = new ArrayList<>();
        this.nextChunks = new ArrayList<>();
        this.nextChunk = null;
        this.startChunk = null;
        this.range = 0;
        this.shulkers.clear();
        this.signs.clear();
    }

    public boolean onPacketRead(Packet<?> packet)
    {
        if (this.getSettings().get(8).toToggle().state && packet instanceof SPacketChunkData && this.mc.world.getBiome(this.mc.player.getPosition()) != Biomes.HELL)
        {
            SPacketChunkData chunkPack = (SPacketChunkData) packet;

            Chunk chunk = new Chunk(this.mc.world, chunkPack.getChunkX(), chunkPack.getChunkZ());
            chunk.read(chunkPack.getReadBuffer(), chunkPack.getExtractedSize(), chunkPack.isFullChunk());

            List<Block> blocks = Arrays.asList(Blocks.BEDROCK, Blocks.BARRIER, Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL);

            StringBuilder content = new StringBuilder();

            for (int x = 0; x < 15; ++x)
            {
                for (int y = 0; y < 255; ++y)
                {
                    for (int z = 0; z < 15; ++z)
                    {
                        Block b = chunk.getBlockState(x, y, z).getBlock();

                        if (blocks.contains(b) && (b != Blocks.BEDROCK || y > 5))
                        {
                            this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Illegal: " + b.getLocalizedName() + ": " + (chunk.x * 16 + x) + ", " + y + ", " + (chunk.z * 16 + z)));
                            content.append("Illegal | ").append(b.getLocalizedName()).append(" | ").append(chunk.x * 16 + x).append(", ").append(y).append(", ").append(chunk.z * 16 + z).append(" | ").append(this.dtf.format(LocalDateTime.now())).append("\n");
                        }
                    }
                }
            }

            if (content.length() > 0)
            {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content.toString()), "stashfinder.txt");
            }
        }

        return false;
    }

    public void onEnable()
    {
        if (this.startChunk != null)
        {
            RuhamaLogger.log("StashFinder: Starting from " + this.startChunk.getXStart() + ", " + this.startChunk.getZStart());

            this.range = ((int) Math.max(Math.abs(this.mc.player.posX - (double) this.startChunk.getXStart()), Math.abs(this.mc.player.posZ - (double) this.startChunk.getZStart())) >> 4) - 1;
        } else if (this.getSettings().get(0).toMode().mode == 1)
        {
            this.range = ((int) Math.max(Math.abs(this.mc.player.posX), Math.abs(this.mc.player.posZ)) >> 4) - 1;

            if (this.range < 0)
            {
                this.range = 0;
            }

            this.startChunk = new ChunkPos(0, 0);
        } else
        {
            this.startChunk = new ChunkPos(this.mc.player.getPosition());
        }

        if (this.getSettings().get(8).toToggle().state)
        {
            this.mc.renderGlobal.loadRenderers();
        }
    }

    public void onUpdate()
    {
        if (this.getSettings().get(10).toToggle().state)
        {
            StringBuilder content;
            Iterator teIter;
            TileEntity t;

            if (this.getSettings().get(4).toToggle().state && this.mc.player.ticksExisted % 10 == 0)
            {
                content = new StringBuilder();
                teIter = this.mc.world.loadedTileEntityList.iterator();

                ferngayler:

                while (true)
                {
                    do
                    {
                        do
                        {
                            if (!teIter.hasNext())
                            {
                                if (content.length() > 0)
                                {
                                    FileMang.createFile("stashfinder.txt");
                                    FileMang.appendFile(StringUtils.chop(content.toString()), "stashfinder.txt");
                                }

                                break ferngayler;
                            }

                            t = (TileEntity) teIter.next();
                        } while (!(t instanceof TileEntityShulkerBox));
                    } while (this.shulkers.contains(t.getPos()));

                    int count = 0;
                    Iterator tileEntityIterator = this.mc.world.loadedTileEntityList.iterator();

                    while (true)
                    {
                        TileEntity t1;
                        do
                        {
                            do
                            {
                                if (!tileEntityIterator.hasNext())
                                {
                                    this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Shulker: " + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ()));
                                    content.append(Objects.requireNonNull(t.getDisplayName()).getUnformattedText()).append(" | ").append(Block.REGISTRY.getNameForObject(t.getBlockType())).append(" | ").append(t.getPos().getX()).append(", ").append(t.getPos().getY()).append(", ").append(t.getPos().getZ()).append(" | ").append(this.dtf.format(LocalDateTime.now())).append(" (").append(count).append(" nearby)\n");

                                    this.shulkers.add(t.getPos());

                                    continue ferngayler;
                                }

                                t1 = (TileEntity) tileEntityIterator.next();
                            } while (t1 == t);
                        } while (!(t1 instanceof TileEntityChest) && !(t1 instanceof TileEntityShulkerBox));

                        if (t1.getPos().distanceSq(t.getPos()) < 20.0D)
                        {
                            ++count;
                        }
                    }
                }
            }

            if (this.getSettings().get(5).toToggle().state && this.mc.player.ticksExisted % 10 == 0)
            {
                content = new StringBuilder();
                HashMap<ChunkPos, Integer> chunkMap = new HashMap<>();
                Iterator tileEntityIterator = this.mc.world.loadedTileEntityList.iterator();

                while (tileEntityIterator.hasNext())
                {
                    TileEntity te = (TileEntity) tileEntityIterator.next();

                    if (te instanceof TileEntityChest)
                    {
                        Integer i = chunkMap.get(new ChunkPos(te.getPos()));
                        chunkMap.put(new ChunkPos(te.getPos()), i == null ? 0 : i + 1);
                    }
                }

                tileEntityIterator = chunkMap.entrySet().iterator();

                while (tileEntityIterator.hasNext())
                {
                    Entry<ChunkPos, Integer> e = (Entry) tileEntityIterator.next();

                    if (!this.chestList.contains(e.getKey()) && (double) e.getValue() >= this.getSettings().get(9).toSlider().getValue())
                    {
                        this.chestList.add(e.getKey());
                        String text = e.getValue() + "x Chest | " + e.getKey().getXStart() + ", " + e.getKey().getZStart();

                        this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3" + text));

                        content.append(text).append(" | ").append(this.dtf.format(LocalDateTime.now())).append("\n");
                    }
                }

                if (content.length() > 0)
                {
                    FileMang.createFile("stashfinder.txt");
                    FileMang.appendFile(StringUtils.chop(content.toString()), "stashfinder.txt");
                }
            }

            if (this.getSettings().get(6).toToggle().state && this.mc.player.ticksExisted % 10 == 0)
            {
                content = new StringBuilder();
                teIter = this.mc.world.loadedEntityList.iterator();

                while (teIter.hasNext())
                {
                    Entity e = (Entity) teIter.next();

                    if (e instanceof AbstractChestHorse)
                    {
                        AbstractChestHorse e1 = (AbstractChestHorse) e;

                        if (e1.hasChest() && !this.dupeList.contains(e1.getUniqueID()))
                        {
                            this.dupeList.add(e1.getUniqueID());
                            this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Dupe / " + e1.getDisplayName().getUnformattedText() + ": " + e1.posX + ", " + e1.posY + ", " + e1.posZ));

                            content = new StringBuilder("Dupe | " + e1.getDisplayName().getUnformattedText() + " | " + e1.posX + ", " + e1.posY + ", " + e1.posZ + " | " + this.dtf.format(LocalDateTime.now()) + "\n");
                        }
                    }
                }

                if (content.length() > 0)
                {
                    FileMang.createFile("stashfinder.txt");
                    FileMang.appendFile(StringUtils.chop(content.toString()), "stashfinder.txt");
                }
            }

            if (this.getSettings().get(7).toToggle().state && this.mc.player.ticksExisted % 10 == 0)
            {
                content = new StringBuilder();
                teIter = this.mc.world.loadedTileEntityList.iterator();

                while (teIter.hasNext())
                {
                    t = (TileEntity) teIter.next();

                    if (t instanceof TileEntitySign)
                    {
                        TileEntitySign t1 = (TileEntitySign) t;

                        if (!this.signs.contains(t1.getPos()) && !Arrays.equals(t1.signText, new ITextComponent[] {new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")}))
                        {
                            this.signs.add(t1.getPos());
                            this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Sign: <\"" + t1.signText[0].getUnformattedText() + " | " + t1.signText[1].getUnformattedText() + " | " + t1.signText[2].getUnformattedText() + " | " + t1.signText[3].getUnformattedText() + "\"> at: " + t1.getPos().getX() + ", " + t1.getPos().getY() + ", " + t1.getPos().getZ()));

                            content = new StringBuilder("Sign <" + t1.signText[0].getUnformattedText() + " | " + t1.signText[1].getUnformattedText() + " | " + t1.signText[2].getUnformattedText() + " | " + t1.signText[3].getUnformattedText() + "> | " + t1.getPos().getX() + ", " + t1.getPos().getY() + ", " + t1.getPos().getZ() + " | " + this.dtf.format(LocalDateTime.now()) + "\n");
                        }
                    }
                }

                if (content.length() > 0)
                {
                    FileMang.createFile("stashfinder.txt");
                    FileMang.appendFile(StringUtils.chop(content.toString()), "stashfinder.txt");
                }
            }

            if (!this.mc.player.isElytraFlying() && this.elytraing)
            {
                this.elytraing = false;
                this.elytratime = 40;
            }

            --this.elytratime;

            boolean flat = Objects.requireNonNull(ModuleManager.getModuleByName("ElytraFly")).isToggled() && Objects.requireNonNull(ModuleManager.getModuleByName("ElytraFly")).getSettings().get(0).toMode().mode == 0;

            if (!flat && this.getSettings().get(3).toToggle().state && this.elytratime > 0 && !this.mc.player.onGround && this.mc.currentScreen != null)
            {
                this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, Action.START_FALL_FLYING));
                this.timeout = 80;
            }

            if (this.timeout > 0)
            {
                --this.timeout;
            }

            if (flat && !this.mc.gameSettings.keyBindBack.isKeyDown())
            {
                this.elytraing = true;
                this.elytratime = 0;

                if (this.startChunk == null)
                {
                    this.startChunk = new ChunkPos(this.mc.player.getPosition());
                }

                int view = 16;
                int step = 1;

                boolean sorted = false;

                ChunkPos c;

                int x;
                int z;

                for (x = -view; x <= view; x += 16)
                {
                    for (z = -view; z <= view; z += 16)
                    {
                        c = new ChunkPos(this.mc.player.getPosition().add(x, 0, z));

                        if (!this.chunks.contains(c))
                        {
                            this.chunks.add(c);

                            if (this.nextChunks.contains(c))
                            {
                                this.nextChunks.remove(c);
                                if (!sorted)
                                {
                                    this.nextChunks.sort(Comparator.comparingDouble(a -> a.getBlock(8, 0, 8).distanceSq(this.mc.player.getPosition())));
                                    sorted = true;
                                }
                            }
                        }
                    }
                }

                if (!this.nextChunks.isEmpty())
                {
                    this.nextChunk = this.nextChunks.get(0);
                    this.facePos(this.nextChunk.getXStart() + 8, this.nextChunk.getZStart() + 8);

                    Vec3d forward = (new Vec3d(0.0D, 0.0D, Objects.requireNonNull(ModuleManager.getModuleByName("ElytraFly")).getSettings().get(4).toSlider().getValue())).rotateYaw(-((float) Math.toRadians(this.mc.player.rotationYaw)));
                    this.mc.player.setVelocity(forward.x, forward.y, forward.z);
                } else
                {
                    this.chunks.clear();
                    this.range += (int) this.getSettings().get(1).toSlider().getValue();

                    for (x = this.startChunk.x - this.range; x <= this.startChunk.x + this.range; ++x)
                    {
                        for (z = this.startChunk.z - this.range; z <= this.startChunk.z + this.range; ++z)
                        {
                            if (Math.abs(x - this.startChunk.x) > this.range - step || Math.abs(z - this.startChunk.z) > this.range - step)
                            {
                                c = new ChunkPos(x, z);
                                if (!this.chunks.contains(c))
                                {
                                    this.nextChunks.add(c);
                                }
                            }
                        }
                    }

                    this.nextChunks.sort(Comparator.comparingDouble(a -> a.getBlock(8, 0, 8).distanceSq(this.mc.player.getPosition())));
                }
            }
        }
    }

    public void onRender()
    {
        if (this.getSettings().get(2).toToggle().state)
        {
            Iterator chonkIter = this.chunks.iterator();

            ChunkPos c;
            while (chonkIter.hasNext())
            {
                c = (ChunkPos) chonkIter.next();
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(c.getBlock(0, 0, 0), c.getBlock(16, 0, 16)), 1.0F, 0.0F, 0.0F, 0.3F);
            }

            chonkIter = this.nextChunks.iterator();

            while (chonkIter.hasNext())
            {
                c = (ChunkPos) chonkIter.next();
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(c.getBlock(0, 0, 0), c.getBlock(16, 0, 16)), 0.0F, 0.0F, 1.0F, 0.3F);
            }

            if (this.nextChunk != null)
            {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.nextChunk.getBlock(0, 0, 0), this.nextChunk.getBlock(16, 0, 16)), 0.0F, 1.0F, 0.0F, 0.3F);
            }
        }
    }

    public void facePos(double x, double z)
    {
        double diffX = x - this.mc.player.posX;
        double diffZ = z - this.mc.player.posZ;
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;

        mc.player.rotationYaw += MathHelper.wrapDegrees(yaw - this.mc.player.rotationYaw);
    }
}
