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
    public int saveLeftTuple(LeftTuple leftTuple) {
        int sinkId = getSinkId(leftTuple.getSink());
        Integer parentId = leftTuple.getParentId();
        return m_jdbcAgendaItemManagerUtil.saveLeftTuple(parentId, sinkId,
                leftTuple);
    }

    @Override
    public int saveRightTuple(RightTuple rightTuple) {
        int sinkId = getSinkId(rightTuple.getRightTupleSink());
        return m_jdbcAgendaItemManagerUtil.saveRightTuple(sinkId, rightTuple);
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
    public Object readObject(ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException {
        return m_jdbcAgendaItemManagerUtil.readObject(resultSet);
    }

    @Override
    public Integer readTupleId(ResultSet resultSet) throws SQLException {
        return m_jdbcAgendaItemManagerUtil.readTupleId(resultSet);
    }

    @Override
    public void closeEverything(Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        JdbcAgendaItemManagerUtil.closeEverything(connection, statement,
                resultSet);
    }

    @Override
    public void removeRightTuple(RightTuple rightTuple) {
        m_jdbcAgendaItemManagerUtil.removeRightTuple(rightTuple.getTupleId(),
                rightTuple.getSinkId());
    }

    @Override
    public void removeLeftTuple(LeftTuple leftTuple) {
        m_jdbcAgendaItemManagerUtil.removeLeftTuple(leftTuple.getTupleId(),
                leftTuple.getSinkId());
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
}
