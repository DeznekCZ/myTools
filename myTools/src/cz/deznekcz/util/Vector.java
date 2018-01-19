package cz.deznekcz.util;


import java.util.Random;

/**
 * Vector
 * instances represent an 3-dimensional vectors
 * 
 * 
 * @version 16:05:32 24. 6. 2015
 * @author Zdeněk Novotný (DeznekCZ)
 */
public class Vector{

	/** Constant for converting degrees angles to radians */
	final public static double DEG_TO_RAD = Math.PI / 180.0;
	/** Sizes of {@link Vector} */
	public double x,y,z;
	/** {@link Vector} with size 1.0 */
	final public static Vector X = new Vector(1.0,0.0,0.0);
	/** {@link Vector} with size 1.0 */
	final public static Vector Y = new Vector(0.0,1.0,0.0);
	/** {@link Vector} with size 1.0 */
	final public static Vector Z = new Vector(0.0,0.0,1.0);
	/** NULL vector (0.0,0.0,0.0) */
	final public static Vector NULL = new Vector();
	/** Instance of {@link Random} */
	final private static Random RANDOM = new Random();
	
	/**
	 * Constructor of {@link Vector} with specified sizes
	 * @param x size
	 * @param y size
	 * @param z zize
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructor of {@link Vector} with default sizes (0.0,0.0,0.0).
	 * Vector with same values is {@link Vector}.{@link #NULL} 
	 */
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * Creates a copy of one {@link Vector} or returns a sum of all vector in params.
	 * If is instance {@code null} returns Vector(0.0,0.0,0.0) 
	 * @param vectors one or couple of instance of {@link Vector}
	 * @return new instance of {@link Vector}
	 */
	public static Vector createVector(Vector... vectors) {
		if (vectors == null) return new Vector();
		if (vectors.length == 1 && vectors[0] != null) 
			return new Vector(vectors[0].x, vectors[0].y, vectors[0].z);
		Vector v = new Vector();
		for (int i = 0; i < vectors.length; i++) {
			v.add(vectors[i]);
		}
		return v;
	}

	/**
	 * Creates a vector with specified sizes
	 * @param x size
	 * @param y size
	 * @param z zize
	 * @return new instance of {@link Vector}
	 */
	public static Vector createVector(double x, double y, double z) {
		return new Vector(x, y, z);
	}

	/**
	 * Adds vector sizes to {@link Vector}
	 * @param vector instance of Vector()
	 * @return this
	 */
	public Vector add(Vector vector) {
		if (vector == null) return this;
		this.x += vector.x;
		this.y += vector.y;
		this.z += vector.z;
		return this;
	}
	
	/**
	 * Adds sizes to {@link Vector}
	 * @param vector instance of Vector()
	 * @return this
	 */
	public Vector add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Removes vector sizes from {@link Vector}
	 * @param vector instance of Vector()
	 * @return this
	 */
	public Vector rem(Vector vector) {
		if (vector == null) return this;
		this.x -= vector.x;
		this.y -= vector.y;
		this.z -= vector.z;
		return this;
	}
	
