package org.jlab.calib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.root.attr.Attributes;
import org.root.base.EvioWritableTree;
import org.root.histogram.Axis;
import org.root.histogram.MultiIndex;
import org.root.pad.TGCanvas;

/**
 * Specifies the methods to create a 2D Histogram and operations to fill it and
 * set values to its bins
 * 
 * @author Erin Kirby
 * @version 061714
 */
public class MyH3S implements EvioWritableTree{

	private String hName = "basic3D";
	private Axis xAxis = new Axis();
	private Axis yAxis = new Axis();
	private Axis zAxis = new Axis();
	private short[] hBuffer;
	private MultiIndex offset;
        private Attributes attr = new Attributes(); 
        private short     maximumBinValue = 0;
        
	public MyH3S() {
		offset = new MultiIndex(xAxis.getNBins(), yAxis.getNBins(), zAxis.getNBins());
		
		hBuffer = new short[offset.getArraySize()];
		
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
    }

    public void setName(String name){ this.hName = name;}
	/**
	 * Creates an empty 3D Histogram with 1 bin x,y, and z axes
	 * 
	 * @param name
	 *            the desired name of the 3D Histogram
	 */
	public MyH3S(String name) {
		hName = name;
		offset = new MultiIndex(xAxis.getNBins(), yAxis.getNBins(), zAxis.getNBins());
		
		hBuffer = new short[offset.getArraySize()];
		
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
	}

	/**
	 * Creates a 3D Histogram with the specified parameters.
	 * 
	 * @param name
	 *            the name of the histogram
	 * @param bx
	 *            the number of x axis bins
	 * @param xmin
	 *            the minimum x axis value
	 * @param xmax
	 *            the maximum x axis value
	 * @param by
	 *            the number of y axis bins
	 * @param ymin
	 *            the minimum y axis value
	 * @param ymax
	 *            the maximum y axis value
	 */
	public MyH3S(String name, int bx, double xmin, double xmax, 
			                int by, double ymin, double ymax,
			                int bz, double zmin, double zmax) {
		hName = name;
		this.set(bx, xmin, xmax, by, ymin, ymax, bz, zmin, zmax);
		offset = new MultiIndex(bx, by, bz);
		
		hBuffer = new short[offset.getArraySize()];
		
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
	}

    public MyH3S(String name, String title, int bx, double xmin, double xmax, 
    									    int by, double ymin, double ymax,
    									    int bz, double zmin, double zmax) {
                
		hName = name;
                this.setTitle(title);
		this.set(bx, xmin, xmax, by, ymin, ymax, bz, zmin, zmax);
		offset = new MultiIndex(bx, by, bz);
		
		hBuffer = new short[offset.getArraySize()];
		
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
                
        //System.out.println("Array size: "+ offset.getArraySize());
	}
	/**
	 * Sets the bins to the x and y axes and creates the buffer of the histogram
	 * 
	 * @param bx
	 *            number of bins on the x axis
	 * @param xmin
	 *            the minimum value on the x axis
	 * @param xmax
	 *            the maximum value on the x axis
	 * @param by
	 *            number of bins on the y axis
	 * @param ymin
	 *            the minimum value on the y axis
	 * @param ymax
	 *            the maximum value on the y axis
	 */
	public final void set(int bx, double xmin, double xmax, 
			              int by, double ymin, double ymax,
			              int bz, double zmin, double zmax) {
		
		xAxis.set(bx, xmin, xmax);
		yAxis.set(by, ymin, ymax);
		zAxis.set(bz, zmin, zmax);
		
		offset = new MultiIndex(bx, by, bz);
		int buff = offset.getArraySize();
		
		hBuffer = new short[buff];
	}

	/**
	 * 
	 * @return the name of the Histogram
	 */
	public String getName() {
		return hName;
	}

	/**
	 * 
	 * @return the x-axis of the 3D Histogram
	 */
	public Axis getXAxis() {
		return xAxis;
	}

