<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children To Save - New Bank Admin</title>
    <%@include file="include/commonHead.jsp"%>
    <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
  </head>
  <body class="markAsBankAdmin">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">

        <h1>Mark Volunteer As Bank Admin</h1>

        <h2>For "<c:out value="${bankName}"/>"</h2>

        <table id="volunteersToPromote" class="responsive">
          <thead>
            <tr>
              <th scope="col">Name</th>
              <th scope="col">Email</th>
              <th scope="col">Phone</th>
              <th scope="col">
                <span class="ada-read">Column of Make Admin buttons</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <c:if test="${empty volunteers}">
              <tr>
                <td colspan="4" class="emptyTableMessage">The bank has no volunteers.</td>
              </tr>
            </c:if>
            <c:forEach items="${volunteers}" var="volunteer">
              <tr>
                <td class="center">
                  <c:out value="${volunteer.firstName}"/>
                  <c:out value="${volunteer.lastName}"/>
                </td>
                <td class="center">
                  <c:out value="${volunteer.email}"/>
                </td>
                <td class="center">
                  <c:out value="${volunteer.phoneNumber}"/>
                </td>
                <td class="action">
                  <form method="POST" id="formData" action="markAsBankAdmin.htm">
                    <input type="hidden" id="userId" name="userId" value="<c:out value="${volunteer.userId}"/>"/>
                    <input type="hidden" id="bankId" name="bankId" value="<c:out value="${bankId}"/>"/>
                    <button class="editOrRegister" type="submit">Make Admin</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>

        <br>

        <div>
          <button onclick="js.loadURL('<c:out value="${cancelURL}"/>')" class="editOrRegister cancel">Cancel</button>
        </div>

      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
  </body>
</html>
