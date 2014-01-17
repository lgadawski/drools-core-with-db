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
    int saveLeftTuple(LeftTuple leftTuple);

    /**
     * @param rightTuple
     */
    int saveRightTuple(RightTuple rightTuple);

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

    /**
     * Removes rightTuple from right memory.
     * 
     * @param rightTuple
     *            - to be removed.F
     */
    void removeRightTuple(RightTuple rightTuple);

    /**
     * Removes leftTuple from left memory.
     * 
     * @param leftTuple
     */
    void removeLeftTuple(LeftTuple leftTuple);

    /**
     * @param resultSet
     * @return
     * @throws SQLException 
     */
    Integer readTupleId(ResultSet resultSet) throws SQLException;

}
