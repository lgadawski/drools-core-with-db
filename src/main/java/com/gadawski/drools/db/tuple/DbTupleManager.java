/**
 * 
 */
package com.gadawski.drools.db.tuple;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.Sink;

import com.gadawski.util.db.jdbc.JdbcAgendaItemManagerUtil;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class DbTupleManager implements IDbTupleManager {
    /**
     * 
     */
    private static DbTupleManager INSTANCE = null;
    /**
     * Entity manager util instance.
     */
    private JdbcAgendaItemManagerUtil m_jdbcAgendaItemManagerUtil;

    /**
     * @return
     */
    public static synchronized DbTupleManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbTupleManager();
            return INSTANCE;
        }
        return INSTANCE;
    }

    private DbTupleManager() {
        this.m_jdbcAgendaItemManagerUtil = JdbcAgendaItemManagerUtil
                .getInstance();
    }

    @Override
    public void saveLeftTuple(LeftTuple leftTuple) {
        int sinkId = getSinkId(leftTuple.getSink());
        m_jdbcAgendaItemManagerUtil.saveLeftTuple(sinkId, leftTuple);
    }

    @Override
    public void saveRightTuple(RightTuple rightTuple) {
        int sinkId = getSinkId(rightTuple.getRightTupleSink());
        m_jdbcAgendaItemManagerUtil.saveRightTuple(sinkId, rightTuple);
    }

    @Override
    public List<Object> getLeftTuples(int sinkId) {
        return m_jdbcAgendaItemManagerUtil.getLeftTuples(sinkId);
    }

    @Override
    public List<Object> getRightTuples(int sinkId) {
        return m_jdbcAgendaItemManagerUtil.getRightTuples(sinkId);
    }

    @Override
    public ResultSet getRightTupleCursor(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        return m_jdbcAgendaItemManagerUtil.getRightTupleCursor(sinkId,
                connection, statement, resultSet);
    }

    @Override
    public ResultSet getLeftTupleCursor(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        return m_jdbcAgendaItemManagerUtil.getLeftTupleCursor(sinkId,
                connection, statement, resultSet);
    }

    @Override
    public Object readObject(ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException {
        return m_jdbcAgendaItemManagerUtil.readObject(resultSet);
    }

    /**
     * @param sink
     * @return
     */
    private int getSinkId(final Sink sink) {
        int sinkId = -1;
        if (sink != null) {
            return sink.getId();
        }
        return sinkId;
    }

    @Override
    public void closeEverything(Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        JdbcAgendaItemManagerUtil.closeEverything(connection, statement, resultSet);
    }
}
