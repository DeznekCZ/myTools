package cz.deznekcz.javaplay.extending;

public class Extendables {

	public static void main(String[] args) {
		Parent c = new Child();
		
		c.callOutIn();

		c.callInAbstractOut();
		
		c.callInOut();
	}

}
