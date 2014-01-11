package com.gadawski.drools.db;

import org.drools.common.AgendaItem;
import org.drools.spi.Activation;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public interface IAgendaItemManager {
    /**
     * Save agenda item to db.
     * 
     * @param item
     *            to be saved.
     */
    public void saveAgendaItem(final AgendaItem item);

    /**
     * Get agenda item from db. Where rownum = 1. And deletes them from db.
     * 
     * @return agenda item
     */
    public Activation getNextAgendaItem();

    /**
     * Clears agenda.
     */
    public void clearAgenda();

    /**
     * @return number of agenda items.
     * 
     */
    public int getNumberOfAgendaItems();

    /**
     * @return
     */
    public Activation[] getAndClearActivations();

    /**
     * @return
     */
    public Activation[] getActications();

    /**
     * @param agendaItem
     */
    public void removeAgendaItem(AgendaItem agendaItem);
}
