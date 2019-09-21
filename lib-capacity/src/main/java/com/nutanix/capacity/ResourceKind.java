package com.nutanix.capacity;

public enum ResourceKind {
		COMPUTE, 
		MEMORY, 
		STORAGE;
		
		String[] unitSymbols;
		
		public Unit getBaseUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.KB;
			case COMPUTE: 
				return CpuUnit.NONE;
			default: return null;
			}
		}
		public Unit getHighestUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.GB;
			case COMPUTE: 
			default: return null;
			}
		}
		public Unit getSmallestUnit() {
			switch (this) {
			case MEMORY: 
			case STORAGE: return MemoryUnit.B;
			case COMPUTE: 
			default: return null;
			}
		}
		
		public Quantity newQuantity(String s) {
			String[] tokens = s.split(" ");
			Quantity q = null;
			double value = Double.parseDouble(tokens[0]);
			String unit  = tokens.length > 1 ? tokens[1] : null;
			switch (this) {
			case MEMORY:
				q = new Memory(value, unit);
				break;
			case STORAGE:
				q = new Memory(value, unit);
				break;
			case COMPUTE:
				q = new CPU((int)value);
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
					case "B": return MemoryUnit.B; 
					case "KB": return MemoryUnit.KB; 
					case "MB": return MemoryUnit.MB; 
					case "GB": return MemoryUnit.GB; 
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


