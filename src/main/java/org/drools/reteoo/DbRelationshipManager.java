package org.drools.reteoo;

import org.drools.common.InternalFactHandle;

import com.gadawski.util.db.EntityManagerUtil;
import com.gadawski.util.facts.Relationship;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class DbRelationshipManager implements RelationshipManager {
    /**
     * Instance of {@link DbRelationshipManager}.
     */
    private static DbRelationshipManager INSTANCE;

    /**
     * 
     */
    private DbRelationshipManager() {
    }

    /**
     * @return instance of {@link DbRelationshipManager}.s
     */
    public static DbRelationshipManager getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            return new DbRelationshipManager();
        }
    }

    @Override
    public Relationship createRelationship(final LeftTuple leftTuple,
            final RightTuple rightTuple, final LeftTupleSink sink) {
        final Relationship relationship = new Relationship();
        relationship.setJoinNode((long) sink.getId());
        // add right fact
        relationship.setObject(rightTuple.getFactHandle().getObject());
        // add left facts
        final InternalFactHandle[] leftFacts = leftTuple.getFactHandles();
        for (final InternalFactHandle internalFactHandle : leftFacts) {
            relationship.setObject(internalFactHandle.getObject());
        }
        return relationship;
    }

    @Override
    public Relationship createRelationship(final int joinNodeId, final Object object) {
        return new Relationship(joinNodeId, object);
    }

    @Override
    public void saveRelationship(final Relationship relationship) {
        final EntityManagerUtil entityManagerUtil = EntityManagerUtil.getInstance();
        entityManagerUtil.saveRelationship(relationship);
    }

}
