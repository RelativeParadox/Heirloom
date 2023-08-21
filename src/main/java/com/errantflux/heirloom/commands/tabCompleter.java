package com.errantflux.heirloom.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class tabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add(0, "store");
            commands.add(1, "recall");
            commands.add(2, "view");
            commands.add(3,"info");
            commands.add(4, "inspect");
            commands.add(4, "changelog");

            return commands;
        } else {
            if (Objects.equals(args[0], "view")){
                //View Command
                return null;
            } else if(Objects.equals(args[0], "store")){

                //Store Command
                if (args.length > 2){
                    return null;
                } else {
                    List<String> store = new ArrayList<>();
                    store.add(0, "item");
                    store.add(1, "entity");
                    store.add(2, "help");
                    return store;
                }

            } else if(Objects.equals(args[0], "recall")){

                //Recall Command
                if (args.length > 2){
                    return null;
                } else {
                    List<String> recall = new ArrayList<>();
                    recall.add(0, "1");
                    recall.add(1, "2");
                    recall.add(2, "3");
                    return recall;
                }

            } else if(Objects.equals(args[0], "inspect")){
                List<String> inspect = new ArrayList<>();
                inspect.add(0,"1");
                inspect.add(1,"2");
                inspect.add(2,"3");
                return null;
            } else if(Objects.equals(args[0], "info")){
                return null;
            }
        }
        return null;
    }
}
