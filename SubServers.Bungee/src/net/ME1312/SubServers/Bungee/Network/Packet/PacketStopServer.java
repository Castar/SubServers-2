package net.ME1312.SubServers.Bungee.Network.Packet;

import net.ME1312.SubServers.Bungee.Host.Server;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.ME1312.SubServers.Bungee.Library.Version.Version;
import net.ME1312.SubServers.Bungee.Network.Client;
import net.ME1312.SubServers.Bungee.Network.PacketIn;
import net.ME1312.SubServers.Bungee.Network.PacketOut;
import net.ME1312.SubServers.Bungee.SubPlugin;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * Stop Server Packet
 */
public class PacketStopServer implements PacketIn, PacketOut {
    private SubPlugin plugin;
    private int response;
    private String message;
    private String id;

    /**
     * New PacketStopServer (In)
     *
     * @param plugin SubPlugin
     */
    public PacketStopServer(SubPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * New PacketStopServer (Out)
     *
     * @param response Response ID
     * @param message Message
     * @param id Receiver ID
     */
    public PacketStopServer(int response, String message, String id) {
        this.response = response;
        this.message = message;
        this.id = id;
    }

    @Override
    public JSONObject generate() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("r", response);
        json.put("m", message);
        return json;
    }

    @Override
    public void execute(Client client, JSONObject data) {
        try {
            Map<String, Server> servers = plugin.api.getServers();
            if (!servers.keySet().contains(data.getString("server").toLowerCase())) {
                client.sendPacket(new PacketStopServer(3, "There is no server with that name", (data.keySet().contains("id"))?data.getString("id"):null));
            } else if (!(servers.get(data.getString("server").toLowerCase()) instanceof SubServer)) {
                client.sendPacket(new PacketStopServer(4, "That Server is not a SubServer", (data.keySet().contains("id"))?data.getString("id"):null));
            } else if (!((SubServer) servers.get(data.getString("server").toLowerCase())).isRunning()) {
                client.sendPacket(new PacketStopServer(5, "That SubServer is not running", (data.keySet().contains("id"))?data.getString("id"):null));
            } else if (data.keySet().contains("force") && data.getBoolean("force")) {
                if (((SubServer) servers.get(data.getString("server").toLowerCase())).terminate((data.keySet().contains("player"))?UUID.fromString(data.getString("player")):null)) {
                    client.sendPacket(new PacketStopServer(0, "Terminating SubServer", (data.keySet().contains("id"))?data.getString("id"):null));
                } else {
                    client.sendPacket(new PacketStopServer(1, "Couldn't terminate SubServer", (data.keySet().contains("id"))?data.getString("id"):null));
                }
            } else {
                if (((SubServer) servers.get(data.getString("server").toLowerCase())).stop((data.keySet().contains("player"))?UUID.fromString(data.getString("player")):null)) {
                    client.sendPacket(new PacketStopServer(0, "Stopping SubServer", (data.keySet().contains("id"))?data.getString("id"):null));
                } else {
                    client.sendPacket(new PacketStopServer(1, "Couldn't stop SubServer", (data.keySet().contains("id"))?data.getString("id"):null));
                }
            }
        } catch (Throwable e) {
            client.sendPacket(new PacketStopServer(2, e.getClass().getCanonicalName() + ": " + e.getMessage(), (data.keySet().contains("id"))?data.getString("id"):null));
        }
    }

    @Override
    public Version getVersion() {
        return new Version("2.11.0a");
    }
}
