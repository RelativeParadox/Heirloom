package com.errantflux.heirloom.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class log {

    public static Logger logger = Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getLogger();

    private static File file;
    private static FileConfiguration customFile;
    public static FileConfiguration hllog = logfile();

    public static Logger getLogger(){
        return logger;
    }

    public static FileConfiguration logfile(){
        //Create datafile name
        String fileName = "log.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Make datafile if none exist.
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                System.out.println("Failed to Create New Config File, PlayerID:[Logsys]");
            }
        }
        //Load config and set metadata
        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.set("Meta.Name", "Logsys");
        customFile.set("Meta.Version", 1);

        return customFile;
    }

    public static boolean savelogfile(FileConfiguration datafile){
        //Create datafile name
        String fileName = "log.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Save File
        try{
            datafile.save(file);
        }catch (IOException e){
            System.out.println("Failed to Save New Config File, PlayerID:[Logsys]");
            return false;
        }

        return true;
    }

    public static void info(String string){
        logger.info(string);
        hllog.set(java.time.LocalDate.now().toString() + "." + java.time.LocalTime.now().toString(), "[Info]: " + string);
        savelogfile(hllog);
    }

    public static void warn(String string){
        logger.warning(string);
        hllog.set(java.time.LocalDate.now().toString() + "." + java.time.LocalTime.now().toString(), "[Warning]: " + string);
        savelogfile(hllog);
    }

    public static void err(String string){
        logger.severe(string);
        hllog.set(java.time.LocalDate.now().toString() + "." + java.time.LocalTime.now().toString(), "[Error]: " + string);
        savelogfile(hllog);
    }
}
