package pl.edu.agh.araucaria.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.agh.araucaria.db.utils.BaseRecord;
import pl.edu.agh.araucaria.enums.DatabaseType;
import pl.edu.agh.araucaria.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by marekmagik on 2015-05-02.
 */
public class DAO {

    private static final Logger log = LogManager.getLogger(DAO.class);

    private static final String CONNECTION_REFUSED = "Cannot connect to SQL Server";

    private static final int DATABASE_TIMEOUT_IS_SECS = 5;

    private ConnectionConfig connectionConfig;

    private Connection dbConnection;

    private Long userId;

    public boolean isConnectionAlive() {
        try {
            return dbConnection != null && dbConnection.isValid(DATABASE_TIMEOUT_IS_SECS);
        } catch (SQLException e) {
            return false;
        }
    }

    //TODO: ca³kowita enkapsulacja, public -> private
    public Connection createDatabaseConnection(ConnectionConfig connectionConfig) throws DatabaseConnectionException {
        try {
            if (DatabaseType.SQLSERVER.equals(connectionConfig.getDatabaseType())) {
                StringBuilder addressBuilder = new StringBuilder("jdbc:odbc:Driver={SQL Server};Server=")
                        .append(connectionConfig.getAddress())
                        .append(";Database=")
                        .append(connectionConfig.getDatabaseName());

                dbConnection = DriverManager.getConnection(addressBuilder.toString(),
                        connectionConfig.getUsername(), connectionConfig.getPassword());

            } else if (DatabaseType.MYSQL.equals(connectionConfig.getDatabaseType())) {
                StringBuilder addressBuilder = new StringBuilder("jdbc:mysql://")//
                        .append(connectionConfig.getAddress())
                        .append("/")
                        .append(connectionConfig.getDatabaseName())
                        .append("?user=").append(connectionConfig.getUsername());

                dbConnection = DriverManager.getConnection(addressBuilder.toString());
            }
            return dbConnection;

        } catch (Exception e) {
            log.error(CONNECTION_REFUSED + ": " + e.toString());
            throw new DatabaseConnectionException(CONNECTION_REFUSED, e);
            //TODO: display error in UI layer
//                    JOptionPane.showMessageDialog(Araucaria,
//                            "<html><center><font color=red face=helvetica><b>Unable to connect to database.<br>Please try later.</b></font></center></html>", "Unable to connect",
//                            JOptionPane.ERROR_MESSAGE);
//                    setMessageLabelText("Unable to connect to database. Please try later.");
        }
    }

    public Statement getNewStatement() throws SQLException {
        return isConnectionAlive() ? dbConnection.createStatement() : null;
    }


    public boolean doLogin(ConnectionConfig connectionConfig, String applicationUser, String userPassword) throws DatabaseConnectionException {
        this.connectionConfig = connectionConfig;

        createDatabaseConnection(connectionConfig);

        String userStatement = "user";
        String passwordStatement = "password";
        if (DatabaseType.SQLSERVER.equals(connectionConfig.getDatabaseType())) {
            userStatement = "[user]";
            passwordStatement = "[password]";
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM araucaria_users \nWHERE ")
                .append(userStatement)
                .append(" = ")
                .append("'")
                .append(BaseRecord.escapeQuotes(applicationUser)).append("' AND ")
                .append(passwordStatement)
                .append("  = '")
                .append(BaseRecord.escapeQuotes(userPassword)).append("'");

        ResultSet resultSet = null;
        try (Statement statement = getNewStatement()) {
            resultSet = statement.executeQuery(queryBuilder.toString());

            // If resultSet has any entries, username exists
            if (resultSet.next()) {
                //TODO: move
                //setMessageLabelText("User " + newUser + " logged in.");
                //loggedInUser = newUser;
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("loginUser: " + ex.toString());
        } finally {
            try {
                resultSet.close();
            } catch (Exception ignored) {
            }
        }
        return false;
    }


}
