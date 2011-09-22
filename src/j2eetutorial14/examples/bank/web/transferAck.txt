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

<center>
<jsp:useBean id="transferBean" class="com.sun.ebank.web.TransferBean" scope="request"/>
<jsp:useBean id="customerBean" class="com.sun.ebank.web.CustomerBean" scope="request"/>
<table border=1 cellpadding=2 cellspacing=0 width=500 summary="layout">
<tr>
<td valign=bottom>
  <b><fmt:message key="AccountName"/></b>
</td>
<td valign=bottom>
  <b><fmt:message key="AccountId"/></b>
</td>
<td valign=bottom>
  <b><fmt:message key="AccountBalance"/></b>
</td>
<td valign=bottom>
  <b><fmt:message key="AccountCredit"/></b>
</td>
</tr>

<jsp:useBean id="date" class="com.sun.ebank.web.DateHelper" scope="request" />
<jsp:setProperty name="customerBean" property="account" value="${transferBean.fromAccountId}" />
<c:set var="ad" value="${customerBean.accountDetails}" /> 
<c:url var="url" value="/accountHist?accountId=${ad.accountId}&date=0&year=<%=year%>&sinceMonth=<%=month%>&sinceDay=1&beginMonth=<%=month%>&beginDay=1&endMonth=<%=month%>&endDay=1&activityView=0&sortOption=0" />
<tr>
  <td><a href="${url}">${ad.description}</td>
  <td>${ad.accountId}</td>
  <td align="right"><fmt:formatNumber value="${ad.balance}" type="currency" /></td>
  <td align="right"><c:if test="${ad.creditLine != 0}">
      <fmt:formatNumber value="${ad.remainingCredit}" type="currency" /></td>
     </c:if>
     <c:if test="${ad.creditLine == 0}">
     	&nbsp;</td>
     </c:if>
</tr>
<jsp:setProperty name="customerBean" property="account" value="${transferBean.toAccountId}" />
<c:set var="ad" value="${customerBean.accountDetails}" /> 
<c:url var="url" value="/accountHist?accountId=${ad.accountId}&date=0&year=${date.year}&sinceMonth=${date.month}&sinceDay=1&beginMonth=${date.month}&beginDay=1&endMonth=${date.month}&endDay=1&activityView=0&sortOption=0" />
<tr>
  <td><a href="${url}">${ad.description}</td>
  <td>${ad.accountId}</td>
  <td align="right"><fmt:formatNumber value="${ad.balance}" type="currency" /></td>
  <td align="right">
     <c:if test ="${ad.creditLine != 0}">
      	\$${ad.remainingCredit}</td>
     </c:if>
     <c:if test="${ad.creditLine == 0}">
     	&nbsp;</td>
     </c:if>
</tr>
</table>

<p><fmt:message key="TransferAck"/> <fmt:formatNumber value="${transferBean.transferAmount}" type="currency" />.  
</center>
