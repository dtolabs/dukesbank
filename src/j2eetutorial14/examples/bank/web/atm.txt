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
<jsp:setProperty name="customerBean" property="customer" value="${pageContext.request.userPrincipal.name}"/>
<c:set var="accounts" value="${customerBean.accounts}" />
<center>
<table border=0 cellpadding=2 cellspacing=0 width=500 summary="layout"> 
  <c:url var="url" value="/atmAck" />
  <form name="atm" method="post" action="${url}" >		  
  <tr>
  <td>
  <select name=operation> 
    <option value=0<c:if test="${param.operation == 0}"> selected</c:if>><fmt:message key="Withdraw" /></option>  	   
    <option value=1<c:if test="${param.operation == 1}"> selected</c:if>><fmt:message key="Deposit" /></option>   
  </select>
  </td>

  <td>
  <select name=accountId>
    <c:forEach items="${accounts}" var="ad">
      <c:if test="${param.accountId == ad.accountId}">
        <c:if test="${ad.type != 'Credit'}">
          <option value="${ad.accountId}" selected>${ad.description} (<fmt:formatNumber value="${ad.balance}" type="currency" />)</option>
        </c:if>
        <c:if test="${ad.type == 'Credit'}">
          <option value="${ad.accountId}" selected>${ad.description} (<fmt:formatNumber value="${ad.remainingCredit}" type="currency" />)</option>
        </c:if>
      </c:if>
      <c:if test="${param.accountId != ad.accountId}">
        <c:if test="${ad.type != 'Credit'}">
          <option value="${ad.accountId}">${ad.description} (<fmt:formatNumber value="${ad.balance}" type="currency" />)</option>
        </c:if>
        <c:if test="${ad.type == 'Credit'}">
          <option value="${ad.accountId}">${ad.description} (<fmt:formatNumber value="${ad.remainingCredit}" type="currency" />)</option>
        </c:if>
      </c:if>
    </c:forEach>
  </select>
  </td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  </tr>

  <tr>
  <td><b><fmt:message key="ATMAmount"/></b></td>
  <td><input type="text" size="15" name="amount"></td>
  <td><input type="submit" value="<fmt:message key='Submit'/>"></td>
  <td><input type="reset" value="<fmt:message key='Clear'/>"></td>
  </tr>
    
  </form>
</table>
</center>
