package com.example.dataBase.Model.Collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.dataBase.Model.BaseEntity;
import com.example.dataBase.Model.GameModel;

public class GameList extends ArrayList<GameModel> {
    public GameList() {
        super();
    }

    public GameList(Collection<? extends BaseEntity> lst) {
        super(lst.stream().map(item -> (GameModel) item).collect(Collectors.toList()));
    }

    public List<GameModel> orderByGameID() {
        Comparator<? super GameModel> comp = null;
        if (this.size() == 0) {
            return null;
        } else {
            comp = new Comparator<GameModel>() {
                @Override
                public int compare(GameModel game1, GameModel game2) {
                    return game1.getId() - game2.getId();
                }
            };
        }
        this.sort(comp);
        return this;
    }

    public List<GameModel> orderByBoardSize() {
        Comparator<? super GameModel> comp = null;
        if (this.size() == 0) {
            return null;
        } else {
            comp = new Comparator<GameModel>() {
                @Override
                public int compare(GameModel game1, GameModel game2) {
                    return game1.getBoardSize() - game2.getBoardSize();
                }
            };
        }
        this.sort(comp);
        return this;
    }
}
