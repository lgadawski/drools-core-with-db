package com.gadawski.db;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.drools.common.AgendaItem;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.Activation;

import com.gadawski.util.db.EntityManagerUtil;

public class DbAgendaItemManager implements IAgendaItemManager {
    /**
     * Based on annotations, table name.
     */
    private static final String AGENDA_ITEMS_ENTITY_NAME = "AgendaItem";
    // = AgendaItem.class
    // .getAnnotation(Table.class).name();
    /**
     * Instance of {@link DbAgendaItemManager}.
     */
    private static DbAgendaItemManager INSTANCE = null;
    /**
     * Entity manager util instance.
     */
    private final EntityManagerUtil m_entityManagerUtil;
    /**
     * Relationship manager.
     */
    private DbRelationshipManager m_dbRelationshipManager = DbRelationshipManager
            .getInstance();;

    /**
     * 
     */
    private DbAgendaItemManager() {
        m_entityManagerUtil = EntityManagerUtil.getInstance();
    }

    /**
     * @return instance of {@link DbAgendaItemManager}.
     */
    public static synchronized DbAgendaItemManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbAgendaItemManager();
        }
        return INSTANCE;
    }

    @Override
    public void saveAgendaItem(final AgendaItem item) {
        m_entityManagerUtil.saveObject(item);
    }

    @Override
    public Activation getNextAgendaItem() {
        final AgendaItem item = getFirstRow();
        // TODO possible null poniter exception
        LeftTuple tuple = RuleTerminalNode.createLeftTuple(
                m_dbRelationshipManager.getRelationiship(item
                        .getRelationshipId()), null);
        item.setTuple(tuple);
        m_entityManagerUtil.remove(item);
        return item;
    }

    @Override
    public void clearAgenda() {
        m_entityManagerUtil.truncateTable(AGENDA_ITEMS_ENTITY_NAME);
    }

    @Override
    public int getNumberOfAgendaItems() {
        return m_entityManagerUtil
                .getTotalNumberOfRows(AGENDA_ITEMS_ENTITY_NAME);
    }

    @Override
    public Activation[] getAndClearActivations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Activation[] getActications() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeAgendaItem(final AgendaItem agendaItem) {
        // TODO Auto-generated method stub

    }

    /**
     * Creates criteria query and gets first row from table.
     * 
     * @return first row from AgendaItem table.
     */
    private AgendaItem getFirstRow() {
        final CriteriaBuilder builder = m_entityManagerUtil
                .getCriteriaBuilder();
        final CriteriaQuery<AgendaItem> query = builder
                .createQuery(AgendaItem.class);
        final Root<AgendaItem> root = query.from(AgendaItem.class);
        final TypedQuery<AgendaItem> tQuery = m_entityManagerUtil
                .getEntityManager().createQuery(query); // yes, it looks
                                                        // horrible..
        tQuery.setFirstResult(0);
        tQuery.setMaxResults(1);
        return tQuery.getSingleResult();
    }
}
