package com.gmail.val59000mc.scoreboard.placeholders;

import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.Placeholder;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import org.bukkit.entity.Player;

public class TeamNamePlaceholder extends Placeholder{

    public TeamNamePlaceholder(){
        super("team-name");
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType, String placeholder) {
        return uhcPlayer.getTeam().name;
    }

}