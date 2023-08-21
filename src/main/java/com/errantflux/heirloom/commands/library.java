package com.errantflux.heirloom.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.errantflux.heirloom.files.librarydata;

import java.util.UUID;

public class library implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Notes:
        // /library - Browse the public library
        // /library <topicTag> - Browse all books with tag
        // /library manage - Manage your uploaded books
        // /library upload <public/private>
        if (label.equalsIgnoreCase("library")){
            if (sender instanceof ConsoleCommandSender){
                sender.sendMessage(ChatColor.RED + "Players Only");
                return true;
            }
            if (sender instanceof BlockCommandSender) {
                sender.sendMessage(ChatColor.RED + "Players Only");
                return true;
            }

            Player player = (Player) sender;

            if (args.length > 0){
                if (args[0].equalsIgnoreCase("manage")){
                    //Display Manage GUI
                    player.sendMessage(ChatColor.RED + "Manage not yet online");

                } else if (args[0].equalsIgnoreCase("upload")){

                    //Check for item in hand
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                        // Not Holding Anything
                        player.sendMessage(ChatColor.RED + "No Item in Main Hand");
                        return true;
                    }

                    //Grab Itemstack
                    ItemStack playerItem = player.getInventory().getItemInMainHand();

                    //Get UUID
                    UUID uuid = player.getUniqueId();

                    //Check for legal item
                    String itemMat = String.valueOf(playerItem.getType());

                    if (!itemMat.contains("BOOK")){
                        player.sendMessage(ChatColor.RED + "Only Books Allowed");
                        return true;
                    }
                    //Check for legal stacksize
                    if (playerItem.getAmount() > 1){
                        player.sendMessage(ChatColor.RED + "Only One Item Allowed (Stacksize is over 1)");
                        return true;
                    }

                    if (!(args.length > 1)){
                        player.sendMessage( ChatColor.RED + "Usage: /library upload <private/public>");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("private")){
                        player.sendMessage( ChatColor.BLUE + "Uploading Book");
                        //Store
                        if (librarydata.checkForDatafile()){
                            boolean saved = librarydata.addPrivateBook(playerItem, uuid);
                            if (saved){
                                player.sendMessage( ChatColor.GREEN + "Book Uploaded");
                                player.getInventory().setItemInMainHand(null);
                            }
                            librarydata.saveDatafile();
                        } else {
                            player.sendMessage( ChatColor.RED + "No Datafile Found");
                        }
                        return true;

                    }else if (args[1].equalsIgnoreCase("public")) {
                        player.sendMessage( ChatColor.BLUE + "Uploading Book");
                        //Store
                        if (librarydata.checkForDatafile()){
                            if (args.length > 2) {
                                librarydata.addPublicBook(playerItem, uuid, args[2].toLowerCase());
                                player.sendMessage( ChatColor.GREEN + "Book Uploaded with tag: "+ args[2].toLowerCase());
                            } else {
                                librarydata.addPublicBook(playerItem, uuid, "");
                                player.sendMessage( ChatColor.GREEN + "Book Uploaded with no Tag");
                            }
                            librarydata.saveDatafile();
                        } else {
                            player.sendMessage( ChatColor.RED + "No Datafile Found");
                        }
                        return true;
                    }
                } else {
                    //Look up to see if the term is a tag,
                    //if so provide all books with that tag
                    // if not provide all books that have a title containing the search term.
                    player.sendMessage(ChatColor.RED + "Tag and Search not yet online");
                    return true;
                }
                //something else?
            } else {
                // /library command
                player.sendMessage(ChatColor.RED + "Library Browsing not yet online");
                return true;
            }
        }

        return true;
    }


}
