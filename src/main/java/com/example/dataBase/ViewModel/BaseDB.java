package com.example.dataBase.ViewModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.example.dataBase.Model.BaseEntity;

public abstract class BaseDB extends BaseEntity {
    private static final String URL_PATH = "jdbc:mysql://localhost/Tic_tac_toe_games";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "tt112233";

    private Connection connection;
    private Statement stmt;
    protected ResultSet res;

    protected abstract BaseEntity createModel(BaseEntity entity, ResultSet res) throws SQLException;

    protected abstract BaseEntity newEntity();

    protected ArrayList<ChangeEntity> inserted = new ArrayList<>();
    protected ArrayList<ChangeEntity> updated = new ArrayList<>();
    protected ArrayList<ChangeEntity> deleted = new ArrayList<>();

    public abstract String createInsertSql(BaseEntity entity);

    public abstract String createUpdateSql(BaseEntity entity);

    public abstract String createDeleteSql(BaseEntity entity);

    public BaseDB() {
        super();
        try {
            // Loading driver
            Class.forName(DRIVER_CLASS);
            // Connecting database...
            connection = DriverManager.getConnection(URL_PATH, username, password);
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BaseEntity> select(String sqlStr) {
        ArrayList<BaseEntity> list = new ArrayList<BaseEntity>();
        try (Statement stmt = connection.createStatement();
                ResultSet res = stmt.executeQuery(sqlStr)) {
            while (res.next()) {
                BaseEntity entity = newEntity();
                createModel(entity, res); // Now passing ResultSet as parameter
                list.add(entity);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public int saveChanges() {
        int rows = 0;
        String sqlStr = "";
        try {
            Statement statement = connection.createStatement();
            for (ChangeEntity item : inserted) {
                sqlStr = item.getSqlCreator().CreateSql(item.getEntity());
                rows += statement.executeUpdate(sqlStr, Statement.RETURN_GENERATED_KEYS);

                res = statement.getGeneratedKeys();
                if (res.next()) {
                    item.getEntity().setId(res.getInt(1));
                }
            }
            for (ChangeEntity item : updated) {
                sqlStr = item.getSqlCreator().CreateSql(item.getEntity());
                rows += statement.executeUpdate(sqlStr);
            }
            for (ChangeEntity item : deleted) {
                sqlStr = item.getSqlCreator().CreateSql(item.getEntity());
                rows += statement.executeUpdate(sqlStr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "\nSQL: " + sqlStr);
        } finally {
            inserted.clear();
            updated.clear();
            deleted.clear();
        }
        return rows;
    }

    protected abstract BaseDB me();

    public void insert(BaseEntity entity) {
        BaseEntity reqEntity = this.newEntity();
        BaseDB me = this.me();
        SQLCreator sqlCreator = null;
        if (entity != null && entity.getClass() == reqEntity.getClass()) {
            sqlCreator = new SQLCreator() {
                @Override
                public String CreateSql(BaseEntity entity) {
                    return me.createInsertSql(entity);
                }
            };
        }
        ChangeEntity changeEntity = new ChangeEntity(entity, sqlCreator);
        inserted.add(changeEntity);
    }

    public void update(BaseEntity entity) {
        BaseEntity reqEntity = this.newEntity();
        BaseDB me = this.me();
        SQLCreator sqlCreator = null;
        if (entity != null && entity.getClass() == reqEntity.getClass()) {
            sqlCreator = new SQLCreator() {
                @Override
                public String CreateSql(BaseEntity entity) {
                    return me.createInsertSql(entity);
                }
            };
            ChangeEntity changeEntity = new ChangeEntity(entity, sqlCreator);
            updated.add(changeEntity);
        }
    }

    public void delete(BaseEntity entity) {
        BaseEntity reqEntity = this.newEntity();
        BaseDB me = this.me();
        SQLCreator sqlCreator = null;
        if (entity != null && entity.getClass() == reqEntity.getClass()) {
            sqlCreator = new SQLCreator() {
                @Override
                public String CreateSql(BaseEntity entity) {
                    return me.createInsertSql(entity);
                }
            };
            ChangeEntity changeEntity = new ChangeEntity(entity, sqlCreator);
            deleted.add(changeEntity);
        }
    }

}
