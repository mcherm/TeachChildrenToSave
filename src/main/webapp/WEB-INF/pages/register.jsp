<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Register</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="register">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp"%>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h3>Register</h1>

                <div><h3>I am a banker:</h3></div>
                <button onclick="js.loadURL('registerVolunteer.htm');">Volunteer</button>

                <%--<button onclick="js.loadURL('bank.htm');">Register my bank to join</button>--%>

                <div><h3>I am a teacher:</h3></div>
                <button onclick="js.loadURL('registerTeacher.htm');">Sign up my class</button>

            </main>

            <aside>
                <img src="tcts/img/iStock_000032938910Small-happy-student-and-teacher.jpg" alt="" aria-hidden="true">
            </aside>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>

    </body>
</html>