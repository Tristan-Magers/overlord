package me.chainsaw.overlord;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class JoinSignManager implements Listener, PluginMessageListener {

    static List<String> SignServersServer = new ArrayList<String>();
    static List<Sign> SignServersSigns = new ArrayList<Sign>();

    static String permission = "cheep.action.joinsign.create";

    static JavaPlugin plugin;

    public JoinSignManager(JavaPlugin pl) {
        plugin = pl;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                for (String server : SignServersServer) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("PlayerCount");
                    out.writeUTF(server);
                    plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
            }
        }, 50, 50);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
        if (!channel.equals("BungeeCord"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(msg);
        String subchannel = in.readUTF();

        if (subchannel.equals("PlayerCount")) {
            String server = in.readUTF();
            int playercount = in.readInt();

            if (SignServersServer.contains(server)) {
                Sign sign = SignServersSigns.get(SignServersServer.indexOf(server));

                sign.setLine(2,
                        "§7" + playercount + "§f/§64");
                sign.update();
            }
        }
    }

    @EventHandler
    public static void onSignPlace(SignChangeEvent e) {
        if (e.getPlayer().hasPermission(permission)) {
            if (e.getLine(0).equalsIgnoreCase("[tele]")) {
                String server = e.getLine(1);

                Sign sign = (Sign) e.getBlock().getState();

                sign.setLine(0, "");
                sign.setLine(1, "§5Duel TNT");
                sign.setLine(2, "§70§f/§64");
                sign.setLine(3, server);

                sign.update();

                SignServersServer.add(server);
                SignServersSigns.add(sign);
            }
        }
    }

    @EventHandler
    public static void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
            return;

        //String act = e.getAction().valueOf();

        //if(!act.equals("RIGHT_CLICK_BLOCK")) return;

        if (e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            String server = sign.getLine(3);

            if (!SignServersSigns.contains(sign)) {
                e.getPlayer().sendMessage("dead sign");
                SignServersServer.add(server);
                SignServersSigns.add(sign);
                return;
            }

            //String server = SignServersServer.get(SignServersSigns.indexOf(sign));

            //e.getPlayer().sendMessage("§3§l[§2Cheep§3§l] §aConnecting to §6" + server + "§a...");

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            e.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }

}
