package de.guntram.mcmod.itemholo;

import java.util.HashMap;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(modid = ItemHolo.MODID, 
        version = ItemHolo.VERSION,
	clientSideOnly = true, 
	guiFactory = "de.guntram.mcmod.itemholo.GuiFactory",
	acceptedMinecraftVersions = "[1.12]"
)

/*
class MobTitleChangeState {
    boolean stillLoaded;
    int     state;
    static  final int HAD_TITLE=0;
    static  final int NO_TITLE=1;
}
*/

public class ItemHolo
{
    static final String MODID="itemholo";
    static final String VERSION="1.1";
    
    private HashMap<Entity, Integer> knownItems;
    /* private HashMap<Entity, MobTitleChangeState> knownMobs; */
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        knownItems=new HashMap<>();
        /* knownMobs=new HashMap<>(); */
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(confHandler);
    }
    
    @SubscribeEvent
    public void onItemSpawn(EntityJoinWorldEvent event) {
        Entity item = event.getEntity();
        if (item!=null && item instanceof EntityItem && ConfigurationHandler.showItemTitles()) {
            knownItems.put(item, 0);
        }
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        int i;
        Entity[] workItems=knownItems.keySet().toArray(new Entity[knownItems.size()]);
        
        Pattern itemPattern=null, mobPattern=null;
        String  itemString, mobString;
        
        if ((itemString=ConfigurationHandler.getItemPattern())!=null
        &&   !itemString.isEmpty())
            itemPattern=Pattern.compile(itemString);

        if ((mobString=ConfigurationHandler.getMobPattern())!=null
        &&   !mobString.isEmpty())
            mobPattern=Pattern.compile(mobString);
        
        for (i=0; i<workItems.length; i++) {
            Entity entity = workItems[i];
            if (entity.isDead)
                knownItems.remove(entity);
            else if (entity instanceof EntityItem
                 && ((EntityItem)entity).getItem().getItem()==Items.AIR) {
                /* do nothing */
            } else if (entity instanceof EntityItem) {
                ItemStack stack=((EntityItem)entity).getItem();
                int count=stack.getCount();
                if (knownItems.get(entity)!=count) {
                    Item item=stack.getItem();
                    String displayName=item.getItemStackDisplayName(stack);
                    if (itemPattern==null
                    ||  itemPattern.matcher(displayName).matches()) {
                        entity.setCustomNameTag(count+" "
                            +item.getItemStackDisplayName(stack));
                        entity.setAlwaysRenderNameTag(true);
                        knownItems.put(entity, count);
                    }
                }
            }
        }
        
        if (Minecraft.getMinecraft().world!=null
        &&  Minecraft.getMinecraft().world.loadedEntityList!=null
        &&  ConfigurationHandler.showMobTitles()) {
            for (Entity entity: Minecraft.getMinecraft().world.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase mob=(EntityLivingBase) entity;
                    String s = EntityList.getEntityString(mob);
                    if (s == null)
                    {
                        s = "generic";
                    }
                    s=I18n.translateToLocal("entity." + s + ".name");
                    if (mobPattern==null
                    ||  mobPattern.matcher(s).matches()) {
                        entity.setCustomNameTag(s+ "("+mob.getHealth()+")");
                        entity.setAlwaysRenderNameTag(true);
                    }
                }
            }
        }
    }
}
