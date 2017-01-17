import javax.swing.*;

import java.awt.*;
import java.util.stream.*;

import nz.sodium.Stream;
import swidgets.*;
import nz.sodium.*;

class Inputs {
	final SButton plus1;
	final SButton minus1;

	final SButton plus2;
	final SButton minus2;

	final SButton sum;
	final SButton mult;

	public Inputs(SButton plus1, SButton minus1, SButton plus2, SButton minus2, SButton sum,
			SButton mult) {
		this.plus1 = plus1;
		this.minus1 = minus1;
		this.plus2 = plus2;
		this.minus2 = minus2;
		this.sum = sum;
		this.mult = mult;
	}
}

class Outputs {
	final SLabel value1;
	final SLabel value2;
	final SLabel composedValue;

	public Outputs(SLabel value1, SLabel value2, SLabel composedValue) {
		this.value1 = value1;
		this.value2 = value2;
		this.composedValue = composedValue;
	}
}

public class Example {
	public Cell<Integer> incrDecr(Stream<?> incr, Stream<?> decr) {
		Stream<Integer> delta = incr.map(v -> 1).orElse(decr.map(v -> -1));

		return delta.accum(0, (dt, v) -> dt + v);
	}

	public Outputs buildFlow(Inputs inputs) {
		Cell<Integer> value1 = incrDecr(inputs.plus1.sClicked, inputs.minus1.sClicked);
		Cell<Integer> value2 = incrDecr(inputs.plus2.sClicked, inputs.minus2.sClicked);

		Stream<Integer> sum = inputs.sum.sClicked.snapshot(value1, value2, (u, v1, v2) -> v1 + v2);
		Stream<Integer> mult = inputs.mult.sClicked.snapshot(value1, value2, (u, v1, v2) -> v1 * v2);

		Cell<Integer> result = sum.orElse(mult).hold(0);

		return new Outputs(
				new SLabel(value1.map(Object::toString)),
				new SLabel(value2.map(Object::toString)),
				new SLabel(result.map(Object::toString))
		);
	}

	public void layout(JFrame view) {
		Inputs inputs = new Inputs(
				new SButton("+"),
				new SButton("-"),
				new SButton("+"),
				new SButton("-"),
				new SButton("sum"),
				new SButton("mult")
		);

		Outputs outputs = buildFlow(inputs);


		java.util.stream.Stream.of(
				outputs.value1, inputs.plus1, inputs.minus1,
				outputs.value2, inputs.plus2, inputs.minus2,
				outputs.composedValue, inputs.sum, inputs.mult)
				.forEach(view::add);
	}

	public static void main(String[] args) {
		JFrame view = new JFrame("spinner");
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setLayout(new GridLayout(3, 3));

		Transaction.runVoid(() -> {
			new Example().layout(view);
		});

		view.setSize(400, 160);
		view.setVisible(true);
	}
}
