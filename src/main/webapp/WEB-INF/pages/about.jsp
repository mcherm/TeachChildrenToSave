<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - About Us</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="about">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp"%>
        
        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="about">

                <h1>About Us</h1>

                <jsp:include page="/WEB-INF/pages/sites/${site}/aboutBody.jsp"/>

                <h2>Contact us</h2>

                <p>
                    If you have any questions, don’t hesitate to
                    <button type="button" onclick="js.loadURL('contact.htm')" class="editOrRegister">Contact Us</button>.
                </p>

            </main>

            <aside>
                <img src="tcts/img/iStock_000010713646Small-kids-writing.jpg" alt="" aria-hidden="true">
            </aside>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>

    </body>
</html>