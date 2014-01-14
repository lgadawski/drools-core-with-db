package com.gadawski.drools.db.tuple;

import java.util.List;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public interface IDbTupleManager {

    /**
     * @param leftTuple
     */
    void saveLeftTuple(LeftTuple leftTuple);

    /**
     * @param id
     * @return
     */
    List<Object> getLeftTuples(int id);

    /**
     * @param id
     * @return
     */
    List<Object> getRightTuples(int id);

    /**
     * @param rightTuple
     */
    void saveRightTuple(RightTuple rightTuple);

}
