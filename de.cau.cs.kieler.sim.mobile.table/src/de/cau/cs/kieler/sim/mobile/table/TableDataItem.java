/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.sim.mobile.table;

import java.util.NoSuchElementException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * The class TableDataItem provides a CustomItem that can be listed in
 * the form of the MobileDataTable to display one entry of a TableData.
 * This TableData entry is also linked by this CustomItem. 
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 */
public class TableDataItem extends CustomItem {

	/** The constant UPPER indicating where the cursor in the form is */
	private final static int UPPER = 0;
    
    /** The constant IN indicating where the cursor in the form is */
    private final static int IN = 1;
    
    /** The constant LOWER indicating where the cursor in the form is */
    private final static int LOWER = 2;
    
    /** The status indicating where the cursor in the form is. */
    private int status = UPPER;
	
    /** The align top flag to align the image at the top (multi line). */
    private boolean alignTop;
	
	/** The TableData that is linked to this CustomItem. */
	private TableData tableData;
	
	/** The used font. */
	private Font font;
	
	/** The width. */
	private int width;
	
	/** The foreground color. */
	private final int COLOR_FOREGROUND;
	
	/** The highlighted foreground color. */
	private final int COLOR_HIGHLIGHTED_FOREGROUND;
	
	/** The text variable calculates & holds the text in a displayable form. */
	private Text text;
	
	/** The left offset LABELOFFSET_LEFT. */
	private final static int LABELOFFSET_LEFT = 46;
	
	/** The right offset LABELOFFSET_RIGHT. */
	private final static int LABELOFFSET_RIGHT = 5;
	
	/** The minimal height of the entry MINHEIGHT. */
	private final static int MINHEIGHT = 22;
	
