package com.errantflux.heirloom.commands;

import com.errantflux.heirloom.files.playerdata;
import com.errantflux.heirloom.files.serialize;
import com.errantflux.heirloom.util.log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.errantflux.heirloom.files.serialize.serializeEntity;

public class cmdInspect {
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

    public static boolean command(CommandSender sender, Command command, String label, String[] args){
        if (sender.hasPermission("heirloom.inspect")){
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

            if (args.length > 1){
                //Inspect Stored Item
                String slotNum = args[1];
                String openSlot = "Slot" + slotNum;

                //Check for object in specified slot
                if (datafile.get(openSlot + ".obj") == ""){
                    player.sendMessage(ChatColor.RED + "Empty Slot Specified");
                    return true;
                }

                String encodedObject = String.valueOf(datafile.get(openSlot + ".obj"));
                String objectName = String.valueOf(datafile.get(openSlot + ".name"));
                String objectType = String.valueOf(datafile.get(openSlot + ".type"));

                if (Objects.equals(objectType, "entity")) {
                    byte[] byteEntity = Base64.getDecoder().decode(encodedObject);
                    String decodedEntity = new String(byteEntity, StandardCharsets.UTF_8);
                    player.sendMessage(ChatColor.UNDERLINE + objectName);
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GREEN + decodedEntity);
                } else {
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
                    if (playerItem.hasItemMeta()){
                        if (playerItem.getItemMeta().hasDisplayName()) {
                            player.sendMessage(ChatColor.UNDERLINE + playerItem.getItemMeta().getDisplayName());
                            player.sendMessage("");
                            player.sendMessage(ChatColor.GREEN + playerItem.getType().name());
                        } else {
                            player.sendMessage(ChatColor.UNDERLINE + playerItem.getType().name());
                            player.sendMessage("");
                        }
                        if (playerItem.getItemMeta().hasAttributeModifiers()){
                            player.sendMessage(ChatColor.BLUE + playerItem.getItemMeta().getAttributeModifiers().toString());
                            player.sendMessage("");
                        }
                        if (playerItem.getItemMeta().hasEnchants()){
                            player.sendMessage(ChatColor.LIGHT_PURPLE + playerItem.getItemMeta().getEnchants().toString());
                            player.sendMessage("");
                        }
                        if (playerItem.getItemMeta().hasLore()){
                            player.sendMessage(ChatColor.BLUE + playerItem.getItemMeta().getLore().toString());
                            player.sendMessage("");
                        }
                    } else {
                        player.sendMessage(ChatColor.UNDERLINE + playerItem.getType().name());
                        player.sendMessage("");
                    }
                }
                log.info(player.getDisplayName() + " Inspected an Item");
                return true;
            } else {
                //Inspect Entity
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
                    player.sendMessage(ChatColor.RED + "Players cannot be inspected!");
                    return true;
                }

                player.sendMessage(ChatColor.LIGHT_PURPLE + playerEntity.getLocation().toString());

                if (playerEntity.isInvulnerable()){
                    player.sendMessage(ChatColor.GREEN + "Entity is Invulnerable");
                } else {
                    player.sendMessage(ChatColor.RED + "Entity is Not Invulnerable");
                }
                playerEntity.isPersistent();
                if(playerEntity.getLastDamageCause() != null){
                    player.sendMessage(ChatColor.GOLD + playerEntity.getLastDamageCause().toString());
                }


                int cooldown = playerEntity.getPortalCooldown();
                player.sendMessage(ChatColor.LIGHT_PURPLE + Integer.toString(cooldown));

                //Serialize entity
                String encodedEntity = serialize.serializeEntity(playerEntity);
                byte[] byteEntity = Base64.getDecoder().decode(encodedEntity);
                String decodedEntity = new String(byteEntity, StandardCharsets.UTF_8);
                player.sendMessage(ChatColor.GREEN + decodedEntity);
                log.info(player.getDisplayName() + " Inspected an Entity");
                return true;
            }
        }
        return true;
    }
}
