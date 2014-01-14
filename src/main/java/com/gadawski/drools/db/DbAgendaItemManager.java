package com.gadawski.drools.db;

import org.drools.common.AgendaItem;
import org.drools.spi.Activation;

import com.gadawski.util.db.jdbc.JdbcAgendaItemManagerUtil;

public class DbAgendaItemManager implements IAgendaItemManager {
    // = AgendaItem.class
    // .getAnnotation(Table.class).name();
    /**
     * Instance of {@link DbAgendaItemManager}.
     */
    private static DbAgendaItemManager INSTANCE = null;
    /**
     * Entity manager util instance.
     */
    private final JdbcAgendaItemManagerUtil m_jdbcAgendaItemManagerUtil;

    /**
     * 
     */
    private DbAgendaItemManager() {
        m_jdbcAgendaItemManagerUtil = JdbcAgendaItemManagerUtil.getInstance();
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
        m_jdbcAgendaItemManagerUtil.saveObject(item);
    }

    @Override
    public Activation getNextAgendaItem() {
        final AgendaItem item = (AgendaItem) m_jdbcAgendaItemManagerUtil
                .getNextAgendaItemObject();
        m_jdbcAgendaItemManagerUtil.removeNextAgendaItem();
        return item;
    }

    @Override
    public void clearAgenda() {
        m_jdbcAgendaItemManagerUtil.truncateAgendaItems();
    }

    @Override
    public int getNumberOfAgendaItems() {
        return m_jdbcAgendaItemManagerUtil.getTotalNumberOfRows();
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
        // final CriteriaBuilder builder = m_jdbcEntityManagerUtil
        // .getCriteriaBuilder();
        // final CriteriaQuery<AgendaItem> query = builder
        // .createQuery(AgendaItem.class);
        // final Root<AgendaItem> root = query.from(AgendaItem.class);
        // final TypedQuery<AgendaItem> tQuery = m_jdbcEntityManagerUtil
        // .getEntityManager().createQuery(query); // yes, it looks
        // // horrible..
        // tQuery.setFirstResult(0);
        // tQuery.setMaxResults(1);
        // return tQuery.getSingleResult();
        return null;
    }
}
