package net.ME1312.SubServers.Bungee.Host;

import net.ME1312.SubServers.Bungee.Library.Config.YAMLSection;
import net.ME1312.SubServers.Bungee.Library.Config.YAMLValue;
import net.ME1312.SubServers.Bungee.Library.Exception.InvalidServerException;
import net.ME1312.SubServers.Bungee.Library.ExtraDataHandler;
import net.ME1312.SubServers.Bungee.Network.Client;
import net.ME1312.SubServers.Bungee.Network.ClientHandler;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONObject;

import java.net.InetSocketAddress;

/**
 * Server Class
 */
public class Server extends BungeeServerInfo implements ClientHandler, ExtraDataHandler {
    private YAMLSection extra = new YAMLSection();
    private Client client = null;
    private String nick = null;
    private String motd;
    private boolean restricted;
    private boolean hidden;

    public Server(String name, InetSocketAddress address, String motd, boolean hidden, boolean restricted) throws InvalidServerException {
        super(name, address, ChatColor.translateAlternateColorCodes('&', motd), restricted);
        if (name.contains(" ")) throw new InvalidServerException("Server names cannot have spaces: " + name);
        this.motd = motd;
        this.restricted = restricted;
        this.hidden = hidden;
    }

    @Override
    public Client getSubDataClient() {
        return client;
    }

    @Override
    public void linkSubDataClient(Client client) {
        if (this.client == null) {
            client.setHandler(this);
            this.client = client;
        } else if (client == null) {
            this.client = null;
        } else throw new IllegalStateException("A SubData Client is already linked to Server: " + getName());
    }

    /**
     * Get the Display Name of this Server
     *
     * @return Display Name
     */
    public String getDisplayName() {
        return (nick == null)?getName():nick;
    }

    /**
     * Sets the Display Name for this Server
     *
     * @param value Value (or null to reset)
     */
    public void setDisplayName(String value) {
        this.nick = value;
    }

    /**
     * If the server is hidden from players
     *
     * @return Hidden Status
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Set if the server is hidden from players
     *
     * @param value Value
     */
    public void setHidden(boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the MOTD of the Server
     *
     * @return Server MOTD
     */
    @Override
    public String getMotd() {
        return motd;
    }

    /**
     * Sets the MOTD of the Server
     *
     * @param value Value
     */
    public void setMotd(String value) {
        this.motd = value;
    }

    /**
     * Gets if the Server is Restricted
     *
     * @return Restricted Status
     */
    @Override
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Sets if the Server is Restricted
     *
     * @param value Value
     */
    public void setRestricted(boolean value) {
        this.restricted = value;
    }

    @Override
    public void addExtra(String handle, Object value) {
        extra.set(handle, value);
    }

    @Override
    public boolean hasExtra(String handle) {
        return extra.getKeys().contains(handle);
    }

    @Override
    public YAMLValue getExtra(String handle) {
        return extra.get(handle);
    }

    @Override
    public YAMLSection getExtra() {
        return extra.clone();
    }

    @Override
    public void removeExtra(String handle) {
        extra.remove(handle);
    }
}
