package be.ephys.wltdo;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
  @Override
  public void preInit() {
    super.preInit();

    RenderingRegistry.registerEntityRenderingHandler(EntityWolf.class, NewWolfRenderer::new);
  }
}
