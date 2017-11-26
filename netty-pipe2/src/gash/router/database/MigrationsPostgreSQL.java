package gash.router.database;

public class MigrationsPostgreSQL {

    public String createMessageTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE IF NOT EXISTS messages ( " +
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
        return sql.append("CREATE SEQUENCE IF NOT EXISTS messages_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
    public String createGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE IF NOT EXISTS groups ( " +
                " gid             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('group_'::text || (substr(md5((random())::text), 1, 4) || (nextval('group_seq'::regclass))::text)),\n" +
                " gname          varchar NOT NULL, " +
                " created        TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " archived       TIMESTAMP\n" +
                ");").toString();
    }
    public String seqGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE IF NOT EXISTS group_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
    public String createUserTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE IF NOT EXISTS users ( " +
                " id             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('group_'::text || (substr(md5((random())::text), 1, 4) || (nextval('user_seq'::regclass))::text)),\n" +
                " email          varchar, " +
                " password          varchar, " +
                " username       varchar,\n" +
                " created        TIMESTAMP CONSTRAINT no_null NOT NULL DEFAULT now(),\n" +
                " archived       TIMESTAMP\n" +
                ");").toString();
    }
    public String seqUserTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE IF NOT EXISTS user_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
    public String createUserInGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE TABLE IF NOT EXISTS usergroups ( " +
                " ugid             varchar PRIMARY KEY CONSTRAINT no_null NOT NULL DEFAULT ('group_'::text || (substr(md5((random())::text), 1, 4) || (nextval('usergroup_seq'::regclass))::text)),\n" +
                " gid              varchar NOT NULL, " +
                " uid              varchar NOT NULL " +
                ");").toString();
    }
    public String seqUserInGroupTable(){
        StringBuilder sql = new StringBuilder();
        return sql.append("CREATE SEQUENCE IF NOT EXISTS usergroup_seq\n" +
                "                    INCREMENT 1\n" +
                "                    MINVALUE 1000\n" +
                "                    MAXVALUE 999999999\n" +
                "                    START 1001\n" +
                "                    CYCLE\n" +
                "            ;").toString();
    }
}