	/**
	 * 
	 * @return the y-axis of the 3D Histogram
	 */
	public Axis getYAxis() {
		return yAxis;
	}
	
	/**
	 * 
	 * @return the z-axis of the 3D Histogram
	 */
	public Axis getZAxis() {
		return zAxis;
	}

    public short getMaximum(){
    	short maximum = 0;
    	int i = 0;
     	while(i < offset.getArraySize())
     	{
     		if(hBuffer[i]>maximum) maximum = hBuffer[i];
     		++i;
     	}
     	return maximum;
    }

	/**
	 * Checks if that bin is valid (exists)
	 * 
	 * @param bx
	 *            The x coordinate of the bin
	 * @param by
	 *            The y coordinate of the bin
	 * @param bz
	 *            The z coordinate of the bin
	 * @return The truth value of the validity of that bin
	 */
	private boolean isValidBins(int bx, int by, int bz) {
		if ((bx >= 0) && (bx < xAxis.getNBins()) 
		 && (by >= 0) && (by < yAxis.getNBins())
		 && (bz >= 0) && (bz < zAxis.getNBins())){
			return true;
		}
		return false;
	}

	/**
	 * Finds the bin content at that bin
	 * 
	 * @param bx
	 *            The x coordinate of the bin
	 * @param by
	 *            The y coordinate of the bin
	 * @return The content at that bin
	 */
	public short getBinContent(int bx, int by, int bz) {
		if (this.isValidBins(bx, by, bz)) {
			int buff = offset.getArrayIndex(bx, by, bz);
            if(buff>=0 && buff< offset.getArraySize())
            {
            	return hBuffer[buff];
            }
            else 
            {
            	System.out.println("[Index] error for binx = "+ bx +
                                    " biny = " + by +
                                    " binz = " + bz);
            }
		}
		return 0;
	}
	
	
	/**
	* Sets the x-axis title to the specified parameter
	* @param xTitle		The desired title of the x-axis
	*/
	public final void setXTitle(String xTitle) {
		//this.getXaxis().setTitle(xTitle);
		this.attr.getProperties().setProperty("xtitle", xTitle);
	}
        
	/**
	* Sets the y-axis title to the specified parameter
	* 
	* @param yTitle		The desired title of the y-axis
	*/
	public final void setYTitle(String yTitle) {
		//this.getYaxis().setTitle(yTitle);
		this.attr.getProperties().setProperty("ytitle", yTitle);
	}
	
	/**
	* Sets the z-axis title to the specified parameter
	* 
	* @param zTitle		The desired title of the z-axis
	*/
	public final void setZTitle(String zTitle) {
		//this.getYaxis().setTitle(zTitle);
		this.attr.getProperties().setProperty("ztitle", zTitle);
	}
        
	/**
	* The getter for the histogram title.
	* @return Title of the histogram.
	*/
	public String getTitle(){
		//return this.histTitle;
		return this.attr.getProperties().getProperty("title","");
	}
	
	/**
	* The getter for the x-axis title.
	* 
	* @return		The title of the x-axis as a string
	*/
	public String getXTitle() {
		return this.attr.getProperties().getProperty("xtitle", "");
		//return this.getXaxis().getTitle();
	}
        
	/**
	* The getter for the y-axis title.
	* 
	* @return		The title of the y-axis as a string
	*/
	public String getYTitle() {
		return this.attr.getProperties().getProperty("ytitle", "");
		//return this.getYaxis().getTitle();
	}
	
	/**
	* The getter for the z-axis title.
	* 
	* @return		The title of the z-axis as a string
	*/
	public String getZTitle() {
		return this.attr.getProperties().getProperty("ztitle", "");
		//return this.getZaxis().getTitle();
	}
        
	/**
	* Sets the specified parameter as the title of the histogram
	* 
	* @param title		The desired title of the histogram
	*/
	public final void setTitle(String title) {
		//histTitle = title;
		this.attr.getProperties().setProperty("title", title);
	}
        
