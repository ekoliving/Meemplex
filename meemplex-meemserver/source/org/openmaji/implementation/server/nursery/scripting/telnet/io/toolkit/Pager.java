/*
 * @(#)Pager.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Vector;

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.ColorHelper;



/**
 */
public class Pager {

	//Associations
	private BasicTerminalIO myIO;
	//Members
	private StringReader source;
	private String myPrompt;
	private int myStopKey;
	private Vector chunks;
	private int chunkpos;
	private int lastnewchunk;
	private boolean eos;
	private int termrows;
	private int termcols;
	private boolean noprompt;
	private boolean showpos;
	
	/**
	 * Constructor method 
	 */
	 public Pager(BasicTerminalIO io){
	 	myIO=io;
	 	setPrompt(DEFAULT_PROMPT);
	 	setStopKey(DEFAULT_STOPKEY);
	 	termrows=myIO.getRows();
	 	termcols=myIO.getColumns();
	 }//constructor
	
	
	/**
	 * Constructor method for a pager with a prompt set and a default stop key.
	 * 
	 * @param prompt String that represents the paging prompt.
	 * @param stopKey String that represents the stop key.
	 */
	 public Pager(BasicTerminalIO io,String prompt, char stopKey) {
	 	myIO=io;
	 	myPrompt=prompt;
	 	myStopKey=stopKey;
	 	termrows=myIO.getRows();
	 	termcols=myIO.getColumns();
	 }//constructor
	
	/**
	 * Mutator method for the pagers stop key.
	 * 
	 * @param key char that represents the new stop key.
	 */
 	 public void setStopKey(char key) {
 	 	myStopKey=(int)key;
 	 }//setStopKey
	
	/**
	 * Mutator method for the pagers prompt.
	 *
	 * @param prompt String that represents the new promptkey.
	 */
	 public void setPrompt(String prompt) {
	 	myPrompt=prompt;
	 }//setPrompt

	/**
	 * Method to make the pager add pager postion to the prompt.
	 * 
	 * 
	 */
	 public void setShowPosition(boolean b){
	 	showpos=b;
	 }//setShowPosition

	/**
	 * Method that pages the String to the client terminal,
	 * being aware of its geometry, and its geometry changes.
	 * 
	 * @param str String to be paged.
	 */
  	 public void page(String str) {
  	 	myIO.setAutoflushing(true);
  	 	//store raw
  	 	source=new StringReader(str);
  	 	//do renderchunks
  	 	chunkpos=0;
  	 	lastnewchunk=0;
  	 	eos=false;
  	 	noprompt=false;
  	 	
  	 	renderChunks();
  		myIO.homeCursor();
		myIO.eraseScreen();
		myIO.write((String)chunks.elementAt(chunkpos));
  	 	myIO.write(myPrompt+((showpos)? ("["+(chunkpos+1)+"/"+chunks.size()+"]"):""));
  	 	//storage for read byte
  	 	int in=0;
  		do {
  		    noprompt=false;
			  		
  			//get next key
  			in=myIO.read();
  			if(terminalGeometryChanged()){
  				try { source.reset(); } catch (Exception ex){}
  				renderChunks();
  				chunkpos=0;
  				lastnewchunk=0;
  				eos=false;
  				noprompt=false;
  				myIO.homeCursor();
				myIO.eraseScreen();
				myIO.write((String)chunks.elementAt(chunkpos));
  	 			myIO.write(myPrompt+((showpos)? ("["+(chunkpos+1)+"/"+chunks.size()+"]"):""));
  				continue;
  			}
  			switch(in) {
  				case BasicTerminalIO.UP:
  					drawPreviousPage();
  					break; 
  				case BasicTerminalIO.DOWN:
  					drawNextPage();
  					break;
  				case SPACE:
  					drawNextPage();
  					break;
  				default:
  					//test for stopkey, cant be switched because not constant
  					if(in==myStopKey){
  						//flag loop over
  						in=-1;
  						continue; //so that we omit prompt and return
  					} else {
  						myIO.bell();
  						continue;
  					}
  			}
  			if(eos) {
  				in=-1;
  				continue;
  			}
  			//prompt
  			if(!noprompt) myIO.write(myPrompt+((showpos)? ("["+(chunkpos+1)+"/"+chunks.size()+"]"):""));
  			
  			//System.out.println("DEBUG: chunkpos=" +chunkpos+" lastnewchunk="+lastnewchunk);
  		
  		}while (in!=-1);
  		
  		myIO.eraseToBeginOfLine();
  		myIO.write("\n");
  		source.close();
  		myIO.setAutoflushing(false);
  	 }//page(String)
	
