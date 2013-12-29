package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.drools.common.InternalFactHandle;

import com.gadawski.util.db.EntityManagerUtil;
import com.gadawski.util.facts.Relationship;

/**
 * {@link DbRelationshipManager} is responsible for managing relationships,
 * creating, saving and getting from db.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class DbRelationshipManager implements IRelationshipManager {
    /**
     * Instance of {@link DbRelationshipManager}.
     */
    private static DbRelationshipManager INSTANCE = null;
    /**
     * Entity manager util instance.
     */
    private EntityManagerUtil m_entityManagerUtil = EntityManagerUtil
            .getInstance();

    /**
     * 
     */
    private DbRelationshipManager() {
    }

    /**
     * @return instance of {@link DbRelationshipManager}.
     */
    public static synchronized DbRelationshipManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbRelationshipManager();
        }
        return INSTANCE;
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
    public Relationship createRelationship(final int joinNodeId,
            final Object object) {
        return new Relationship(joinNodeId, object);
    }

    @Override
    public void saveRelationship(final Relationship relationship) {
        m_entityManagerUtil.saveRelationship(relationship);
    }

    @Override
    public List<Relationship> getRalationships(int joinNodeId) {
        final CriteriaBuilder builder = m_entityManagerUtil
                .getCriteriaBuilder();
        final CriteriaQuery<Relationship> query = builder
                .createQuery(Relationship.class);
        final Root<Relationship> root = query.from(Relationship.class);
        query.select(root).where(
                builder.equal(root.get("joinNode_ID"), joinNodeId));
        final TypedQuery<Relationship> tQuery = m_entityManagerUtil
                .createQuery(query);
        List<Relationship> results = new ArrayList<Relationship>();
        results = tQuery.getResultList();
        return results;
    }
}
