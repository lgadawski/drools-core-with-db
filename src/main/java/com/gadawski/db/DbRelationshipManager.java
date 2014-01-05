package com.gadawski.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;
import org.hibernate.Session;

import com.gadawski.util.db.EntityManagerUtil;
import com.gadawski.util.facts.AgendaItemRelationship;
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
    private final EntityManagerUtil m_entityManagerUtil;

    /**
     * 
     */
    private DbRelationshipManager() {
        m_entityManagerUtil = EntityManagerUtil.getInstance();
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
    public AgendaItemRelationship createAgendaItemRelationship(
            final LeftTuple leftTuple, final RightTuple rightTuple,
            final LeftTupleSink sink) {
        final Relationship relationship = createRelationship(leftTuple,
                rightTuple, sink);
        final AgendaItemRelationship aRelationship = new AgendaItemRelationship(
                relationship);
        return aRelationship;
    }

    @Override
    public Relationship createRelationship(final InternalFactHandle fact,
            final LeftTupleSink sink) {
        return new Relationship(sink.getId(), fact.getObject());
    }

    @Override
    public void saveRelationship(final Relationship relationship) {
        m_entityManagerUtil.saveRelationship(relationship);
    }

    @Override
    public List<Relationship> getRalationships(final int joinNodeId) {
        // think about named query
        final CriteriaBuilder builder = m_entityManagerUtil
                .getCriteriaBuilder();
        final CriteriaQuery<Relationship> query = builder
                .createQuery(Relationship.class);
        final Root<Relationship> root = query.from(Relationship.class);
        query.select(root).where(
                builder.equal(root.get("joinNode_ID"), joinNodeId));
        final TypedQuery<Relationship> tQuery = m_entityManagerUtil
                .createQuery(query);
        final List<Relationship> results = new ArrayList<Relationship>();
        results.addAll(tQuery.getResultList());
        m_entityManagerUtil.clear();
        return results;
    }

    @Override
    public Relationship getRelationiship(final Long relationshipId) {
        // code repetitions! ugly!
        final CriteriaBuilder builder = m_entityManagerUtil
                .getCriteriaBuilder();
        final CriteriaQuery<Relationship> query = builder
                .createQuery(Relationship.class);
        final Root<Relationship> root = query.from(Relationship.class);
        query.select(root).where(
                builder.equal(root.get("relationshipID"), relationshipId));
        final TypedQuery<Relationship> tQuery = m_entityManagerUtil
                .createQuery(query);
        return tQuery.getSingleResult();
    }

    @Override
    public Session openSession() {
        return m_entityManagerUtil.openSession();
    }

    @Override
    public List<Relationship> getRelsIterable(int offset, int max, long nodeId) {
        Query query = m_entityManagerUtil.getEntityManager()
        .createNamedQuery(Relationship.FIND_RELS_BY_JOINNODE_ID)
        .setParameter("nodeId", nodeId).setFirstResult(offset)
        .setMaxResults(max);
        List<Relationship> rels = new ArrayList<Relationship>();
        rels.addAll(query.getResultList());
        return rels;
    }
}
