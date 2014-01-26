package com.gadawski.drools.db.tuple;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public interface IDbTupleManager {

    /**
     * Saves left tuple to db.
     * 
     * @param leftTuple
     * @return tuple_id of newly inserted tuple.
     */
    int saveLeftTuple(LeftTuple leftTuple);

    /**
     * Saves right tuple to db.
     * 
     * @param rightTuple
     * @return tuple_id of newly inserted tuple.
     */
    int saveRightTuple(RightTuple rightTuple);

    /**
     * Save fact handle to db.
     * 
     * @param handle
     *            to be saved
     */
    void saveFactHandle(InternalFactHandle handle);

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
     * @param workingMemory
     * @param sink
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    Object readRightTuple(ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException;

    /**
     * @param resultSet
     * @param workingMemory
     * @param sink
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    Object readLeftTuple(ResultSet resultSet) throws IOException,
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
     *            - to be removed.
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
    Integer readLeftTupleId(ResultSet resultSet) throws SQLException;

    /**
     * @param resultSet
     * @return
     * @throws SQLException
     */
    Integer readRightTupleId(ResultSet resultSet) throws SQLException;

    /**
     * @param sinkId
     * @param connection
     * @param statement
     * @param resultSet
     * @return
     * @throws SQLException
     */
    ResultSet getRightTupleResultSet(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet)
            throws SQLException;

    /**
     * Retracts given fact handle.
     * 
     * @param factHandle
     */
    void retractFactHandle(InternalFactHandle factHandle);

    /**
     * Gets FactHandle from db,
     * 
     * @param handleId
     *            - id of fact handle in table.
     * @param workingMemory
     * @return fact handle for given id.
     */
    InternalFactHandle getFactHandle(Integer handleId);

    /**
     * @param tupleId
     * @return
     */
    Object getRightTuple(Integer tupleId);

    /**
     * Removes all left tuples associated with given right tuple that are
     * rightTuple's childs.
     * 
     * @param rightTuple
     *            - it's childs left tuples will be removed from db.
     */
    void removeRightTupleChilds(RightTuple rightTuple);
}
