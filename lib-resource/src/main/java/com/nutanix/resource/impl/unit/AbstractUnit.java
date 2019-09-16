package com.nutanix.resource.impl.unit;

import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;

public abstract class AbstractUnit implements Unit {
	private final Resource.Kind kind;
	private final String symbol;
	private final double baseConversionFactor;
	
	
	public AbstractUnit(Resource.Kind kind, double f) {
		this(kind, f, null);
	}

	public AbstractUnit(Resource.Kind kind, double f, String symbol) {
		this.kind = kind;
		this.baseConversionFactor = f;
		this.symbol = symbol;
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

	
}
