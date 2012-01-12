<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  	<!--<
  	xsl:import href="transform-glossary.xsl"/>
  	<xsl:import href="transform-beanshell-functions.xsl"/>
  	-->
  	<xsl:import href="transform-common.xsl"/>



  <xsl:template match="link">
    <xsl:element name="a">
      <xsl:attribute name="href">
        <xsl:value-of select="url"/>.xml
      </xsl:attribute>
      <xsl:value-of select="name"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
