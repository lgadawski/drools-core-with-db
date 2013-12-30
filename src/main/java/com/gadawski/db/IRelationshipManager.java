package com.gadawski.db;

import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;

import com.gadawski.util.facts.Relationship;

/**
 * 
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
}
