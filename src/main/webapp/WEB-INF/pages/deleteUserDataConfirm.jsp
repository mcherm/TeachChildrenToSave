<%-- Page for Site Admins to manage the Database --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children to Save - Site Administrator Manage Database</title>
    <%@include file="include/commonHead.jsp"%>
  </head>
  <body id="deleteUserDataConfirm" class="deleteUserDataConfirm">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">


        <h1>
          Delete User Data
        </h1>

        <p><strong  class="seriousWarning">ARE YOU SURE?</strong></p>

        <p>
          This action will delete all classes, all teachers, all volunteers, and all bank admins.
          (The production data IS backed up and can be restored for up to 30 days.)
        </p>

        <ul class="noUl">
          <li class="mb1">
            <form method="POST" action="deleteUserDataConfirm.htm">
              <button type="submit" class="editOrRegister delete">Delete Personal Data</button>
            </form>
            <button onclick="js.loadURL('manageDatabase.htm');" class="editOrRegister">
              Cancel
            </button>
          </li>
        </ul>

      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>

  </body>
</html>