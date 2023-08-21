package com.errantflux.heirloom.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class librarydata {

    private static File file;
    private static FileConfiguration customFile;

    public static void setupDatafile(){
        //Create datafile name
        String fileName = "library.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Make datafile if none exist.
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                return;
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.addDefault("meta", "Library Datafile");
        customFile.addDefault("publicBooks", null);
        customFile.addDefault("publicBooks.count", 0);
        customFile.addDefault("privateBooks", null);
        customFile.addDefault("privateBooks.count", 0);

        customFile.options().copyDefaults(true);

        try{
            customFile.save(file);
        }catch (IOException e){
            return;
        }

        return;

    }

    public static boolean checkForDatafile(){
        //Create datafile name
        String fileName = "library.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Check for file
        if(file.exists()){
            return true;
        } else{
            return false;
        }


    }
    public static FileConfiguration libraryDatafile(){
        //Create datafile name
        String fileName = "library.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Make datafile if none exist.
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //oof
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);

        return customFile;
    }

    public static boolean saveDatafile(){
        //Create datafile name
        String fileName = "library.yml";

        //Specify datafile
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Heirloom").getDataFolder(), fileName);

        //Save File
        try{
            customFile.save(file);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public static boolean addPublicBook(ItemStack book, UUID uuid, String topicTag){
        // book1
        //      -book
        //      -uploader
        //      -checkOuts
        //      -singleStatus
        //      -archived
        //      -tag
        customFile.set("publicBooks.count", ((Integer) customFile.get("publicBooks.count")) + 1 );
        String count = customFile.get("publicBooks.count").toString();
        customFile.set("publicBooks.book" + count + ".book", serialize.encodeItem(book));
        customFile.set("publicBooks.book" + count + ".uploader", uuid.toString());
        customFile.set("publicBooks.book" + count + ".checkOuts", 0);
        customFile.set("publicBooks.book" + count + ".single", false);
        customFile.set("publicBooks.book" + count + ".archived", false);
        customFile.set("publicBooks.book" + count + ".topicTag", topicTag);
        return true;
    }

    public static boolean addPrivateBook(ItemStack book, UUID uuid){

        // uuid
        //      -books
        //        -count
        //        -book
        //      -hist
        String id = uuid.toString();
        if (customFile.get("privateBooks." + id + ".books.count") == null){
            customFile.set("privateBooks." + id + ".books.count", 0);
        }

        customFile.set("privateBooks." + id + ".books.count", ((Integer) customFile.get("privateBooks." + id + ".books.count")) + 1 );
        String count = customFile.get("privateBooks." + id + ".books.count").toString();
        customFile.set("privateBooks." + id + ".books.book" + count, serialize.encodeItem(book));
        return true;
    }

}
