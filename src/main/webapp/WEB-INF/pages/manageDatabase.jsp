<%-- Page for Site Admins to manage the Database --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children to Save - Site Administrator Manage Database</title>
    <%@include file="include/commonHead.jsp"%>
  </head>
  <body id="manageDatabase" class="manageDatabase">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">


        <h1>
          Manage Database
        </h1>

        <h2>Actions</h2>

        <ul class="noUl">
          <li class="mb1">
            <button onclick="js.loadURL('deleteUserDataConfirm.htm');" class="editOrRegister delete">
              Delete Personal Data
            </button>
          </li>
        </ul>
        <ul class="noUl">
          <li class="mb1">
            <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
          </li>
        </ul>


      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>

  </body>
</html>