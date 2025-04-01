<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>Teach Children To Save - Admin Edit Allowed Values</title>
    <%@include file="include/commonHead.jsp"%>
  </head>
  <body class="editAllowedValues">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

      <%@include file="include/navigation.jsp" %>

      <main id="main">

        <h1>
          Edit Allowed Values
        </h1>

        <div id="actions">

          <h2>Actions</h2>

          <ul class="noUl">

            <li class="mb1">
              <button onclick="js.loadURL('listAllowedDates.htm');" class="editOrRegister">
                Add/Remove Class Dates
              </button>
            </li>

            <li class="mb1">
              <button onclick="js.loadURL('listAllowedValues.htm?valueType=Time')" class="editOrRegister">
                Add/Remove Class Times
              </button>
            </li>


            <%-- FIXME: Put these on a common page --%>
            <li class="mb1">
              <button onclick="js.loadURL('listAllowedValues.htm?valueType=Grade')" class="editOrRegister">
                Add/Remove Class Grades
              </button>
            </li>

            <%-- FIXME: Put these on a common page --%>
            <li class="mb1">
              <button onclick="js.loadURL('listAllowedValues.htm?valueType=Delivery%20Method')" class="editOrRegister">
                Add/Remove Class Delivery Methods
              </button>
            </li>

            <li class="mb1">
              <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
            </li>

          </ul>
        </div>

      </main>

    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
  </body>
</html>
