/*
 * @(#)Colorizer.java
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

package org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal;


/**
 * Singleton utility class for translating  
 * internal color/style markup into ANSI defined
 * escape sequences. It uses a very simple but effective
 * lookup table, and does the job without sophisticated 
 * parsing routines. It should therefore perform quite
 * fast.
 *
 * @author Dieter Wimberger
 * @version 1.0 06/09/2000 
 */
 public final class Colorizer {

	private static Object Self;				//Singleton instance reference
	private int[] colortranslation;		//translation table
//	private int leng;



	/**
	 * Constructs a Colorizer with its translation table.
	 */
	 private Colorizer() {

		colortranslation=new int[128];

		colortranslation[83]=S;
		colortranslation[82]=R;
		colortranslation[71]=G;
		colortranslation[89]=Y;
		colortranslation[66]=B;
		colortranslation[77]=M;
		colortranslation[67]=C;
		colortranslation[87]=W;

		colortranslation[115]=s;
		colortranslation[114]=r;
		colortranslation[103]=g;
		colortranslation[121]=y;
		colortranslation[98]=b;
		colortranslation[109]=m;
		colortranslation[99]=c;
		colortranslation[119]=w;

		colortranslation[102]=f;
		colortranslation[100]=d;
		colortranslation[105]=i;
		colortranslation[106]=j;
		colortranslation[117]=u;
		colortranslation[118]=v;
		colortranslation[101]=e;
		colortranslation[110]=n;
		colortranslation[104]=h;
		colortranslation[97]=a;
		
		Self=this;
	}//constructor

	/**
	 * Translates all internal markups within the String
	 * into ANSI Escape sequences.<br>
	 * The method is hooked into BasicTerminalIO.write(String str), so
	 * it is not necessary to call it directly.
	 *
	 * @param str String with internal color/style markups.
	 * @param support boolean that represents Terminals ability to support GR sequences.
	 *        if false, the internal markups are ripped out of the string.
	 *     
	 * @return String with ANSI escape sequences (Graphics Rendition), if support is true,
	 *         String without internal markups or ANSI escape sequences if support is false.   
	 */
	 public String colorize(String str, boolean support){
			
		StringBuffer out=new StringBuffer(str.length()+20);
		int parsecursor=0;
		int foundcursor=0;
		
		boolean done=false;

		while (!done) { 
			foundcursor = str.indexOf(ColorHelper.MARKER_CODE,parsecursor);
			if (foundcursor!=-1) {
				out.append(str.substring(parsecursor,foundcursor));
				if (support){
					out.append(addEscapeSequence(str.substring(foundcursor+1,foundcursor+2)));
				}
				parsecursor=foundcursor+2;
			} else {
				out.append(str.substring(parsecursor,str.length()));
				done=true;
			}
		}

		/*
		 * This will always add a "reset all" escape sequence 
		 * behind the input string.
		 * Basically this is a good idea, because developers tend to
		 * forget writing colored strings properly.
		 */	 
		 if (support)out.append(addEscapeSequence("a"));	

		 return out.toString();
	 }//colorize



	 private String addEscapeSequence(String attribute){
		
		StringBuffer tmpbuf=new StringBuffer(10);

		byte[] tmpbytes=attribute.getBytes();
		int key=(int) tmpbytes[0];      

		tmpbuf.append((char)27);
		tmpbuf.append((char)91);
		tmpbuf.append((new Integer(colortranslation[key])).toString());
		tmpbuf.append((char)109);

		return tmpbuf.toString();
	 }//addEscapeSequence


 	/**
	 * Returns the reference of the Singleton instance.
	 * 
	 * @return reference to Colorizer singleton instance.
	 */
	 public static Colorizer getReference() {
 		if(Self!=null) {
 			return (Colorizer)Self;
 		} else {
 			return new Colorizer();
 		}
	 }//getReference

/*** Test Harness **/

	private static void announceResult(boolean res){
		if(res) {
			System.out.println("[#"+testcount+"] ok.");
		} else {
			System.out.println("[#"+testcount+"] failed (see possible StackTrace).");
		}
	}//announceResult

	private static int testcount=0;
	private static Colorizer myColorizer;
	
	private static void announceTest(String what){
		testcount++;
		System.out.println("Test #"+testcount+" ["+what+"]:");		
	}//announceTest
	
	private static void bfcolorTest(String color){
		System.out.println("->"+myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR",color),true)+"<-");
	}//bfcolorTest
	
	private static void fcolorTest(String color){
		System.out.println("->"+myColorizer.colorize(ColorHelper.colorizeText("COLOR",color),true)+"<-");
	}//fcolorTest

	private static void bcolorTest(String color){
		System.out.println("->"+myColorizer.colorize(ColorHelper.colorizeBackground("     ",color),true)+"<-");
	}//bcolorTest

	/**
	 * Invokes the build in test harness, and will produce styled and colored
	 * output directly on the terminal.
	 *
	 */
	 public static void main(String[] args){
		try{
			announceTest("Instantiation");
			myColorizer=Colorizer.getReference();
			announceResult(true);
			
			announceTest("Textcolor Tests");
			fcolorTest(ColorHelper.BLACK);
			fcolorTest(ColorHelper.RED);
			fcolorTest(ColorHelper.GREEN);
			fcolorTest(ColorHelper.YELLOW);
			fcolorTest(ColorHelper.BLUE);
			fcolorTest(ColorHelper.MAGENTA);
			fcolorTest(ColorHelper.CYAN);
			fcolorTest(ColorHelper.WHITE);
			announceResult(true);
			
			announceTest("Bold textcolor Tests");
			bfcolorTest(ColorHelper.BLACK);
			bfcolorTest(ColorHelper.RED);
			bfcolorTest(ColorHelper.GREEN);
			bfcolorTest(ColorHelper.YELLOW);
			bfcolorTest(ColorHelper.BLUE);
			bfcolorTest(ColorHelper.MAGENTA);
			bfcolorTest(ColorHelper.CYAN);
			bfcolorTest(ColorHelper.WHITE);
			announceResult(true);
			
			announceTest("Background Tests");
			bcolorTest(ColorHelper.BLACK);
			bcolorTest(ColorHelper.RED);
			bcolorTest(ColorHelper.GREEN);
			bcolorTest(ColorHelper.YELLOW);
			bcolorTest(ColorHelper.BLUE);
			bcolorTest(ColorHelper.MAGENTA);
			bcolorTest(ColorHelper.CYAN);
			bcolorTest(ColorHelper.WHITE);
			announceResult(true);
			
			announceTest("Mixed Color Tests");
			System.out.println("->"+myColorizer.colorize(ColorHelper.colorizeText("COLOR",ColorHelper.WHITE,ColorHelper.BLUE),true)+"<-");
			System.out.println("->"+myColorizer.colorize(ColorHelper.colorizeText("COLOR",ColorHelper.YELLOW,ColorHelper.GREEN),true)+"<-");	
			System.out.println("->"+myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR",ColorHelper.WHITE,ColorHelper.BLUE),true)+"<-");
			System.out.println("->"+myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR",ColorHelper.YELLOW,ColorHelper.GREEN),true)+"<-");	
			
			
			announceResult(true);
			
			announceTest("Style Tests");
			System.out.println("->"+myColorizer.colorize(ColorHelper.boldText("Bold"),true)+"<-");			
			System.out.println("->"+myColorizer.colorize(ColorHelper.italicText("Italic"),true)+"<-");
			System.out.println("->"+myColorizer.colorize(ColorHelper.underlinedText("Underlined"),true)+"<-");
			System.out.println("->"+myColorizer.colorize(ColorHelper.blinkingText("Blinking"),true)+"<-");
			
			announceResult(true);
			
			
			
			
			announceTest("Visible length test");
			String colorized=ColorHelper.boldcolorizeText("STRING",ColorHelper.YELLOW);
			
			System.out.println("->"+myColorizer.colorize(colorized,true)+"<-");
			System.out.println("Visible length="+ColorHelper.getVisibleLength(colorized));
			
			colorized=ColorHelper.boldcolorizeText("BANNER",ColorHelper.WHITE,ColorHelper.BLUE) +
					  ColorHelper.colorizeText("COLOR",ColorHelper.WHITE,ColorHelper.BLUE) +
					  ColorHelper.underlinedText("UNDER");
			System.out.println("->"+myColorizer.colorize(colorized,true)+"<-");
			System.out.println("Visible length="+ColorHelper.getVisibleLength(colorized));
			
			announceResult(true);
			
			
			
			if(false) throw new Exception();	//this will shut up jikes
			
		
		
		}catch (Exception e) {
			announceResult(false);
			e.printStackTrace();
		}
	}//main (test routine)						


//Constants
private static final int 
			 /*black*/	S=30, s=40,
			 /*red*/	R=31, r=41,
			 /*green*/      G=32, g=42,
			 /*yellow*/     Y=33, y=43,
			 /*blue*/       B=34, b=44,
			 /*magenta*/    M=35, m=45,
			 /*cyan*/       C=36, c=46,
			 /*white*/      W=37, w=47,
		
			 /*bold*/	 f=1,
			 /*!bold*/ 	 d=22, //normal color or normal intensity
                         /*italic*/      i=3,
			 /*!italic*/     j=23,    
			 /*underlined*/  u=4,
			 /*!underlined*/ v=24, 
			 /*blink*/       e=5,
                         /*steady*/      n=25,  //not blinking
			 /*hide*/	 h=8,  //concealed characters
			 /*all out*/     a=0;

}//class Colorizer