	/**
	* Sets the bin to that value
	* 
	* @param bx
	*            The x coordinate of the bin
	* @param by
	*            The y coordinate of the bin
	* @param bz
	*            The z coordinate of the bin
	* @param w
	*            The desired value to set the bin to
	*/
	public void setBinContent(int bx, int by, int bz, short w) {
		if (this.isValidBins(bx, by, bz)) {
			int buff = offset.getArrayIndex(bx, by, bz);
			hBuffer[buff] = w;
		}
	}

	/**
	 * Adds 1.0 to the bin with that value
	 * 
	 * @param x
	 *            the x coordinate value
	 * @param y
	 *            the y coordinate value
	 * @param z
	 *            the z coordinate value
	 */
	public void fill(double x, double y, double z) {
		int bin = this.findBin(x, y, z);
		if (bin >= 0)
			this.addBinContent(bin);
	}

	public void fill(double x, double y, double z , double w) {
		int bin = this.findBin(x, y, z);
		if (bin >= 0) {
			this.addBinContent(bin, w);
		}
	}

	/**
	 * Increments the current bin by 1.0
	 * 
	 * @param bin
	 *            the bin in array indexing format to increment
	 */
	private void addBinContent(int bin) {
		hBuffer[bin] += 1;
        if(hBuffer[bin]>this.maximumBinValue) 
            this.maximumBinValue = hBuffer[bin];
	}

	/**
	 * Increments the bin with that value by that weight
	 * 
	 * @param bin
	 *            the bin to add the content to, in array indexing format
	 * @param w
	 *            the value to add to the bin content
	 */
	private void addBinContent(int bin, double w) {
		hBuffer[bin] += w;
        if(hBuffer[bin]>this.maximumBinValue) 
            this.maximumBinValue = hBuffer[bin];
	}
       /*
        public ArrayList<H1D>  getSlicesX(){
            ArrayList<H1D>  slices = new ArrayList<H1D>();
            for(int loop = 0; loop < this.getXAxis().getNBins(); loop++){
                H1D slice = this.sliceX(loop);
                slice.setName(this.getName()+"_"+loop);
                slices.add(slice);
            }
            return slices;
        }
        
        public ArrayList<H1D>  getSlicesY(){
            ArrayList<H1D>  slices = new ArrayList<H1D>();
            for(int loop = 0; loop < this.getYAxis().getNBins(); loop++){
                H1D slice = this.sliceY(loop);
                slice.setName(this.getName()+"_"+loop);
                slices.add(slice);
            }
            return slices;
        }
        
        public void add(H2D h){
            if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                    h.getYAxis().getNBins()==this.getYAxis().getNBins()){
                for(int loop = 0; loop < this.hBuffer.length; loop++){
                    this.hBuffer[loop] = this.hBuffer[loop] + h.hBuffer[loop];
                }
            } else {
                System.out.println("[warning] ---> error adding histograms " 
                        + this.getName() + "  " + h.getName()
                        + ". inconsistent bin numbers");
            }
        }
        
        
        public static H2D  divide(H2D h1, H2D h2){
            if((h1.getXAxis().getNBins()!=h2.getXAxis().getNBins())||
                    (h1.getYAxis().getNBins()!=h2.getYAxis().getNBins())
                    ){
                System.out.println("[H2D::divide] error : histograms have inconsistent bins");
                return null;
            }
            
            H2D h2div = new H2D(h1.getName()+"_DIV",
                    h1.getXAxis().getNBins(),h1.getXAxis().min(),h1.getXAxis().max(),
                    h1.getYAxis().getNBins(),h1.getYAxis().min(),h1.getYAxis().max()                    
            );
            for(int bx = 0; bx < h1.getXAxis().getNBins();bx++){
                for(int by = 0; by < h1.getYAxis().getNBins();by++){
                    double bc = 0;
                    if(h2.getBinContent(bx, by)!=0){
                        h2div.setBinContent(bx, by, h1.getBinContent(bx, by)/h2.getBinContent(bx, by));
                    }
                }    
            }
            return h2div;
        }
        
        public void divide(H2D h){
            if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                    h.getYAxis().getNBins()==this.getYAxis().getNBins()){
                for(int loop = 0; loop < this.hBuffer.length; loop++){
                    if(h.hBuffer[loop]==0){
                        this.hBuffer[loop] = 0.0;
                    } else {
                        this.hBuffer[loop] = this.hBuffer[loop]/h.hBuffer[loop];
                    }
                }
            } else {
                System.err.println("[H2D::divide] error the bins in 2d histogram do not match");
            }
        }
        */
	/**
	 * Finds which bin has that value.
	 * 
	 * @param x
	 *            The x value to search for
	 * @param y
	 *            The y value to search for
	 * @return The bin, in array indexing format, which holds that x-y value
	 */
	public int findBin(double x, double y, double z) {
		int bx = xAxis.getBin(x);
		int by = yAxis.getBin(y);
		int bz = zAxis.getBin(z);
		if (this.isValidBins(bx, by, bz)) {
			return (offset.getArrayIndex(bx, by, bz));
		}
		return -1;
	}

