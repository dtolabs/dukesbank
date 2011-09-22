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
<jsp:useBean id="customerBean" class="com.sun.ebank.web.CustomerBean" scope="request"/>
<jsp:setProperty name="customerBean" property="customer" value="${pageContext.request.userPrincipal.name}"/>
<c:set var="accounts" value="${customerBean.accounts}" />

<jsp:useBean id="atmBean" class="com.sun.ebank.web.ATMBean" scope="request"/>
<jsp:useBean id="date" class="com.sun.ebank.web.DateHelper" scope="request" />
<c:url var="url" value="/accountHist?accountId=${atmBean.accountId}&date=0&year=${date.year}&sinceMonth=${date.month}&sinceDay=1&beginMonth=${date.month}&beginDay=1&endMonth=${date.month}&endDay=1&activityView=0&sortOption=0" />
<fmt:message key="CustomerString"/> ${atmBean.operationString} \$${atmBean.amount} ${atmBean.prepString} <a href="${url}">${atmBean.selectedAccount.description}</a>.
<p><fmt:message key="BalanceString"/> <fmt:formatNumber value="${atmBean.selectedAccount.balance}" type="currency" />.
</center>






