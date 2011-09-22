<?xml version="1.0" encoding="ISO-8859-1"?>

BASIC TEMPLATE
==============
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  >
  <xsl:output method="html"/>   --????
         
</xsl:stylesheet>


SIMPLE COPY
===========
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  >

    <xsl:template match="title">
      <xsl:copy>
        <xsl:apply-templates/>
      </xsl:copy>
    </xsl:template>

  </xsl:stylesheet>
</xsl:stylesheet>

--this template rule only acts on nodes representing title elements. 
  Any attributes of title elements have their own nodes in the input tree 
  and require their own template rule or rules to be copied.

--xsl:copy-of element, on the other hand, can copy the entire subtree of
  the node


IGNORE
======
<xsl:template match="nickname">    -- ignore any with that name
</xsl:template>

and

<xsl:template match="project[@status='canceled']">    --ignore with name and matching attribute
</xsl:template>


RENAME
======
<xsl:template match="article"> 
  <html><body>
    <xsl:apply-templates/> 
  </html></body>
</xsl:template> 


CONVERT ATTRIBUTE VALUE TO AN ELEMENT
=====================================
<xsl:template match="poem">
  <ode>
    <author>John Milton</author>
    <year><xsl:value-of select="@year"/></year>
    <xsl:apply-templates/>
  </ode>
</xsl:template>


ADDING ATTRIBUTES
=================                      _Note: CURLY braces
  <xsl:template match="thnad">        /
    <widget status="done" hue="{@color}" number="{amount}" pos="{position() + 6}"/>
                     ^_1         ^_2                ^_3            ^_4
  </xsl:template>

  1. hardcoded string: "done"
  2. value of the color attribute value of the current thnad element.
  3. Contents of thnad's child element, amount.
  4. The element node's position within its parent node, plus 6. 

XPATH FUNCTIONS
===============
String
  concat(string, string, ...)     Concatenate multiple arguments
  substring(string, start#)       Part of a string
  substring(string, start#, end#) Part of string

  text( )  match any text node that's an immediate child of the context node

Boolean
  starts-with(string, string)         True if first string begins with second one
  contains(string, string)            True if first string includes second one
  not()           True if argument is not true
  true()/false()  True/not true

Node
  last()           Number (of elements)
  position()       Context position
  count(node-set)  Number of nodes in node-set
  name(node-set?)  First node name in node-set
  (Elementname);   Namespace identifier if there is one


WILDCARDS IN TEMPLATES
======================
  <xsl:template match="*"><xsl:apply-templates select="*"/></xsl:template>

  The * does not match attributes, text nodes, comments, or processing 
  instruction nodes. Thus in this example, output comes only from child
  elements that have their own templates that override this one.


