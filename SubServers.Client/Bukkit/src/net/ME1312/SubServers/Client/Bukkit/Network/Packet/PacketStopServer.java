package net.ME1312.SubServers.Client.Bukkit.Network.Packet;

import net.ME1312.SubServers.Client.Bukkit.Library.JSONCallback;
import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketOut;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class PacketStopServer implements PacketIn, PacketOut {
    private static HashMap<String, JSONCallback> callbacks = new HashMap<String, JSONCallback>();
    private UUID player;
    private boolean force;
    private String server;
    private String id;

    public PacketStopServer() {}
    public PacketStopServer(String server, boolean force, String id, JSONCallback callback) {
        this.player = null;
        this.server = server;
        this.force = force;
        this.id = id;
        callbacks.put(id, callback);
    }
    public PacketStopServer(UUID player, String server, boolean force, String id, JSONCallback callback) {
        this.player = player;
        this.server = server;
        this.force = force;
        this.id = id;
        callbacks.put(id, callback);
    }

    @Override
    public JSONObject generate() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        if (player != null) json.put("player", player.toString());
        json.put("server", server);
        json.put("force", force);
        return json;
    }

    @Override
    public void execute(JSONObject data) {
        callbacks.get(data.getString("id")).run(data);
        callbacks.remove(data.getString("id"));
    }

    @Override
    public Version getVersion() {
        return new Version("2.11.0a");
    }
}
