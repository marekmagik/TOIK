package pl.edu.agh.araucaria.exceptions;

/**
 * Created by marekmagik on 2015-05-05.
 */
public class DatabaseConnectionException extends Exception {

    /*
    * Define only one constructor.
    * */
    public DatabaseConnectionException(String message, Throwable reason) {
        super(message, reason);
    }

}