	/**
	 * Generates a 3D array with the content in the histogram
	 * 
	 * @return a 3D array with each bin in its array index
	 */
	public double[][][] getContentBuffer() {
		double[][][] buff = new double[xAxis.getNBins()][yAxis.getNBins()][zAxis.getNBins()];
		for (int xloop = 0; xloop < xAxis.getNBins(); xloop++) {
			for (int yloop = 0; yloop < yAxis.getNBins(); yloop++) {
				for (int zloop = 0; zloop < zAxis.getNBins(); zloop++) {
					buff[xloop][yloop][zloop] = this.getBinContent(xloop, yloop, zloop);
				}
			}
		}
		return buff;
	}

	/**
	 * Creates an error buffer with each element being 0.0
	 * 
	 * @return a double 3D array with a size of xAxis * yAxis with each element
	 *         being 0.0
	 */
	public double[][][] getErrorBuffer() {
		double[][][] buff = new double[xAxis.getNBins()][yAxis.getNBins()][zAxis.getNBins()];
		for (int xloop = 0; xloop < xAxis.getNBins(); xloop++) {
			for (int yloop = 0; yloop < yAxis.getNBins(); yloop++) {
				for (int zloop = 0; zloop < zAxis.getNBins(); zloop++) {
					buff[xloop][yloop][zloop] = 0.0;
				}
			}
		}
		return buff;
	}

