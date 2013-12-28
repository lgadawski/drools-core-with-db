package org.drools.reteoo;

import java.util.List;

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
    public Relationship createRelationship(int joinNodeId, Object object);

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
