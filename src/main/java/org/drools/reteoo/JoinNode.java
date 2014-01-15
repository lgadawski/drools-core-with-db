/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.drools.base.DroolsQuery;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.PropagationContext;

import com.gadawski.drools.config.MyAppConfig;
import com.gadawski.drools.db.tuple.DbTupleManager;
import com.gadawski.drools.db.tuple.IDbTupleManager;
import com.gadawski.util.db.jdbc.JdbcAgendaItemManagerUtil;
import com.gadawski.util.db.jdbc.Statements;

public class JoinNode extends BetaNode {

    private static final long serialVersionUID = 510l;
    /**
     * 
     */
    private transient IDbTupleManager m_tupleManager;
    /**
     * 
     */
    private JdbcAgendaItemManagerUtil m_jdbcManager;

    public JoinNode() {

    }

    public JoinNode(final int id,
                    final LeftTupleSource leftInput,
                    final ObjectSource rightInput,
                    final BetaConstraints binder,
                    final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               binder,
               context );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        this.lrUnlinkingEnabled = context.getRuleBase().getConfiguration().isLRUnlinkingEnabled();
    }

    public void assertLeftTuple( final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( lrUnlinkingEnabled &&
             leftUnlinked( context,
                           workingMemory,
                           memory ) ) {
            return;
        }        

        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        ContextEntry[] contextEntry = memory.getContext();
//        boolean useLeftMemory = true; 
        boolean useLeftMemory = !MyAppConfig.USE_DB;       
        
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }
        
        if ( useLeftMemory ) {
            memory.getLeftTupleMemory().add( leftTuple );
        } else {
            m_tupleManager = DbTupleManager.getInstance();
            m_tupleManager.saveLeftTuple(leftTuple);
        }
        
        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );

        FastIterator it = getRightIterator( rightMemory );

        if (MyAppConfig.USE_DB) {
            getAndPropagateSerialDBTupleFromLeft(contextEntry,
                    useLeftMemory, context, workingMemory, leftTuple);
        } else {
            for (RightTuple rightTuple = getFirstRightTuple(leftTuple,
                    rightMemory, context, it); rightTuple != null; rightTuple = (RightTuple) it
                    .next(rightTuple)) {

                propagateFromLeft(rightTuple, leftTuple, contextEntry,
                        useLeftMemory, context, workingMemory);
            }
        }

        this.constraints.resetTuple( contextEntry );
    }

    /**
     * @param contextEntry
     * @param useLeftMemory
     * @param context
     * @param workingMemory
     * @param leftTuple
     */
    private void getAndPropagateSerialDBTupleFromLeft(
            ContextEntry[] contextEntry, boolean useLeftMemory,
            PropagationContext context, InternalWorkingMemory workingMemory,
            LeftTuple leftTuple) {
        m_jdbcManager = JdbcAgendaItemManagerUtil.getInstance();
        m_tupleManager = DbTupleManager.getInstance();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = m_jdbcManager.getConnection();
            connection.setAutoCommit(false);
            statement = connection
                    .prepareStatement(Statements.SELECT_RIGHT_TUPLES);
            statement.setInt(1, this.getId());
            statement.setFetchSize(JdbcAgendaItemManagerUtil.FETCH_SIZE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RightTuple tuple = (RightTuple) m_tupleManager
                        .readObject(resultSet);
                tuple.restoreTupleAfterSerialization(workingMemory, this);
                propagateFromLeft(tuple, leftTuple, contextEntry,
                        useLeftMemory, context, workingMemory);
            }
            connection.setAutoCommit(true);
            m_tupleManager.closeEverything(connection, statement, resultSet); // release
        } catch (SQLException e) {
            // TODO handle exceptions!
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void propagateFromLeft( RightTuple rightTuple, LeftTuple leftTuple, ContextEntry[] contextEntry, boolean useLeftMemory, PropagationContext context, InternalWorkingMemory workingMemory ) {
        final InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.constraints.isAllowedCachedLeft( contextEntry,
                handle ) ) {
            this.sink.propagateAssertLeftTuple( leftTuple,
                    rightTuple,
                    null,
                    null,
                    context,
                    workingMemory,
                    useLeftMemory );
        }
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

        if ( lrUnlinkingEnabled &&
             rightUnlinked( context,
                            workingMemory,
                            memory ) ) {
            context.getPropagationAttemptsMemory().add( this );
            return;
        }

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  context );

        if (MyAppConfig.USE_DB) {
            m_tupleManager = DbTupleManager.getInstance();
            m_tupleManager.saveRightTuple(rightTuple);
        } else {
            memory.getRightTupleMemory().add( rightTuple );
        }
        
        if ( !MyAppConfig.USE_DB ) {
            if ( memory.getLeftTupleMemory() == null || memory.getLeftTupleMemory().size() == 0 ) {
                // do nothing here, as no left memory
                return;
            }
        }

        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               factHandle );

        if ( MyAppConfig.USE_DB ) {
            getAndPropagateSerialDBTupleFromRight(context, workingMemory, memory, rightTuple);
        } else {
            FastIterator it = getLeftIterator( leftMemory );
            for ( LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                propagateFromRight( rightTuple, leftTuple, memory, context, workingMemory );
            }
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    /**
     * @param context
     * @param workingMemory
     * @param memory
     * @param rightTuple
     */
    private void getAndPropagateSerialDBTupleFromRight(
            PropagationContext context, InternalWorkingMemory workingMemory,
            BetaMemory memory, RightTuple rightTuple) {
        m_jdbcManager = JdbcAgendaItemManagerUtil.getInstance();
        m_tupleManager = DbTupleManager.getInstance();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            connection = m_jdbcManager.getConnection();
            connection.setAutoCommit(false);

            statement = connection
                    .prepareStatement(Statements.SELECT_LEFT_TUPLES);
            statement.setInt(1, this.getId());
            statement.setFetchSize(JdbcAgendaItemManagerUtil.FETCH_SIZE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LeftTuple tuple = (LeftTuple) m_tupleManager
                        .readObject(resultSet);
                tuple.restoreTupleAfterSerialization(workingMemory, this);
                propagateFromRight(rightTuple, tuple, memory, context,
                        workingMemory);
            }
            connection.setAutoCommit(true);
            m_tupleManager.closeEverything(connection, statement, resultSet);// release
        } catch (SQLException e) {
            // TODO handle exceptions
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void propagateFromRight( RightTuple rightTuple, LeftTuple leftTuple, BetaMemory memory, PropagationContext context, InternalWorkingMemory workingMemory ) {
        if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                leftTuple ) ) {
            // wm.marshaller.write( i, leftTuple )
            this.sink.propagateAssertLeftTuple( leftTuple,
                    rightTuple,
                    null,
                    null,
                    context,
                    workingMemory,
                    true );
        }
    }



    public void retractRightTuple( final RightTuple rightTuple,
                                   final PropagationContext context,
                                   final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( lrUnlinkingEnabled &&
             memory.isRightUnlinked() ) {
            return;
        }

        memory.getRightTupleMemory().remove( rightTuple );

        if ( rightTuple.firstChild != null ) {
            this.sink.propagateRetractRightTuple( rightTuple,
                                                  context,
                                                  workingMemory );
        }
    }

    public void retractLeftTuple( final LeftTuple leftTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( lrUnlinkingEnabled &&
             memory.isLeftUnlinked() ) {
            return;
        }

        memory.getLeftTupleMemory().remove( leftTuple );
        if ( leftTuple.getFirstChild() != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    public void modifyRightTuple( final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // WTD here
        //                if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
        //                                                 rightTuple,
        //                                                 workingMemory ) ) {
        //                    // destroy right tuple
        //                    rightTuple.unlinkFromRightParent();
        //                    return;
        //                }

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.getRightTupleMemory().removeAdd( rightTuple );

        if ( memory.getLeftTupleMemory() != null && memory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples.
            return;
        }

        LeftTuple childLeftTuple = rightTuple.firstChild;

        LeftTupleMemory leftMemory = memory.getLeftTupleMemory();


        FastIterator it = getLeftIterator( leftMemory );        
        LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, context, it );
        
        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // We assume a bucket change if leftTuple == null        
        if ( childLeftTuple != null && leftMemory.isIndexed() && !it.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
            // our index has changed, so delete all the previous propagations
            this.sink.propagateRetractRightTuple( rightTuple,
                                                  context,
                                                  workingMemory );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if LeftTupleMemory is empty
        if ( leftTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    propagateFromRight( rightTuple, leftTuple, memory, context, workingMemory );
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    childLeftTuple = propagateOrModifyFromRight( rightTuple, leftTuple, childLeftTuple, memory, context, workingMemory );
                }
            }
        }

        this.constraints.resetFactHandle( memory.getContext() );
    }

    protected LeftTuple propagateOrModifyFromRight( RightTuple rightTuple, LeftTuple leftTuple, LeftTuple childLeftTuple, BetaMemory memory, PropagationContext context, InternalWorkingMemory workingMemory ) {
        if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                    leftTuple ) ) {
            if ( childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    childLeftTuple,
                                                    context,
                                                    workingMemory,
                                                    true );
            } else {
                childLeftTuple = this.sink.propagateModifyChildLeftTuple( childLeftTuple,
                                                                          leftTuple,
                                                                          context,
                                                                          workingMemory,
                                                                          true );
            }
        } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
            childLeftTuple = this.sink.propagateRetractChildLeftTuple( childLeftTuple,
                                                                       leftTuple,
                                                                       context,
                                                                       workingMemory );
        }
        // else do nothing, was false before and false now.
        return childLeftTuple;
    }

    public void modifyLeftTuple( final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory ) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        ContextEntry[] contextEntry = memory.getContext();

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.getLeftTupleMemory().removeAdd( leftTuple );

        this.constraints.updateFromTuple( contextEntry,
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = leftTuple.getFirstChild();

        RightTupleMemory rightMemory = memory.getRightTupleMemory();

        FastIterator it = getRightIterator( rightMemory );

        RightTuple rightTuple = getFirstRightTuple( leftTuple,
                                                    rightMemory,
                                                    context,
                                                    it );
        
        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // if rightTuple is null, we assume there was a bucket change and that bucket is empty        
        if ( childLeftTuple != null && rightMemory.isIndexed() && !it.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory())) ) {
            // our index has changed, so delete all the previous propagations
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if RightTupleMemory is empty
        if ( rightTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    propagateFromLeft( rightTuple, leftTuple, contextEntry, true, context, workingMemory );
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                    childLeftTuple = propagateOrModifyFromLeft( rightTuple, leftTuple, childLeftTuple, contextEntry, context, workingMemory );
                }
            }
        }

        this.constraints.resetTuple( contextEntry );
    }

    protected LeftTuple propagateOrModifyFromLeft( RightTuple rightTuple,
                                                   LeftTuple leftTuple,
                                                   LeftTuple childLeftTuple,
                                                   ContextEntry[] contextEntry,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory ) {
        final InternalFactHandle handle = rightTuple.getFactHandle();

        if ( this.constraints.isAllowedCachedLeft( contextEntry,
                                                   handle ) ) {
            if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    childLeftTuple,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    true );
            } else {
                childLeftTuple = this.sink.propagateModifyChildLeftTuple( childLeftTuple,
                                                                          rightTuple,
                                                                          context,
                                                                          workingMemory,
                                                                          true );
            }
        } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
            childLeftTuple = this.sink.propagateRetractChildLeftTuple( childLeftTuple,
                                                                       rightTuple,
                                                                       context,
                                                                       workingMemory );
        }
        // else do nothing, was false before and false now.
        return childLeftTuple;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink( final LeftTupleSink sink,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory ) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        FastIterator it = memory.getLeftTupleMemory().fastIterator();

        @SuppressWarnings("rawtypes")
        final Iterator tupleIter = memory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              leftTuple );
            for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( leftTuple, (InternalFactHandle) context.getFactHandle(), it );
                  rightTuple != null; rightTuple = (RightTuple) it.next( rightTuple ) ) {
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           rightTuple.getFactHandle() ) ) {
                    sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
                                                                rightTuple,
                                                                null,
                                                                null,
                                                                sink,
                                                                true ),
                                          context,
                                          workingMemory );
                }
            }

            this.constraints.resetTuple( memory.getContext() );
        }
    }

    @Override
    public void modifyLeftTuple( InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory ) {

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( lrUnlinkingEnabled &&
             memory.isLeftUnlinked() ) return;

        super.modifyLeftTuple( factHandle,
                               modifyPreviousTuples,
                               context,
                               workingMemory );
    }

    @Override
    public void modifyObject( InternalFactHandle factHandle,
                              ModifyPreviousTuples modifyPreviousTuples,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory ) {

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        if ( lrUnlinkingEnabled &&
             memory.isRightUnlinked() ) return;

        super.modifyObject( factHandle,
                            modifyPreviousTuples,
                            context,
                            workingMemory );
    }

    public short getType() {
        return NodeTypeEnums.JoinNode;
    }

  
    
    public String toString() {
        return "[JoinNode(" + this.getId() + ") - " + getObjectTypeNode().getObjectType() + "]";
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
}
