package com.errantflux.heirloom;

import com.errantflux.heirloom.commands.*;
import com.errantflux.heirloom.files.librarydata;
import com.errantflux.heirloom.util.log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.Objects;

public final class Heirloom extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic
        log.info("Heirloom Start");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        librarydata.setupDatafile();
        getCommand("heirloom").setTabCompleter(new tabCompleter());
        getCommand("hl").setTabCompleter(new tabCompleter());
        getCommand("library").setExecutor(new library());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Heirloom Stop");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //https://www.youtube.com/watch?v=NubWqnpNZ98&list=PLfu_Bpi_zcDNEKmR82hnbv9UxQ16nUBF7&index=8&ab_channel=KodySimpson
        // /heirloom - about heirloom

        if(command.getName().equalsIgnoreCase("heirloom") || command.getName().equalsIgnoreCase("hl")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if(args.length > 0) {
                    if (Objects.equals(args[0], "view")){
                        //View Command
                        Boolean view = cmdView.command(sender, command, label, args);

                    } else if(Objects.equals(args[0], "store")){
                        //Store Command
                        Boolean store = cmdStore.command(sender, command, label, args);

                    } else if(Objects.equals(args[0], "recall")){
                        //Recall Command
                        Boolean recall = cmdRecall.command(sender, command, label, args);

                    } else if(Objects.equals(args[0], "inspect")){
                        //Recall Command
                        Boolean inspect = cmdInspect.command(sender, command, label, args);

                    } else if(Objects.equals(args[0], "info")){

                        p.sendMessage("Heirloom is a multidimensional utility plugin.");
                        p.sendMessage("Heirloom allows you to store and recall up to 3 objects, this is preformed through the store and recall commands.");
                        p.sendMessage("Storage costs nothing, however recall costs 10 Levels. You can view what is in your heirloom slots using the view command.");
                    } else if(Objects.equals(args[0], "changelog")){

                        p.sendMessage("Heirloom 1.1 Changelog:");
                        p.sendMessage("+ Implemented Proper Logging");
                        p.sendMessage("+ Added Changelog");
                        p.sendMessage("+ Added Datafile Metadata");
                        p.sendMessage("+ Added Datafile Updater");
                        p.sendMessage("+ Added Lore to the Item Inspector");
                        p.sendMessage("+ Added Data to the Entity Inspector");
                        p.sendMessage("+ Added /hl shorthand");
                        p.sendMessage("+ Added Admin Direct Logger");
                        p.sendMessage("- Removed ability to store invulnerable entities");
                        p.sendMessage("~ Fixed Fox Null Owner Error");
                    }
                } else {
                    p.sendMessage("Usage: /heirloom <store> <entity/item> or /heirloom <recall> <slotNum>");
                    p.sendMessage("For more info use /heirloom info and to view your slots use /heirloom view");
                }
            } else if( sender instanceof ConsoleCommandSender){
                System.out.println("Only a player can run this command!");
            } else if (sender instanceof BlockCommandSender){
                System.out.println("Only a player can run this command!");
            }
        }
        //if(command.getName().equalsIgnoreCase("hl")){
        //    log.info(sender.getName() + ": " + "hl");
        //}
        if(command.getName().equalsIgnoreCase("log") ){ //&& sender.hasPermission("heirloom.log")
            if(args.length > 0) {
                if (Objects.equals(args[0], "info")){
                    log.info(sender.getName() + ": " + args[1]);
                }
                if (Objects.equals(args[0], "warn")){
                    log.warn(sender.getName() + ": " + args[1]);
                }
                if (Objects.equals(args[0], "err")){
                    log.err(sender.getName() + ": " + args[1]);
                }
            }
        }
        return true;
    }
}
