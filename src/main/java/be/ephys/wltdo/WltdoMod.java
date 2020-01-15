package be.ephys.wltdo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = WltdoMod.MODID, name = WltdoMod.NAME, version = WltdoMod.VERSION)
public class WltdoMod {
  public static final String MODID = "who-let-the-dogs-out";
  public static final String NAME = "Who Let The Dogs Out";
  public static final String VERSION = "1.0.0";

  @SidedProxy(modId = MODID, clientSide = "be.ephys.wltdo.ClientProxy", serverSide = "be.ephys.wltdo.CommonProxy")
  public static CommonProxy sidedProxy;

  public static final SimpleNetworkWrapper NETWORKER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

  private static Logger logger;
  private static Random rand = new Random();

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();

    NETWORKER.registerMessage(NewSkinMessageHandler.class, NewSkinMessageHandler.NewSkinMessage.class, 0, Side.CLIENT);
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(NewSkinMessageHandler.class);

    sidedProxy.preInit();
  }

  @SubscribeEvent
  public void onWolfSync(PlayerEvent.StartTracking trackEvent) {
    Entity entity = trackEvent.getTarget();
    if (!(entity instanceof EntityWolf)) {
      return;
    }

    EntityWolf wolf = (EntityWolf) entity;
    IMessage message = new NewSkinMessageHandler.NewSkinMessage(
      wolf.getEntityId(),
      WolfSkin.getWolfSkinId(wolf.getUniqueID())
    );

    NETWORKER.sendTo(message, (EntityPlayerMP) trackEvent.getEntityPlayer());
  }

  @SubscribeEvent
  public void onWolfConstructed(EntityEvent.EntityConstructing constructingEvent) {
    // store wolf skin on construction
    // so we can add new skins without any issue
    // and so baby wolves will inherit them

    if (constructingEvent.getEntity().world.isRemote) {
      return;
    }

    Entity entity = constructingEvent.getEntity();
    if (!(entity instanceof EntityWolf)) {
      return;
    }

    EntityWolf wolf = (EntityWolf) entity;

    if (!WolfSkin.hasPermanentSkin(wolf)) {
      WolfSkin.setWolfSkinId(wolf, WolfSkin.getWolfSkinId(wolf.getUniqueID()));
    }
  }

  private static void syncWolfSkin(EntityWolf wolf) {
    IMessage message = new NewSkinMessageHandler.NewSkinMessage(
      wolf.getEntityId(),
      WolfSkin.getWolfSkinId(wolf.getUniqueID())
    );

    NETWORKER.sendToAllTracking(message, wolf);
  }

  @SubscribeEvent
  public void onWolfBreed(BabyEntitySpawnEvent babySpawnEvent) {

    Entity child = babySpawnEvent.getChild();
    if (!(child instanceof EntityWolf)) {
      return;
    }

    if (child.world.isRemote) {
      return;
    }

    // inherit the skin of one of the parents, with a 1/100 chance of having another skin
    if (rand.nextFloat() < 0.01) {
      return; // let onWolfConstructed handle assigning the random skin
    }

    Entity parent = rand.nextBoolean()
      ? babySpawnEvent.getParentA()
      : babySpawnEvent.getParentB();

    if (!(parent instanceof EntityWolf)) {
      // sanity check, if we got the wrong entity (bred by an entity of a different mod maybe)
      // use the other parent
      parent = (parent == babySpawnEvent.getParentA()) ? babySpawnEvent.getParentB() : babySpawnEvent.getParentA();

      // if neither parent is a wolf, random skin
      if (!(parent instanceof EntityWolf)) {
        return;
      }
    }

    EntityWolf childWolf = (EntityWolf) child;
    EntityWolf parentWolf = (EntityWolf) parent;

    WolfSkin.setWolfSkinId(childWolf, WolfSkin.getWolfSkinId(parentWolf));
  }
}
