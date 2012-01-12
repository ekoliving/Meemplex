package org.openmaji.implementation.server.nursery.jini;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


public interface JiniServices extends Facet
{
  /* ---------- Nested class for SPI ----------------------------------------- */

  public class spi
  {
    public static JiniServices create()
    {
      return ((JiniServices) MajiSPI.provider().create(JiniServices.class));
    }

    public static String getIdentifier()
    {
      return "jiniServices";
    }
  }
}
