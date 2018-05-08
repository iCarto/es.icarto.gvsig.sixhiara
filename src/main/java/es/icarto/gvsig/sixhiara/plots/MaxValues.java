package es.icarto.gvsig.sixhiara.plots;

import java.util.ArrayList;
import java.util.List;

public class MaxValues {

	public final class MaxValue {
		public final String k;
		public final Number max;
		public final Number min;
		public final Number yAxisMin;
		public final Number yAxisMax;

		public MaxValue(String k, Number max, Number min) {
			this.k = k;
			this.max = max;
			this.min = min;
			this.yAxisMax = null;
			this.yAxisMin = null;
		}

		public MaxValue(String k, Number max, Number min, Number yAxisMax,
				Number yAxisMin) {
			this.k = k;
			this.max = max;
			this.min = min;
			this.yAxisMax = yAxisMax;
			this.yAxisMin = yAxisMin;
		}

		public Integer stringToIndex(String s) {
			if (k.equals("c_nitrit")) {
				switch (s) {
				case "3":
					return 0;
				case "< 10":
					return 1;
				case "10 - 25":
					return 2;
				case "25 - 50":
					return 3;
				case "50 - 100":
					return 4;
				case "100 - 250":
					return 5;
				case "> 250":
					return 6;
				default:
					return -100;
				}
			} else if (k.equals("c_nitrat")) {
				switch (s) {
				case "< 10":
					return 0;
				case "10 - 25":
					return 1;
				case "25 - 50":
					return 2;
				case "50":
					return 3;
				case "50 - 100":
					return 4;
				case "100 - 250":
					return 5;
				case "> 250":
					return 6;
				default:
					return -100;
				}
			}
			return null;
		}

		public boolean isString() {
			return k.equals("c_nitrat") || k.equals("c_nitrit");
		}

	}

	private final ArrayList<MaxValue> maxValues;

	public MaxValues() {
		maxValues = new ArrayList<MaxValue>();
		add("c_conduct", 2000, 50);
		add("c_ph", 8.5, 6.5, 5, 9);
		add("c_nitrat", 50, null);
		add("c_nitrit", 3, null);
		add("turbidez", 5, null);
		add("conductiv", 2000, 50);
		add("ph", 8.5, 6.5, 5, 9);
		add("alcalin_f", null, null);
		add("alcalinid", null, null);
		add("carbonato", null, null);
		add("bicarbona", null, null);
		add("dureza", 500, null);
		add("mo", 0.07, null);
		add("sol_total", 1000, null);
		add("nitratos", 50, null);
		add("nitritos", 3, null);
		add("coli_feca", 0, null);
		add("coli_tot", 0, null);
		add("e_coli", 0, null);
		add("cl_resid", 0.2, 0.5);
		add("cloruros", 250, null);
		add("sulfatos", 1.5, null);
		add("ca", 50, null);
		add("mg", 50, null);
		add("amonio", 1.5, null);
		add("arsenico", 0.01, null);
		add("na", 200, null);
		add("fe", 0.3, null);
		add("mn", 0.1, null);
		add("al", 0.2, null);
		add("b", 0.3, null);
		add("cd", 0.003, null);
		add("cr3", 0.05, null);
		add("cr6", 0.05, null);
		add("cu", 1, null);
		add("hg", 0.001, null);
		add("ni", 0.02, null);
		add("pb", 0.01, null);
		add("ni", 0.02, null);
		add("zn", 3, null);

	}

	private void add(String k, Number max, Number min, Number minY, Number maxY) {
		MaxValue maxValue = new MaxValue(k, max, min, maxY, minY);
		maxValues.add(maxValue);
	}

	private void add(String k, Number max, Number min) {
		MaxValue maxValue = new MaxValue(k, max, min);
		maxValues.add(maxValue);
	}

	public MaxValue getMaxFor(String key) {
		for (MaxValue maxValue : maxValues) {
			if (maxValue.k.equalsIgnoreCase(key)) {
				return maxValue;
			}
		}
		return null;
	}

	public List<MaxValue> getMaxValues() {
		return maxValues;
	}
}
