package com.errantflux.heirloom.commands;

import com.errantflux.heirloom.files.playerdata;
import com.errantflux.heirloom.files.serialize;
import com.errantflux.heirloom.util.log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


public class cmdStore{

    private static boolean getLookingAt(Player player, LivingEntity entity){
        Location eye = player.getEyeLocation();
        Vector toEntity = entity.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.99D;
    }

    private static List<Entity> getViewedEntities(Player player){
        List<Entity> entities = new ArrayList<>();
        for(Entity e : player.getNearbyEntities(10,10,10)){
            if(e instanceof LivingEntity){
                if(getLookingAt(player, (LivingEntity) e)){
                    entities.add(e);
                }
            }
        }
        return entities;
    }


    public static boolean command(CommandSender sender, Command command, String label, String[] args) {
        //Store Item Through Heirloom
            if (label.equalsIgnoreCase("heirloom") || label.equalsIgnoreCase("hl")){
                // /heirloom store item
                //Check if Player
                if (!(sender instanceof Player)){
                    // Is Console
                    return true;
                }
                //Cast sender to player
                Player player = (Player) sender;

                //Check Args
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /heirloom store <item/entity/help>");
                    return true;
                }
                if (Objects.equals(args[1], "help")) {
                    player.sendMessage(ChatColor.BLUE + "You can store entities or items using this command.");
                    player.sendMessage(ChatColor.BLUE + "For items hold it in your hand, for entities look towards their eyes.");
                    player.sendMessage(ChatColor.BLUE + "You can then type /heirloom store <entity/item>, it can be tricky for entities.");
                    return true;
                }

                //Grab Players UUID for Datafile Lookup
                UUID uuid = player.getUniqueId();

                //Check for Datafile
                if(!playerdata.checkForDatafile(uuid)){
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
                String openSlot;
                if (datafile.get("Slot1.obj") == ""){
                    openSlot = "Slot1";
                } else if (datafile.get("Slot2.obj") == ""){
                    openSlot = "Slot2";
                } else if (datafile.get("Slot3.obj") == "") {
                    openSlot = "Slot3";
                } else {
                    player.sendMessage(ChatColor.RED + "No Slots Available");
                    return true;

                }

                //player.sendMessage(args);

                //If Entity Storage
                if (Objects.equals(args[1], "entity")){
                    player.sendMessage(ChatColor.BLUE + "Looking for Entity");

                    //Grab Entity
                    List<Entity> entityList = getViewedEntities(player);

                    //No Entities
                    if (entityList.size() == 0){
                        player.sendMessage(ChatColor.RED + "No Entities found!");
                        return true;
                    }

                    //Grab The Nearest Entity
                    Entity playerEntity = entityList.get(0);
                    player.sendMessage(ChatColor.BLUE + "Entity Found: " + String.valueOf(playerEntity.getType()));

                    //Check for valid entity
                    if(String.valueOf(playerEntity.getType()).contains("TNT")){
                        player.sendMessage(ChatColor.RED + "TNT not Allowed! Boom!");
                        return true;
                    }

                    //Check for valid entity
                    if(String.valueOf(playerEntity.getType()).contains("PLAYER")){
                        player.sendMessage(ChatColor.RED + "Players cannot be stored!");
                        return true;
                    }

                    //Stop armor stand, remove when add armor stand support.
                    if(String.valueOf(playerEntity.getType()).contains("ARMOR_STAND")){
                        player.sendMessage(ChatColor.RED + "Armor Stands cannot be stored!");
                        return true;
                    }

                    //Stop Invulnerable Entities from being stored
                    if(playerEntity.isInvulnerable()){
                        player.sendMessage(ChatColor.RED + "Invulnerable Entities cannot be stored!");
                        return true;
                    }

                    //Serialize entity
                    String encodedEntity = serialize.serializeEntity(playerEntity);

                    //player.sendMessage("Entity: " + encodedEntity);

                    //Write Entity to Slot
                    datafile.set(openSlot + ".obj", encodedEntity);
                    datafile.set(openSlot + ".type", "entity");
                    datafile.set(openSlot + ".name", playerEntity.getType().toString());

                    //Check for completeness
                    if (datafile.get(openSlot + ".obj") != encodedEntity){
                        player.sendMessage(ChatColor.RED + "Data Set Error");
                    }

                    //Display Saved Message
                    player.sendMessage(ChatColor.BLUE + "Stored: " + String.valueOf(playerEntity.getType()) + " in " + openSlot);
                    log.info(player.getDisplayName() + " stored " + String.valueOf(playerEntity.getType()) + " in " + openSlot);

                    //Save Datafile
                    boolean saved = playerdata.saveDatafile(uuid, datafile);
                    //player.sendMessage("Saved Item? " + saved);
                    if (saved){
                        playerEntity.remove();
                    } else {
                        //Delete Item
                        player.sendMessage(ChatColor.RED + "Save Error! Attempting Recovery!");
                        return true;
                    }

                } else {
                    //If Item Storage

                    //Check for item in hand
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                        // Not Holding Anything
                        player.sendMessage(ChatColor.RED + "No Item in Main Hand");
                        return true;
                    }

                    //Grab Itemstack
                    ItemStack playerItem = player.getInventory().getItemInMainHand();

                    //Check for legal item
                    String itemMat = String.valueOf(playerItem.getType());

                    if (itemMat.contains("SHULKER_BOX")){
                        player.sendMessage(ChatColor.RED + "Shulker Boxes Not Allowed");
                        return true;
                    }
                    //Check for legal stacksize
                    if (playerItem.getAmount() > 1){
                        player.sendMessage(ChatColor.RED + "Only One Item Allowed (Stacksize is over 1)");
                        return true;
                    }

                    //Grab Item Name
                    String itemName = "";
                    if (Objects.requireNonNull(playerItem.getItemMeta()).hasDisplayName()){
                        itemName = playerItem.getItemMeta().getDisplayName();
                    }

                    //If Pass, Add Current Server Meta

                    //Serialize Itemstack
                    String encodedItem = "";
                    try{
                        ByteArrayOutputStream io = new ByteArrayOutputStream();
                        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                        os.writeObject(playerItem);
                        os.flush();
                        byte[] serializedItem = io.toByteArray();

                        encodedItem = Base64.getEncoder().encodeToString(serializedItem);
                    } catch (IOException e){
                        player.sendMessage(ChatColor.RED + "Item Encode Error");
                        return true;
                    }

                    //Debug Output
                    //player.sendMessage("[Debug] Item: " + itemName + "; " + encodedItem);

                    //Write Item to Slot
                    datafile.set(openSlot + ".obj", encodedItem);
                    datafile.set(openSlot + ".type", "item");
                    datafile.set(openSlot + ".name", playerItem.getType().toString());

                    //Check for completeness
                    if (datafile.get(openSlot + ".obj") != encodedItem){
                        player.sendMessage(ChatColor.RED + "Data Set Error");
                    }

                    //Display Saved Message
                    String itemType = String.valueOf(player.getInventory().getItemInMainHand().getType());
                    player.sendMessage(ChatColor.BLUE + "Stored: " + itemType + " in " + openSlot);
                    log.info(player.getDisplayName() + " stored " + itemType + " in " + openSlot);

                    //Save Datafile
                    boolean saved = playerdata.saveDatafile(uuid, datafile);
                    //player.sendMessage("Saved Item? " + saved);
                    if (saved){
                        player.getInventory().setItemInMainHand(null);
                    } else {
                        //Delete Item
                        player.sendMessage(ChatColor.RED + "Save Error! Attempting Recovery!");
                        return true;
                    }
                }
            }
        return true;
    }
}
