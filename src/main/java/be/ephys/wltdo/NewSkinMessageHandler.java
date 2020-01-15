package be.ephys.wltdo;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class NewSkinMessageHandler implements IMessageHandler<NewSkinMessageHandler.NewSkinMessage, IMessage> {

  private static final Map<Integer, NewSkinMessageHandler.NewSkinMessage> pendingMessages = new HashMap<>();

  @SubscribeEvent()
  public static void onWolfJoin(EntityJoinWorldEvent joinEvent) {
    // client-side only
    // process messages for entities that were not loaded when the message was received

    if (!joinEvent.getWorld().isRemote) {
      return;
    }

    Entity entity = joinEvent.getEntity();
    int entityId = entity.getEntityId();

    if (!pendingMessages.containsKey(entityId)) {
      return;
    }

    NewSkinMessageHandler.NewSkinMessage message = pendingMessages.remove(entityId);
    processMessage(message, entity);
  }

  private static void processMessage(NewSkinMessageHandler.NewSkinMessage message, Entity entity) {
    if (!(entity instanceof EntityWolf)) {
      return;
    }

    WolfSkin.setWolfSkinId((EntityWolf) entity, message.getEntitySkin());
  }

  @Override
  public IMessage onMessage(NewSkinMessageHandler.NewSkinMessage message, MessageContext ctx) {

    if (!ctx.side.equals(Side.CLIENT)) {
      return null;
    }

    int entityId = message.getEntityId();
    Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityId());

    // the entity has not loaded on the client-side yet
    // store the message for processing once the entity has loaded (#onWolfJoin)
    if (entity == null) {
      pendingMessages.put(entityId, message);
    } else {
      processMessage(message, entity);
    }

    return null;
  }

  public static class NewSkinMessage implements IMessage {

    private int entityId;
    private int entitySkin;

    // receiver constructor
    public NewSkinMessage() {}

    // sender constructor
    public NewSkinMessage(int entityId, int entitySkin) {
      this.entityId = entityId;
      this.entitySkin = entitySkin;
    }

    public int getEntityId() {
      return entityId;
    }

    public int getEntitySkin() {
      return entitySkin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
      entityId = buf.readInt();
      entitySkin = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
      buf.writeInt(entityId);
      buf.writeInt(entitySkin);
    }
  }
}
