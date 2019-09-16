package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public abstract class AbstractCapacity implements Capacity {
	private final Resource.Kind kind;
	private final double amount;
	private final Unit unit;
	private final boolean isIntegral;

	public AbstractCapacity(Resource.Kind kind, double amount, Unit unit) {
		this(kind, amount, unit, true);
	}

	protected AbstractCapacity(Resource.Kind kind, double amount, Unit unit, boolean integral) {
		this.kind = kind;
		this.amount = amount;
		this.unit = unit;
		this.isIntegral = integral;
	}

	public int compareTo(Capacity o) {
		if (o.getKind() != this.getKind()) {
			throw new IllegalArgumentException();
		}
		Capacity q2 = o.convert(this.unit);
		if (this.amount > q2.getAmount()) {
			return 1;
		} else if (this.amount < q2.getAmount()) {
			return -1;
		} else {
			return 0;
		}
	}

	public Resource.Kind getKind() {
		return kind;
	}

	public double getAmount() {
		return amount;
	}

	public Unit getUnit() {
		return unit;
	}

	public Capacity plus(Capacity other) {
		assertSameKind(this, other);
		Capacity added = other.convert(this.getUnit());
		double a = this.amount + added.getAmount();
		return this.clone(a);
	}

	public Capacity minus(Capacity other) {
		assertSameKind(this, other);
		Capacity taken = other.convert(this.getUnit());
		double a = this.amount - taken.getAmount();
		return this.clone(a);
	}

	public Capacity convert(Unit to) {
		double f = to.getConversionFactor(this.unit);
		return this.clone(f * amount);
	}

	@Override
	public double fraction(Capacity other) {
		assertSameKind(this, other);
		double f = other.getUnit().getConversionFactor(this.unit);
		return f * other.getAmount() / this.amount;
	}

	@Override
	public boolean isIntegral() {
		return isIntegral;
	}

	public String toString() {
		if (isIntegral) {
			return String.format("%s:%d %s", getKind(), (int) getAmount(), getUnit());
		} else {
			return String.format("%s:%f %s", getKind(), getAmount(), getUnit());
		}
	}
	
	void assertSameKind(Capacity c1, Capacity c2) {
		if (c1.getKind() != c2.getKind()) {
			throw new IllegalArgumentException("different kind of capacity " 
					+ c1.getKind() + " and " +  c2.getKind());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!Capacity.class.isInstance(obj)) return false; 
		Capacity other = Capacity.class.cast(obj);
		
		if (other.getKind() != this.getKind()) {
			return false;
		}
		Capacity c2 = other.convert(this.getUnit());
		return c2.getAmount() == this.getAmount();
	}

	@Override
	public Capacity times(int n) {
		return this.clone(amount * n);
	}

	

}
