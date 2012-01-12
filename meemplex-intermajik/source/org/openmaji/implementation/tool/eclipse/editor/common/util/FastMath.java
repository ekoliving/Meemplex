/*
 * Created on 8/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.common.util;

/**
 * @author Kin Wong
 * The class <code>FastMath</code> contains methods for trigonometric functions 
 * using fixed-size lookup and simple linear interpolation.
 * <p>
 */
public class FastMath {
	static public final double PI2 = 2 * Math.PI;
	//static private final int TABLE_SIZE = 360;
	//static private double[] cosTable;
	
	static {
		//cosTable = new double[TABLE_SIZE];
	}
	/**
	 * Returns the trigonometric sine of an angle.  Special cases:
	 * <ul><li>If the argument is NaN or an infinity, then the 
	 * result is NaN.
	 * <li>If the argument is zero, then the result is a zero with the
	 * same sign as the argument.</ul>
	 * <p>
	 * A result must be within 1 ulp of the correctly rounded result.  Results
	 * must be semi-monotonic.
	 *
	 * @param   a   an angle, in radians.
	 * @return  the sine of the argument.
	 */
	static public double sin(double a) {
		return Math.sin(a);	
	}
	/**
	 * Returns the trigonometric cosine of an angle. Special cases:
	 * <ul><li>If the argument is NaN or an infinity, then the 
	 * result is NaN.</ul>
	 * <p>
	 * A result must be within 1 ulp of the correctly rounded result.  Results
	 * must be semi-monotonic.
	 *
	 * @param   a   an angle, in radians.
	 * @return  the cosine of the argument.
	 */
	static public double cos(double a) {
		return Math.cos(a);
	}
}
