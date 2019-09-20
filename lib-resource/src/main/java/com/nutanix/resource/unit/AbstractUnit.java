package com.nutanix.resource.unit;

import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public abstract class AbstractUnit implements Unit {
	private final Resource.Kind kind;
	private final String symbol;
	private final double baseConversionFactor;
	
	
	public AbstractUnit(Resource.Kind kind, double f, String symbol) {
		if (kind == null) throw new IllegalArgumentException("resource kind can not be null");
		if (f == 0) throw new IllegalArgumentException("conversion factor  can not be 0");
		
		this.kind = kind;
		this.baseConversionFactor = f;
		this.symbol = symbol == null ? "" : symbol;
	}

	public double getConversionFactor(Unit other) {
		return other.getBaseConversionFactor()
			/this.getBaseConversionFactor();
	}

	public Resource.Kind getKind() {
		return kind;
	}
	public String getSymbol() {
		return symbol;
	}

	public double getBaseConversionFactor() {
		return baseConversionFactor;
	}

	public boolean isBase() {
		return Math.abs(baseConversionFactor- 1) < 1.0E-08;
	}
	
	public String toString() {
		return symbol == null ? "" : getSymbol();
	}

	@Override
	public final int compareTo(Unit o) {
		return new Double(o.getBaseConversionFactor())
				.compareTo(new Double(this.baseConversionFactor));
	}

}
