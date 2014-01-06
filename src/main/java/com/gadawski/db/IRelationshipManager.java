package com.gadawski.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.gadawski.util.facts.AgendaItemRelationship;
import com.gadawski.util.facts.Relationship;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public interface IRelationshipManager {

    /**
     * Creates {@link Relationship} from {@link LeftTuple} and
     * {@link RightTuple}.
     * 
     * @param leftTuple
     * @param rightTuple
     * @param sink
     */
    public Relationship createRelationship(final LeftTuple leftTuple,
            final RightTuple rightTuple, final LeftTupleSink sink);

    /**
     * Creates {@link Relationship} from fact handle object and join node id.
     * 
     * @param joinNodeId
     * @param object
     */
    public Relationship createRelationship(final InternalFactHandle fact,
            final LeftTupleSink sink);

    /**
     * Saves relationship to db.
     * 
     * @param relationship
     *            - {@link Relationship} to save.
     */
    public void saveRelationship(final Relationship relationship);

    /**
     * Gets all relationship for given join node id.
     * 
     * @param joinNodeId
     *            - join node for relationship.
     * @return - list of relationship associated with join node id.
     */
    public List<Relationship> getRalationships(int joinNodeId);

    /**
     * Gets concrete {@link Relationship} for given relationship id.
     * 
     * @param relationshipId
     *            - id of relationship.
     * @return concrete relationship from db.
     */
    public Relationship getRelationiship(Long relationshipId);

    /**
     * @param leftTuple
     * @param rightTuple
     * @param sink
     * @return
     */
    public AgendaItemRelationship createAgendaItemRelationship(
            LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink);

    /**
     * Unwraps hibernate's session from {@link EntityManager} and opens
     * {@link Session}.
     * 
     * @return {@link Session} based on {@link EntityManager}.
     */
    public Session openSession();

    /**
     * @param offset
     * @param i
     * @param j
     * @return
     */
    public List<Relationship> getRelsIterable(int offset, int i, long j);

    /**
     * Creates query that searches relationships for given nodeId.
     * 
     * @param nodeId
     * @return the Query.
     */
    public Query createQueryGetRelsByJoinNodeId(final long nodeId);

    /**
     * Creates {@link ScrollableResults} iterator for given query. Sets
     * read-only mode, fetch size defined in {@link DbRelationshipManager},
     * cacheable to false and ForwardOnly mode.
     * 
     * @param query
     *            to get iterator for.
     * @return {@link ScrollableResults} iterator.
     */
    public ScrollableResults getScrollableResultsIterator(final Query query);
}
