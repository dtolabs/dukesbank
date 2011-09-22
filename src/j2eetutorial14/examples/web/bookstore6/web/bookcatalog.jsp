<%--
 * Copyright (c) 2004 Sun Microsystems, Inc.  All rights reserved.  U.S. 
 * Government Rights - Commercial software.  Government users are subject 
 * to the Sun Microsystems, Inc. standard license agreement and 
 * applicable provisions of the FAR and its supplements.  Use is subject 
 * to license terms.  
 * 
 * This distribution may include materials developed by third parties. 
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks 
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and 
 * other countries.  
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. Tous droits reserves.
 * 
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de 
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions 
 * en vigueur de la FAR (Federal Acquisition Regulations) et des 
 * supplements a celles-ci.  Distribue par des licences qui en 
 * restreignent l'utilisation.
 * 
 * Cette distribution peut comprendre des composants developpes par des 
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE 
 * sont des marques de fabrique ou des marques deposees de Sun 
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
--%>

<f:view>
<f:loadBundle var="bundle" basename="messages.BookstoreMessages"/>

<h:messages/>
<br>

<h:form               id="form">

  <h:commandLink      id="check"
                  action="cart"
               immediate="true"
                rendered="#{cart.numberOfItems > 0}">
    <h:outputText
                   value="#{bundle.CartCheck}"/>
  </h:commandLink>

  <h:outputText   escape="false"
                   value="&nbsp;"
                rendered="#{cart.numberOfItems > 0}"/>

  <h:commandLink      id="clear"
                  action="#{catalog.clear}"
               immediate="true"
                rendered="#{cart.numberOfItems > 0}">
    <h:outputText
                   value="#{bundle.ClearCart}"/>
  </h:commandLink>

  <h:outputText   escape="false"
                   value="&nbsp;"
                rendered="#{cart.numberOfItems > 0}"/>

  <h:commandLink      id="buy"
                  action="#{catalog.buy}"
               immediate="true"
                rendered="#{cart.numberOfItems > 0}">
    <h:outputText
                  value="#{bundle.Buy}"/>
  </h:commandLink>

  <h:outputText  escape="false"
               rendered="#{cart.numberOfItems > 0}"
                  value="<br><br>"/>

  <h:dataTable       id="books"
          columnClasses="list-column-center, list-column-left, list-column-left,
                         list-column-right, list-column-center"
            headerClass="list-header"
	    footerClass="list-footer"
             rowClasses="list-row-even, list-row-odd"
             styleClass="list-background"
	     	summary="#{bundle.BookCatalog}"
                  value="#{bookDBAO.books}"
                    var="book">

    <f:facet       name="header">
      <h:outputText
                  value="#{bundle.Choose}"/>
    </f:facet>

    <h:column > 
      <f:facet     name="header">
        <h:outputText 
                 value="#{bundle.Quantities}" />
      </f:facet>
      <h:outputText value = "#{catalog.bookQuantity}" />
      <f:facet           name="footer">
       <h:outputText  value="#{catalog.totalBooks}" />
      </f:facet>
    </h:column>
    

    <h:column>
      <f:facet     name="header">
        <h:outputText
                  value="#{bundle.ItemTitle}"/>
      </f:facet>
      <h:commandLink
                 action="#{catalog.details}">
        <h:outputText
                  value="#{book.title}"/>
      </h:commandLink>
    </h:column>

    <h:column>
      <f:facet     name="header">
        <h:outputText
                  value="#{bundle.By}"/>
      </f:facet>
      <h:outputText
                  value="#{book.firstName} #{book.surname}"/>
    </h:column>
    
   

    <h:column>
      <f:facet     name="header">
        <h:outputText
                  value="#{bundle.ItemPrice}"/>
      </f:facet>
      <h:outputText
                  value="#{book.price}">
        <f:convertNumber
                   type="currency"/>
      </h:outputText>
    </h:column>

    
    <h:column>
      <h:commandButton
                     id="add"
                 action="#{catalog.add}"
                  value="#{bundle.CartAdd}"/>
    </h:column>

    

  </h:dataTable>

</h:form>

</f:view>
