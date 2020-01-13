package com.example.examplemod;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class NewWolfRenderer extends RenderWolf {

  private static final ResourceLocation[][] textures = new ResourceLocation[][] {
    new ResourceLocation[] {
      new ResourceLocation("textures/entity/wolf/wolf.png"),
      new ResourceLocation("textures/entity/wolf/wolf_tame.png"),
      new ResourceLocation("textures/entity/wolf/wolf_angry.png")
    },
    new ResourceLocation[] {
      new ResourceLocation(ExampleMod.MODID, "textures/entity/wolf/dog1.png"),
      new ResourceLocation(ExampleMod.MODID, "textures/entity/wolf/dog1_tame.png"),
      new ResourceLocation(ExampleMod.MODID, "textures/entity/wolf/dog1_angry.png")
    }
  };

  public NewWolfRenderer(RenderManager p_i47187_1_) {
    super(p_i47187_1_);
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityWolf entity) {

    UUID uuid = entity.getUniqueID();
    int textureCount = NewWolfRenderer.textures.length;

    int textureId = Math.abs((int) ((uuid.getLeastSignificantBits() % textureCount) + (uuid.getMostSignificantBits() % textureCount))) % textureCount;
    ResourceLocation[] textures = NewWolfRenderer.textures[textureId];

    if (entity.isTamed()) {
      return textures[1];
    } else {
      return entity.isAngry() ? textures[2] : textures[0];
    }
  }
}
