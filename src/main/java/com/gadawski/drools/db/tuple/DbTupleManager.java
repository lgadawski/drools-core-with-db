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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.Sink;

import com.gadawski.util.db.jdbc.JdbcManagerUtil;

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
    private JdbcManagerUtil m_jdbcManager;

    /**
     * @return
     */
    public static synchronized DbTupleManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DbTupleManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DbTupleManager();
                }
            }
            return INSTANCE;
        }
        return INSTANCE;
    }

    private DbTupleManager() {
        this.m_jdbcManager = JdbcManagerUtil.getInstance();
    }

    @Override
    public int saveLeftTuple(LeftTuple leftTuple) {
        int sinkId = getSinkId(leftTuple.getSink());
        Integer parentId = leftTuple.getParentId();
        Integer handleId = leftTuple.getHandleId();
        Integer parentRightTupleId = leftTuple.getParentRightTupleId();
        Integer tupleId = m_jdbcManager.saveLeftTuple(parentId, handleId,
                parentRightTupleId, sinkId, leftTuple);
        if (tupleId != null) {
            leftTuple.setTupleId(tupleId);
        }
        return tupleId;
    }

    @Override
    public int saveRightTuple(RightTuple rightTuple) {
        int sinkId = getSinkId(rightTuple.getRightTupleSink());
        Integer handleId = rightTuple.getHandleId();
        int tupleId = m_jdbcManager
                .saveRightTuple(handleId, sinkId, rightTuple);
        rightTuple.setTupleId(tupleId);
        return tupleId;
    }

    @Override
    public void saveFactHandle(InternalFactHandle handle) {
        int handleId = handle.getId();
        m_jdbcManager.saveFactHandle(handleId, handle);
    }

    @Override
    public void updateRightTuple(RightTuple rightTuple) {
        m_jdbcManager.updateRightTuple(rightTuple.getTupleId(), rightTuple);
    }

    @Override
    public List<Object> getLeftTuples(int sinkId) {
        return m_jdbcManager.getLeftTuples(sinkId);
    }

    @Override
    public List<Object> getRightTuples(int sinkId) {
        return m_jdbcManager.getRightTuples(sinkId);
    }

    @Override
    public Object getFactHandle(Integer handleId) {
        InternalFactHandle handle = (InternalFactHandle) m_jdbcManager
                .getFactHandle(handleId);
        return handle;
    }

    @Override
    public Object getRightTuple(Integer tupleId) {
        RightTuple tuple = (RightTuple) m_jdbcManager.getRightTuple(tupleId);
        tuple.restoreTupleAfterSerialization();
        return tuple;
    }

    @Override
    public RightTuple readRightTuple(ResultSet resultSet,
            InternalWorkingMemory workingMemory, Sink sink) throws IOException,
            ClassNotFoundException, SQLException {
        RightTuple tuple = (RightTuple) m_jdbcManager.readObject(resultSet);
        tuple.restoreTupleAfterSerialization();
        return tuple;
    }

    @Override
    public LeftTuple readLeftTuple(ResultSet resultSet,
            InternalWorkingMemory workingMemory, Sink sink) throws IOException,
            ClassNotFoundException, SQLException {
        LeftTuple tuple = (LeftTuple) m_jdbcManager.readObject(resultSet);
        tuple.setTupleId(m_jdbcManager.readLeftTupleId(resultSet));
        tuple.restoreTupleAfterSerialization(sink);
        return tuple;
    }

    @Override
    public Integer readLeftTupleId(ResultSet resultSet) throws SQLException {
        return m_jdbcManager.readLeftTupleId(resultSet);
    }

    @Override
    public Integer readRightTupleId(ResultSet resultSet) throws SQLException {
        return m_jdbcManager.readRightTupleId(resultSet);
    }

    @Override
    public void closeEverything(Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        JdbcManagerUtil.closeEverything(connection, statement, resultSet);
    }

    @Override
    public void removeRightTupleChilds(RightTuple rightTuple) {
        Integer childRightTupleId = rightTuple.getTupleId();
        if (childRightTupleId != null) {
            m_jdbcManager.removeRightTupleChilds(childRightTupleId);
        }
    }

    @Override
    public void removeRightTuple(RightTuple rightTuple) {
        Integer tupleId = rightTuple.getTupleId();
        if (tupleId != null) {
            m_jdbcManager.removeRightTuple(tupleId, rightTuple.getSinkId());
        }
    }

    @Override
    public void removeLeftTuple(LeftTuple leftTuple) {
        Integer tupleId = leftTuple.getTupleId();
        if (tupleId != null) {
            m_jdbcManager.removeLeftTuple(tupleId, leftTuple.getSinkId());
        }
    }

    @Override
    public void retractFactHandle(InternalFactHandle factHandle) {
        int id = factHandle.getId();
        if (id >= 0) {
            m_jdbcManager.removeFactHandle(id);
        }
    }

    @Override
    public ResultSet getRightTupleResultSet(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet)
            throws SQLException {
        return null;
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
