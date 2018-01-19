package cz.deznekcz.reference;

import java.util.function.Consumer;
import java.util.function.Function;

import cz.deznekcz.util.Vector;
import javafx.beans.InvalidationListener;

public class OutVector extends Out<Vector> {

	public static class Binder {
		OutVector origin;
		OutVector[] supliers;
		Function<Vector,Vector> transformer;
		double lastValue;
		InvalidationListener supportListener;
		Consumer<Double> setter;
		
		
		
		private static final int X_AXIS = 1;
		private static final int Y_AXIS = 2;
		private static final int Z_AXIS = 3;

		public Binder(Consumer<Double> setter, int axis) {
			this.setter = setter;
			switch (axis) {
			case X_AXIS:
				supportListener = (o) -> {
					setter.accept(transformer.apply(origin.get()).x);
				};
				break;
			case Y_AXIS:
				supportListener = (o) -> {
					setter.accept(transformer.apply(origin.get()).y);
				};
				break;
			case Z_AXIS:
				supportListener = (o) -> {
					setter.accept(transformer.apply(origin.get()).z);
				};
				break;

			default:
				break;
			}
		}

		public void unbind() {
			if (isBound()) {
				origin.removeListener(supportListener);
				if (supliers != null && supliers.length > 0) {
					for (OutVector outVector : supliers) {
						outVector.removeListener(supportListener);
					}
				}
				origin = null;
			}
		}

		public void bind(Function<Vector, Vector> trans, OutVector origin, OutVector...supliers) {
			this.transformer = trans;
			this.origin = origin;
			this.supliers = supliers;

			this.origin.addListener(supportListener);
			
			if (this.supliers != null && this.supliers.length > 0)
				for (OutVector outVector : this.supliers) {
					outVector.addListener(supportListener);
				}
		}

		public boolean isBound() {
			return origin != null && origin.hasListener(supportListener);
		}

		public static final Function<Vector,Vector> COPY = (v) -> v;
		
		public static final Function<Vector,Vector> NORM = (v) -> v.clone().scale(1/v.size());
		
		public static Function<Vector, Vector> REMOVE(OutVector a) {
			return (v) -> {
				return v.clone().rem(a.get());
			};
		}
		
		public static Function<Vector, Vector> REMOVE(Vector a) {
			return (v) -> {
				return v.clone().rem(a);
			};
		}
		
		public static Function<Vector, Vector> ADD(OutVector a) {
			return (v) -> {
				return v.clone().add(a.get());
			};
		}
		public static Function<Vector, Vector> ADD(Vector a) {
			return (v) -> {
				return v.clone().add(a);
			};
		}
		
		public static Function<Vector, Vector> SCALED(OutDouble a) {
			return (v) -> {
				return v.clone().scale(a.get());
			};
		}
		
		public static Function<Vector, Vector> SCALED(double a) {
			return (v) -> {
				return v.clone().scale(a);
			};
		}

		
	}
	private Binder Xbound = new Binder(this::setX,Binder.X_AXIS);
	private Binder Ybound = new Binder(this::setY,Binder.Y_AXIS);
	private Binder Zbound = new Binder(this::setZ,Binder.Z_AXIS);
	private OutDouble size = OutDouble.init();
	
	protected OutVector(Vector defaultValue) {
		super(defaultValue);
		onChange((n) -> size.set(n.size()));
	}

	public synchronized void setX(double apply) {
		Vector v = get();
		v.x = apply;
		set(v);
	}
	
	public synchronized void setY(double apply) {
		Vector v = get();
		v.y = apply;
		set(v);
	}
	
	public synchronized void setZ(double apply) {
		Vector v = get();
		v.z = apply;
		set(v);
	}

	@SuppressWarnings("unchecked")
	public static OutVector init() {return new OutVector(new Vector());}
	
	public static OutVector init(Vector v) {return new OutVector(v != null ? v : new Vector());}

	public static OutVector init(OutVector ov) {
		OutVector nov = new OutVector(new Vector());
		nov.bindTo(ov);
		return nov;
	}
	
	public static OutVector init(Function<Vector, Vector> trans, OutVector origin, OutVector...vectors) {
		OutVector nov = new OutVector(new Vector());
		nov.bindALL(trans, origin, vectors);
		return nov;
	}

	public static OutVector init(int x, int y, int z) {
		return new OutVector(new Vector(x, y, z));
	}
	
	public void bindTo(OutVector ov) {
		bindALL(Binder.COPY,ov);
	}
	/**
	 * 
	 * @param ov
	 * @param trans
	 */
	public void bindALL(Function<Vector, Vector> trans, OutVector origin, OutVector...vectors) {
		bindX(trans,origin,vectors);
		bindY(trans,origin,vectors);
		bindZ(trans,origin,vectors);
	}
	
	@Override
	public <O> void unbind() {
		unbindX();
		unbindY();
		unbindZ();
	}

	public void bindX(Function<Vector, Vector> trans, OutVector origin, OutVector...vectors) {
		Xbound.unbind();
		Xbound.bind(trans,origin,vectors);
	}

	public void unbindX() {
		Xbound.unbind();
	}

	public void bindY(Function<Vector, Vector> trans, OutVector origin, OutVector...vectors) {
		Ybound.unbind();
		Ybound.bind(trans,origin,vectors);
	}

	public void unbindY() {
		Ybound.unbind();
	}

	public void bindZ(Function<Vector, Vector> trans, OutVector origin, OutVector...vectors) {
		Zbound.unbind();
		Zbound.bind(trans,origin,vectors);
	}

	public void unbindZ() {
		Zbound.unbind();
	}
	
	public boolean isBound() {
		return isBoundX() || isBoundY() || isBoundZ();
	}

	public boolean isBoundX() {
		return Xbound.isBound();
	}

	public boolean isBoundY() {
		return Ybound.isBound();
	}

	public boolean isBoundZ() {
		return Zbound.isBound();
	}

	public void set(double x, double y, double z) {
		set(new Vector(x,y,z));
	}

	public OutDouble size() {
		return size;
	}

	public double getX() {
		return get().x;
	}

	public double getY() {
		return get().y;
	}

	public double getZ() {
		return get().z;
	}
}
