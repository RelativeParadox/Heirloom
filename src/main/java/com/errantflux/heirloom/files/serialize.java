package com.errantflux.heirloom.files;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class serialize {

    public static String serializeEntity(Entity entity) {
        String serializedEntity;
        try {
            JsonObject root = new JsonObject();

            //Nameable Data
            String type = entity.getType().name();
            root.addProperty("type", type);
            String name = entity.getCustomName();
            root.addProperty("name", name);

            if (entity instanceof Ageable){
                Integer age = ((Ageable) entity).getAge();
                root.addProperty("age", age);
                Boolean isAdult = ((Ageable) entity).isAdult();
                root.addProperty("isAdult", isAdult);
            }
            if (entity instanceof  AbstractHorse){
                Double jumpStrength = ((AbstractHorse) entity).getJumpStrength();
                root.addProperty("jumpStrength", jumpStrength);

                ItemStack saddle = ((AbstractHorse) entity).getInventory().getSaddle();
                boolean hasSaddle;
                hasSaddle = saddle != null;
                root.addProperty("hasSaddle", hasSaddle);
            }
            if (entity instanceof Attributable){
                Double maxHealth = ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                root.addProperty("maxHealth", maxHealth);
            }
            if (entity instanceof Damageable){
                Double health = ((Damageable) entity).getHealth();
                root.addProperty("health", health);
            }
            if (entity instanceof Tameable){
                Boolean tamed = ((Tameable) entity).isTamed();
                root.addProperty("tamed", tamed);
                if (tamed){
                    String owner = Objects.requireNonNull(((Tameable) entity).getOwner()).getUniqueId().toString();
                    root.addProperty("owner", owner);
                }
            }
            if (entity instanceof Sittable){
                Boolean sitting = ((Sittable) entity).isSitting();
                root.addProperty("sitting", sitting);
            }

            if (entity instanceof  Horse){
                ItemStack armor = ((Horse) entity).getInventory().getArmor();
                if(armor != null) {
                    if (armor.getType().name().contains("ARMOR")) {
                        String armorType = armor.getType().toString();
                        root.addProperty("armorType", armorType);
                    }
                }
                String color = ((Horse) entity).getColor().name();
                root.addProperty("color", color);

                String style = ((Horse) entity).getStyle().name();
                root.addProperty("style", style);
            }
            if (entity instanceof Wolf){
                String collarColor = ((Wolf) entity).getCollarColor().name();
                root.addProperty("collarColor", collarColor);
            }
            if (entity instanceof Cat) {
                String catType = ((Cat) entity).getCatType().name();
                root.addProperty("catType", catType);
                String collarColor = ((Cat) entity).getCollarColor().name();
                root.addProperty("collarColor", collarColor);
            }
            //Axolotl
            if (entity instanceof Axolotl) {
                String axolotlType = ((Axolotl) entity).getVariant().name();
                root.addProperty("axolotlType", axolotlType);
                Boolean isPlayingDead = ((Axolotl) entity).isPlayingDead();
                root.addProperty("isPlayingDead", isPlayingDead);
            }
            //Parrot
            if (entity instanceof Parrot) {
                String parrotType = ((Parrot) entity).getVariant().name();
                root.addProperty("parrotType", parrotType);
            }
            //llamas
            if(entity instanceof Llama) {
                String llamaColor = ((Llama) entity).getColor().name();
                root.addProperty("llamaColor", llamaColor);
                Integer llamaStrength = ((Llama) entity).getStrength();
                root.addProperty("llamaStrength", llamaStrength);

                ItemStack decor = ((Llama) entity).getInventory().getDecor();
                if(decor != null){
                    if(decor.getType().name().contains("CARPET")){
                        String decorType = decor.getType().toString();
                        root.addProperty("decorType", decorType);
                    }
                }
            }
            //Chested Horse
            if (entity instanceof ChestedHorse) {
                Boolean isChested = ((ChestedHorse) entity).isCarryingChest();
                root.addProperty("isChested", isChested);
            }

            if (entity instanceof Slime) {
                Integer slimeSize = ((Slime) entity).getSize() + 1;
                root.addProperty("slimeSize", slimeSize);
            }
            //Rabbit
            if (entity instanceof Rabbit) {
                String rabbitType = ((Rabbit) entity).getRabbitType().name();
                root.addProperty("rabbitType", rabbitType);
            }

            if (entity instanceof Fox) {
                String foxType = ((Fox) entity).getFoxType().name();
                root.addProperty("foxType", foxType);

                Boolean isCrouching = ((Fox) entity).isCrouching();
                root.addProperty("isCrouching", isCrouching);

                if(((Fox) entity).getFirstTrustedPlayer() != null){
                    String foxOwner = Objects.requireNonNull(((Fox) entity).getFirstTrustedPlayer().getUniqueId().toString());
                    root.addProperty("foxOwner", foxOwner);
                }

                if(((Fox) entity).getSecondTrustedPlayer() != null) {
                    String foxCoOwner = Objects.requireNonNull(((Fox) entity).getSecondTrustedPlayer().getUniqueId().toString());
                    root.addProperty("foxCoOwner", foxCoOwner);
                }


            }
            //Villager

            //Tropical Fish

            //armor stand (Do later)

            //Dump Entity as string
            serializedEntity = root.toString();
            //System.out.println(serializedEntity);

            //Encode Entity
            byte[] byteEntity = serializedEntity.getBytes(StandardCharsets.UTF_8);
            String encodedEntity = Base64.getEncoder().encodeToString(byteEntity);
            return encodedEntity;

        } catch(JsonIOException e){
            return "";
        }
    }

    public static Entity deserializeEntity(World world, Location location, String encodedEntity) {
        //Decode Entity
        byte[] byteEntity = Base64.getDecoder().decode(encodedEntity);
        String decodedEntity = new String(byteEntity, StandardCharsets.UTF_8);
        //System.out.println(decodedEntity);

        //Parse JSON
        JsonObject root = new JsonParser().parse(decodedEntity).getAsJsonObject();
        
        //Set Type of Entity to Recall
        EntityType recallType = EntityType.valueOf(root.get("type").toString().replace("\"", ""));


        //Spawn Empty Entity
        Entity recalledEntity = world.spawnEntity(location, recallType);

        if (!root.get("name").isJsonNull()){
            recalledEntity.setCustomName(root.get("name").toString().replace("\"", ""));
            recalledEntity.setCustomNameVisible(true);
        }
        //Set Age
        if (root.has("age")){
            ((Ageable) recalledEntity).setAge(root.get("age").getAsInt());
        }
        //Set Adult/Baby
        if (root.has("isAdult")){
            if (root.get("isAdult").getAsBoolean()){
                ((Ageable) recalledEntity).setAdult();
            } else {
                ((Ageable) recalledEntity).setBaby();
            }

        }
        if (root.has("jumpStrength")){
            ((AbstractHorse) recalledEntity).setJumpStrength(root.get("jumpStrength").getAsDouble());
        }
        if (root.has("maxHealth")){
            ((Attributable) recalledEntity).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(root.get("maxHealth").getAsDouble());
        }
        if (root.has("health")){
            ((Damageable) recalledEntity).setHealth(root.get("health").getAsDouble());
        }
        if (root.has("tamed")) {
            ((Tameable) recalledEntity).setTamed(root.get("tamed").getAsBoolean());
        }
        if (root.has("owner")){
            UUID ownerID = UUID.fromString(root.get("owner").toString().replace("\"", ""));
            AnimalTamer owner = Bukkit.getPlayer(ownerID);
            ((Tameable) recalledEntity).setOwner(owner);
        }
        if (root.has("hasSaddle")){
            if (root.get("hasSaddle").getAsBoolean()){
                ItemStack saddle = new ItemStack(Material.SADDLE);
                saddle.setAmount(1);
                ((AbstractHorse) recalledEntity).getInventory().setSaddle(saddle);
            }
        }
        if (root.has("color")){
            Horse.Color color = Horse.Color.valueOf(root.get("color").getAsString().replace("\"", ""));
            ((Horse) recalledEntity).setColor(color);
        }
        if (root.has("style")){
            Horse.Style style = Horse.Style.valueOf(root.get("style").getAsString().replace("\"", ""));
            ((Horse) recalledEntity).setStyle(style);
        }

        if (root.has("armorType")) {
            ItemStack armor = new ItemStack(Material.valueOf(root.get("armorType").getAsString().replace("\"","")));
            ((Horse) recalledEntity).getInventory().setArmor(armor);
        }

        if (root.has("collarColor")){
            DyeColor collarColor = DyeColor.valueOf(root.get("collarColor").getAsString().replace("\"", ""));
            if (recallType == EntityType.CAT){
                ((Cat) recalledEntity).setCollarColor(collarColor);
            }
            if (recallType == EntityType.WOLF){
                ((Wolf) recalledEntity).setCollarColor(collarColor);
            }
        }
        if (root.has("catType")){
            Cat.Type catType = Cat.Type.valueOf(root.get("catType").getAsString().replace("\"", ""));
            ((Cat) recalledEntity).setCatType(catType);
        }
        if (root.has("sitting")){
            ((Sittable) recalledEntity).setSitting(root.get("sitting").getAsBoolean());
        }
        if (root.has("axolotlType")){
            Axolotl.Variant axolotlType = Axolotl.Variant.valueOf(root.get("axolotlType").getAsString().replace("\"",""));
            ((Axolotl) recalledEntity).setVariant(axolotlType);
        }
        if (root.has("isPlayingDead")){
            ((Axolotl) recalledEntity).setPlayingDead(root.get("isPlayingDead").getAsBoolean());
        }
        if (root.has("parrotType")){
            Parrot.Variant parrotType = Parrot.Variant.valueOf(root.get("parrotType").getAsString().replace("\"",""));
            ((Parrot) recalledEntity).setVariant(parrotType);
        }
        if (root.has("llamaColor")){
            Llama.Color llamaColor = Llama.Color.valueOf(root.get("llamaColor").getAsString().replace("\"", ""));
            ((Llama) recalledEntity).setColor(llamaColor);
        }
        if (root.has("llamaStrength")) {
            ((Llama) recalledEntity).setStrength(root.get("llamaStrength").getAsInt());
        }
        if (root.has("decorType")) {
            ItemStack decor = new ItemStack(Material.valueOf(root.get("decorType").getAsString().replace("\"","")));
            ((Llama) recalledEntity).getInventory().setDecor(decor);
        }
        if (root.has("isChested")){
            ((ChestedHorse) recalledEntity).setCarryingChest(root.get("isChested").getAsBoolean());
        }
        if (root.has("slimeSize")){
            ((Slime) recalledEntity).setSize(root.get("slimeSize").getAsInt());
        }
        if (root.has("rabbitType")){
            Rabbit.Type rabbitType = Rabbit.Type.valueOf(root.get("rabbitType").getAsString().replace("\"",""));
            ((Rabbit) recalledEntity).setRabbitType(rabbitType);
        }
        if (root.has("foxType")){
            Fox.Type foxType = Fox.Type.valueOf(root.get("foxType").getAsString().replace("\"",""));
        }
        if (root.has("isCrouching")) {
            ((Fox) recalledEntity).setCrouching(root.get("isCrouching").getAsBoolean());
        }
        if (root.has("foxOwner")) {
            UUID foxOwnerID = UUID.fromString(root.get("foxOwner").toString().replace("\"", ""));
            AnimalTamer foxOwner = Bukkit.getPlayer(foxOwnerID);
            ((Fox) recalledEntity).setFirstTrustedPlayer(foxOwner);
        }
        if (root.has("foxCoOwner")) {
            UUID foxCoOwnerID = UUID.fromString(root.get("foxCoOwner").toString().replace("\"", ""));
            AnimalTamer foxCoOwner = Bukkit.getPlayer(foxCoOwnerID);
            ((Fox) recalledEntity).setSecondTrustedPlayer(foxCoOwner);
        }

        return recalledEntity;
    }

    public static String encodeItem(ItemStack playerItem){
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
            return "";
        }
        return encodedItem;
    }

    public static ItemStack decodeItem(String encodedItem){
        //Deserialize Itemstack
        ItemStack playerItem;
        try {
            byte[] serializedItem;
            serializedItem = Base64.getDecoder().decode(encodedItem);
            ByteArrayInputStream in = new ByteArrayInputStream(serializedItem);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);
            playerItem = (ItemStack) is.readObject();


        } catch (IOException | ClassNotFoundException e) {
            return new ItemStack(Material.AIR);
        }
        return playerItem;
    }
}
