<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Register</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="register">
        <%@include file="include/header.jsp"%>

        <a href="#main" class="ada-read">Skip to main content</a>

        
        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Register</h1>

                <button onclick="js.loadURL('registerVolunteer.htm');">Volunteer</button>

                <%--<button onclick="js.loadURL('bank.htm');">Register my bank to join</button>--%>

                <button onclick="js.loadURL('registerTeacher.htm');">Sign up my class</button>

            </main>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>

    </body>
</html>