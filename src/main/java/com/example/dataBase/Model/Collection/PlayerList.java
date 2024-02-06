package com.example.dataBase.Model.Collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.dataBase.Model.BaseEntity;
import com.example.dataBase.Model.PlayerModel;

public class PlayerList extends ArrayList<PlayerModel> {

    public PlayerList() {
        super();
    }

    public PlayerList(Collection<? extends BaseEntity> lst) {
        super(lst.stream().map(item -> (PlayerModel) item).collect(Collectors.toList()));
    }

    public List<PlayerModel> orderByName() {
        Comparator<? super PlayerModel> comp = null;
        if (this.size() == 0) {
            return null;
        } else {
            comp = new Comparator<PlayerModel>() {
                @Override
                public int compare(PlayerModel player1, PlayerModel player2) {
                    return player1.getPlayerName().compareTo(player2.getPlayerName());
                }
            };
        }
        this.sort(comp);
        return this;
    }
}
