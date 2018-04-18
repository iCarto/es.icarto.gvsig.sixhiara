package es.icarto.gvsig.sixhiara.forms;

import java.math.BigDecimal;

import es.icarto.gvsig.navtableforms.IValidatableForm;
import es.icarto.gvsig.navtableforms.calculation.Calculation;

public class QuantidadeAguaCalculateQuanAgua extends Calculation {

	public QuantidadeAguaCalculateQuanAgua(IValidatableForm form) {
		super(form);
	}

	@Override
	protected String resultName() {
		return "quan_agua";
	}

	@Override
	protected String[] operandNames() {
		return new String[] { "prof_campo", "nivel_agua" };
	}

	@Override
	protected String calculate() {
		BigDecimal value = operandValue("prof_campo").subtract(operandValue("nivel_agua"));
		
		if (value.floatValue() < 0) {
			value = new BigDecimal("0");
		}

		return formatter.format(value);
	}

}
