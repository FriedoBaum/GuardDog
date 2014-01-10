

package com.archmageinc.GuardDog;

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ArchmageInc
 */
public class GuardDogListener implements Listener {
    
    protected GuardDog plugin;
    
    public GuardDogListener(GuardDog plugin){
        this.plugin =   plugin;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        if(!(event.getRightClicked() instanceof Wolf))
            return;
        
        Wolf wolf       =   (Wolf) event.getRightClicked();
        Player player   =   event.getPlayer();
        
        if(!wolf.isTamed() || !wolf.getOwner().equals(player))
            return;
        
        if(!player.getItemInHand().getType().equals(Material.PUMPKIN_SEEDS))
            return;
        
        if(!plugin.createGuard(wolf))
            return;
        
        player.getInventory().removeItem(new ItemStack(Material.PUMPKIN_SEEDS,1));
        player.sendMessage(ChatColor.GREEN+"Guard Dog"+ChatColor.WHITE+" ready for action");
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(!(event.getEntity() instanceof Wolf))
            return;
        
        Wolf wolf   =   (Wolf) event.getEntity();
        plugin.destroyGuard(wolf);
    }
    
    
    
}