	/**
	 * Removes sizes from {@link Vector}
	 * @param vector instance of Vector()
	 * @return this
	 */
	public Vector rem(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	/**
	 * Multiplicates {@link Vector} by double value
	 * @param value double value for multiplication
	 * @return this
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	@Deprecated()
	public Vector mul(double value) {
		this.x *= value;
		this.y *= value;
		this.z *= value;
		return this;
	}
	
	/**
	 * Scales vector for all values
	 * @param value scaling value
	 * @return this (scaled vector)
	 * 
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	public Vector scale(double value) {
		this.x *= value;
		this.y *= value;
		this.z *= value;
		return this;
	}
	
	/**
	 * Scales vector for all values
	 * @param x scaling value
	 * @param y scaling value
	 * @param z scaling value
	 * @return this (scaled vector)
	 * 
	 * @see #scale(double)
	 * @see #scale(Vector)
	 */
	public Vector scale(double x, double y, double z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	/**
	 * Scales vector for all values
	 * @param value scaling Vector
	 * @return this (scaled vector)
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 */
	public Vector scale(Vector vector) {
		this.x *= vector.x;
		this.y *= vector.y;
		this.z *= vector.z;
		return this;
	}

	/**
	 * Multiplicates {@link Vector} by vector sizes
	 * @param vector instance of Vector()
	 * @return this
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	@Deprecated()
	public Vector mulByPart(Vector vector) {
		this.x *= vector.x;
		this.y *= vector.y;
		this.z *= vector.z;
		return this;
	}
	
	/**
	 * Multiplicates {@link Vector} by vector created from double values
	 * @param x value
	 * @param y value
	 * @param z value
	 * @return this
	 */
	public Vector mulByPart(double x, double y, double z) {
		return mulByPart(new Vector(x,y,z));
	}
	
	/**
	 * Vector multiplication of {@link Vector} and other vector
	 * @param vector instance of {@link Vector}
	 * @return new instance of {@link Vector}
	 */
	public Vector mulVec(Vector vector) {
		return new Vector(
					this.y * vector.z - this.z * vector.y,
					this.z * vector.x - this.x * vector.z,
					this.x * vector.y - this.y * vector.x
				);
	}
	
	/**
	 * Scalar multiplication of {@link Vector} and other vector
	 * @param vector instance of {@link Vector}
	 * @return double value
	 */
	public double mulScal(Vector vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z;
	}

	/**
	 * Divide {@link Vector} by value
	 * @param value double value
	 * @return this
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	@Deprecated
	public Vector div(double value) {
		if (value == 0.0) return this;
		this.x /= value;
		this.y /= value;
		this.z /= value;
		return this;
	}

	/**
	 * Divide {@link Vector} by specific values for every part
	 * @param vector vector of values
	 * @return this
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	@Deprecated
	public Vector divByPart(Vector vector) {
		if (vector.x == 0.0 && vector.y == 0.0 && vector.z == 0.0) return this;
		this.x /= vector.x;
		this.y /= vector.y;
		this.z /= vector.z;
		return this;
	}

	/**
	 * Divide {@link Vector} by specific values for every part
	 * @param x value
	 * @param y value
	 * @param z value
	 * @return this
	 * 
	 * @see #scale(double)
	 * @see #scale(double, double, double)
	 * @see #scale(Vector)
	 */
	@Deprecated
	public Vector divByPart(double x, double y, double z) {
		return scale(1/x, 1/y, 1/z);
	}
	
	@Override
	public String toString() {
		return String.format("%.2f, %.2f, %.2f (%.2f)", x,y,z,size());
	}

	/**
	 * Returns p-norm of between two vectors (p-norm - vector = Vector.NULL)
	 * @param vector instance of {@link Vector}
	 * @return double value of norm
	 */
	public double norm(Vector vector, double pValue) {
		double xd = Math.abs(this.x - vector.x);
		double yd = Math.abs(this.y - vector.y);
		double zd = Math.abs(this.z - vector.z);
		return Math.pow(Math.pow(xd, pValue) + Math.pow(yd, pValue) + Math.pow(zd, pValue), 1 / pValue);
	}

	/**
	 * Distance between this and other insrance of {@link Vector}
	 * @param vector instance of {@link Vector}
	 * @return double value
	 */
	public double dist(Vector vector) {
		return norm(vector,2.0);
	}
	
	/**
	 * Retuns a size of {@link Vector}
	 * @return double size
	 */
	public double size() {
		return dist(NULL);
	}

	public static Vector center(Vector... vectors) {
		Vector v = new Vector();
		if (vectors == null) return v;
		else if (vectors.length == 0) return v;
		else if (vectors.length == 1) return vectors[0].clone();
		else {
			for (int i = 0; i < vectors.length; i++) {
				v.add(vectors[i]);
			}
			return v.div((double) vectors.length);
		}
	}
	
	/**
	 * Creates a copy of this {@link Vector}
	 */
	public Vector clone() {
		return new Vector(x, y, z);
	}

	/**
	 * Returns a random oriented vector with size
	 * @param size
	 * @return new instance of {@link Vector}
	 */
	public static Vector random(final double size) {
		final double ax = RANDOM.nextDouble() * Math.PI * 2.0;
		final double ay = RANDOM.nextDouble() * Math.PI * 2.0;
		final double az = RANDOM.nextDouble() * Math.PI * 2.0;
		return new Vector(
					Math.cos(ay) * Math.sin(az) * size,
					Math.cos(az) * Math.sin(ax) * size,
					Math.cos(ax) * Math.sin(ay) * size
				);
	}

	/**
	 * Invert values of this {@link Vector}
	 * @return this
	 */
	public Vector invert() {
		this.x *= -1.0;
		this.y *= -1.0;
		this.z *= -1.0;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean solve = false;
		if (obj instanceof Vector
				&& x == ((Vector) obj).x
				&& y == ((Vector) obj).y
				&& z == ((Vector) obj).z) {
			solve = true;
		}
		return solve;
	}

	/**
	 * Copy values of vector
	 * @param vector instance of another {@link Vector} 
	 * @return this
	 */
	public Vector set(Vector vector) {
		x = vector.x;
		y = vector.y;
		z = vector.z;
		return this;
	}

	/**
	 * Copy values of vector
	 * @param x size
	 * @param y size
	 * @param z size
	 * @return this
	 */
	public Vector set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	@Override
	public int hashCode() {
		return ((int) x << 8) | ((int) y << 4) | (int) z;
	}
	
	/**
	 * Transform the position of <b>this</b> vector by defined center and angles
	 * @param center position of rotation center
	 * @param angles angles in radians
	 * @return this (rotated vector)
	 */
	public Vector rotate(Vector center, Vector angles) {
		final Vector sin = new Vector(
				Math.sin(angles.x),
				Math.sin(angles.y),
				Math.sin(angles.z)
				);
		final Vector cos = new Vector(
				Math.cos(angles.x),
				Math.cos(angles.y),
				Math.cos(angles.z)
				);
		rem(center);
		set(x, y * cos.x - z * sin.x, y * sin.x + z * cos.x);
		set(x * cos.y + z * sin.y, y, z * cos.y - x * sin.y);
		set(x * cos.z - y * sin.z, x * sin.z + y * cos.z, z);
		add(center);
		return this;
	}
}
