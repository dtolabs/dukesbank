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
 * 
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="customerBean" class="com.sun.ebank.web.CustomerBean" scope="request"/>
<jsp:setProperty name="customerBean" property="customer" value="${pageContext.request.userPrincipal.name}"/>
<c:set var="accounts" value="${customerBean.accounts}" />
<c:forEach items="${accounts}" begin="0" end="0" var="ad">
<c:set var="accountId" value="${ad.accountId}" />
</c:forEach>
<center> 
<table border=0 cellpadding=10 cellspacing=25 width=600 summary="layout">
  <tr>      
  <c:url var="url" value="/accountList" />
    <td bgcolor="#CE9A00"><a href="${url}"><fmt:message key="AccountList"/></a></td>
  <c:url var="url" value="/transferFunds" />
    <td bgcolor="#CE9A00"><a href="${url}"><fmt:message key="TransferFunds"/></a></td>
  <c:url var="url" value="/atm?accountId=${accountId}&operation=0" />
    <td bgcolor="#CE9A00"><a href="${url}"><fmt:message key="ATM"/></a></td>
  <c:url var="url" value="/logoff" />
    <td bgcolor="#CE9A00"><a href="${url}"><fmt:message key="Logoff"/></a></td>
  </tr>
</table>
</center>
