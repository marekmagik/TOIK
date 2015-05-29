package pl.edu.agh.araucaria.db;

import pl.edu.agh.araucaria.enums.DatabaseType;

/**
 * Created by marekmagik on 2015-05-02.
 */
public class ConnectionConfig {

    private final String address;

    private final String databaseName;

    private final DatabaseType databaseType;

    private final String username;

    private final String password;

    private ConnectionConfig(Builder builder) {
        this.address = builder.address;
        this.databaseName = builder.databaseName;
        this.databaseType = builder.databaseType;
        this.username = builder.username;
        this.password = builder.password;
    }

    public static class Builder {

        private String address;

        private String databaseName;

        private DatabaseType databaseType;

        private String username;

        private String password;

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setDatabaseType(DatabaseType databaseType) {
            this.databaseType = databaseType;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public ConnectionConfig build() {
            return new ConnectionConfig(this);
        }

    }

    public String getAddress() {
        return address;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
