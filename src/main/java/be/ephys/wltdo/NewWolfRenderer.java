package be.ephys.wltdo;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class NewWolfRenderer extends RenderWolf {

  public NewWolfRenderer(RenderManager p_i47187_1_) {
    super(p_i47187_1_);
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityWolf entity) {

    int textureId = WolfSkin.getWolfSkinId(entity);
    ResourceLocation[] textures = WolfSkin.getTextures(textureId);

    if (entity.isTamed()) {
      return textures[1];
    } else {
      return entity.isAngry() ? textures[2] : textures[0];
    }
  }
}
