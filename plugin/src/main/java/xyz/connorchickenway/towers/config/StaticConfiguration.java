package xyz.connorchickenway.towers.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.material.Wool;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.builder.Team;
import xyz.connorchickenway.towers.nms.NMSVersion;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.ItemBuilder;
import xyz.connorchickenway.towers.utilities.ItemUtils;
import xyz.connorchickenway.towers.utilities.StringUtils;
import xyz.connorchickenway.towers.utilities.location.Location;

import static xyz.connorchickenway.towers.utilities.StringUtils.color;

public class StaticConfiguration
{

    private static AmazingTowers plugin = AmazingTowers.getInstance();

    /** team name **/
    public static String red_name, blue_name;
    /** chat **/
    public static boolean chat_enabled;
    public static String global_prefix, normal_format, team_format, global_format;
    /** drop armor **/
    public static boolean drop_armor;
    /** instant repawn **/
    public static boolean instant_respawn;
    /** server name **/
    public static String server_name;
    /** OPTIONS **/
    /** MULTIARENA **/
    /** spawn location **/
    public static Location spawn_location;
    /** ATTACHED **/
    public static String attached_sign_material, 
        attached_lobby_color, attached_starting_color, 
        attached_game_color, attached_finish_color, 
        attached_reloading_color;
    /** SIGN **/
    public static String lobby_status, starting_status,
        game_status, finish_status, reloading_status;
    public static List<String> sign_lines;    
    
    //
    public static String itemType;
    public static String redTitle;
    public static int red_position;
    public static String blueTitle;
    public static int blue_position;
    public static String quitTitle;
    public static int quit_position;

    public static void load()
    {
        FileConfiguration config = plugin.getConfig();
        red_name = config.getString( "team_name.red", "RED" );
        blue_name = config.getString( "team_name.blue", "BLUE" );
        chat_enabled = config.getBoolean( "chat.enabled", true );
        global_prefix = config.getString( "chat.global_prefix", "!" );
        normal_format = config.getString( "chat.format.normal_chat", "%prefix% &f%player% &8: &f%msg%" );
        team_format = config.getString( "chat.format.team_chat",
                "%color_team%&l[%team_name%] %prefix% &f%player% &8: &f%msg%" );
        global_format = config.getString( "chat.format.global_chat",
                "%color_team%&l[GLOBAL] %prefix% &f%player% &7: &f%msg%" );
        drop_armor = config.getBoolean( "drop_leather_armor", false );
        instant_respawn = config.getBoolean( "instant_respawn", true );
        spawn_location = Location.deserialize( config.getString( "options.multiarena.spawn" ) );
        server_name = config.getString( "options.bungee-mode.server_name", "towers_lobby" );
        /** SIGN **/
        final String signLocation = "options.multiarena.sign"; 
        attached_sign_material = config.getString( signLocation + ".attached.material" , "STAINED_GLASS" );
        attached_lobby_color = config.getString( signLocation + ".attached.color.lobby" , "GREEN" );
        attached_starting_color = config.getString( signLocation + ".attached.color.starting" , "YELLOW" );
        attached_game_color = config.getString( signLocation + ".attached.color.game" , "RED" );
        attached_finish_color = config.getString( signLocation + ".attached.color.finish" , "PURPLE" );
        attached_reloading_color = config.getString( signLocation + ".attached.color.reload" , "BLACK" );
        /** STATUS **/
        lobby_status = color( config.getString( signLocation + ".status.lobby" , "&aWaiting" ) );
        starting_status = color( config.getString( signLocation + ".status.starting" , "&eStarting" ) );
        game_status = color( config.getString( signLocation + ".status.game" , "&cIn-Game" ) );
        finish_status = color( config.getString( signLocation + ".status.finish" , "&5Finish" ) );
        reloading_status = color( config.getString( signLocation + ".status.reload" , "&0Reloading" ) );
        sign_lines = config.getStringList( signLocation + ".lines" );

        //
        itemType = config.getString( "items.type", "WOOL" );
        if ( !itemType.equalsIgnoreCase( "WOOL" ) && !itemType.equalsIgnoreCase( "DYE" ) )
            itemType = "WOOL";
        redTitle = config.getString( "items.red.name", "&c&lJOIN TEAM RED &7(Right Click)" );
        red_position = config.getInt( "items.red.position", 0 );
        blueTitle = config.getString( "items.blue.name", "&9&lJOIN TEAM BLUE &7(Right Click)" );
        blue_position = config.getInt( "items.blue.position", 1 );
        quitTitle = config.getString( "items.quit.name", "&c&lReturn to lobby &7(Right Click)" );
        quit_position = config.getInt( "items.quit.positon", 8 );        
        ItemUtils.redItem = createItem( Team.RED, itemType );
        ItemUtils.blueItem = createItem( Team.BLUE, itemType );
        Material quitMaterial = StringUtils.searchEnum( Material.class, NMSVersion.isNewerVersion ? "RED_BED" : "BED" );
        ItemUtils.quitItem = ItemBuilder.of( quitMaterial )
                                        .setDisplayName( quitTitle )
                                        .toItemStack();                                        
    }

    private static ItemStack createItem( Team team, String type )
    {
        if ( NMSVersion.isNewerVersion )
        {
            String t = team.name() + "_" + type;
            if ( type.equalsIgnoreCase( "dye" ) )
            {
                if ( NMSVersion.is1_13() )
                    if ( team == Team.RED ) t = "RED_ROSE";
                    else t = "LAPIS_LAZULI";
            }
            return ItemBuilder.of( StringUtils.searchEnum( Material.class, t ) )
                    .setDisplayName( team == Team.RED ? redTitle : blueTitle )
                    .toItemStack();
        }
        ItemStack itemStack;
        if ( type.equalsIgnoreCase( "dye" ) )
        {
            Dye dye = new Dye( team.getDyeColor() );
            itemStack = dye.toItemStack( 1 );
        }
        else
        {
            Wool wool = new Wool( team.getDyeColor() );
            itemStack = wool.toItemStack( 1 );
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName( StringUtils.color( team == Team.RED ? redTitle : blueTitle ) );
        itemStack.setItemMeta( itemMeta );
        return itemStack;
    }

    public static void loadGameMode()
    {
        String gString = plugin.getConfig().getString( "game_mode" );
        if ( gString != null )
        {
            GameMode gameMode = StringUtils.searchEnum( GameMode.class, gString );
            if ( gameMode != null )
            {
                GameMode.setGameMode( gameMode );
                return;
            }
        }
        GameMode.setGameMode( GameMode.MULTI_ARENA ); 
    }

}
