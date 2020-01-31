package com.gmail.val59000mc.players;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.entity.Player;

import java.util.List;

public class PredefinedTeam{

    public String name;
    private List<String> members;

    public PredefinedTeam(String name, List<String> members){
        this.name = name;
        this.members = members;
    }

    public boolean containsPlayer(Player player){
        for (String name : members){
            if (name.equalsIgnoreCase(player.getName())){
                return true;
            }
        }

        return false;
    }

    public UhcPlayer getOnlineTeamMember(Player excluding){
        PlayersManager pm = GameManager.getGameManager().getPlayersManager();

        for (UhcPlayer uhcPlayer : pm.getPlayersList()){
            if (uhcPlayer.isOnline() && members.contains(uhcPlayer.getName()) && !uhcPlayer.getName().equals(excluding.getName())){
                return uhcPlayer;
            }
        }

        return null;
    }

}