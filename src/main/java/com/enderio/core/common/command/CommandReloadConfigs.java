package com.enderio.core.common.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import com.enderio.core.EnderCore;
import com.enderio.core.common.event.ConfigFileChangedEvent;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;

public class CommandReloadConfigs extends CommandBase {
  
  public static final CommandReloadConfigs SERVER = new CommandReloadConfigs(Side.SERVER);
  public static final CommandReloadConfigs CLIENT = new CommandReloadConfigs(Side.CLIENT);

  private static List<String> validModIDs = new ArrayList<String>();

  private final Side side;

  static {
    EnderCore.logger.info("Sending dummy event to all mods");

    for (ModContainer mod : Loader.instance().getActiveModList()) {
      ConfigFileChangedEvent event = new ConfigFileChangedEvent(mod.getModId());      
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isSuccessful()) {
        validModIDs.add(mod.getModId());
      }
    }
  }
  
  private CommandReloadConfigs(Side side) {
    this.side = side;
  }

  @Override
  public String getCommandName() {
    return side == Side.SERVER ? "reloadServerConfigs" : "reloadConfigs";
  }

  @Override
  public String getCommandUsage(ICommandSender p_71518_1_) {
    return "/" + getCommandName() + " <modid> (<modid2> <modid3> ...)";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
    if (args.length >= 1) {
      String[] avail = validModIDs.toArray(new String[validModIDs.size()]);

      for (int i = 0; i < args.length - 1; i++) {
        avail = ArrayUtils.removeElement(avail, args[i]);
      }

      return getListOfStringsMatchingLastWord(args, avail);
    }

    return null;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.getEntityWorld().isRemote || super.checkPermission(server, sender);
  }
  
  
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (side == Side.CLIENT == sender.getEntityWorld().isRemote)
      for (String s : args) {
        boolean validModid = false;
        for (ModContainer mod : Loader.instance().getModObjectList().keySet()) {
          if (mod.getModId().equals(s)) {
            validModid = true;
          }
        }

        if (validModid) {
          ConfigFileChangedEvent event = new ConfigFileChangedEvent(s);
          MinecraftForge.EVENT_BUS.post(event);

          if (event.isSuccessful()) {
            sendResult(sender, s, "success");
          } else {
            sendResult(sender, s, "fail");
          }
        } else {
          sendResult(sender, s, "invalid");
        }
      }
  }

  private void sendResult(ICommandSender player, String modid, String result) {
    player.addChatMessage(new TextComponentString(EnderCore.lang.localize("command.config.result." + result, modid)));
  }

}
