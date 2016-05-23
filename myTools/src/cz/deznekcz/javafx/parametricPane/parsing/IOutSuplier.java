package cz.deznekcz.javafx.parametricPane.parsing;


import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.Out.IntegerOut;
import cz.deznekcz.util.Builder;

public interface IOutSuplier {
	IntegerOut indexOut = IntegerOut.create();
	Builder<IntegerOut> indexBuilder = Builder.create(indexOut);
	Out<AParameter<?>> out = Out.init();
}
