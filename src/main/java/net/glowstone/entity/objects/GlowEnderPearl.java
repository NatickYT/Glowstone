package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class GlowEnderPearl extends GlowEntity implements EnderPearl {

    private ProjectileSource shooter;
    private float speed;
    
    public GlowEnderPearl(Location location, float speed) {
        super(location);
        setDrag(0.99, false);
        setDrag(0.99, true);
        setGravityAccel(new Vector(0,-0.06,0));
        this.speed = speed;
        setVelocity(location.getDirection().multiply(this.speed));
    }

    @Override
    public boolean doesBounce() {
        return false;
    }

    @Override
    public ProjectileSource getShooter() {
        return this.shooter;
    }

    @Override
    public void setBounce(boolean bounce) {
        // TODO: Auto-generated method stub
    }

    @Override
    public void setShooter(ProjectileSource source) {
        this.shooter = source;
    }

    @Override
    protected void pulsePhysics() {
        Location velLoc = location.clone().add(velocity);
        velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));

        velocity.setX(velocity.getX() * 0.95);
        velocity.setZ(velocity.getZ() * 0.95);

        setRawLocation(velLoc);

        // If the EnderPearl collides with anything except air/fluids
        if (!location.getBlock().isLiquid() && !location.getBlock().isEmpty() && shooter instanceof  Entity) {
            ((Entity) shooter).teleport(location);
            this.remove();
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Arrays.asList(
                new SpawnObjectMessage(id, getUniqueId(), SpawnObjectMessage.THROWN_ENDERPEARL, location),
                new EntityMetadataMessage(id, metadata.getEntryList()),
                // These keep the client from assigning a random velocity
                new EntityTeleportMessage(id, location),
                new EntityVelocityMessage(id, getVelocity())
            );
    }
}