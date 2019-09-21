package com.nutanix.capacity;


/**
 * amount of something in a specific unit.
 * A quantity is orderable.
 * 
 * <p>
 * A quantity is immutable too.
 * An algebra is defined on quantities. Adding a quantity
 * to another results a third quantity. Needless to say,
 * two quantities can only be added if they are of same kind.
 * 
 * 
 * 
 * @author pinaki.poddar
 *
 */
public interface Quantity extends Comparable<Quantity>{
	/**
	 * gets kind of this receiver.
	 * @return an enumerated kind
	 */
	ResourceKind getKind();
	
	/**
	 * gets amount of this receiver
	 * @return never negative
	 */
	double getValue();
	
	/**
	 * gets unit of this receiver.
	 * The following assertion is always true,
	 * <pre> q.getKind() == q.getUnit().getKind() </pre>
	 * 
	 * @return an unit of same {@link Unit#getKind() kind}
	 * of that of this receiver.
	 */
	Unit   getUnit();
	
	/**
	 * converts this receiver to a quantity of given unit.
	 * 
	 * @param to a unit of same kind of this receiver
	 * @return a quantity wich is equal to receiver
	 * but of given unit
	 */
	Quantity convert(Unit to);
	
	/**
	 * affirms if this quantity has integral value
	 * @return true if values are integer.
	 */
	boolean isIntegral();
	
	/**
	 * adds this receiver to given quantity
	 * @param other
	 * @return a new quantity whose unit is same
	 * as this receiver's unit.
	 */
	Quantity plus(Quantity other);
	
	/**
	 * subtracts given quantity from this receiver
	 * @param other another quantity to be subtracted
	 * @return a new quantity whose unit is same
	 * as this receiver's unit.
	 * 
	 * It is not possible to subtract if the resultant
	 * quantuty is negative
	 */
	Quantity minus(Quantity other);
	
	/**
	 * multiplies this receivers with given 
	 * multiplication factor
	 * 
	 * @param n multiplication factor, must be non-zero
	 * positive
	 * @return
	 */
	Quantity times(int n);
	
	/**
	 * fraction of this receiver and given quantity,
	 * with this receiver as denominator
	 * 
	 * @param other another quantity
	 * @return
	 */
	double fraction(Quantity other);
	
	Unit getPreferredUnit();
	
	/**
	 * clone this receiver to a quantity of 
	 * given amount and  given unit
	 * 
	 * @param amount
	 * @param unit
	 * @return
	 */
	Quantity clone(double value, Unit unit);

}
