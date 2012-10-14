<%@ taglib uri='/WEB-INF/tlds/template.tld' prefix='template' %>

<template:insert template="../template/template.jsp">
  <template:put name='title' content='Guest' direct='true'/>
  <template:put name='header' content="../template/header.jsp" />
  <template:put name='menu' content="../template/menu.jsp" />
  <template:put name='content' content="../guest/guest.jsp"/>
  <template:put name='footer' content="../template/footer.jsp" />
</template:insert>