package additional.test;

import javax.swing.*;

public class TriFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	
	private double maxX = +10;
	private double maxY = +10;
	private double minX = -10;
	private double minY = -10;

	/**
	 * This is the default constructor
	 */
	public TriFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 300);
		this.setContentPane(getJContentPane());
		this.setTitle("Poly2Tri");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new TriPainting();
			jContentPane.setLayout(null);
		}
		return jContentPane;
	}
	
	/**
	 * Applies only to new triangles. Set before using / calling addTriangle()
	 * @param newMaxX
	 */
	public void setMaxX(double newMaxX){
		if (newMaxX > 0) maxX = newMaxX+0.5;
		else maxX = newMaxX+0.5;
		((TriPainting)this.getContentPane()).maxX = maxX;
	}

	/**
	 * Applies only to new triangles. Set before using / calling addTriangle()
	 * @param newMaxY
	 */
	public void setMaxY(double newMaxY){
		if (newMaxY > 0) maxY = newMaxY+0.5;
		else maxY = newMaxY+0.5;
		((TriPainting)this.getContentPane()).maxY = maxY;
	}
	
	/**
	 * Applies only to new triangles. Set before using / calling addTriangle()
	 * @param newMinX
	 */	
	public void setMinX(double newMinX){
		if (newMinX > 0) minX = newMinX-0.5;
		else minX = newMinX-0.5;
		((TriPainting)this.getContentPane()).minX = minX;
	}
	
	/**
	 * Applies only to new triangles. Set before using / calling addTriangle()
	 * @param newMinY
	 */
	public void setMinY(double newMinY){
		if (newMinY > 0) minY = newMinY-0.5;
		else minY = newMinY-0.5;	
		((TriPainting)this.getContentPane()).minY = minY;
	}
	
	
	
	public void addTriangle(double x1, double y1, double x2, double y2, double x3, double y3){
		((TriPainting)this.getContentPane()).addPolygon(new double[]{x1,y1,x2,y2,x3,y3});
	}

}