	/**
	 * Specifies the region in the 2D histogram with those attributes
	 * 
	 * @param name
	 *            The name of the histogram
	 * @param bx_start
	 *            The x coordinate beginning
	 * @param bx_end
	 *            The x coordinate end
	 * @param by_start
	 *            The y coordinate beginning
	 * @param by_end
	 *            The y coordinate end
	 * @return A 2D histogram with the entered specifications
	 */
	/*
	public H2D getRegion(String name, int bx_start, int bx_end,
			int by_start, int by_end) {
		double xBinWidth = xAxis.getBinWidth(bx_start);
		double newXMin = xAxis.min() + (xBinWidth * bx_start);
		double newXMax = xAxis.min() + (xBinWidth * bx_end);

		double yBinWidth = yAxis.getBinWidth(by_start);
		double newYMin = yAxis.min() + (yBinWidth * by_start);
		double newYMax = yAxis.min() + (yBinWidth * by_end);
		H2D regHist = new H2D(name, bx_end - bx_start, newXMin,
				newXMax, by_end - by_start, newYMin, newYMax);

		double content = 0.0;
		for (int y = by_start; y < by_end; y++) {
			for (int x = bx_start; x < bx_end; x++) {
				content = this.getBinContent(x, y);
				regHist.setBinContent(x, y, content);
			}
		}
		return regHist;
	}
        */
	
	
	/*
        public H2D histClone(String name){
            H2D hclone = new H2D(name,
                    this.xAxis.getNBins(),this.xAxis.min(),this.xAxis.max(),
                    this.yAxis.getNBins(),this.yAxis.min(),this.yAxis.max()
            );
            for(int loop = 0; loop < this.hBuffer.length; loop++){                
                hclone.hBuffer[loop] = this.hBuffer[loop];
            }
            return hclone;
        }
        
        */
	/**
	 * Creates a projection of the 2D histogram onto the X Axis, adding up all
	 * the y bins for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the x-axis
	 */
	public MyH1D projectionX() {
		String name = "X Projection";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int xNum = xAxis.getNBins();
		
		MyH1D projX = new MyH1D(name, xNum, xMin, xMax);

		double height = 0.0;
		for (int x = 0; x < xAxis.getNBins(); x++) {
			height = 0.0;
			for (int y = 0; y < yAxis.getNBins(); y++) {
				for (int z = 0; z < zAxis.getNBins(); z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projX.setBinContent(x, height);
		}

		return projX;
	}
	
	/**
	 * Creates a projection of the 3D histogram onto the X Axis, adding up all
	 * the y and z bins (in a range, inclusive) for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the x-axis
	 */
	public MyH1D projectionX(String name, int bymin, int bymax, int bzmin, int bzmax) {
		//String name = "X Projection";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int xNum = xAxis.getNBins();
		
		if(bymin < 0) bymin = 0;
		if(bymax >= yAxis.getNBins()) bymax = yAxis.getNBins() - 1;
		if(bzmin < 0) bzmin = 0;
		if(bzmax >= zAxis.getNBins()) bzmax = zAxis.getNBins() - 1;
		
		MyH1D projX = new MyH1D(name, xNum, xMin, xMax);

		double height = 0.0;
		for (int x = 0; x < xAxis.getNBins(); x++) {
			height = 0.0;
			for (int y = bymin; y <= bymax; y++) {
				for (int z = bzmin; z <= bzmax; z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projX.setBinContent(x, height);
		}

		return projX;
	}

	/**
	 * Creates a projection of the 2D histogram onto the Y Axis, adding up all
	 * the x bins for each y bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the y-axis
	 */
	public MyH1D projectionY() {
		String name = "Y Projection";
		double yMin = yAxis.min();
		double yMax = yAxis.max();
		int yNum = yAxis.getNBins();
		MyH1D projY = new MyH1D(name, yNum, yMin, yMax);

		double height = 0.0;
		for (int y = 0; y < yAxis.getNBins(); y++) {
			height = 0.0;
			for (int x = 0; x < xAxis.getNBins(); x++) {
				for (int z = 0; z < zAxis.getNBins(); z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projY.setBinContent(y, height);
		}

		return projY;
	}
	
	/**
	 * Creates a projection of the 3D histogram onto the Y Axis, adding up all
	 * the x and z bins (in a range, inclusive) for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram3D
	 *         object onto the y-axis
	 */
	public MyH1D projectionY(String name, int bxmin, int bxmax, int bzmin, int bzmax) {
		//String name = "X Projection";
		double yMin = yAxis.min();
		double yMax = yAxis.max();
		int yNum = yAxis.getNBins();
		
		if(bxmin < 0) bxmin = 0;
		if(bxmax >= xAxis.getNBins()) bxmax = xAxis.getNBins() - 1;
		if(bzmin < 0) bzmin = 0;
		if(bzmax >= zAxis.getNBins()) bzmax = zAxis.getNBins() - 1;
		
		MyH1D projY = new MyH1D(name, yNum, yMin, yMax);

		double height = 0.0;
		for (int y = 0; y < yAxis.getNBins(); y++) {
			height = 0.0;
			for (int x = bxmin; x <= bxmax; x++) {
				for (int z = bzmin; z <= bzmax; z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projY.setBinContent(y, height);
		}

		return projY;
	}
	
	/**
	 * Creates a projection of the 3D histogram onto the Z Axis, adding up all
	 * the x and y bins (in a range, inclusive) for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram3D
	 *         object onto the y-axis
	 */
	public MyH1D projectionZ(String name, int bxmin, int bxmax, int bymin, int bymax) {
		//String name = "X Projection";
		double zMin = zAxis.min();
		double zMax = zAxis.max();
		int zNum = zAxis.getNBins();
		
		if(bxmin < 0) bxmin = 0;
		if(bxmax >= xAxis.getNBins()) bxmax = xAxis.getNBins() - 1;
		if(bymin < 0) bymin = 0;
		if(bymax >= yAxis.getNBins()) bymax = yAxis.getNBins() - 1;
		
		MyH1D projZ = new MyH1D(name, zNum, zMin, zMax);

		double height = 0.0;
		for (int z = 0; z < zAxis.getNBins(); z++) {
			height = 0.0;
			for (int x = bxmin; x <= bxmax; x++) {
				for (int y = bymin; y <= bymax; y++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projZ.setBinContent(z, height);
		}

		return projZ;
	}

	/**
	 * Creates a 1-D Histogram slice of the specified y Bin
	 * 
	 * @param xBin		the bin on the y axis to create a slice of
	 * @return 			a slice of the x bins on the specified y bin as a 1-D Histogram
	 */
	/*
	public H1D sliceX(int xBin) {
		String name = "Slice of " + xBin + " X Bin";
		double xMin = yAxis.min();
		double xMax = yAxis.max();
		int xNum    = yAxis.getNBins();
		H1D sliceX = new H1D(name, name, xNum, xMin, xMax);

		for (int x = 0; x < xNum; x++) {
			sliceX.setBinContent(x, this.getBinContent(xBin,x));
		}
		return sliceX;
	}
	*/

	/**
	 * Creates a 1-D Histogram slice of the specified x Bin
	 * 
	 * @param yBin			the bin on the x axis to create a slice of
	 * @return 				a slice of the y bins on the specified x bin as a 1-D Histogram
	 */
	/*
	public H1D sliceY(int yBin) {
		String name = "Slice of " + yBin + " Y Bin";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int    xNum = xAxis.getNBins();
		H1D sliceY = new H1D(name, name, xNum, xMin, xMax);

		for (int y = 0; y < xNum; y++) {
			sliceY.setBinContent(y, this.getBinContent(y,yBin));
		}

		return sliceY;
	}
*/
	/*
	public double[] offset() {
		return hBuffer;
	}
	*/
	
	/**
	* Resets the content of the histogram, sets all bin contents to 0
	*/
	public void reset(){
		for(int bin = 0; bin < this.hBuffer.length; bin++){
			this.hBuffer[bin] = 0;
		}
	}
        
    @Override
    public TreeMap<Integer, Object> toTreeMap() {
        TreeMap<Integer, Object> hcontainer = new TreeMap<Integer, Object>();
        hcontainer.put(1, new int[]{3});     
        byte[] nameBytes = this.hName.getBytes();
        hcontainer.put(2, nameBytes);
        hcontainer.put(3, new int[]{this.getXAxis().getNBins(),this.getYAxis().getNBins(),this.getZAxis().getNBins()});
        hcontainer.put(4, new double[]{
            this.getXAxis().min(),this.getXAxis().max(),
            this.getYAxis().min(),this.getYAxis().max(),
            this.getZAxis().min(),this.getZAxis().max()
        });
        hcontainer.put(5, this.hBuffer);
        return hcontainer;
    }

    @Override
    public void fromTreeMap(Map<Integer, Object> map){
        if(map.get(1) instanceof int[]){
            if(  ((int[]) map.get(1))[0]==3){
                int[]    nbins      = ((int[]) map.get(3));
                double[] binsrange  = ((double[]) map.get(4));
                byte[] name     = (byte[]) map.get(2);
                hName = new String(name);                
                this.set(nbins[0], binsrange[0],binsrange[1],
                         nbins[1], binsrange[2],binsrange[3],
                         nbins[2], binsrange[4],binsrange[5]);
                
                double[] binc = (double[]) map.get(5);
                //double[] bine = (double[]) map.get(5);
                System.arraycopy(binc, 0, hBuffer, 0, binc.length);
            }
        }
    }

    /*
    public DataRegion getDataRegion() {
        DataRegion  region = new DataRegion();
        region.MINIMUM_X = this.xAxis.getBinCenter(0)-this.xAxis.getBinWidth(0)/2.0;
        region.MAXIMUM_X = this.xAxis.getBinCenter(this.xAxis.getNBins()-1)-
                this.xAxis.getBinWidth(this.xAxis.getNBins()-1)/2.0;
        region.MINIMUM_Y = this.yAxis.getBinCenter(0)-this.yAxis.getBinWidth(0)/2.0;
        region.MAXIMUM_Y = this.yAxis.getBinCenter(this.yAxis.getNBins()-1)-
                this.yAxis.getBinWidth(this.yAxis.getNBins()-1)/2.0;
        
        region.MINIMUM_Z = 0;
        region.MAXIMUM_Z = 0;
        for(int bin = 0; bin < this.hBuffer.length;bin++){
            //region.MAXIMUM_Z = this.maximumBinValue;
            if(this.hBuffer[bin]>region.MAXIMUM_Z) region.MAXIMUM_Z = this.hBuffer[bin];
            if(this.hBuffer[bin]<region.MINIMUM_Z) region.MINIMUM_Z = this.hBuffer[bin];
        }
        if(region.MINIMUM_Z==region.MAXIMUM_Z){
            region.MAXIMUM_Z = region.MINIMUM_Z + 1;
        }
        return region;
    }
    */

    public Integer getDataSize() {
        return this.xAxis.getNBins()*this.yAxis.getNBins()*this.zAxis.getNBins();
    }
    
    public Double getDataX(int index) {
        return 1.0;
    }

    public Double getDataY(int index) {
        return 1.0;
    }

    public Double getErrorX(int index) {
        return 1.0;

    }

    public Double getErrorY(int index) {
        return 1.0;
    }

    public Attributes getAttributes() {
        return this.attr;
    }

    public short getData(int x, int y, int z) {
        return this.getBinContent(x, y, z);
    }

    public Integer getDataSize(int axis) {
        if(axis==0) return this.getXAxis().getNBins();
        if(axis==1) return this.getYAxis().getNBins();
        if(axis==2) return this.getZAxis().getNBins();
        return 0;
    }

	
	public static void main(String[] args){ 
		MyH3S testh3 = new MyH3S("test1", 5, 0.0, 5.0, 
					                      5, 0.0, 5.0,
				                          5, 0.0, 5.0);
		
		testh3.fill(0.5, 0.5, 0.5);
		testh3.fill(0.5, 0.5, 0.5);
		testh3.fill(1.5, 1.5, 1.5);
		testh3.fill(4.5, 4.5, 4.5);
		testh3.fill(0.1, 4.5, 2.5);
		//System.out.println("bin: " + 0 + " content: " + testh3.getBinContent(0, 1, 0));
		//System.out.println("bin: " + 1 + " content: " + testh3.getBinContent(1, 1, 1));
		TGCanvas testcanv = new TGCanvas();
		MyH1D testh1 = new MyH1D();
		
		testh1 = testh3.projectionY("test",0,1,0,2).histClone("newtest");
		//testh1.fill(5.0);
		testcanv.draw(testh1);
		
	}

	
}
