/*
 * @(#)Editarea.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

/*
 * This code is based upon code from the TelnetD library.
 * http://sourceforge.net/projects/telnetd/
 * Used under the BSD license.
 */

package org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit;

import java.util.Vector;

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;


/**
 * Class that implements an Editarea.
 *
 * @author Dieter Wimberger
 * @version 1.0 alpha 27/04/2000
 */
 public class Editarea extends ActiveComponent {

	//Members
	//private int colCursor=0;
    private int rowCursor=0;
	private int myRows=0;
	private boolean firstrun=true;
	private int firstVisibleRow=0;
//	private int lastcursor=0;
    private String hardwrap="\n";
	private String softwrap=" ";

	//Associations
	private Vector lines;
	private Editline line;

	 public Editarea(BasicTerminalIO io, String name,int rowheight,int maxrows) {
		super(io,name);
		lines=new Vector(); 
		myRows=maxrows;
		firstrun=true;
		firstVisibleRow=0;
		setDimension(new Dimension(myIO.getColumns(),rowheight));
	 }//constructor
   
	/**
	 * Accessor method for field buffer size.
	 * @return int that represents the number of chars in the fields buffer.
	 */
	 public int getSize() {
		int size=0;
		//iterate over buffers and accumulate size
		//think of solution for hardwraps
		return size;
	 }//getSize

	 public void setHardwrapString(String str){
	 	hardwrap=str;
	 }//setHardwrapString

	 public String getHardwrapString() {
	 	return hardwrap;
	 }//getHardwrapString
	 
	 public void setSoftwrapString(String str) {
	 	softwrap=str;
	 }//setSoftwrapString
	 
	 public String getSoftwrapString() {
	 	return softwrap;
	 }//getSoftwrapString
	 
	 public String getValue() {
		StringBuffer sbuf=new StringBuffer();
		//iterate over buffers and accumulate size
		Editline el=null;
		for (int i=0;i<lines.size();i++) {
			el=getLine(i);
			sbuf.append(el.getValue()).append(((el.isHardwrapped())? hardwrap:softwrap));
		}
		return sbuf.toString();
	 }//getValue
     
	 public void setValue(String str)
	 		throws BufferOverflowException {

		//buffers
		lines.removeAllElements();
		//cursor
		rowCursor=0;
		//colCursor=0;
		// think of a buffer filling strategy

	 }//setValue
	 
	 public void clear() {
		
		//Buffers
		lines.removeAllElements();
 	   //Cursor
		rowCursor=0;
		//colCursor=0;
		//Screen
		draw();

	 }//clear

	 public void run() {
//	 	int oldcursor=0;
	 	boolean done=false;
		myIO.setAutoflushing(false);
		//check flag
	 	if(firstrun) {
			//reset flag
			firstrun=false;
			//make a new editline
			line=createLine();
			appendLine(line);
		}
		
		do {
			//switch return of a line
			switch (line.run()) {
				case BasicTerminalIO.UP:
					if(rowCursor>0) {
						if (firstVisibleRow==rowCursor) {
							scrollUp();
						} else {
							cursorUp();
						}
					} else {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.DOWN:
					
					if(rowCursor<(lines.size()-1)) {
						if(rowCursor==firstVisibleRow+(myDim.getHeight()-1)) {
							scrollDown();
						} else {
							cursorDown();
						}
					} else {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.ENTER:
					/*				
					System.out.println("DEBUG:firstVisibleRow:"+firstVisibleRow);
					System.out.println("DEBUG:rowCursor:"+rowCursor);
					System.out.println("DEBUG:lines:"+lines.size());
					System.out.println("DEBUG:maxRows:"+myRows);
					System.out.println("DEBUG:height:"+myDim.getHeight());
					*/
					//ensure exit on maxrows line
					if(rowCursor==(myRows-1)) {
						done=true;
					} else {
						if (!hasLineSpace()) {
							myIO.bell();
						} else {
							String wrap=line.getHardwrap();
							line.setHardwrapped(true);
							
							if (rowCursor==(lines.size()-1)) {
								appendNewLine();
							} else {
								insertNewLine();
							}
							//cursor
							rowCursor++;
							//activate new row
							activateLine(rowCursor);
							//set value of new row
							try { 
								line.setValue(wrap);
								line.setCursorPosition(0);
								myIO.moveLeft(line.size()); 
							}catch (Exception ex) {}	
						}
					}
					break;
				case BasicTerminalIO.TABULATOR:
					//set cursor to end of field?
					
					done=true;
					break;
					
				case BasicTerminalIO.LEFT:
					if(rowCursor>0) {
						if (firstVisibleRow==rowCursor) {
							scrollUp();
							line.setCursorPosition(line.size());
							myIO.moveRight(line.size());
						} else {
							//Cursor
							rowCursor--;
							//buffer
							activateLine(rowCursor);
							line.setCursorPosition(line.size());
							
							//screen
							myIO.moveUp(1);
							myIO.moveRight(line.size());
						}
					} else {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.RIGHT:
					if(rowCursor<(lines.size()-1)) {
						if(rowCursor==firstVisibleRow+(myDim.getHeight()-1)) {
							line.setCursorPosition(0);
							myIO.moveLeft(line.size());
							scrollDown();
						} else {
							//Cursor
							rowCursor++;
							//screen horizontal
							myIO.moveLeft(line.size());
							//buffer
							activateLine(rowCursor);
							line.setCursorPosition(0);
							//screen
							myIO.moveDown(1);
						}
					} else {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.BACKSPACE:
					if(rowCursor==0 || line.size()!=0 || rowCursor==firstVisibleRow) {
						myIO.bell();
					} else {
						//take line from buffer
						//and draw update all below
						removeLine();
					}
					break;	
				default:
					if (!hasLineSpace()) {
						myIO.bell();
					} else {
						String wrap=line.getSoftwrap();
						//System.out.println("softwrap:"+wrap);
						line.setHardwrapped(false);
							
						if (rowCursor==(lines.size()-1)) {
							appendNewLine();
						} else {
							insertNewLine();
						}
						//cursor
						rowCursor++;
						//activate new row
						activateLine(rowCursor);
						//set value of new row
						try { 
							line.setValue(wrap);
							//getLine(rowCursor-1).getLastRelPos();
							//line.setCursorPosition(0);
							//myIO.moveLeft(line.size());
						}catch (Exception ex) {}	
					}
					
				}
				myIO.flush();
			} while (!done);
	 }//run
	 
	 private void scrollUp() {
	 	
	 	int horizontalpos=line.getCursorPosition();
	 	//System.out.println("Debug:scrolling:up:horpos:"+horizontalpos);
	 	//System.out.println("Debug:scrolling:up");
	 	//Cursors
	 	firstVisibleRow--;
	 	rowCursor--;
	 	
	 	//buffer
	 	activateLine(rowCursor);
	 	line.setCursorPosition(horizontalpos);
	 	
	 	//screen
		//horizontal
		
		//content:
		int lasthorizontal=horizontalpos;
		int count=0;
	 	for (int i=firstVisibleRow;i<(firstVisibleRow+myDim.getHeight())&&i<lines.size();i++){
				//System.out.println("Debug:scrolling:up:drawing line "+i);
				myIO.moveLeft(lasthorizontal);
				Editline lin=(Editline)lines.elementAt(i);
				lasthorizontal=lin.size();
				myIO.eraseToEndOfLine();
				myIO.write(lin.getValue());
				myIO.moveDown(1);
				count++;
		}
		//vertical:
		myIO.moveUp(count);
		//horizontal:
		if(lasthorizontal>horizontalpos) {
			myIO.moveLeft(lasthorizontal-horizontalpos);
		} else if(lasthorizontal<horizontalpos) {
			myIO.moveRight(horizontalpos-lasthorizontal);
		}
		
	 	if(horizontalpos>line.getCursorPosition()) {
	 		myIO.moveLeft(horizontalpos-line.getCursorPosition());
	 	}
	 }//scrollUp
	 
	 private void cursorUp() {
	 	//System.out.println("Debug:cursor:up");
	 	
	 	int horizontalpos=line.getCursorPosition();
	 	//Cursor
	 	rowCursor--;
	 	//buffer
	 	activateLine(rowCursor);
	 	line.setCursorPosition(horizontalpos);
	 	//screen
	 	//vertical
	 	myIO.moveUp(1);
	 	//horizontal
	 	if(horizontalpos>line.getCursorPosition()) {
	 		myIO.moveLeft(horizontalpos-line.getCursorPosition());
	 	}
	 }//cursorUp
	 
	 private void scrollDown() {
	 	//System.out.println("Debug:scrolling:down");
	 	int horizontalpos=line.getCursorPosition();
	 	
	 	//Cursors
	 	firstVisibleRow++;
	 	rowCursor++;
	 	
	 	//buffer
	 	activateLine(rowCursor);
	 	line.setCursorPosition(horizontalpos);
	 	
	 	//screen
	 	//vertical:
	 	myIO.moveUp(myDim.getHeight()-1);
		//content:
		int lasthorizontal=horizontalpos;
	 	for (int i=firstVisibleRow;i<(firstVisibleRow+myDim.getHeight());i++){
				//System.out.println("Debug:scrolling:up:drawing line "+i);
				myIO.moveLeft(lasthorizontal);
				Editline lin=(Editline)lines.elementAt(i);
				lasthorizontal=lin.size();
				
				myIO.eraseToEndOfLine();
				myIO.write(lin.getValue());
				myIO.moveDown(1);
		}
		//correct move down and last write
		myIO.moveUp(1);
		//horizontal:
		if(lasthorizontal>horizontalpos) {
			myIO.moveLeft(lasthorizontal-horizontalpos);
		} else if(lasthorizontal<horizontalpos) {
			myIO.moveRight(horizontalpos-lasthorizontal);
		}
		
	 	if(horizontalpos>line.getCursorPosition()) {
	 		myIO.moveLeft(horizontalpos-line.getCursorPosition());
	 	}
	 }//scrollDown
	 
	 private void cursorDown() {
	 	//System.out.println("Debug:cursor:down");
	 	int horizontalpos=line.getCursorPosition();
	 	//Cursor
	 	rowCursor++;
	 	//buffer
	 	activateLine(rowCursor);
	 	line.setCursorPosition(horizontalpos);
	 	//screen
	 	myIO.moveDown(1);
	 	if(horizontalpos>line.getCursorPosition()) {
	 		myIO.moveLeft(horizontalpos-line.getCursorPosition());
	 	}
	 }//cursorDown
	 
	 
	 private void appendNewLine() {
	 	//System.out.println("Debug:appendline");
	 	//buffer
	 	appendLine(createLine());
		
		if(rowCursor==firstVisibleRow+(myDim.getHeight()-1)) {
			//System.out.println("Debug:appendline:scroll");
			//this will "scroll"
			firstVisibleRow++;
			//System.out.println("Debug:appendline:scroll:firstvis:"+firstVisibleRow);
			//System.out.println("Debug:appendline:scroll:rowCursor:"+rowCursor);
			//System.out.println("Debug:appendline:scroll:movevert:"+(myDim.getHeight()-1));
			//vertical
			myIO.moveUp(myDim.getHeight()-1);
			myIO.moveLeft(line.getCursorPosition());	
	 		//content
//	 		int lasthorizontal=line.getCursorPosition();	
	 		for (int i=firstVisibleRow;i<(firstVisibleRow+myDim.getHeight());i++){
	 			//System.out.println("Debug:appendline:scroll:line:"+i);
				Editline lin=(Editline)lines.elementAt(i);
				myIO.eraseToEndOfLine();
				myIO.write(lin.getValue());
				myIO.moveLeft(lin.size());
				myIO.moveDown(1);
			}
			//correct the move to down in last place
			myIO.moveUp(1);
			
		} else {
			//System.out.println("Debug:appendline:NOscroll");
			//this wont need a scroll redraw
			myIO.moveLeft(line.getCursorPosition());
			myIO.moveDown(1);
		}
	 }//appendNewLine
	 
	 private void insertNewLine() {
	 	//System.out.println("Debug:insertline:");
	 	//buffer
	 	insertLine(rowCursor+1,createLine());
		
		
		if(rowCursor==firstVisibleRow+(myDim.getHeight()-1)) {
			//System.out.println("Debug:insertline:scroll");
			//this will "scroll"
			firstVisibleRow++;
			//System.out.println("Debug:insertline:scroll:firstvis:"+firstVisibleRow);
			//System.out.println("Debug:appendline:scroll:rowCursor:"+rowCursor);
			//System.out.println("Debug:appendline:scroll:movevert:"+(myDim.getHeight()-1));
			//vertical
			myIO.moveUp(myDim.getHeight()-1);	
	 		//content
	 		int lasthorizontal=line.getCursorPosition();	
	 		for (int i=firstVisibleRow;i<(firstVisibleRow+myDim.getHeight());i++){
	 			//System.out.println("Debug:appendline:scroll:line:"+i);
				myIO.moveLeft(lasthorizontal);
				Editline lin=(Editline)lines.elementAt(i);
				lasthorizontal=lin.size();
				myIO.eraseToEndOfLine();
				myIO.write(lin.getValue());
				myIO.moveDown(1);
				
			}
			//correct the move to down in last place
			myIO.moveUp(1);
			
		} else {
			//System.out.println("Debug:insertline:NOscroll");
			//we have to redraw any line below rowCursor+1 anyway
			myIO.moveDown(1);
			myIO.moveLeft(line.getCursorPosition());
			
			int count=0;
			for (int i=rowCursor+1;i<(firstVisibleRow+myDim.getHeight())&&i<lines.size();i++){
	 			//System.out.println("Debug:insertline:redrawing line:"+i);
				myIO.eraseToEndOfLine();
				Editline lin=(Editline)lines.elementAt(i);
				myIO.write(lin.getValue());	
				myIO.moveLeft(lin.size());
				myIO.moveDown(1);
				count++;
			}
			myIO.moveUp(count);
		}
		
	 }//insertNewLine
	 
	 private void removeLine() {
	 	
	 	//buffer
	 	deleteLine(rowCursor);
	 	activateLine(rowCursor-1);
	 	//Cursor
	 	rowCursor--;
	 	
	 	
	 	//Screen
	 	//content redraw
//		int lasthorizontal=0;
		int count=0;
		for (int i=rowCursor+1;i<(firstVisibleRow+myDim.getHeight());i++){
	 		if(i<lines.size()) {
	 			//System.out.println("Debug:removeline:redrawing line:"+i);
	 			myIO.eraseToEndOfLine();
	 			Editline lin=(Editline)lines.elementAt(i);
	 			myIO.write(lin.getValue());
	 			myIO.moveLeft(lin.size());
	 			myIO.moveDown(1);
				count++;
			}else {
				myIO.eraseToEndOfLine();
				myIO.moveDown(1);
				count++;
			}
		}
		//cursor readjustment
		//vertical
		myIO.moveUp(count+1);		
		//horizontal
		
		line.setCursorPosition(line.size());
		myIO.moveRight(line.size());
	 }//removeLine
	 
	 public void draw() {
	 	if(myPosition != null) {
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
			int count=0;
			for (int i=firstVisibleRow;i<(firstVisibleRow+myDim.getHeight()) && i< lines.size();i++){
				myIO.eraseToEndOfLine();
	 			Editline lin=(Editline)lines.elementAt(i);
	 			myIO.write(lin.getValue());
	 			myIO.moveLeft(lin.size());
	 			myIO.moveDown(1);
				count++;	
			}
			int corr=(firstVisibleRow+count)-rowCursor;
			if(corr>0) {
				myIO.moveUp(corr);
			}		
		}
		myIO.flush();
	 }//draw

	
	 private void activateLine(int pos) {
	 	line=getLine(pos);
	 }//activateLine
	 
	 private boolean hasLineSpace() {
	 	return (lines.size()<myRows);
	 }//hasLineSpace
	 
	 private Editline createLine() {
	 	return new Editline(myIO);
	 }//newLine
	 
	 private void deleteLine(int pos) {
	 	lines.removeElementAt(pos);
	 }//deleteLine

	 private void insertLine(int pos,Editline el) {
	 	lines.insertElementAt(el,pos);
	 }//insertLine
		
	 private void appendLine(Editline el) {
		lines.addElement(el);
	 }//appendLine

	 private Editline getLine(int pos) {
	 	return (Editline)lines.elementAt(pos);
	 }//getLine

 }//class Editarea

