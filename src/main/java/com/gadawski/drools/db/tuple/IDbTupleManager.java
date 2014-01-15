package com.gadawski.drools.db.tuple;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public interface IDbTupleManager {

    /**
     * @param leftTuple
     */
    void saveLeftTuple(LeftTuple leftTuple);

    /**
     * @param id
     * @return
     */
    List<Object> getLeftTuples(int id);

    /**
     * @param id
     * @return
     */
    List<Object> getRightTuples(int id);

    /**
     * @param rightTuple
     */
    void saveRightTuple(RightTuple rightTuple);

    /**
     * Gets cursor for right tuples from Database.
     * 
     * @param id
     * @param resultSet
     * @param statement
     * @param connection
     * @return
     */
    ResultSet getRightTupleCursor(int id, Connection connection,
            PreparedStatement statement, ResultSet resultSet);

    /**
     * @param id
     * @return
     */
    ResultSet getLeftTupleCursor(int id, Connection connection,
            PreparedStatement statement, ResultSet resultSet);

    /**
     * Reads object from cursor
     * 
     * @param resultSet
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    Object readObject(ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException;

    /**
     * @param connection
     * @param statement
     * @param resultSet
     */
    void closeEverything(Connection connection, PreparedStatement statement,
            ResultSet resultSet);

}
