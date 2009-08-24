package de.cau.cs.kieler.sim.mobile.table;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Screen;

public class TableDataItem extends CustomItem {

	//where the cursor in the form is
    private final static int UPPER = 0;
    private final static int IN = 1;
    private final static int LOWER = 2;
    private int status = UPPER;
	
    private boolean alignTop;
	private TableData tableData;
	private Font font;
	private Font font_focus;
	private int width;
	private final int COLOR_FOREGROUND;
	private final int COLOR_HIGHLIGHTED_FOREGROUND;
	private final int COLOR_BACKGROUND;
	private final int COLOR_HIGHLIGHTED_BACKGROUND;
	
	//private Text le_focus;
	private Text le_normal;
	
	private final static int LABELOFFSET_LEFT = 46;
	private final static int LABELOFFSET_RIGHT = 5;
	private final static int MINHEIGHT = 22;
	
  protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
          int[] visRect_inout) {
	  switch (dir) {
	  case Canvas.DOWN:
		  if (status == UPPER) {
//			  status = IN;
//			  MobileDataTable.getInstance().setLastSelected(this);
//			  repaint();
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
//			  status = IN;
//			  MobileDataTable.getInstance().setLastSelected(this);
//			  repaint();
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
  
   public void setStatus(int status) {
	   this.status = status;
   }
  
  	public boolean lastTableItem() {
  		int index = this.tableData.getParentTableDataList()
  												.indexOf(this.tableData);
  		return (index == this.tableData.getParentTableDataList().size()-1);
  	}
  	
  	public boolean firstTableItem() {
  		int index = this.tableData.getParentTableDataList()
  												.indexOf(this.tableData);
  		return (index == 0);
  	}
  	
  	public void setAlignTop(boolean alignTop) {
  		this.alignTop = alignTop;
  	}
  
//    public boolean hasFocus() {
//    	return ((this.status == IN));
//    }
    public boolean hasFocus() {
    	return ((this.status == IN)
    			|| (MobileDataTable.getInstance().lastSelected == this));
    }
  
	public TableData getTableData() {
		return tableData;
	}
	
	private int getHeight() {
		int height = getLabelHeight(getWidth());
		if (height < this.MINHEIGHT) return this.MINHEIGHT;
		return height;
	}

	private int getWidth() {
		return this.width;
	}

	public String getLabel() {
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
	
	public TableDataItem(TableData tableData,
						int width) {
		super("");
		this.alignTop = false;
		this.tableData = tableData;
		this.font = Font.getFont(Font.FACE_SYSTEM, 
								 Font.STYLE_PLAIN, 
								 Font.SIZE_MEDIUM);
		this.font_focus = Font.getFont(Font.FACE_SYSTEM, 
				 				 Font.STYLE_BOLD, 
				 				 Font.SIZE_MEDIUM);
		COLOR_FOREGROUND = Display.getDisplay(MobileDataTable.getInstance()).
							getColor(Display.COLOR_FOREGROUND);
		COLOR_BACKGROUND = Display.getDisplay(MobileDataTable.getInstance()).
							getColor(Display.COLOR_BACKGROUND);
		COLOR_HIGHLIGHTED_FOREGROUND = Display.getDisplay(MobileDataTable.getInstance()).
							getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND);
		COLOR_HIGHLIGHTED_BACKGROUND = Display.getDisplay(MobileDataTable.getInstance()).
							getColor(Display.COLOR_HIGHLIGHTED_BORDER);
		this.width = width;
		//le_focus = new Text(font_focus, getLabel(), getLabelWidth());
		le_normal = new Text(font, getLabel(), getLabelWidth());
	}

	protected int getMinContentHeight() {
		return getHeight();
	}

	protected int getMinContentWidth() {
		return getWidth();
	}

	protected int getPrefContentHeight(int width) {
		return getHeight();
	}

	protected int getPrefContentWidth(int height) {
		return getWidth();
	}
	
	private int getLabelWidth() {
		return getWidth()-LABELOFFSET_LEFT-LABELOFFSET_RIGHT-3;
	}
	
	int getLabelHeight() {
		return getLabelHeight(getLabelWidth());
	}
	
    int getLabelHeight(int w) {
        if (getLabel() == null || getLabel().length() == 0) {
            return 0;
        } 
        int h1 = MINHEIGHT;
        int h2 = MINHEIGHT;
//        if (this.hasFocus()) {
//            if (le_focus != null)
//            	h2 = le_focus.getHeight();
//        }
//        else {
//            if (le_normal != null) 
             	h1 = le_normal.getHeight();
//        }
        int returnHeight = (h1 < h2 ? h2 : h1);
        return returnHeight;
    }

    
    //---------------------------------------------------------------------
    
    public void refresh() {
    	this.repaint();
    }
    
    //---------------------------------------------------------------------
	
	protected void paint(Graphics g, int w, int h) {
		//draw box if highlighted
		if (this.hasFocus()) {
			g.setColor(60, 120,255);
			g.fillRoundRect(1, 1, getWidth()-1, getHeight()-4, 8, 8);
			g.setColor(this.COLOR_FOREGROUND);
		}
		
		//Font font = Font.getFont(Font.FONT_INPUT_TEXT + Font.SIZE_SMALL);

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
    	if (checked != null)
    		g.drawImage(checked, 5, diff-1, 0);
    	g.drawImage(image, 25, diff, 0);
    	
    	//g.setFont(font);
		//g.drawString(this.getLabel(), 42, h-getHeight()-1, 0);

		if (this.hasFocus())
    		g.setColor(this.COLOR_HIGHLIGHTED_FOREGROUND);
    	else
    		g.setColor(COLOR_FOREGROUND);
		
//        if (this.hasFocus()) {
//            le_focus.drawTo(g, LABELOFFSET_LEFT, 0, 
//            					h+getLabelHeight());
//            le_focus.reset();
//        }
//        else {
            le_normal.drawTo(g, LABELOFFSET_LEFT, 0, 
            					h+getLabelHeight());
            le_normal.reset();
//        }

	}
	
	//=========================================================================
	//=========================================================================

	
	public class Text {
	    private Font font;
	    private int width;
	    private int height;
	    private int position;
	    private int length;
	    private int start = 0;
	    private int size;

	    //---------------------------------------------------------------------
	    
	    private String[] textArray;
	    
	    //---------------------------------------------------------------------

	    public Text(Font font, String text, int width) {
	        this.font = font;
	        this.width = width;
	        this.length = text.length();
	        setText(text);
	    }

	    //---------------------------------------------------------------------
	    
	    public int getSize() {
	    	return size;
	    }
	    
	    //---------------------------------------------------------------------

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
	    
	    public boolean hasMoreElements() {
	        return (position <= (length - 1));
	    }


	    //---------------------------------------------------------------------
	    
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

	    public int getHeight() {
	    	return this.height;
	    }
	    
	    //---------------------------------------------------------------------

	    public void reset() {
	        start = 0;
	        position = 0;
	    }
	}
}
