package me.gallent.HSUVote;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import me.gallent.hsupoints.PointCounter;

public class HSUVoteMain extends JavaPlugin implements Listener{

	public Plugin pCountPlugin = getServer().getPluginManager().getPlugin("HSUPointCounter");

	PointCounter pCount = (PointCounter) pCountPlugin;

	public File voteStorage=null;
	public FileConfiguration vSConfig=null;
	
	public ItemStack mPoint = null;	
	public ItemStack sPoint = null;	
	public ItemStack gPoint = null;	
	public Player player = null;

	@Override
	public void onEnable() {
		pCount.pointSys.test1();
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("HSUVote")||label.equalsIgnoreCase("PCVote")) {
			if(args.length==0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lHSU Vote"));
				sender.sendMessage(ChatColor.GREEN+"Version 1.0");
				sender.sendMessage(ChatColor.GREEN+"By Gallent");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&oCommands:"));
				sender.sendMessage(ChatColor.GOLD+"HSUVote Use");
				sender.sendMessage(ChatColor.GOLD+"HSUVote Check");
				return true;				
			}
			if(args[0].equalsIgnoreCase("use")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED+"You cannot use this in the console.");
				}
				pCount.pointSys.pointItems();
				this.mPoint = pCount.pointSys.mPoint;
				this.sPoint = pCount.pointSys.sPoint;
				this.gPoint=pCount.pointSys.gPoint;
				this.player=(Player) sender;
			}
			return false;
		}
		return false;
	}
	
	//Config info
	public void reloadVData() {
		if(this.voteStorage==null) {
			try {
				this.voteStorage=new File(getDataFolder(), "OfflineVotes.yml");
			} catch (NullPointerException npe) {
				Bukkit.getConsoleSender().sendMessage("Reloaded vdata without player");
			}
		}
		this.vSConfig=YamlConfiguration.loadConfiguration(this.voteStorage);
		InputStream defaultStream = getResource(""+voteStorage);
		if(defaultStream!=null) {
			YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.vSConfig.setDefaults(testConfig);
		}
	}
	public FileConfiguration getVData() {
		if (this.vSConfig == null)
			reloadVData();
		return this.vSConfig;
	}
	
	public void saveVData() {
		if(this.vSConfig==null||this.voteStorage==null) {
			return;
		}
		try {
			this.getVData().save(this.voteStorage);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Saving Failed");
		}
	}
	

	@EventHandler
	public void onVote(VotifierEvent event) {
		Vote v = event.getVote();
		String pName=v.getUsername();
		Player p=Bukkit.getServer().getPlayer(pName);
		if(p==null||!p.isOnline()) {
			if(getVData().contains("VoteStorage.Username."+pName)) {
				int Votes=getVData().getInt("VoteStorage.Username."+pName);
				++Votes;
				getVData().set("VoteStorage.Username."+pName, Votes);
				return;
			}
			getVData().set("VoteStorage.Username."+pName, 1);
			return;
		}
		String vUUID="VoteStorage.UUID."+p.getUniqueId().toString();
		if(getVData().contains(vUUID+".Votes")) {
			int Votes=getVData().getInt(vUUID+".Votes");
			++Votes;
			getVData().set(vUUID+".Votes", Votes);
			getVData().set(vUUID+".Username", pName);
			return;
		}
		getVData().set(vUUID+".Votes", 1);
		getVData().set(vUUID+".Username", pName);
		return;
	}
}
