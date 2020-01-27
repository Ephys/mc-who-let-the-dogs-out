package be.ephys.wltdo;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public final class WolfSkin {
  public static final String WOLF_SKIN_KEY = WltdoMod.MODID + ":skin";

  private static final Map<Integer, String> textures = new HashMap<>();

  private static final int[] regular = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
  private static final int[] moonmoons = new int[] { 8, 9 };

  static {
    textures.put(0, "vanilla");
    textures.put(1, "black");
    textures.put(2, "collie1");
    textures.put(3, "collie2");
    textures.put(4, "dalmatian");
    textures.put(5, "dog1");
    textures.put(6, "huskylike");
    textures.put(7, "white");

    // rare spawn
    textures.put(8, "moonmoon");
    textures.put(9, "huskymoonmoon");

    // special spawn
    textures.put(10, "ice");
    textures.put(11, "fire");

    textures.put(12, "halloween");
    textures.put(13, "rainbow");

    textures.put(16, "creeper");

    // TODO necrodermal virus
    textures.put(14, "zombie");
    // TODO Nullodermal Virus
    textures.put(15, "skelly");
  }

  private static final Map<Integer, ResourceLocation[]> textureCache = new HashMap<>();

  static {
    // vanilla textures
    textureCache.put(0, new ResourceLocation[] {
      new ResourceLocation("textures/entity/wolf/wolf.png"),
      new ResourceLocation("textures/entity/wolf/wolf_tame.png"),
      new ResourceLocation("textures/entity/wolf/wolf_angry.png")
    });
  }

  public static ResourceLocation[] getTextures(int id) {
    if (textureCache.containsKey(id)) {
      return textureCache.get(id);
    }

    String textureName = textures.get(id);
    ResourceLocation[] textures = new ResourceLocation[] {
      new ResourceLocation(WltdoMod.MODID, "textures/entity/wolf/" + textureName + "/neutral.png"),
      new ResourceLocation(WltdoMod.MODID, "textures/entity/wolf/" + textureName + "/tame.png"),
      new ResourceLocation(WltdoMod.MODID, "textures/entity/wolf/" + textureName + "/angry.png")
    };

    textureCache.put(id, textures);

    return textures;
  }

  public static void setWolfSkinId(EntityWolf wolf, int id) {
    NBTTagCompound wolfData = wolf.getEntityData();
    wolfData.setInteger(WOLF_SKIN_KEY, id);
  }

  public static int getWolfSkinId(UUID uuid) {
    Random rand = new Random(uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits());

    if (rand.nextFloat() < 0.03) {
      return moonmoons[rand.nextInt(moonmoons.length - 1)];
    }

    return regular[rand.nextInt(regular.length - 1)];
  }

  public static int getWolfSkinId(EntityWolf wolf) {
    NBTTagCompound wolfData = wolf.getEntityData();

    if (wolfData.hasKey(WolfSkin.WOLF_SKIN_KEY)) {
      return wolfData.getInteger(WolfSkin.WOLF_SKIN_KEY);
    } else {
      return WolfSkin.getWolfSkinId(wolf.getUniqueID());
    }
  }

  public static boolean hasPermanentSkin(EntityWolf wolf) {
    return wolf.getEntityData().hasKey(WolfSkin.WOLF_SKIN_KEY);
  }
}
