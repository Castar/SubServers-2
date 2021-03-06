package net.ME1312.SubServers.Bungee.Network.Packet;

import net.ME1312.SubServers.Bungee.Host.Host;
import net.ME1312.SubServers.Bungee.Host.Server;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.ME1312.SubServers.Bungee.Library.Version.Version;
import net.ME1312.SubServers.Bungee.Network.Client;
import net.ME1312.SubServers.Bungee.Network.PacketIn;
import net.ME1312.SubServers.Bungee.Network.PacketOut;
import net.ME1312.SubServers.Bungee.SubPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

/**
 * Download Server List Packet
 */
public class PacketDownloadServerList implements PacketIn, PacketOut {
    private SubPlugin plugin;
    private String host;
    private String id;

    /**
     * New PacketDownloadServerList (In)
     *
     * @param plugin SubPlugin
     */
    public PacketDownloadServerList(SubPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * New PacketDownloadServerList (Out)
     *
     * @param plugin SubPlugin
     * @param host Host (or null for all)
     * @param id Receiver ID
     */
    public PacketDownloadServerList(SubPlugin plugin, String host, String id) {
        this.plugin = plugin;
        this.host = host;
        this.id = id;
    }

    @Override
    public JSONObject generate() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        if (host == null || host.equals("")) {
            JSONObject exServers = new JSONObject();
            for (Server server : plugin.exServers.values()) {
                JSONObject info = new JSONObject();
                info.put("display", server.getDisplayName());
                JSONObject players = new JSONObject();
                for (ProxiedPlayer player : server.getPlayers()) {
                    JSONObject pinfo = new JSONObject();
                    pinfo.put("name", player.getName());
                    pinfo.put("nick", player.getDisplayName());
                    players.put(player.getUniqueId().toString(), pinfo);
                }
                info.put("players", players);
                exServers.put(server.getName(), info);
            }
            json.put("servers", exServers);
        }

        if (this.host == null || !this.host.equals("")) {
            JSONObject hosts = new JSONObject();
            for (Host host : plugin.api.getHosts().values()) {
                if (this.host == null || this.host.equalsIgnoreCase(host.getName())) {
                    JSONObject hinfo = new JSONObject();
                    hinfo.put("enabled", host.isEnabled());
                    hinfo.put("display", host.getDisplayName());
                    JSONObject servers = new JSONObject();
                    for (SubServer server : host.getSubServers().values()) {
                        JSONObject sinfo = new JSONObject();
                        sinfo.put("enabled", server.isEnabled() && host.isEnabled());
                        sinfo.put("display", server.getDisplayName());
                        sinfo.put("running", server.isRunning());
                        sinfo.put("temp", server.isTemporary());
                        JSONObject players = new JSONObject();
                        for (ProxiedPlayer player : server.getPlayers()) {
                            JSONObject pinfo = new JSONObject();
                            pinfo.put("name", player.getName());
                            pinfo.put("nick", player.getDisplayName());
                            players.put(player.getUniqueId().toString(), pinfo);
                        }
                        sinfo.put("players", players);
                        servers.put(server.getName(), sinfo);
                    }
                    hinfo.put("servers", servers);
                    hosts.put(host.getName(), hinfo);
                }
            }
            json.put("hosts", hosts);
        }

        return json;
    }

    @Override
    public void execute(Client client, JSONObject data) {
        client.sendPacket(new PacketDownloadServerList(plugin, (data.keySet().contains("host"))?data.getString("host"):null, (data.keySet().contains("id"))?data.getString("id"):null));
    }

    @Override
    public Version getVersion() {
        return new Version("2.11.0a");
    }
}
