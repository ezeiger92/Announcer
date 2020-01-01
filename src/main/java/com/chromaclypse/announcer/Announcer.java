package com.chromaclypse.announcer;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.chromaclypse.api.Defaults;
import com.chromaclypse.api.Log;
import com.chromaclypse.api.command.CommandBase;
import com.chromaclypse.api.command.Context;
import com.chromaclypse.api.messages.Text;

import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Announcer extends JavaPlugin implements Listener {

	private static class Index {
		public int value = 0;
	}

	private Random random = new Random();

	private List<Integer> tasks = Defaults.emptyList();

	private Announcements config = new Announcements();

	@Override
	public void onEnable() {
		TabExecutor ano = new CommandBase()
				.calls(this::helpCommand)
				.with().arg("reload").calls(this::reloadCommand)
				.with().arg("version").calls(CommandBase::pluginVersion)
				.getCommand();
		
		getCommand("announcer").setExecutor(ano);
		getCommand("announcer").setTabCompleter(ano);
		getServer().getPluginManager().registerEvents(this, this);
		startup();
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((JavaPlugin) this);
	}

	public void startup() {
		for(Integer i : tasks) {
			getServer().getScheduler().cancelTask(i);
		}

		config.init(this);

		for(Announcements.Instance announcement : config.announcements) {

			Index i = new Index();
			List<String> messages = announcement.messages;
			String permission = announcement.permission;

			Log.info("interval: " + announcement.interval);
			Log.info("delay: " + announcement.offset);

			tasks.add(getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

				if(i.value >= messages.size()) {
					i.value = 0;
				}

				if(i.value == 0 && announcement.random) {
					Collections.shuffle(messages, random);
				}

				String[] message = Text.format().colorize(messages.get(i.value ++)).split(NEWLINE);

				for(Player p : getServer().getOnlinePlayers()) {
					if(permission.equals("*") || p.hasPermission(permission)) {
						p.sendMessage(message);
					}
				}
			},
			announcement.offset, announcement.interval));
		}
	}
	
	private boolean helpCommand(Context context) {
		context.Sender().sendMessage("Announcer: /"+context.Alias()+" reload");
		return true;
	}
	
	private boolean reloadCommand(Context context) {
		startup();
		context.Sender().sendMessage("Announcer: Reloaded config!");
		return true;
	}

	public static final String NEWLINE = "(?:\r?\n)";

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getPlayer().hasPlayedBefore()) {
			if(config.regular_join.length() != 0) {
				event.setJoinMessage(Text.format().colorize(config.regular_join).replace("%name%", event.getPlayer().getName()));
			}
		}
		else {
			if(config.first_join.length() != 0) {
				event.setJoinMessage(Text.format().colorize(config.first_join).replace("%name%", event.getPlayer().getName()));
			}
		}

		Log.info(event.getJoinMessage());
	}
}
