package fr.arthurbambou.fblink;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import discord.DiscordBot;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FBLink implements DedicatedServerModInitializer {

	private static ConfigManager configManager;
	private static DiscordBot discordBot;
	private static MinecraftServer server;

	@Override
	public void onInitializeServer() {
		configManager = new ConfigManager();
		discordBot = new DiscordBot(configManager.init(), configManager.config, server);
		discordBot.onServerStartup();
		configManager.config.token = "";
	}

	public static void regenConfig() {
		configManager.regenConfig();
		discordBot = new DiscordBot(configManager.init(), configManager.config, server);
		configManager.config.token = "";
	}

	public static DiscordBot getDiscordBot() {
		return discordBot;
	}

	public static void onStartup(MinecraftServer srv) {
		server = srv;
	}

	protected class ConfigManager {
		private File CONFIG_PATH = FabricLoader.getInstance().getConfigDirectory();

		private final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

		private File configFile;
		private Gson gson = DEFAULT_GSON;
		private Config DefaultConfig = new Config();

		private Config config;

		protected String init() {
			String configFilename = "fblink";
			configFile = new File(CONFIG_PATH, configFilename + ".json");
			if (!configFile.exists()) {
				return saveConfig(DefaultConfig);
			}
			return loadConfig();
		}

		private String saveConfig(Config instanceConfig) {
			try (FileWriter fileWriter = new FileWriter(configFile)) {
				fileWriter.write(gson.toJson(instanceConfig));
				config = instanceConfig;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (instanceConfig.token != DefaultConfig.token) {
				return instanceConfig.token;
			}
			return DefaultConfig.token;
		}

		public String regenConfig() {
			try (FileWriter fileWriter = new FileWriter(configFile)) {
				fileWriter.write(gson.toJson(DefaultConfig));
				config = DefaultConfig;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return DefaultConfig.token;
		}

		public String loadConfig() {
			try (FileReader fileReader = new FileReader(configFile)) {
				config = gson.fromJson(fileReader, Config.class);
				if (config.token == null) {
					config.token = DefaultConfig.token;
				}
				if (config.chatChannels == null) {
					config.chatChannels = DefaultConfig.chatChannels;
				}
				if (config.logChannels == null) {
					config.logChannels = DefaultConfig.logChannels;
				}
				if (config.discordToMinecraft == null) {
					config.discordToMinecraft = DefaultConfig.discordToMinecraft;
				}
				if (config.minecraftToDiscord == null) {
					config.minecraftToDiscord = DefaultConfig.minecraftToDiscord;
				}
				if (config.minecraftToDiscord.messages == null) {
					config.minecraftToDiscord.messages = DefaultConfig.minecraftToDiscord.messages;
				}
				if (config.minecraftToDiscord.messages.serverStarted == null) {
					config.minecraftToDiscord.messages.serverStarted = DefaultConfig.minecraftToDiscord.messages.serverStarted;
				}
				if (config.minecraftToDiscord.messages.serverStarting == null) {
					config.minecraftToDiscord.messages.serverStarting = DefaultConfig.minecraftToDiscord.messages.serverStarting;
				}
				if (config.minecraftToDiscord.messages.serverStopped == null) {
					config.minecraftToDiscord.messages.serverStopped = DefaultConfig.minecraftToDiscord.messages.serverStopped;
				}
				return saveConfig(config);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class Config {
		private String token = "";
		public String discordToMinecraft = "[%player] %message";
		public List<String> chatChannels = new ArrayList<String>();
		public List<String> logChannels = new ArrayList<String>();
		public boolean ignoreBots = true;
		public MinecraftToDiscord minecraftToDiscord = new MinecraftToDiscord();

		public class MinecraftToDiscord {
			public MinecraftToDiscordMessage messages = new MinecraftToDiscordMessage();
			public MinecraftToDiscordBooleans booleans = new MinecraftToDiscordBooleans();
		}

		public class MinecraftToDiscordMessage {
			public String serverStarting = "Server is starting !";
			public String serverStarted = "Server Started";
			public String serverStopped = "Server Stopped";
		}

		public class MinecraftToDiscordBooleans {
			public boolean customChannelDescription = false;
			public boolean MCtoDiscordTag = false;
			public boolean PlayerMessages = true;
			public boolean JoinAndLeftMessages = true;
			public boolean AdvancementMessages = true;
			public boolean LogMessages = true;
			public boolean DeathMessages = true;
		}
	}
}
