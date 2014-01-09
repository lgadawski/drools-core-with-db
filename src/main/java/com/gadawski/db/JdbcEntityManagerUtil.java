package com.gadawski.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.drools.common.AgendaItem;

/**
 * Utility class that uses JDBC connections to persist data.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
/**
 * @author l.gadawski@gmail.com
 * 
 */
public class JdbcEntityManagerUtil {
    /**
     * 
     */
    private static final String DRIVER_PACKAGE = "oracle.jdbc.driver.OracleDriver";
    /**
     * 
     */
    private static final String CONNECTION_URL = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
    /**
     * 
     */
    private static final String USER_NAME = "gadon";
    /**
     * 
     */
    private static final String PASSWORD = "abelrm";
    /**
     * 
     */
    private static final String INSERT_STATEMENT = "INSERT into A_AGENDA_ITEMS "
            + "(agenda_item_id, agenda_object) values (a_agenda_items_seq.NEXTVAL, ?)";
    /**
     * 
     */
    private static final String SELECT_ROW = "SELECT * from A_AGENDA_ITEMS where agenda_item_id = ";
    /**
     * Instance.
     */
    private static final JdbcEntityManagerUtil INSTANCE = null;
    /**
     * 
     */
    private Connection connection = null;

    /**
     * Private constructor to block creating objects.
     */
    private JdbcEntityManagerUtil() {

    }

    /**
     * @return instance of {@link JdbcEntityManagerUtil} class.
     */
    public static synchronized JdbcEntityManagerUtil getInstance() {
        if (INSTANCE == null) {
            return new JdbcEntityManagerUtil();
        }
        return INSTANCE;
    }

    /**
     * @return
     */
    public AgendaItem getNextAgendaItemObject() {
//        return getObject(objectId);
        return null;
    }

    /**
     * 
     * @param object
     *            to be saved.
     */
    public void saveObject(final Object object) {
        initilizeConnection();
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            byte[] data = byteOutputStream.toByteArray();

            PreparedStatement statement = connection
                    .prepareStatement(INSERT_STATEMENT);
            statement.setObject(1, null); // set agenda_item_id null to be
                                          // generated from seq
            statement.setObject(1, data);
            statement.executeUpdate();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param objectId
     * @return
     */
    public Object getObject(int objectId) {
        initilizeConnection();
        try {
            PreparedStatement statement = connection
                    .prepareStatement(SELECT_ROW + objectId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        resultSet.getBytes("agenda_object"));
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        inputStream);

                Object object = objectInputStream.readObject();
                objectInputStream.close();
                return object;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param item
     */
    public void remove(final AgendaItem item) {
        // TODO Auto-generated method stub

    }

    /**
     * @param agendaItemsEntityName
     */
    public void truncateTable(final String agendaItemsEntityName) {
        // TODO Auto-generated method stub

    }

    public int getTotalNumberOfRows(final String agendaItemsEntityName) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Initializes connection.
     */
    private void initilizeConnection() {
        try {
            Class.forName(DRIVER_PACKAGE);
        } catch (final ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found.");
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME,
                    PASSWORD);
        } catch (final SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }

    /**
     * @param item
     */
    public void removeAgendaItem(AgendaItem item) {
        // TODO Auto-generated method stub
        
    }
}
