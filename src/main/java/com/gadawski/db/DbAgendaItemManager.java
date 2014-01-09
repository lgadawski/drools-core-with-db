package com.gadawski.db;

import org.drools.common.AgendaItem;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.Activation;

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
//    private final EntityManagerUtil m_jdbcEntityManagerUtil;
    private final JdbcEntityManagerUtil m_jdbcEntityManagerUtil;
    /**
     * Relationship manager.
     */
    private final DbRelationshipManager m_dbRelationshipManager = DbRelationshipManager
            .getInstance();;

    /**
     * 
     */
    private DbAgendaItemManager() {
        m_jdbcEntityManagerUtil = JdbcEntityManagerUtil.getInstance();
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
    public void saveAgendaItem(AgendaItem item) {
        m_jdbcEntityManagerUtil.saveObject(item);
    }

    @Override
    public Activation getNextAgendaItem() {
        final AgendaItem item = (AgendaItem) m_jdbcEntityManagerUtil.getNextAgendaItemObject();
        // TODO possible null pointer exception
        final LeftTuple tuple = RuleTerminalNode.createLeftTuple(
                m_dbRelationshipManager.getRelationiship(item
                        .getRelationshipId()), null);
        item.setTuple(tuple);
        m_jdbcEntityManagerUtil.removeAgendaItem(item);
        return item;
    }

    @Override
    public void clearAgenda() {
        m_jdbcEntityManagerUtil.truncateTable(AGENDA_ITEMS_ENTITY_NAME);
    }

    @Override
    public int getNumberOfAgendaItems() {
        return m_jdbcEntityManagerUtil
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
    @SuppressWarnings("unused")
    private AgendaItem getFirstRow() {
//        final CriteriaBuilder builder = m_jdbcEntityManagerUtil
//                .getCriteriaBuilder();
//        final CriteriaQuery<AgendaItem> query = builder
//                .createQuery(AgendaItem.class);
//        final Root<AgendaItem> root = query.from(AgendaItem.class);
//        final TypedQuery<AgendaItem> tQuery = m_jdbcEntityManagerUtil
//                .getEntityManager().createQuery(query); // yes, it looks
//                                                        // horrible..
//        tQuery.setFirstResult(0);
//        tQuery.setMaxResults(1);
//        return tQuery.getSingleResult();
        return null;
    }
}