	/**
	 * Method that pages text read from an InputStream.
	 *  
	 * @param in InputStream representing a source for paging.
	 */
	 public void page(InputStream in) 
	 		throws IOException{
	 	
	 	//buffer prepared for about 3k
	 	StringBuffer inbuf=new StringBuffer(3060);
	 	
	 	//int buffering read
	 	int b=0;
	 	
	 	while(b!=-1) {
	 			b=in.read();
	 		if(b!=-1){
	 			inbuf.append((char)b);
	 		}
	 	}
	 	
	 	//now page the string
	 	page(inbuf.toString());
	 }//page(InputStream)


	 private void drawNextPage(){
		//System.out.println("drawing next page");
		if(chunkpos==lastnewchunk){
			drawNewPage();	
		} else {
			myIO.homeCursor();
			myIO.eraseScreen();
			myIO.write((String)chunks.elementAt(++chunkpos));
		}
	 }//drawNextPage


	 private void drawPreviousPage(){
	 	//System.out.println("drawing previous page");
	 	if(chunkpos>0){
	 		myIO.homeCursor();
			myIO.eraseScreen();
			myIO.write((String)chunks.elementAt(--chunkpos));
	 	} else {
	 		myIO.bell();
	 		noprompt=true;
	 	}
	 }//drawPreviousPage
	 
	 private void drawNewPage(){
	 
	 	//increase counters
	 	chunkpos++;
	 	lastnewchunk++;
	 	//System.out.println("drawing new page chunkpos="+chunkpos+" lastnewchunk="+lastnewchunk);
	 	if(chunkpos<chunks.size()){
	 		myIO.homeCursor();
			myIO.eraseScreen();
			myIO.write((String)chunks.elementAt(chunkpos));
			//if(chunkpos==chunks.size()-1) {
			//	eos=true;
			//	noprompt=true;
			//}
	 	}
	 	else {
	 		//flag end
	 		eos=true;
	 		noprompt=true;
	 	}
	 }//drawNewPage
	 
	 private void renderChunks(){
	 	//System.out.println("Rendering Chunks");
	 	//prepare with 10 as default, should be much larger normally 
	 	chunks=new Vector(10);
	 	//prepare a buffer the size of cols + security span
	 	StringBuffer sbuf=new StringBuffer(termcols+25);
	 	int b=0;
	 	int cols=0;
	 	int rows=0;
	 	boolean colorskip=false;
	 	
	 	do {
	 		//check rows to advance chunks
	 		if(rows==termrows-1){
	 			//add chunk to vector
	 			chunks.addElement(sbuf.toString());
	 			//replace for new buffer
	 			sbuf=new StringBuffer(termcols+25);
	 			//reset counters
	 			cols=0;
	 			rows=0;
	 		}
	 		//try read next byte
	 		try { 
	 			b=source.read(); 
	 		} catch (IOException ioex) {
	 			 b=-1; 
	 		}
	 		if (b==-1) {
	 			chunks.addElement(sbuf.toString());
	 			continue; //will end the loop
	 		} else if(b==ColorHelper.MARKER_CODE || colorskip) {
	 			//add it, flag right for next byte and skip counting
	 			sbuf.append((char)b);
	 			if (!colorskip) {
	 				colorskip=true;	
	 			} else {
	 				colorskip=false;
	 			}
	 			continue;	
	 		} else if (b==10) {
	 			//advance a row
	 			rows++;
	 			//reset cols!!!!
	 			cols=0;
	 			//append the newline char
	 			sbuf.append("\n");
	 			//go into next loop run
	 			continue;
	 		} else {
	 			sbuf.append((char)b);
	 			cols++;
	 		}
	 		
	 		//check cols to advance rows
	 		if (cols==termcols) {
	 			rows++;
	 			//append a newline
	 			sbuf.append("\n");
	 			//reset cols!!!
	 			cols=0;
	 		}
	 	} while(b!=-1);
	 	//System.out.println("Done:"+chunks.size());
	 }//renderChunks
	 
	 
	 private boolean terminalGeometryChanged(){
	 	if(termrows!=myIO.getRows() || termcols!=myIO.getColumns()) {
	 		termrows=myIO.getRows();
	 		termcols=myIO.getColumns();
	 		return true;
	 	} else {
	 		return false;
	 	}
	 }//terminalGeometryChanged

/***
 * Constant definitions
 */	
 private static final char DEFAULT_STOPKEY='s';
 private static final String DEFAULT_PROMPT="[Cursor Up,Cursor Down,Space,s (stop)] " ;
 private static final int SPACE=32;
 

}//class Pager

