package additional.test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * It's as nasty as it could be ... slow as hell, but writing the code was fast :D
 * @author Jimmy
 *
 */
public class TriPainting extends JPanel {
	
	public ArrayList polygons = new ArrayList();
	
	public double maxX = 10;
	public double maxY = 10;
	public double minX = -1;
	public double minY = -1;
	
	public TriPainting(){
	}
	
	public void addPolygon(double[] xy){
		polygons.add(xy);
	}
	
	protected int[] getPoint(double x, double y){
		double xSize = this.getWidth() / (maxX - minX);
		double ySize = (this.getHeight()) / (maxY - minY);
				
		double[] center = { minX + (maxX - minX)/2, minY + (maxY - minY)/2 };
		
		int[] realCenter = { this.getWidth() / 2, (this.getHeight()) / 2 };
		
		int[] point =   { (int)(Math.round(realCenter[0] - center[0]*xSize + x*xSize)),
		                  (int)(Math.round(realCenter[1] + center[1]*ySize - y*ySize)) 
		                };
		                  
		return point;
		
	}
	
	public void paint(Graphics g){
		double[] xy; Polygon p;
		for (int i = 0; i < polygons.size(); ++i){
			xy = (double[])polygons.get(i);
			
			p = new Polygon();

			int[] point1 = getPoint(xy[0], xy[1]);
			int[] point2 = getPoint(xy[2], xy[3]);
			int[] point3 = getPoint(xy[4], xy[5]);
			
			p.addPoint(point1[0], point1[1]);
			p.addPoint(point2[0], point2[1]);
			p.addPoint(point3[0], point3[1]);
			
			g.setColor(Color.BLUE);			
			g.fillPolygon(p);			
			g.setColor(Color.BLACK);			
			g.drawPolygon(p);	
		}
		
	}

}
