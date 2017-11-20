package gash.router.database;

public class MigrationsPostgreSQL {

    public String createMessageTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE messages ( " +
                " id             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('msg_'::text || (substr(md5((random())::text), 1, 4) || (nextval('messages_seq'::regclass))::text)),\n" +
                " message        varchar NOT NULL, " +
                " to_id          varchar CONSTRAINT no_null NOT NULL,\n" +
                " from_id        varchar CONSTRAINT no_null NOT NULL,\n" +
                " created        TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " archived       TIMESTAMP\n" +
                ");").toString();
    }
    public String seqMessageTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE messages_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
    public String createGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE groups ( " +
                " gid             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('group_'::text || (substr(md5((random())::text), 1, 4) || (nextval('group_seq'::regclass))::text)),\n" +
                " gname          varchar NOT NULL, " +
                " created        TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " archived       TIMESTAMP\n" +
                ");").toString();
    }
    public String seqGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE group_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
    public String createUserTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE users ( " +
                " id             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('group_'::text || (substr(md5((random())::text), 1, 4) || (nextval('user_seq'::regclass))::text)),\n" +
                " email          varchar NOT NULL, " +
                " password          varchar NOT NULL, " +
                " username       varchar,\n" +
                " recentActiveTime TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " created        TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " archived       TIMESTAMP\n" +
                ");").toString();
    }
    public String seqUserTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE user_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }

}

