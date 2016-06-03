package additional.test;

import additional.Triangulation;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;

public class TriTest {
	
	protected static double[] temp;
	protected static int i;
	
	public static void printVertices(double[][] vertices){
		
		System.out.println("Vertices:");
		for (i = 1; i < vertices.length; ++i){
			temp = vertices[i];
			if (i % 6 == 0) System.out.println();
			System.out.print("["+temp[0]+", "+temp[1]+"]   ");
		}
		System.out.println();
		if (i % 5 != 0) System.out.println();
	}
	
	public static ArrayList triangles = null;
	public static int numContours;
	public static int[] contours;
	public static double[][] vertices;
	
	public static int number = 0;
	public static ArrayList tstfs = new ArrayList();
	
	public static String name = "";
	
	public static void doTriangulation(){		
		++number;
		triangles = null;
		//printVertices(vertices);
		
		Date begin = new Date();
		
		triangles = Triangulation.triangulate(numContours, contours, vertices);
		
		Date end = new Date();
		
		
	    //printTriangles(triangles);
	    TriFrame tstf = new TriFrame();
	    
	    long ms = end.getTime() - begin.getTime();
	    
	    tstf.setTitle("Tri - "+name+" - Time: "+ms+" miliseconds");
	    
	    System.out.println(name+", time: "+ms+" miliseconds");
	    
	    double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
	    ArrayList t; double[] xy1 = {0,0}, xy2 = {0,0}, xy3 = {0,0};
	    for (int i = 0; i < triangles.size(); ++i){
	    	t = (ArrayList)triangles.get(i);	    	
	    	for (int j = 0; j < 3; ++j){
	    		xy1 = vertices[(Integer)t.get(j)];
	    		if (xy1[0] > maxX) maxX = xy1[0];
	    		if (xy1[0] < minX) minX = xy1[0];
	    		if (xy1[1] > maxY) maxY = xy1[1];
	    		if (xy1[1] < minY) minY = xy1[1];
	    	}
	    }
	    
	    tstf.setMaxX(maxX);
	    
	    tstf.setMinX(minX);
	    
	    
	    tstf.setMaxY(maxY);
	    
	    tstf.setMinY(minY);

	    for (int i = 0; i < triangles.size(); ++i){
	    	t = (ArrayList)triangles.get(i);	   
	    	xy1 = vertices[(Integer)t.get(0)];
	    	xy2 = vertices[(Integer)t.get(1)];
	    	xy3 = vertices[(Integer)t.get(2)];
	    	tstf.addTriangle(xy1[0], xy1[1],
	    				     xy2[0], xy2[1],
	    				     xy3[0], xy3[1]);
	    }	    

	    tstf.setLocation(number*30, number*30);
	    
	    // don't have time to write invokeLater
	    // if you have problems use SwingUtilities.invokeLater( new Runnable(){ ... } );
	    tstf.setVisible(true);
		tstf.toFront();
		
		tstfs.add(tstf); // to maintain the object?	    
	}
	
	public static int skipWhitespaces(String str, int index){
		while ((index < str.length()) && ((str.charAt(index) == ' ') || (str.charAt(index) == '\t'))) ++index;
		return index;
	}
	
	public static Double getNumber(String str, int[] index){
		index[0] = skipWhitespaces(str, index[0]);
		if (index[0] >= str.length()) return Double.NaN;
		StringBuffer temp = new StringBuffer();
		while ( (index[0] < str.length()) && 
			    (
			      (str.charAt(index[0]) == 'e') || (str.charAt(index[0]) == 'E') ||
			      (str.charAt(index[0]) == '+') || (str.charAt(index[0]) == '-') ||
			      (str.charAt(index[0]) == '.') ||
			      ((str.charAt(index[0]) >= '0') && (str.charAt(index[0]) <= '9'))
			    )	
		      ){
			temp.append(str.charAt(index[0]));
			++index[0];
		}
		if (temp.length() == 0) return Double.NaN;
		else return new Double(temp.toString());
	}
	
	public static boolean loadBDMFile(String fileName){
		ArrayList contoursAL = new ArrayList();
		ArrayList verticesAL;
		int numVertices = 0;
		int curNumVertices;
		Double num, num2; int[] index = {0};
		try{
			
			LineNumberReader fr = new LineNumberReader(new FileReader(fileName));
			String line;
			
			while (fr.ready()){
				line = fr.readLine();
				if (line == null) continue;
				if (line.length() == 0) continue;
				if ((line.charAt(0) >= '0') && (line.charAt(0) <= '9')){
					num = getNumber(line, new int[]{0});					
					curNumVertices = (int)Math.round(num);
					numVertices += curNumVertices;
					verticesAL = new ArrayList();
					for (int i = 0; i < curNumVertices; ++i){
						line = fr.readLine();
						if (line == null){
							return false;
						}
						index[0] = 0;
						num = getNumber(line, index);
						if (num == Double.NaN){
							return false;
						}
						num2 = getNumber(line, index);
						if (num2 == Double.NaN){
							return false;
						}
						verticesAL.add(new double[]{num, num2});						
					}
					contoursAL.add(verticesAL);
				} else continue;
			}
			
			fr.close();
			
		} catch (IOException e){
			return false;
		}
		
		numContours = contoursAL.size();
		contours = new int[numContours];
		vertices = new double[numVertices][2];
		int k = 0;
		for (int i = 0; i < numContours; ++i){
			verticesAL = (ArrayList)contoursAL.get(i);
			contours[i] = verticesAL.size();
			for (int j = 0; j < verticesAL.size(); ++j){
				vertices[k++] = (double[])verticesAL.get(j);
			}
		}
		return true;		
	}

