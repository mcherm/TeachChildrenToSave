<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children To Save - Admin Edit Allowed Values</title>
    <%@include file="include/commonHead.jsp"%>
  </head>
  <body class="listAllowedValues">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">

        <h1>
          Edit Allowed <c:out value="${valueType}"/>s
        </h1>

        <div id="actions">

          <h2>Actions</h2>

          <ul class="noUl">
            <li class="mb1">
              <button onclick="js.loadURL('addAllowedValue.htm?valueType=<c:out value="${valueType}"/>')" class="editOrRegister">Add <c:out value="${valueType}"/></button>
            </li>

            <li class="mb1">
              <button onclick="js.loadURL('editAllowedValues.htm')" class="editOrRegister cancel">Back</button>
            </li>

          </ul>
        </div>

        <a download="allowed<c:out value="${valueType}"/>s.xls" href="excel/allowed<c:out value="${valueType}"/>s/<c:out value="${valueType}"/>.htm" class="downloadExcel">Export to Excel</a>

        <table id="listAllowedValuesTable" class="responsive">
          <thead>
            <tr>
              <th scope="col" class="time"><c:out value="${valueType}"/></th>
              <c:if test="${allowedValues.size() > 1}">
                <th scope="col">
                  <span class="ada-read">Column of Delete buttons</span>
                </th>
              </c:if>
            </tr>
          </thead>
          <tbody>
            <c:if test="${empty allowedValues}">
              <td colspan="3" class="emptyTableMessage">There are no allowed <c:out value="${valueType}"/>s now.</td>
            </c:if>
            <c:forEach var="allowedValue" items="${allowedValues}">
              <tr>
                <td class="valueColumn" data-title="<c:out value="${valueType}"/>"><c:out value="${allowedValue}"/></td>
                <c:if test="${allowedValues.size() > 1}">
                  <td class="action">
                    <form method="POST" action="deleteAllowedValue.htm" modelAttribute="formData">
                      <input type="hidden" name="valueType" value='<c:out value="${valueType}"/>' />
                      <input type="hidden" name="allowedValue" value='<c:out value="${allowedValue}"/>' />
                      <button type="submit" class="editOrRegister delete">Delete</button>
                    </form>
                  </td>
                </c:if>
              </tr>
            </c:forEach>
          </tbody>
        </table>

      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
  </body>
</html>
