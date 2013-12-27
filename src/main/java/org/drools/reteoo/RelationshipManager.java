package org.drools.reteoo;

import com.gadawski.util.facts.Relationship;

/**
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public interface RelationshipManager {

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
}
