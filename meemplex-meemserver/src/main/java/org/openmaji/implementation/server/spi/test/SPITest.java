/*
 * @(#)SPITest.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Make this a standard system/unit test.
 * -ag- Complete JavaDoc.
 */

package org.openmaji.implementation.server.spi.test;

import org.openmaji.spi.MajiSPI;
import org.openmaji.utility.uid.UID;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class SPITest {

  public static void main(
    String[] args) {

    test();
  }

  public static void test() {
    try {
      System.out.println(MajiSPI.provider());

      UID uid = UID.spi.create();

      System.out.println("uid: " + uid);
    }
    catch (Exception exception) {
      System.out.println("Exception caught: " + exception);
    }
  }
}
