<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  >
  <!-- XML output! -->
  <xsl:output method="xml"/> 

  <xsl:template match="/">
    <ARTICLE>
       <xsl:apply-templates/>
    </ARTICLE>
  </xsl:template>
 
  <!-- Lower level titles strip out the element tag -->

  <!-- Top-level title -->
  <xsl:template match="/Article/ArtHeader/Title">
    <TITLE> <xsl:apply-templates/> </TITLE>
  </xsl:template>

  <xsl:template match="//Sect1">
      <SECT><xsl:apply-templates/></SECT>
  </xsl:template>

  <!-- Case-change -->
  <xsl:template match="Para">
      <PARA><xsl:apply-templates/></PARA>
  </xsl:template>

</xsl:stylesheet>


