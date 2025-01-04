package xyz.connorchickenway.towers.cmd;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.ConfigurationManager.ConfigName;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.builder.GameBuilder;
import xyz.connorchickenway.towers.game.builder.Team;
import xyz.connorchickenway.towers.game.builder.setup.SetupSession;
import xyz.connorchickenway.towers.game.builder.setup.wand.Wand;
import xyz.connorchickenway.towers.game.kit.Kit;
import xyz.connorchickenway.towers.game.lang.Lang;
import xyz.connorchickenway.towers.game.manager.GameManager;
import xyz.connorchickenway.towers.game.world.BukkitWorldLoader;
import xyz.connorchickenway.towers.game.world.GameWorld;
import xyz.connorchickenway.towers.game.world.SlimeWorldLoader;
import xyz.connorchickenway.towers.game.world.WorldLoader;
import xyz.connorchickenway.towers.utilities.Cuboid;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.MetadataUtils;
import xyz.connorchickenway.towers.utilities.StringUtils;

import static xyz.connorchickenway.towers.AmazingTowers.SLIME_PLUGIN;

public class SubCommandListener {

    @SubCommand(
            subcmd = "config",
            max_args = 2,
            usage = {"reload", "config,scoreboard,lang"},
            error = {"§cConfig name is incorrect.!",
                    "§7Config names: §aconfig, scoreboard, lang"},
            builder_cmd = false,
            wand_usage = false,
            can_console = true
    )
    public CommandReason configCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("reload")) {
            final ConfigName config = ConfigName.fromString(args[1]);
            if (config != null) {
                config.getConfiguration().loadConfiguration();
                if (config == ConfigName.SCOREBOARD)
                    AmazingTowers.getInstance().getScoreboardManager().load();
                else if (config == ConfigName.LANG)
                    Lang.loadMessages();
                sender.sendMessage(ChatColor.GRAY + "Config " + ChatColor.GREEN + config.getName() + ChatColor.GRAY + " has been reloaded!.");
                return CommandReason.OK;
            }
            return CommandReason.ERROR;
        }
        return CommandReason.WRONG_ARGS;
    }

    @SubCommand(
            subcmd = "setpool",
            max_args = 2,
            usage = {"blue|red"},
            error = {"§cWrong team name.",
                    "§7Teams: §cred, §9blue"},
            builder_cmd = true,
            wand_usage = true,
            setup_cmd = true
    )
    public CommandReason setPoolCommand(Player sender, String[] args) {
        Team team = Team.get(args[0]);
        if (team != null) {
            SetupSession session = GameBuilder.getSession(sender);
            Wand wand = session.getWand();
            Cuboid cuboid = wand.createCuboid();
            if (cuboid != null) {
                session.getBuilder().setPool(cuboid, team);
                sender.sendMessage(ChatColor.GRAY + "You have set a pool to " + team + ChatColor.GRAY + " team.!");
                return CommandReason.OK;
            }
            sender.sendMessage(ChatColor.RED + "You must set a region.!");
            return CommandReason.SOMETHING_ELSE;
        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "setspawn",
            max_args = 2,
            usage = {"blue,red,lobby,iron,exp"},
            error = {"§cInvalid argument.", ""},
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason setSpawnCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        Location pLocation = sender.getLocation().clone();
        boolean ret = true;
        String args0 = args[0];
        switch (args0.toLowerCase()) {
            case "red":
                gBuilder.setRedSpawn(pLocation);
                break;
            case "blue":
                gBuilder.setBlueSpawn(pLocation);
                break;
            case "lobby":
                gBuilder.setLobby(pLocation);
                break;
            case "iron":
                gBuilder.setIronGenerator(pLocation);
                break;
            case "exp":
                gBuilder.setExpGenerator(pLocation);
                break;
            default:
                ret = false;
                break;

        }
        if (ret) {
            Team teamName = Team.get(args0);
            sender.sendMessage((teamName != null ? teamName.toString() : args0.toUpperCase()) + "" + ChatColor.GRAY + " spawn has been set.");
            return CommandReason.OK;
        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "setminplayers",
            max_args = 2,
            usage = {"min-players"},
            error = {"§cYou must select a number."},
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason setMinPlayersCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        String args0 = args[0];
        try {
            int x = Integer.parseInt(args0);
            if (x <= 0) {
                sender.sendMessage(ChatColor.RED + "You must set a number greater than 0!");
                return CommandReason.SOMETHING_ELSE;
            }
            if (x > gBuilder.getMaxPlayers()) {
                sender.sendMessage(ChatColor.RED + "You must set a number less than max players.!");
                return CommandReason.SOMETHING_ELSE;
            }
            gBuilder.setMinPlayers(x);
            sender.sendMessage(ChatColor.GRAY + "You have set " + ChatColor.GREEN + x + ChatColor.GRAY + " to min players.!");
            return CommandReason.OK;
        } catch (NumberFormatException ignore) {

        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "setmaxplayers",
            max_args = 2,
            usage = {"max-players"},
            error = {"§cYou must select a number."},
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason setMaxPlayersCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        String args0 = args[0];
        try {
            int x = Integer.parseInt(args0);
            if (x <= 0) {
                sender.sendMessage(ChatColor.RED + "You must set a number greater than 0!");
                return CommandReason.SOMETHING_ELSE;
            }
            if (x < gBuilder.getMinPlayers()) {
                sender.sendMessage(ChatColor.RED + "You must set a number greater than min players.!");
                return CommandReason.SOMETHING_ELSE;
            }
            gBuilder.setMaxPlayers(x);
            sender.sendMessage(ChatColor.GRAY + "You have set " + ChatColor.GREEN + x + ChatColor.GRAY + " to max players.!");
            return CommandReason.OK;
        } catch (NumberFormatException ignore) {

        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "setcount",
            max_args = 2,
            usage = {"seconds"},
            error = "§cYou must select a number.",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason setCountCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        String args0 = args[0];
        try {
            int x = Integer.parseInt(args0);
            gBuilder.setCount(x);
            sender.sendMessage(ChatColor.GRAY + "You have set " + ChatColor.GREEN + x + ChatColor.GRAY + " to count.!");
            return CommandReason.OK;
        } catch (NumberFormatException ignore) {

        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "setmaxpoints",
            max_args = 2,
            usage = {"points"},
            error = "§cYou must select a number.",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason setMaxSecondsCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        String args0 = args[0];
        try {
            int x = Integer.parseInt(args0);
            gBuilder.setMaxPoints(x);
            sender.sendMessage(ChatColor.GRAY + "You have set " + ChatColor.GREEN + x + ChatColor.GRAY + " to max points.!");
            return CommandReason.OK;
        } catch (NumberFormatException ignore) {

        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "create",
            max_args = 3,
            usage = {"arena-name", "bukkit,slime"},
            builder_cmd = false,
            setup_cmd = true
    )
    public CommandReason createCommand(Player sender, String[] args) {
        GameManager gManager = GameManager.get();
        String arenaName = args[0];
        if (gManager.hasGame(arenaName)) {
            sender.sendMessage(ChatColor.RED + arenaName + " already exists.!");
            return CommandReason.SOMETHING_ELSE;
        }
        if (MetadataUtils.has(sender, "setup-session")) {
            sender.sendMessage(ChatColor.RED + "You've already created an arena.!");
            return CommandReason.SOMETHING_ELSE;
        }
        WorldLoader worldLoader = StringUtils.searchEnum(WorldLoader.class, args[1]);
        if (worldLoader == null)
            return CommandReason.WRONG_ARGS;
        GameWorld gameWorld = null;
        World world = null;
        if (worldLoader == WorldLoader.BUKKIT) {
            World w = Bukkit.getWorld(arenaName),
                    defaultWorld = Bukkit.getWorld(StringUtils.DEFAULT_WORLD_NAME);
            if (w != null && w == defaultWorld) {
                sender.sendMessage(ChatColor.RED + "You cannot create an arena with the default world.!");
                return CommandReason.SOMETHING_ELSE;
            }
            world = w != null ? w : BukkitWorldLoader.createWorld(arenaName);
            gameWorld = new BukkitWorldLoader(world);
        } else {
            if (SLIME_PLUGIN != null) {
                SlimeLoader loader = SLIME_PLUGIN.getLoader("file");
                if (Bukkit.getWorld(arenaName) != null) {
                    sender.sendMessage(ChatColor.RED + "There is a world with that name.!");
                    return CommandReason.SOMETHING_ELSE;
                }
                try {
                    SlimeWorld slimeWorld;
                    SlimePropertyMap slimeProperties = SlimeWorldLoader.getProperties();
                    if (loader.worldExists(arenaName))
                        slimeWorld = SLIME_PLUGIN.loadWorld(loader, arenaName, false, slimeProperties);
                    else
                        slimeWorld = SLIME_PLUGIN.createEmptyWorld(loader, arenaName, false, slimeProperties);
                    SLIME_PLUGIN.generateWorld(slimeWorld);
                    world = Bukkit.getWorld(arenaName);
                    gameWorld = new SlimeWorldLoader(slimeWorld);
                } catch (Exception e) {
                    e.printStackTrace();
                    return CommandReason.SOMETHING_ELSE;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "SlimeWorldManager plugin does not load.!");
                return CommandReason.SOMETHING_ELSE;
            }
        }
        if (sender.getWorld() != world)
            sender.teleport(world.getSpawnLocation());
        SetupSession session = new SetupSession(sender);
        session.getBuilder().setName(arenaName).setGameWorld(gameWorld);
        MetadataUtils.set(sender, "setup-session", session);
        sender.sendMessage(ChatColor.GREEN + "You've created an arena.!");
        return CommandReason.OK;
    }

    @SubCommand(
            subcmd = "build",
            max_args = 1,
            usage = "",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason buildCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        if (!gBuilder.hasEveryLocation()) {
            sender.sendMessage(ChatColor.RED + "You cannot build an arena because you must set every location.!");
            return CommandReason.SOMETHING_ELSE;
        }
        if (!gBuilder.hasEveryVariable()) {
            sender.sendMessage(ChatColor.RED + "You cannot build an arena because you must set every variable.!");
            return CommandReason.SOMETHING_ELSE;
        }
        GameWorld gameWorld = gBuilder.getGameWorld();
        final World defaultWorld = StaticConfiguration.spawn_location != null ?
                StaticConfiguration.spawn_location.getWorld() : Bukkit.getWorld(StringUtils.DEFAULT_WORLD_NAME),
                world = gameWorld.getWorld();
        for (Player player : world.getPlayers()) {
            xyz.connorchickenway.towers.utilities.location.Location loc = StaticConfiguration.spawn_location;
            player.teleport(loc != null ? loc.toBukkitLocation() : defaultWorld.getSpawnLocation());
        }
        if (gameWorld instanceof SlimeWorldLoader) {
            boolean unload = gameWorld.unload(true);
            if (unload)
                gameWorld.load();
        } else {
            gameWorld.getWorld().save();
        }
        gBuilder.save();
        GameManager.get().addGame(gBuilder.build());
        sender.sendMessage(ChatColor.GREEN + "Arena " + gBuilder.getName() + " has been built.!");
        MetadataUtils.remove(sender, "setup-sesion");
        sender.getInventory().clear();
        return CommandReason.OK;
    }

    @SubCommand(
            subcmd = "kit",
            max_args = 1,
            usage = "",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason kitCommand(Player sender, String[] args) {
        SetupSession session = GameBuilder.getSession(sender);
        Kit kit = session.getBuilder().setKit(new Kit());
        PlayerInventory pInventory = sender.getInventory();
        kit.addArmor(pInventory.getArmorContents());
        final ItemStack[] contents = pInventory.getContents();
        for (int x = 0; x < contents.length; x++) {
            ItemStack itemStack = contents[x];
            if (itemStack != null)
                kit.addItem(x, itemStack.clone());
        }
        sender.sendMessage(ChatColor.GREEN + "You added a kit to " + session.getBuilder().getName() + " arena.!");
        return CommandReason.OK;
    }

    @SubCommand(
            subcmd = "setspawn",
            max_args = 1,
            usage = "",
            builder_cmd = false,
            setup_cmd = false
    )
    public CommandReason setspawnCommand(Player sender, String[] args) {
        if (!GameMode.isMultiArena()) {
            sender.sendMessage(ChatColor.RED + "Server is not multiarena.!");
            return CommandReason.SOMETHING_ELSE;
        }
        xyz.connorchickenway.towers.utilities.location.Location location = new xyz.connorchickenway.towers.utilities.location.Location(sender.getLocation());
        StaticConfiguration.spawn_location = location;
        AmazingTowers.getInstance().getConfig().set("options.multiarena.spawn", location.serialize());
        AmazingTowers.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Spawn set successfully.!");
        return CommandReason.OK;
    }

    @SubCommand(
            subcmd = "setborder",
            max_args = 1,
            usage = "",
            builder_cmd = true,
            setup_cmd = true,
            wand_usage = true
    )
    public CommandReason setBorderCommand(Player sender, String[] args) {
        SetupSession session = GameBuilder.getSession(sender);
        Wand wand = session.getWand();
        Cuboid cuboid = wand.createCuboid();
        if (cuboid != null) {
            session.getBuilder().setBorder(cuboid);
            sender.sendMessage(ChatColor.GRAY + "You have set a border");
            return CommandReason.OK;
        }
        sender.sendMessage(ChatColor.RED + "You must set a region.!");
        return CommandReason.SOMETHING_ELSE;
    }

    @SubCommand(
            subcmd = "protectspawn",
            max_args = 2,
            usage = {"blue,red"},
            builder_cmd = true,
            setup_cmd = true,
            wand_usage = true
    )
    public CommandReason setProtectCommand(Player sender, String[] args) {
        Team team = Team.get(args[0]);
        if (team != null) {
            SetupSession session = GameBuilder.getSession(sender);
            Wand wand = session.getWand();
            Cuboid cuboid = wand.createCuboid();
            if (cuboid != null) {
                if (team == Team.RED)
                    session.getBuilder().setRedSpawnCuboid(cuboid);
                else
                    session.getBuilder().setBlueSpawnCuboid(cuboid);
                sender.sendMessage(ChatColor.GRAY + "You have set a zone to protect " + team + ChatColor.GRAY + " spawn.!");
                return CommandReason.OK;
            }
            sender.sendMessage(ChatColor.RED + "You must set a region.!");
            return CommandReason.SOMETHING_ELSE;
        }
        return CommandReason.ERROR;
    }

    @SubCommand(
            subcmd = "locations",
            max_args = 1,
            usage = "",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason locationsCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        sender.sendMessage(ChatColor.GRAY + "Locations:");
        checkLocation(sender, "lobby", gBuilder.getLobby());
        checkLocation(sender, "experience-generator", gBuilder.getExpGenerator());
        checkLocation(sender, "iron-generator", gBuilder.getIronGenerator());
        checkLocation(sender, "red-spawn", gBuilder.getRedSpawn());
        checkLocation(sender, "blue-spawn", gBuilder.getBlueSpawn());
        checkLocation(sender, "border", gBuilder.getBorder());
        checkLocation(sender, "red-spawn-cuboid", gBuilder.getRedSpawnCuboid());
        checkLocation(sender, "blue-spawn-cuboid", gBuilder.getBlueSpawnCuboid());
        checkLocation(sender, "red-pool", gBuilder.getRedPool());
        checkLocation(sender, "blue-pool", gBuilder.getBluePool());
        sender.sendMessage(ChatColor.GREEN + "✔" + ChatColor.GRAY + "You set that location");
        sender.sendMessage(ChatColor.RED + "✖" + ChatColor.GRAY + "You do not set that location");
        return CommandReason.OK;
    }

    @SubCommand(
            subcmd = "variables",
            max_args = 1,
            usage = "",
            builder_cmd = true,
            setup_cmd = true
    )
    public CommandReason variablesCommand(Player sender, String[] args) {
        GameBuilder gBuilder = GameBuilder.getSession(sender).getBuilder();
        sender.sendMessage(ChatColor.GRAY + "Variables:");
        checkVariable(sender, "min-players", gBuilder.getMinPlayers());
        checkVariable(sender, "max-players", gBuilder.getMaxPlayers());
        checkVariable(sender, "count", gBuilder.getCount());
        checkVariable(sender, "max-points", gBuilder.getMaxPoints());
        return CommandReason.OK;
    }

    private void checkLocation(Player player, String nameLocation, Object location) {
        player.sendMessage(StringUtils.color(" &7- " + nameLocation + " " + (location != null ? "&a✔" : "&c✖")));
    }

    private void checkVariable(Player player, String variableName, Object variable) {
        player.sendMessage(StringUtils.color(" &7- " + variableName + " : " + (variable != null ? "&a" + variable : "&cnull")));
    }

}
