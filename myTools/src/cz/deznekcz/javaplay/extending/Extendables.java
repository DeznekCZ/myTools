package cz.deznekcz.javaplay.extending;

public class Extendables {

	public static void main(String[] args) {
		Child c = new Child();
		
		c.callOutIn();

		c.callInAbstractOut();
		
		c.callInOut();
	}

}
