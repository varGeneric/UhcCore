package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.exceptions.UhcPlayerJoinException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.threads.KillDisconnectedPlayerThread;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener{
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		GameManager gm = GameManager.getGameManager();
		
		try{
			boolean allowedToJoin = gm.getPlayersManager().isPlayerAllowedToJoin(event.getPlayer());

			if (allowedToJoin){
				// Create player if not existent.
				gm.getPlayersManager().getOrCreateUhcPlayer(event.getPlayer());
			}else{
				throw new UhcPlayerJoinException("An unexpected error as occured.");
			}
		}catch(final UhcPlayerJoinException e){
			event.setKickMessage(e.getMessage());
			event.setResult(Result.KICK_OTHER);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				GameManager.getGameManager().getPlayersManager().playerJoinsTheGame(event.getPlayer());
			}
		}, 1);
		
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent event){
		GameManager gm = GameManager.getGameManager();
		MainConfiguration cfg = gm.getConfiguration();
		if(gm.getGameState().equals(GameState.WAITING) || gm.getGameState().equals(GameState.STARTING)){
			UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());

			if(gm.getGameState().equals(GameState.STARTING)) {
				gm.getPlayersManager().setPlayerSpectateAtLobby(uhcPlayer);
				gm.broadcastInfoMessage(uhcPlayer.getName() + " has left while the game was starting and has been killed.");
				gm.getPlayersManager().strikeLightning(uhcPlayer);
			}else{ // Is waiting
				if(cfg.getAvoidTeamColorVariations() && uhcPlayer.getTeam().getMemberCount() == 1){
					// Team is empty, free up their prefix
					gm.getTeamManager().handleFreedTeamPrefix();
				}
			}

			try{
				uhcPlayer.getTeam().leave(uhcPlayer);
			}catch (UhcTeamException e){
				// Nothing
			}

			gm.getPlayersManager().getPlayersList().remove(uhcPlayer);
		}

		if(gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)){
			UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());
			if(cfg.getEnableKillDisconnectedPlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){
				Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), new KillDisconnectedPlayerThread(event.getPlayer().getUniqueId()),1);
			}
			if(cfg.getSpawnOfflinePlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){
				gm.getPlayersManager().spawnOfflineZombieFor(event.getPlayer());
			}
			gm.getPlayersManager().checkIfRemainingPlayers();
		}
	}

}