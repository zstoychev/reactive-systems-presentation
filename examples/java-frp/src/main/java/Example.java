import java.awt.*;
import java.util.function.BiFunction;

import javax.swing.*;

import nz.sodium.Cell;
import nz.sodium.Stream;
import nz.sodium.Transaction;
import nz.sodium.Unit;
import swidgets.SButton;
import swidgets.SLabel;

class CalculatorInputs {
	public final Stream<Unit> incrementValue1;
	public final Stream<Unit> decrementValue1;

	public final Stream<Unit> incrementValue2;
	public final Stream<Unit> decrementValue2;

	public final Stream<Unit> sum;
	public final Stream<Unit> mult;

	public CalculatorInputs(
			Stream<Unit> incrementValue1, Stream<Unit> decrementValue1,
			Stream<Unit> incrementValue2, Stream<Unit> decrementValue2,
			Stream<Unit> sum, Stream<Unit> mult) {
		this.incrementValue1 = incrementValue1;
		this.decrementValue1 = decrementValue1;
		this.incrementValue2 = incrementValue2;
		this.decrementValue2 = decrementValue2;
		this.sum = sum;
		this.mult = mult;
	}
}

enum CalculatorMode {
	SUM((a, b) -> a + b),
	MULT((a, b) -> a * b);

	public final BiFunction<Integer, Integer, Integer> fn;

	CalculatorMode(BiFunction<Integer, Integer, Integer> fn) {
		this.fn = fn;
	}

}

class Calculator {
	public final Cell<Integer> value1;
	public final Cell<Integer> value2;
	public final Cell<CalculatorMode> mode;
	public final Cell<Integer> result;

	private Cell<Integer> incrDecr(Stream<?> incr, Stream<?> decr) {
		Stream<Integer> delta = incr.map(v -> 1).orElse(decr.map(v -> -1));

		return delta.accum(0, (dt, v) -> dt + v);
	}

	public Calculator(CalculatorInputs inputs) {
		value1 = incrDecr(inputs.incrementValue1, inputs.decrementValue1);
		value2 = incrDecr(inputs.incrementValue2, inputs.decrementValue2);

		Stream<CalculatorMode> sum = inputs.sum.map(c -> CalculatorMode.SUM);
		Stream<CalculatorMode> mult = inputs.mult.map(c -> CalculatorMode.MULT);
		mode = sum.orElse(mult).hold(CalculatorMode.SUM);

		result = value1.lift(value2, mode, (a, b, m) -> m.fn.apply(a, b));
	}
}

public class Example {
	private SLabel label(Cell<?> value) {
		return new SLabel(value.map(Object::toString));
	}

	private void layout(JFrame view) {
		SButton plus1Button = new SButton("+");
		SButton minus1Button = new SButton("-");
		SButton plus2Button = new SButton("+");
		SButton minus2Button = new SButton("-");
		SButton sumButton = new SButton("sum");
		SButton multButton = new SButton("mult");

		CalculatorInputs inputs = new CalculatorInputs(
			plus1Button.sClicked,
			minus1Button.sClicked,
			plus2Button.sClicked,
			minus2Button.sClicked,
			sumButton.sClicked,
			multButton.sClicked
		);
		Calculator calculator = new Calculator(inputs);

		java.util.stream.Stream.of(
				label(calculator.value1), plus1Button, minus1Button,
				label(calculator.value2), plus2Button, minus2Button,
				label(calculator.result), sumButton, multButton)
			.forEach(view::add);
	}

	public static void main(String[] args) {
		JFrame view = new JFrame("spinner");
		view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		view.setLayout(new GridLayout(3, 3));

		Transaction.runVoid(() -> {
			new Example().layout(view);
		});

		view.setSize(400, 160);
		view.setVisible(true);
	}
}
