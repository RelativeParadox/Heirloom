package com.errantflux.heirloom.commands;

import com.errantflux.heirloom.files.playerdata;
import com.errantflux.heirloom.files.serialize;
import com.errantflux.heirloom.util.log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class cmdRecall {
    public static boolean command(CommandSender sender, Command command, String label, String[] args){
        if (label.equalsIgnoreCase("heirloom") || label.equalsIgnoreCase("hl")) {
            // /heirloom recall item
            //Check if Player
            if (!(sender instanceof Player)) {
                // Is Console
                return true;
            }
            //Cast sender to player
            Player player = (Player) sender;

            //Grab Players UUID for Datafile Lookup
            UUID uuid = player.getUniqueId();

            //Check for Datafile
            if (!playerdata.checkForDatafile(uuid)) {
                //If None Make Datafile
                playerdata.setupDatafile(uuid);
            }
            //Grab datafile
            FileConfiguration datafile = playerdata.playerDatafile(uuid);
            //repair datafile
            if(playerdata.updateDatafile(uuid, datafile)){
                //Log that it updated
                player.sendMessage(ChatColor.GREEN + "Datafile Updated to newest version");
            }

            //Check Heirloom Slots
            //player.sendMessage(args);
            String openSlot;
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Slot Number Required for Recall");
                return true;
            }
            String slotArg = args[1];
            if (Objects.equals(slotArg, "1")) {
                openSlot = "Slot1";
            } else if (Objects.equals(slotArg, "2")) {
                openSlot = "Slot2";
            } else if (Objects.equals(slotArg, "3")) {
                openSlot = "Slot3";
            } else {
                player.sendMessage(ChatColor.RED + "Syntax Error: Use whole numbers only for slot number!");
                return true;
            }

            //Check for object in specified slot
            if (datafile.get(openSlot + ".obj") == ""){
                player.sendMessage(ChatColor.RED + "Empty Slot Specified");
                return true;
            }

            //Check for XP Required
            if (player.getLevel() < 10) {
                player.sendMessage(ChatColor.RED + "Not Enough Experience Levels");
                return true;
            }

            //Grab Encoded object
            String encodedObject = String.valueOf(datafile.get(openSlot + ".obj"));
            String objectName = "";
            String objectType = String.valueOf(datafile.get(openSlot + ".type"));

            //Check for obj type == entity
            if (Objects.equals(String.valueOf(datafile.get(openSlot + ".type")), "entity")){
                //If Entity

                Entity recalledEntity = serialize.deserializeEntity(player.getWorld(),player.getLocation(),encodedObject);
                //Check for completeness
                if (!recalledEntity.isValid()){
                    player.sendMessage(ChatColor.RED + "Recall Error!");
                    recalledEntity.remove();
                    return true;
                }
                objectName = recalledEntity.getType().toString();
                player.sendMessage(ChatColor.BLUE + "Recalled " + recalledEntity.getType().toString());
                log.info(player.getDisplayName() + " recalled " + recalledEntity.getType().toString());

            } else {
                //If Item
                //Check for item in hand
                if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    // Holding Something
                    player.sendMessage(ChatColor.RED + "Empty Main Hand Required for Recall");
                    return true;
                }

                //Deserialize Itemstack
                ItemStack playerItem;
                try {
                    byte[] serializedItem;
                    serializedItem = Base64.getDecoder().decode(encodedObject);
                    ByteArrayInputStream in = new ByteArrayInputStream(serializedItem);
                    BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                    playerItem = (ItemStack) is.readObject();


                } catch (IOException | ClassNotFoundException e) {
                    player.sendMessage(ChatColor.RED + "Item Decode Error");
                    return true;
                }

                //Write Item to Hand
                player.getInventory().setItemInMainHand(playerItem);

                //Check for completeness
                if (player.getInventory().getItemInMainHand().getType() != playerItem.getType()) {
                    player.sendMessage(ChatColor.RED + "Inventory Set Error");
                    player.getInventory().setItemInMainHand(null);
                    return true;
                }
                objectName = String.valueOf(playerItem.getType());
                player.sendMessage(ChatColor.BLUE + "Recalled: " + String.valueOf(playerItem.getType()));
                log.info(player.getDisplayName() + " recalled " + String.valueOf(playerItem.getType()));
            }
            //Save in History
            //Get hcount and hname
            if (datafile.get("History.count") == null){
                datafile.set("History.count", 0);
            }
            Integer hcount = (Integer) datafile.get("History.count");
            String shcount = String.valueOf(hcount);
            //Write Item to History
            datafile.set("History.h" + shcount + ".obj", encodedObject);
            datafile.set("History.h" + shcount + ".type", objectType);
            datafile.set("History.h" + shcount + ".name", objectName);

            //Iterate hcount
            datafile.set("History.count",hcount + 1);

            //Write Empty to Slot
            datafile.set(openSlot + ".obj", "");
            datafile.set(openSlot + ".type", "");
            datafile.set(openSlot + ".name", "");

            //Save Datafile
            playerdata.saveDatafile(uuid, datafile);

            //Remove Experience
            player.giveExpLevels(-10);
        }
        return true;
    }
}
