<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children To Save - New Bank Admin</title>
    <%@include file="include/commonHead.jsp"%>
    <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
  </head>
  <body class="newBankAdmin">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">

        <h1>Add New Bank Admin</h1>

        <h2>For "<c:out value="${bankName}"/>"</h2>

        <%@include file="include/errors.jsp"%>

        <div>

          <form:form method="POST" action="newBankAdmin.htm" modelAttribute="formData">

            <form:hidden path="bankId"/>

            <div class="formElementCnt">
              <label>
                <div class="inputCnt">
                  <div class="info">
                    First Name
                  </div>
                  <form:input path="firstName" />
                </div>
              </label>
            </div>

            <div class="formElementCnt">
              <label>
                <div class="inputCnt">
                  <div class="info">
                    Last Name
                  </div>
                  <form:input path="lastName" />
                </div>
              </label>
            </div>

            <div class="formElementCnt">
              <label>
                <div class="inputCnt">
                  <div class="info">
                    Email Address
                  </div>
                  <form:input path="email" />
                </div>
              </label>
            </div>

            <div class="formElementCnt">
              <label>
                <div class="inputCnt">
                  <div class="info">
                    Phone Number
                  </div>
                  <form:input path="phoneNumber" />
                </div>
              </label>
            </div>

            <button type="submit" class="editOrRegister" value="Edit">Save</button>

          </form:form>

        </div>

        <br>

        <div>
          <button onclick="js.loadURL('<c:out value="${cancelURL}"/>')" class="editOrRegister cancel">Cancel</button>
        </div>

      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
  </body>
</html>
