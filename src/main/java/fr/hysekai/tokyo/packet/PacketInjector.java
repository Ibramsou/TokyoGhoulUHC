package fr.hysekai.tokyo.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInSettings;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketInjector extends ChannelInboundHandlerAdapter {

    private final EntityPlayer entityplayer;
    private boolean registered;

    public PacketInjector(Player player) {
        this.entityplayer = ((CraftPlayer) player).getHandle();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object object) throws Exception {
        if (!this.entityplayer.playerConnection.isDisconnected() && object instanceof PacketPlayInSettings) {
            PacketPlayInSettings packet = (PacketPlayInSettings) object;
            byte value = (byte) packet.e();
            if (value != 127) {
                if (!this.registered) {
                    this.entityplayer.getDataWatcher().watch(10, (byte) 127); // Display all parts of skin
                    this.registered = true;
                }
                return;
            }
        }

        super.channelRead(context, object);
    }
}
