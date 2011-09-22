<%--
 * Copyright (c) 2003 Sun Microsystems, Inc.  All rights reserved.  U.S. 
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
 * Copyright (c) 2003 Sun Microsystems, Inc. Tous droits reserves.
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
 * '
--%>
<jsp:useBean id="customerBean" class="com.sun.ebank.web.CustomerBean" scope="request"/>
<jsp:useBean id="transferBean" class="com.sun.ebank.web.TransferBean" scope="request"/>
<jsp:setProperty name="customerBean" property="customer" value="${pageContext.request.userPrincipal.name}"/>
<c:set var="accounts" value="${customerBean.accounts}" />
<center>
<table border=0 cellpadding=2 cellspacing=0 width=500 summary="layout">
   <tr>
      <td valign="top" colspan="4">   
  		<c:url var="url" value="/transferAck" />
      <form name="transfer" method="post" action="${url}" >
        <table border=1 cellpadding=2 cellspacing=0 summary="layout">
        <tr>
          <td><b><fmt:message key="AccountName"/></b></td>
          <td><b><fmt:message key="AccountId"/></b></td>
          <td><b><fmt:message key="AccountBalance"/></b></td>
          <td><b><fmt:message key="AccountFrom"/></b></td>
          <td><b><fmt:message key="AccountTo"/></b></td>
        </tr>
        <c:forEach items="${accounts}" var="ad">
          <tr>
            <td>${ad.description}</td>
            <td>${ad.accountId}</td>
            <td align="right"><fmt:formatNumber value="${ad.balance}" type="currency" /></td>
            <td><input type="radio" name="fromAccountId" value="${ad.accountId}"></td>
            <td><input type="radio" name="toAccountId" value="${ad.accountId}"></td>
          </tr>
        </c:forEach>
        </table>
      </td>
    </tr>

    <tr>
        <td><b><fmt:message key="TransferAmount"/></b></td>
        <td><input type="text" size="15" name="transferAmount"></td>
        <td><input type="submit" value="<fmt:message key='Submit'/>"></td>
        <td><input type="reset" value="<fmt:message key='Clear'/>"></td>
    </form>
    </tr>

</table>
</center>