	public static void main(String[] args) {
		/*
		// 1st input
		numContours = 1;
	    // each number is number of vertex of i-th conture,
	    // first conture is outer (must be counterclockwise), others must be inner (clockwise)
	    contours = new int[]{ 5 };
 
	    vertices = new double[][]{
	        { 1, 1 }, { 4, 0 }, { 3, 5 }, { 2, 8 }, { 0, 0 } 
	    };
	    
	    name = "1st";
	    doTriangulation();
	    
	    // 2nd input
		numContours = 2;
	    // each number is number of vertex of i-th conture,
	    // first conture is outer (must be counterclockwise), others must be inner (clockwise)
	    contours = new int[]{ 3, 3 };
 
	    vertices = new double[][]{
	        { 0, 0 }, { 7, 0 }, { 3, 4 }, 
	        { 2, 1 }, { 2, 2 }, { 3, 1 },  
	    };
	    
	    name = "2nd";
	    doTriangulation();
	    */
	    // 3rd input
		numContours = 3;
	    // each number is number of vertex of i-th conture,
	    // first conture is outer (must be counterclockwise), others must be inner (clockwise)
	    contours = new int[]{ 3, 3, 3 };

	    vertices = new double[][]{
	        { 0, 0 }, { 9, 0 }, { 3, 8 },
	        { 2, 1 }, { 2, 2 }, { 3, 1 },
	        { 6, 2 }, { 6, 1 }, { 5, 2 }
	    };
//
//	    name = "3rd";
//	    doTriangulation();

//		// 4th input
//		numContours = 1;
//		// each number is number of vertex of i-th conture,
//		// first conture is outer (must be counterclockwise), others must be inner (clockwise)
//		contours = new int[]{ 15 };
//
//		vertices = new double[][]{
//				{ 0, 0 }, { 1, 1 }, { 2, 1 }, { 0, 0 }, { 6, 0 }, { 6, 1 }, { 5, 2 }, { 6, 2 }, { 6, 9 }, { 3, 9 },
////				{ 3, 7 }, { 6, 9 }, { 6, 0 }, { 0, 0 }, { 2, 1 }, { 1, 1 },	{ 0, 0 },
////				{ 2, 1 }, { 2, 2 }, { 3, 1 },
////				{ 6, 2 }, { 6, 1 }, { 5, 2 }
//		};

//		name = "4th";
//		doTriangulation();

		numContours = 1;
		// each number is number of vertex of i-th conture,
		// first conture is outer (must be counterclockwise), others must be inner (clockwise)
		contours = new int[]{ 12 };

		vertices = new double[][] {
				{ 11.94162840604072,114.76378679128605 },
				{ 133.88440047889577,212.58478136670215 },
				{ 220.39768383935788,114.88923746090381 },
				{ 146.56261980928394,9.918583181618317 },
				{ 1271.6533369951765,72.89320403385523 },
				{ 1227.3491041908273,127.45683052178474 },
				{ 1249.249544992938,179.0636770818023 },
				{ 1257.2966629115353,184.74939571875018 },
				{ 0.0408331278236472,288.02397617579857 },
				{ 0.08766827544298508,287.9056418132427 },
				{ 0.03962865918490292,287.9056318692641 },
				{ 0.00436581557474109,287.98773018150763 },
		};
		name = "5th";
		doTriangulation();

		/*
	    // BDM Files
	    String prefix = "additional/test/";
	    String[] bdmFiles = { "boxc100.bdm",   "circle1.bdm",
	    					  "circle2.bdm",   "circle3.bdm",
	    					  "crazybox1.bdm", "crazybox2.bdm",
	    					  "guitar.bdm",    "sample1.bdm",
	    					  "sample2.bdm",   "sample3.bdm",
	    					  "superior.bdm" };	    
	
	    int option; boolean end = false, next = false, all = false;
	    
	    option = JOptionPane.showConfirmDialog((JFrame)tstfs.get(tstfs.size()-1), "Do you want to do ALL bdm files additional?");
	    all = option == JOptionPane.YES_OPTION;
	    
		for(int i = 0; i < bdmFiles.length; ++i){
			if (!all){
				next = false;
				option = JOptionPane.showConfirmDialog((JFrame)tstfs.get(tstfs.size()-1), "Do additional of "+bdmFiles[i]+"?");
				switch (option){
					case JOptionPane.CANCEL_OPTION: end =  true; break;
					case JOptionPane.NO_OPTION:     next = true; break;
				}
				if (end)  break;
				if (next) continue;
			}
			if (loadBDMFile(prefix+bdmFiles[i])){
				name = bdmFiles[i];
				doTriangulation();
			} else {
				JOptionPane.showMessageDialog((JFrame)tstfs.get(tstfs.size()-1), "Failed to load "+bdmFiles[i]);
			}
		}
		*/
	}

}
