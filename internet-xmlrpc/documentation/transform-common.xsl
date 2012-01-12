<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>
  <xsl:variable name="root"><xsl:value-of select="/document/@root"/></xsl:variable>

  <!--xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template-->

  <xsl:template match="document">
    <html>
      <xsl:apply-templates select="header"/>
      <xsl:apply-templates select="body"/>
    </html>
  </xsl:template>

  <xsl:template match="header">
    <head>
      <title>
        <xsl:value-of select="//body/page-heading"/>
      </title>

      <xsl:element name="link">
        <xsl:attribute name="rel">stylesheet</xsl:attribute>
        <xsl:attribute name="type">text/css</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="style-sheet"/></xsl:attribute>
      </xsl:element>
    </head>
  </xsl:template>

  <xsl:template match="body">
    <body>
      <xsl:apply-templates/>
    </body>
  </xsl:template>

  <xsl:template match="incl[@href and @id]">    
      <!-- selects any node in specified with attribute id equal to value of  @id --> 
      <xsl:variable name="value" select="@id"/>
      <xsl:apply-templates select="document(@href)//*[@id=$value]"/>
  </xsl:template>
  
   <xsl:template match="incl">
      <xsl:apply-templates select="document(@href)"/>
  </xsl:template>

  <xsl:template match="fragment">
      <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="section-title">
    <div class="pageHeading">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="section-subtitle">
    <div class="sectionSubHeading">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="tutorial-index">
      <div class="paragraph">
        <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="page-heading">
    <table border="0" width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td style="border-left : 8px solid #4E90DB; border-right : 8px solid #4E90DB; border-top : 8px solid #4E90DB; background-color:#4E90DB">
          <div class="pageHeading">
            <!--<img src="{$root}/images/majitek_transparent.png"/>-->
            <xsl:apply-templates/>
          </div>
        </td>
      </tr>
    </table>
    <br/>
  </xsl:template>

  <xsl:template match="section-heading">
    <div class="sectionHeading">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="tut-section">
      <xsl:apply-templates/>
  </xsl:template>

  <!-- suppress index elements -->
  <xsl:template match="index-term"/>
  <xsl:template match="index-phrase"/>
  <xsl:template match="inlinetext"/>

  <xsl:template match="overview">
    <div class="paragraph">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="section-subheading">
    <div class="sectionSubHeading">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="paragraph">
    <div class="paragraph">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="usertype">
    <i><xsl:value-of select="."/> users: </i>
  </xsl:template>

  <xsl:template match="tut-oview">
    <div class="sectionSubHeading">In this Topic</div>
    <div class="paragraph">

      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="menu-selection">
    <span class="menuSelection">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="user-entry">
    <span class="userEntry">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="button">
    <span class="button">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

    <xsl:template match="table">
      <BR/><BR/><TABLE BORDER="1" BORDERCOLOR="#CCCCCC" CELLSPACING="0" CELLPADDING="4">
        <xsl:apply-templates/>
      </TABLE><BR/><BR/>
    </xsl:template>

    <xsl:template match="th"><xsl:apply-templates/></xsl:template>
    <xsl:template match="tbody"><xsl:apply-templates/></xsl:template>

    <xsl:template match="hrow">
      <TR><xsl:apply-templates/></TR>
    </xsl:template>

    <xsl:template match="row">
      <TR><xsl:apply-templates/></TR>
    </xsl:template>

    <xsl:template match="cell">
      <TD VALIGN="TOP">
      <span class="txt">
		<xsl:apply-templates/>
		</span>
      </TD>
    </xsl:template>

    <xsl:template match="hcell">
      <TD bgcolor="#FFFFFF">
      	<center>
      	<span class="txt">
      	<B><xsl:value-of select="."/></B>
      	</span>
      	</center>
     </TD>
    </xsl:template>

  <xsl:template match="image">
    <br/>
    <br/>
    <center>
      <xsl:element name="img">
        <xsl:attribute name="src"><xsl:value-of select="@file"/></xsl:attribute>
        <xsl:attribute name="border">1</xsl:attribute>
      </xsl:element>
    </center>
    <br/>
  </xsl:template>

  <xsl:template match="image-noborder">
    <center>
      <xsl:element name="img">
        <xsl:attribute name="src"><xsl:value-of select="@file"/></xsl:attribute>
        <xsl:attribute name="border">0</xsl:attribute>
      </xsl:element>
    </center>
    <br/>
  </xsl:template>

  <xsl:template match="inline-image">
     <xsl:element name="img">
        <xsl:attribute name="src"><xsl:value-of select="@file"/>
        </xsl:attribute>
      </xsl:element>
      <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="external-link">
    <xsl:value-of select="name"/>
    <xsl-text> at </xsl-text>
    <xsl:element name="a">
      <xsl:attribute name="href">
        <xsl:value-of select="exturl"/>
      </xsl:attribute>
      <xsl:attribute name="target">
        <xsl:value-of select="'_blank'"/>
      </xsl:attribute>
      <xsl:value-of select="exturl"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="unordered-list">
   <div class="paragraph">
    <ul>
      <xsl:apply-templates/>
    </ul>
   </div>
  </xsl:template>

  <xsl:template match="ordered-list">
   <div class="paragraph">
    <ol>
      <xsl:apply-templates/>
    </ol>
   </div>
  </xsl:template>

  <xsl:template match="item">
    <li>
      <xsl:apply-templates/>
      <BR/><BR/>
    </li>
  </xsl:template>

  <xsl:template match="text">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="codebox">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="pre">
    <table  width="*" border="1" border-color="#FFFFFF" bgcolor="#EEEEEE" cellspacing="0" cellpadding="4">
      <tr>
        <td>
          <pre>
      <xsl:apply-templates/>
          </pre>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="todo">
    <div class="todo">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="generated">
    <span class="generated">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="typed">
    <span class="typed">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="inline-code">
    <span class="inlineCode">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="code">
    <span class="code">
    <pre>
      <xsl:apply-templates/>
    </pre>
    </span>
  </xsl:template>

  <xsl:template match="indent-code">
    <div class="code">
    <pre>
      <xsl:apply-templates/>
    </pre>
    </div>
  </xsl:template>

  <xsl:template match="hilite">
    <span class="codeEmph">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="block-hilite">
    <span class="codeEmph">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="emph">
    <span class="emph">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="reminder">
      <br/>
    <span class="italic">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="src">
    <!-- suppress content of this tag
    refers to java source code in nursery folder -->
  </xsl:template>

  <xsl:template match="note">
    <span class="note">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="important">
    <p><b>Important note: </b>
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="br">
  	<BR/>
  </xsl:template>

  <xsl:template match="token">
    <span class="token">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="emph">
  	<span class="emph">
  		<xsl:apply-templates/>
  	</span>
  </xsl:template>

  <xsl:template match="more-info">
    <div class="sectionSubHeading">
      <img src="../images/IMGUI/help-more.gif" hspace="4"/>
      For More Information
    </div>
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="code-samples">
    <div class="sectionSubHeading">
      <img src="../images/IMGUI/help-code.gif" hspace="4"/>
      Code Samples
    </div>
        <xsl:apply-templates/>
  </xsl:template>

    <!-- version templates NOT for use in final versions -->
    <xsl:template match="oldver">
        <div class="revisionOld">
    <xsl:text> </xsl:text><b>OLD VERSION</b><HR color="#000000" size="1"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="newver">
        <div class="revisionNew">
        <xsl:text> </xsl:text><b>NEW VERSION</b><HR color="#000000" size="1"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>


  <xsl:template match="@*|*|text()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>

  </xsl:stylesheet>