	//-------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#traverse(int, int, int, int[])
	 */
	protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
								int[] visRect_inout) {
	  switch (dir) {
	  case Canvas.DOWN:
		  if (status == UPPER) {
			  //do nothing when outside the item
		  } 
		  else {
			  if (!lastTableItem()) {
				  MobileDataTable.getInstance().setLastSelected(this,true);
				  status = LOWER;
			  	  return false;
			  }
		  }
		  break;
	  case Canvas.UP:
		  if (status == LOWER) {
			  //do nothing when outside the item
		  } else {
			  if (!this.firstTableItem() ||
				  MobileDataTable.getInstance().isMaster()) {
				  MobileDataTable.getInstance().setLastSelected(this,true);
				  status = UPPER;
				  return false;
			  }
		  }
		  break;
	  }
	  MobileDataTable.getInstance().setLastSelected(this,false);
	  status = IN;
	  repaint();
	  return true;
	}
  
	//-------------------------------------------------------------------------

	/**
	 * Sets the status.
	 * 	
	 * @param status the new status
	 */
	public void setStatus(int status) {
	   this.status = status;
	}
  
	//-------------------------------------------------------------------------

	/**
	 * Check whether this is the last item in the table.
	 * 
	 * @return true, if item is the last one
	 */
	public boolean lastTableItem() {
	   int index = this.tableData.getParentTableDataList()
											.indexOf(this.tableData);
	   return (index == this.tableData.getParentTableDataList().size()-1);
	}

	//-------------------------------------------------------------------------
  	
	/**
	 * Check whether this is the first item in the table.
	 * 
	 * @return true, if item is the last one
	 */
	 public boolean firstTableItem() {
  		int index = this.tableData.getParentTableDataList()
  												.indexOf(this.tableData);
  		return (index == 0);
	 }
  	
	 //-------------------------------------------------------------------------
	  
	 /**
	  * Sets the align top flag.
	  * 
	  * @param alignTop the new align top flag
	  */
	 public void setAlignTop(boolean alignTop) {
  		this.alignTop = alignTop;
	 }

	 //-------------------------------------------------------------------------
  
	 /**
	  * Checks whether the item has the focus. This is the case whenever the 
	  * status indicates IN (traversed) or when the MobileDataTable's 
	  * lastSelected variable contains exactly this item.
	  * 
	  * @return true, if item is focused
	  */
	 public boolean hasFocus() {
	   	return ((this.status == IN)
	   			|| (MobileDataTable.getInstance().lastSelected == this));
	 }
  
	 //-------------------------------------------------------------------------

	 /**
	  * Gets the linked TableData entry.
	  * 
	  * @return the TableData
	  */
	 public TableData getTableData() {
		return tableData;
	 }

	 //-------------------------------------------------------------------------
	 
	 /**
	  * Gets the height of the item. If the actual height is smaller than the
	  * minimal height than the latter is returned.
	  * 
	  * @return the height
	  */	
	 private int getHeight() {
//		int height = getLabelHeight(getWidth());
		int height = getLabelTextHeight();
		if (height < TableDataItem.MINHEIGHT) return TableDataItem.MINHEIGHT;
		return height;
	 }

	 //-------------------------------------------------------------------------
	
	 /**
	  * Gets the predefined width of the item.
	  * 
	  * @return the width
	  */
	 private int getWidth() {
		return this.width;
	 }

	 //-------------------------------------------------------------------------

	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.Item#getLabel()
	  */
	 public String getLabel() {
		//if this entry has a non-empty value this is displayed in brackets
		//otherwise only the key is displayed
		String key = tableData.getKey();
		String value = tableData.getValue();
		if ((value != null) && (value.length() > 0)) {
			value = " (" + value + ")";
		}
		String modifiedTag = "";
		if (this.tableData.isModifiedDisplay())
			modifiedTag = "*";
		return modifiedTag + key + value;
	 }
	
	 //-------------------------------------------------------------------------

	 /**
	  * Instantiates a new TableDataItem. The font colors, the alignment are set
	  * to default values. The linked TableData and the predefined with are
	  * parametrized.
	  * 
	  * @param tableData the table data
	  * @param width the width
	  */
	 public TableDataItem(TableData tableData,
						int width) {
		super("");
		this.alignTop = false;
		this.tableData = tableData;
		this.font = Font.getFont(Font.FACE_SYSTEM, 
								 Font.STYLE_PLAIN, 
								 Font.SIZE_MEDIUM);
		COLOR_FOREGROUND = Display.getDisplay(MobileDataTable.getInstance()).
							getColor(Display.COLOR_FOREGROUND);
		COLOR_HIGHLIGHTED_FOREGROUND = Display.getDisplay(
							MobileDataTable.getInstance()).
							getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND);
		this.width = width;
		text = new Text(font, getLabel(), getLabelTextWidth());
	 }

	 //-------------------------------------------------------------------------

	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
	  */
	 protected int getMinContentHeight() {
		return getHeight();
	 }

	 //-------------------------------------------------------------------------
	
	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
	  */
	 protected int getMinContentWidth() {
		return getWidth();
	 }

	 //-------------------------------------------------------------------------

	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
	  */
	 protected int getPrefContentHeight(int width) {
		return getHeight();
	 }

	 //-------------------------------------------------------------------------

	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
	  */
	 protected int getPrefContentWidth(int height) {
		return getWidth();
	 }
	
	 //-------------------------------------------------------------------------

	 /**
	  * Gets the width of the label. This is the total with minus the offsets
	  * minus a constant of 3 pixel.
	  * 
	  * @return the width of the label
	  */
	 public int getLabelTextWidth() {
		return getWidth()-LABELOFFSET_LEFT-LABELOFFSET_RIGHT-3;
	 }
	
	 //-------------------------------------------------------------------------

	 /**
	  * Gets the calculated label height or the minimum height.
	  * 
	  * @return the label height
	  */
	 public int getLabelTextHeight() {
        if (getLabel() == null || getLabel().length() == 0) {
            return 0;
        } 
        int h1 = MINHEIGHT;
        int h2 = MINHEIGHT;
         	h1 = text.getHeight();
        int returnHeight = (h1 < h2 ? h2 : h1);
        return returnHeight;
	 }
    
	 //---------------------------------------------------------------------
    
	 /**
	  * Refresh this TableDataItem.
	  */
	 public void refresh() {
    	this.repaint();
	 }
    
	 //---------------------------------------------------------------------
	
	 /* (non-Javadoc)
	  * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	  */
	 protected void paint(Graphics g, int w, int h) {
		//draw box if highlighted
		if (this.hasFocus()) {
			g.setColor(60, 120,255);
			g.fillRoundRect(1, 1, getWidth()-1, getHeight()-4, 8, 8);
			g.setColor(this.COLOR_FOREGROUND);
		}
		
		//draw a signal and a checked or unchecked image iff this is a signal
		//draw a variable image iff this is not a signal
		Image image = MobileDataTable.getInstance().variableImage;
    	Image checked = null;
    	if (tableData.isSignal()) {
			image = MobileDataTable.getInstance().signalImage;
			try {
	    		if (tableData.isPresent())
	    			checked = Image.createImage("/checked.png"); //this.imgMultiOn;
	    		else
	    			checked = Image.createImage("/unchecked.png"); //this.imgMultiOff;
			} catch(Exception e){}
    	}
    	int diff = h-16+((16-getHeight())/2);
    	if (alignTop) {
    		diff = 4;
    	}
    	//draw the additinal checked image
    	if (checked != null)
    		g.drawImage(checked, 5, diff-1, 0);
    	g.drawImage(image, 25, diff, 0);
    	
    	//set the color of the text depending on the focus
		if (this.hasFocus())
    		g.setColor(this.COLOR_HIGHLIGHTED_FOREGROUND);
    	else
    		g.setColor(COLOR_FOREGROUND);
		
		//draw the multi-line text
        text.drawTo(g, LABELOFFSET_LEFT, 0, h+getLabelTextHeight());
        text.reset();
	}
	
	//=========================================================================

	/**
	 * The Class Text holds the text in a displayable multi-line form. This 
	 * means an array of lines is being calculated each time the setText-method
	 * is called.
	 */
	public class Text {
	    
    	/** The font used for calculations. */
    	private Font font;
	    
    	/** The maximal width. */
    	private int width;
	    
    	/** The calculated height. */
    	private int height;
	    
    	/** The current position when calculating. */
    	private int position;
	    
    	/** The length or number of characters of the whole text. */
    	private int length;
	    
    	/** The starting position that is increased during the calculation. */
    	private int start = 0;
	    
    	/** The size. This is equal to the number of lines. */
    	private int size;

	    /** The text array of lines. */
    	private String[] textArray;
	    
	    //---------------------------------------------------------------------

	    /**
    	 * Instantiates a new text.
    	 * 
    	 * @param font the font to use for calculations
    	 * @param text the text to display
    	 * @param width the maximal width to use for calculations
    	 */
    	public Text(Font font, String text, int width) {
	        this.font = font;
	        this.width = width;
	        this.length = text.length();
	        setText(text);
	    }

	    //---------------------------------------------------------------------
	    
	    /**
    	 * Gets the size.
    	 * 
    	 * @return the size
    	 */
    	public int getSize() {
	    	return size;
	    }
	    
	    //---------------------------------------------------------------------

	    /**
    	 * Sets the text. This method also starts recalculation of lines, size
    	 * and the height of the displayable text field.
    	 * 
    	 * @param text the new text
    	 */
    	public void setText(String text) {
	    	if ((text == null)||(text.equals(""))) {
	    		this.height = 0;
	    		this.textArray = null;
	    		return;
	    	}
	    	size = 0;
	    	height = 4;
	        reset();
	    	int fontHeight = font.getHeight() + 1;
	        while (hasMoreElements()) {
	        	size++; nextElement(text);
	        	this.height += fontHeight;
	        }
	        reset();
	        int c = 0;
	        textArray = new String[size];
	        while (hasMoreElements()) {
	        	textArray[c] = (String)this.nextElement(text);
	        	c++;
	        }
	    }
	    
	    //---------------------------------------------------------------------
	    
	    /**
    	 * Checks for more elements, i.e., if there are additional characters.
    	 * 
    	 * @return true, if there are more characters
    	 */
    	public boolean hasMoreElements() {
	        return (position <= (length - 1));
	    }


	    //---------------------------------------------------------------------
	    
	    /**
    	 * Returns the next element. This returns the next line containing all
    	 * characters from start until the end or from start until the line
    	 * has reached its maximum length.
    	 * 
    	 * @param text the text
    	 * 
    	 * @return the object
    	 * 
    	 * @throws NoSuchElementException the no such element exception
    	 */
    	private Object nextElement(String text) throws NoSuchElementException {
	        try {
	            int next = next(text);
	            String s = text.substring(start, next);
	            start = next;
	            if (text.length() - 1 > start && (text.charAt(start) == ' ' 
	                || text.charAt(start) == '\n')) {
	                position++;
	                start++;
	            }
	            return s;
	        }
	        catch (IndexOutOfBoundsException e) {
	            throw new NoSuchElementException(e.getMessage());
	        }
	        catch (Exception e) {
	            throw new NoSuchElementException(e.getMessage());
	        }
	    }

	    //---------------------------------------------------------------------

	    /**
    	 * Next. Helper method for {@link #nextElement(String)}.
    	 * 
    	 * @param text the text
    	 * 
    	 * @return the end position for the next line (element)
    	 */
    	private int next(String text) {
	        int i = position;
	        int lastBreak = -1;

	        for (; i < length
	           && font.stringWidth(text.substring(position, i)) 
	           										<= width; i++) {
	        	
	            if (text.charAt(i) == ' ') {
	                lastBreak = i;
	            }
	            else if (text.charAt(i) == '\n') {
	                lastBreak = i;
	                break;
	            }
	        }
        	
	        if (i == length) {
	            position = i;
	        }
	        else if (lastBreak == position) {
	            position++;
	        }
	        else if (lastBreak < position) {
	            position = i;
	        }
	        else {
	            position = lastBreak;
	        }

	        return position;
	    }

	    //---------------------------------------------------------------------

	    /**
    	 * This method finally draws the text onto the display when a Graphics
    	 * g is provided together with the coordinates where to place the 
    	 * multi-line text.
    	 * 
    	 * @param g the g
    	 * @param startx the startx
    	 * @param starty the starty
    	 * @param maxY the max y
    	 */
    	public void drawTo(Graphics g, int startx, int starty, int maxY) {
	        int fontHeight = font.getHeight() + 1;

	        for (int c = 0; c < size; c++) {
	        	g.setFont(font);
	            g.drawString(textArray[c], 
	                    startx, starty, Graphics.TOP | Graphics.LEFT);
	            starty += fontHeight;
	        }
	    }
	    
	    //---------------------------------------------------------------------

	    /**
    	 * Gets the calculated height.
    	 * 
    	 * @return the height
    	 */
    	public int getHeight() {
	    	return this.height;
	    }
	    
	    //---------------------------------------------------------------------

	    /**
    	 * Reset for recalculation.
    	 */
    	public void reset() {
	        start = 0;
	        position = 0;
	    }
    	
	}
	//=========================================================================
	
}
