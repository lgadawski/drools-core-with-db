package com.gadawski.util.db;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HibernateDemoTest {
    /**
	 * 
	 */
    private EntityManager m_entityManager;

    @Before
    public void beforeEach() {
//        m_entityManager = EntityManagerUtil.getEntityManagerFactory()
//                .createEntityManager();
    }

    @After
    public void afterEach() {
        m_entityManager.close();
    }

    @Test
    public void testAutoIncrement() {
     /*   EntityTransaction transaction = m_entityManager.getTransaction();
        transaction.begin();

        Car car = Car.createExampleObject();

        // id start at null
        Assert.assertEquals((Long) null, car.getId());

        m_entityManager.persist(car);
        transaction.commit();

        System.out.println("Generated ID: " + car.getId());

        Assert.assertEquals((Long) 1l, car.getId());
        */
    }

    @Test
    public void testHibernateUtilEdited() {
//        EntityManagerUtil.persist(Car.createExampleObject());
    }

}
