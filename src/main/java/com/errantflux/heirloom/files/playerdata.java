package com.errantflux.heirloom.files;

import com.errantflux.heirloom.Heirloom;
import com.errantflux.heirloom.util.log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class playerdata {

    private static File file;
    private static FileConfiguration customFile;

    public static boolean setupDatafile(UUID uuid){
        //Create datafile name
        String fileName = uuid + ".yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Make datafile if none exist.
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                log.warn("Failed to Create New Config File, PlayerID:[" + uuid + "]");
                return false;
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.addDefault("Meta", null);
        customFile.addDefault("Slot1.type", "");
        customFile.addDefault("Slot1.obj", "");
        customFile.addDefault("Slot1.name", "");
        customFile.addDefault("Slot2.type", "");
        customFile.addDefault("Slot2.obj", "");
        customFile.addDefault("Slot2.name", "");
        customFile.addDefault("Slot3.type", "");
        customFile.addDefault("Slot3.obj", "");
        customFile.addDefault("Slot3.name", "");
        customFile.addDefault("History", null);
        customFile.addDefault("Backup", null);
        customFile.options().copyDefaults(true);
        customFile.set("Meta.Name", Bukkit.getPlayer(uuid).getName());
        customFile.set("Meta.Version", 1);

        try{
            customFile.save(file);
        }catch (IOException e){
            log.warn("Failed to Save Config File, PlayerID:[" + uuid + "]");
            return false;
        }

        return true;

    }
    public static boolean checkForDatafile(UUID uuid){
        //Create datafile name
        String fileName = uuid + ".yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Check for file
        if(file.exists()){
            return true;
        } else{
            return false;
        }


    }
    public static FileConfiguration playerDatafile(UUID uuid){
        //Create datafile name
        String fileName = uuid + ".yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Make datafile if none exist.
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                log.warn("Failed to Create New Config File, PlayerID:[" + uuid + "]");
            }
        }
        //Load config and set metadata
        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.set("Meta.Name", Bukkit.getPlayer(uuid).getName());
        customFile.set("Meta.Version", 1);

        return customFile;
    }

    public static boolean saveDatafile(UUID uuid, FileConfiguration datafile){
        //Create datafile name
        String fileName = uuid + ".yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Save File
        try{
            datafile.save(file);
        }catch (IOException e){
            log.warn("Failed to Save New Config File, PlayerID:[" + uuid + "]");
            return false;
        }

        return true;
    }

    public static boolean updateDatafile(UUID uuid, FileConfiguration datafile) {
        //Create datafile name
        String fileName = uuid + ".yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Check for file
        if(file.exists()){
            //If true repair file

            //Check for old file structure
            if(datafile.get("hcount") == null){
                return false;
            }

            //Load history count
            Integer hcount = (Integer) datafile.get("hcount");
            //Set history count in new location
            datafile.set("History.count", hcount);

            //Iterate over old history entries
            for(int i = hcount; i > 0; i--){
                String si = String.valueOf(i - 1);
                if(datafile.get("hist" + si) == null) {
                    continue;
                }
                //Get history data
                String obj = (String) datafile.get("hist" + si + ".obj");
                String type = (String) datafile.get("hist" + si + ".type");
                String name = (String) datafile.get("hist" + si + ".name");
                //Set history data
                datafile.set("History.h" + si + ".obj", obj);
                datafile.set("History.h" + si + ".type", type);
                datafile.set("History.h" + si + ".name", name);
                //Delete old data
                datafile.set("hist" + si + ".obj", null);
                datafile.set("hist" + si + ".type", null);
                datafile.set("hist" + si + ".name", null);

            }
            //remove hcount
            datafile.set("hcount", null);

            //Set Metadata
            datafile.set("Meta.Name", Bukkit.getPlayer(uuid).getName());
            datafile.set("Meta.Version", 1);

            log.info("Scrubbed Player Datafile, PlayerID:[" + uuid + "]");

            //Save to file
            try{
                datafile.save(file);
            }catch (IOException e){
                log.warn("Failed to Save Config File, PlayerID:[" + uuid + "]");
                return false;
            }
            return true;
        } else{
            //if false return
            return false;
        }
    }

}
