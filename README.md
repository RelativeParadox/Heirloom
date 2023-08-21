# Heirloom
A spigot plugin for sentimental storage. 

The heirloom system allows you to carry 3 sentimental items or entities with you wherever you go. The inventories of the items and entities will be cleared except for the saddle slot. 
Heirloom allows you to store and recall up to 3 objects, this is performed through the store and recall commands.
Storage costs nothing, however, recall costs 10 Levels. You can view what is in your heirloom slots using the view command.
Setting the cost to recall items to a level of 10 allows the items/entities to be slowly let into the game world, smoothing out spikes in power. In other words, it prevents players from being too powerful when first entering the world.


## Commands

/heirloom - displays info about the plugin

/heirloom help - displays the help section

/heirloom store item - stores the item in your hand
```
- determines player
- Checks for/makes playerfile
- Checks slot availability
- Checks for required xp amount. (zero for items)
- Checks for item in hand
- Checks for legal item (Ex: Shulkers not Legal)
- Checks for legal stack size (Stack of 1 is Legal)
- Deconstructs item into playerfile
- Adds current server tag/other tags
- Checks for completeness
- Deletes player's item
```

/heirloom store entity - stores the entity you are looking at
```
- determines player
- Checks for/makes playerfile
- Checks slot availability
- Checks for required xp amount. (5lvl for entities)
- Checks for entity in view
- Checks for legal entity (Ex: large hostile mobs illegal)
- Scrubs Entities Inventory
- Deconstructs entity into playerfile
- Adds current server tag/other tags
- Checks for completeness
- Deletes player's entity
```

/heirloom recall <slot> - recalls the item/entity in the designated slot
```
- determines player
- Checks for/makes playerfile
- Checks slot occupancy
- Checks for required xp amount. (10lvl for recall)
- Checks for space availability (Handslot or Ground/Space)
- Spawns blank item/entity
- Reconstructs item/entity from playerfile
- Checks for completeness
- Moves entry to history and removes from slot
```
## Recent Changelog
```
Heirloom 1.1 Changelog:
+ Implemented Proper Logging
+ Added Changelog
+ Added Datafile Metadata
+ Added Datafile Updater
+ Added Lore Text to the Item Inspector
+ Added Data to the Entity Inspector
+ Added /hl shorthand
+ Added Admin Direct Logger
- Removed the ability to store invulnerable entities
~ Fixed Fox Null Owner Error
```

## ToDo
- Update Docs
- Add a glow effect to entities
- add tutorial
- release to public
- finish library system
- build gui
- add administrative commands
- - admin graphical menu
- - such as adding admin playerfile inspection and item restoration
- Add 1.18 and 1.19 animals to the serialization engine
- Add permission nodes
- add shortened auto-choose for /hl store and /heirloom store, ex. automatically store item in hand if none try to store entity


/heirloom view - view your current heirlooms in a GUI
/heirloom edit - admin editing of player datafiles
/heirloom restore - admin command to restore player's items

/library - grants access to a server-wide virtual library
- Players can upload books for all to read, only original authors/admins can take down books
- Anyone on the server can get a copy for some experience levels.


