package com.errantflux.heirloom.commands;

import com.errantflux.heirloom.files.playerdata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class cmdView {

    public static boolean command(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("heirloom") || label.equalsIgnoreCase("hl")) {
            // /heirloom store item
            //Check if Player
            if (!(sender instanceof Player)) {
                // Is Console
                return true;
            }

            //Cast sender to player
            Player player = (Player) sender;

            UUID uuid = player.getUniqueId();

            //Grab Players UUID for Datafile Lookup
            if (args.length > 1) {
                String lookupPlayer = args[1];
                if (!(Bukkit.getPlayer(lookupPlayer) == null)){
                    uuid = Bukkit.getPlayer(lookupPlayer).getUniqueId();
                } else {
                    player.sendMessage(ChatColor.RED + "Player Not Found");
                    return true;
                    //In the future we can try meta.name searches.
                }
            }



            //Check for Datafile
            if (!playerdata.checkForDatafile(uuid)) {
                //If None, Notify
                player.sendMessage(ChatColor.RED + "Datafile Not Found");
            }
            //Grab datafile
            FileConfiguration datafile = playerdata.playerDatafile(uuid);
            //repair datafile
            if(playerdata.updateDatafile(uuid, datafile)){
                //Log that it updated
                player.sendMessage(ChatColor.GREEN + "Datafile Updated to newest version");
            }

            //Check Heirloom Slots
            player.sendMessage("--- " + ChatColor.LIGHT_PURPLE + "Heirloom Slots" + ChatColor.RESET + "---");
            if (datafile.get("Slot1.obj") != "") {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 1:" + ChatColor.BLUE + "        " + datafile.get("Slot1.type").toString().toUpperCase());
                player.sendMessage("        " + ChatColor.BLUE + "        " + datafile.get("Slot1.name").toString());
            } else {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 1:" + ChatColor.RED + "        SLOT EMPTY");
                player.sendMessage("");
            }
            if (datafile.get("Slot2.obj") != "") {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 2:" +ChatColor.GREEN + "        " + datafile.get("Slot2.type").toString().toUpperCase());
                player.sendMessage("        " + ChatColor.GREEN + "        " + datafile.get("Slot2.name").toString());
            } else {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 2:" + ChatColor.RED + "        SLOT EMPTY");
                player.sendMessage("");
            }
            if (datafile.get("Slot3.obj") != "") {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 3:" + ChatColor.YELLOW + "        " + datafile.get("Slot3.type").toString().toUpperCase());
                player.sendMessage("        " + ChatColor.YELLOW + "        " + datafile.get("Slot3.name").toString());
            } else {
                player.sendMessage(ChatColor.UNDERLINE + "SLOT 3:" + ChatColor.RED + "        SLOT EMPTY");
                player.sendMessage("");
            }
            player.sendMessage("-------------------");
            boolean saved = playerdata.saveDatafile(uuid, datafile);
            return true;
        }
        return true;
    }
}
