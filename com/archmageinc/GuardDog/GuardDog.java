package com.archmageinc.GuardDog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GuardDog extends JavaPlugin {
    
    protected String guardFileName  =   "guards";
    protected HashSet<Wolf> guards  =   new HashSet<>();
    
    @Override
    public void onEnable(){
        loadGuards();
        getServer().getPluginManager().registerEvents(new GuardDogListener(this), this);
    }
    
    @Override
    public void onDisable(){
        saveGuards();
        
    }
    
    public void logMessage(String message){
        getLogger().info("["+getDescription().getName()+" "+getDescription().getVersion()+"]: "+message);
    }
    
    public boolean createGuard(Wolf wolf){
        if(guards.contains(wolf))
            return false;
        guards.add(wolf);
        wolf.setCollarColor(DyeColor.LIME);
        wolf.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,300,1));
        logMessage("A new guard dog has been created: "+wolf.getUniqueId().toString());
        saveGuards();
        return true;
    }
    
    public void destroyGuard(Wolf wolf){
        if(!guards.contains(wolf))
            return;
        
        guards.remove(wolf);
        logMessage("A guard dog has been destroyed: "+wolf.getUniqueId().toString());
        saveGuards();

    }
    
    public void saveGuards(){
        File guardFile  =   new File(getDataFolder(),guardFileName);
        if(guardFile.exists())
            guardFile.delete();
        if(guards.isEmpty())
            return;
        try{
            guardFile.createNewFile();
        }catch(IOException e){
            logMessage("Unable to create guard file!");
            return;
        }
        
        FileConfiguration config    =   YamlConfiguration.loadConfiguration(guardFile);
        Iterator<Wolf> itr          =   guards.iterator();
        List<String> guardIds       =   new ArrayList<>();
        while(itr.hasNext()){
            Wolf wolf   =   itr.next();
            guardIds.add(wolf.getUniqueId().toString());
            
        }        
        config.set("guards",guardIds);
        try{
            config.save(guardFile);
        }catch(IOException e){
            logMessage("Unable to save guard file!");
        }
        
    }
    
    protected void loadGuards(){
        File guardFile  =   new File(getDataFolder(),guardFileName);
        if(!guardFile.exists()){
            logMessage("No guard file.");
            return;
        }
        
        FileConfiguration config    =   YamlConfiguration.loadConfiguration(guardFile);
        List<String> guardIds       =   config.getStringList("guards");
        Iterator<World> witr        =   getServer().getWorlds().iterator();
        while(witr.hasNext()){
            World world =   witr.next();
            Iterator<LivingEntity> eitr  =   world.getLivingEntities().iterator();
            while(eitr.hasNext()){
                LivingEntity entity  =   eitr.next();
                if(entity instanceof Wolf){
                    if(guardIds.contains(entity.getUniqueId().toString())){
                        createGuard((Wolf) entity);
                    }
                }
            }// All living entities in world
        }// All worlds on server
        
    }
}
