package com.gmail.val59000mc.players;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.configuration.MainConfiguration;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private final ChatColor[] colors = {
        ChatColor.RED,
        ChatColor.BLUE,
        ChatColor.DARK_GREEN,
        ChatColor.DARK_AQUA,
        ChatColor.DARK_PURPLE,
        ChatColor.YELLOW,
        ChatColor.GOLD,
        ChatColor.GREEN,
        ChatColor.AQUA,
        ChatColor.LIGHT_PURPLE
    };

    private final String[] colorEdits = {
        "",
        "" + ChatColor.BOLD,
        "" + ChatColor.ITALIC,
        "" + ChatColor.UNDERLINE,
        "" + ChatColor.BOLD + "" + ChatColor.ITALIC,
        "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE,
        "" + ChatColor.ITALIC + "" + ChatColor.UNDERLINE,
        "" + ChatColor.ITALIC + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD
    };

    private PlayersManager pm;
    private MainConfiguration cfg;
    private List<String> prefixes;
    private int lastTeamNumber;

    public TeamManager(){
        pm = GameManager.getGameManager().getPlayersManager();
        cfg = GameManager.getGameManager().getConfiguration();
        lastTeamNumber = 0;
        loadPrefixes();
    }

    public List<UhcTeam> getPlayingUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcPlayer player : pm.getPlayersList()){
            if (player.getState() == PlayerState.PLAYING) {
                UhcTeam team = player.getTeam();
                if (!teams.contains(team)) {
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    public List<UhcTeam> getUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcPlayer player : pm.getPlayersList()){

            UhcTeam team = player.getTeam();
            if(!teams.contains(team)) {
                teams.add(team);
            }
        }
        return teams;
    }

    public int getNewTeamNumber(){
        lastTeamNumber++;
        return lastTeamNumber;
    }

    public void loadPrefixes(){
        prefixes = new ArrayList<>();

        for (String colorEdit : colorEdits){
            // When there are enough prefixes for the number of teams (and a new team), break
            if(cfg.getAvoidTeamColorVariations() && prefixes.size() > getUhcTeams().size()){
                break;
            }
            for (ChatColor color : colors){
                prefixes.add(color + colorEdit);
            }
        }
    }

    public void handleFreedTeamPrefix(){
        // The passed team should not be counted in getUhcTeams() since the player is now offline
        if(getUhcTeams().size() <= prefixes.size() - colors.length){
            // We have enough free colors to condense
            // Regenerate prefixes
            loadPrefixes();

            // Change the prefixes of existing teams not in the new list of prefixes
            for (UhcTeam team : getUhcTeams()){
                // If the prefix is not in the list of prefixes
                if(!prefixes.contains(team.getPrefix())){
                    // Give it a new prefix
                    team.setPrefix(getTeamPrefix());
                }
            }
        }
    }

    private List<String> getUsedPrefixes(){
        List<String> used = new ArrayList<>();
        for (UhcTeam team : getUhcTeams()){
            used.add(team.getColor());
        }
        return used;
    }

    public List<String> getFreePrefixes(){
        List<String> used = getUsedPrefixes();
        List<String> free = new ArrayList<>();
        for (String prefix : prefixes){
            if (!used.contains(prefix)){
                free.add(prefix);
            }
        }
        return free;
    }

    public String getTeamPrefix(){
        String prefix = String.valueOf(ChatColor.DARK_GRAY);
        for (String s : prefixes){
            if (!getUsedPrefixes().contains(s)){
                prefix = s;
            }
        }
        return prefix;
    }

    public String getTeamPrefix(String preferenceColor){
        for (String s : getFreePrefixes()){
            if (s.contains(preferenceColor)){
                return s;
            }
        }

        return null;
    }

}