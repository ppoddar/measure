package com.nutanix.capacity;

public enum ResourceKind {
	COMPUTE, MEMORY, STORAGE;

	String[] unitSymbols;

//	public Unit getBaseUnit() {
//		switch (this) {
//		case MEMORY:
//		case STORAGE:
//			return MemoryUnit.KB;
//		case COMPUTE:
//			return CpuUnit.NONE;
//		default:
//			return null;
//		}
//	}

	/**
	 * create quantity of given amount in given unit.
	 * 
	 * @param amount
	 * @param unit
	 * @return
	 * @exception if given unit is not compatible with given kind
	 */
	public Quantity newQuantity(double amount, Unit unit) {
		switch (this) {
		case MEMORY:
			if (!MemoryUnit.class.isInstance(unit)) {
				throw new IllegalArgumentException(unit + " is not an instance of " + MemoryUnit.class);
			}
			return new Memory(amount, (MemoryUnit)unit);
		case STORAGE:
			if (!MemoryUnit.class.isInstance(unit)) {
				throw new IllegalArgumentException(unit + " is not an instance of " + MemoryUnit.class);
			}
			return new Storage(amount, (MemoryUnit)unit);
		case COMPUTE:
			return new CPU((int) amount);
		default:
			throw new RuntimeException();
		}
	}

	public Quantity newQuantity(String s) {
		String[] tokens = s.split(" ");
		Quantity q = null;
		double amount = Double.parseDouble(tokens[0]);
		switch (this) {
			case MEMORY:
				if (tokens.length < 2) {
					throw new IllegalArgumentException("no unit mentioned in " + s);
				}
				MemoryUnit unit = MemoryUnit.fromString(tokens[1]);
				return new Memory(amount, unit);
			case STORAGE:
				if (tokens.length < 2) {
					throw new IllegalArgumentException("no unit mentioned in " + s);
				}
				unit = MemoryUnit.fromString(tokens[1]);
				return new Storage(amount, unit);
			case COMPUTE:
				return new CPU((int) amount);
			default:
				break;
		}
		return q;
	}

	public Unit getUnit(String symbol) {
		switch (this) {
		case MEMORY:
		case STORAGE:
			switch (symbol) {
			case "B":
				return MemoryUnit.B;
			case "KB":
				return MemoryUnit.KB;
			case "MB":
				return MemoryUnit.MB;
			case "GB":
				return MemoryUnit.GB;
			default:
				throw new IllegalArgumentException(this + " has no unit for [" + symbol + "]");
			}
		case COMPUTE:
			if (symbol == null || symbol.trim().length() == 0) {
				return CpuUnit.NONE;
			} else {
				throw new IllegalArgumentException(this + " has no unit for [" + symbol + "]");
			}
		default:
			throw new IllegalArgumentException(this + " has no unit for [" + symbol + "]");
		}
	}

}
