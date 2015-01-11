<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Volunteer Withdraw</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Withdraw from Volunteering for a Class</h1>

                <div>Are you sure you wish to withdraw from volunteering for this class?</div>

                <c:if test="${not empty errorMessage}">
                    <div class="errorMessage">
                        <c:out value="${errorMessage}" default=""  />
                    </div>
                </c:if>

                <div>
                    <form:form method="POST" action="volunteerWithdraw.htm" modelAttribute="formData">

                        <form:hidden path="eventId"/>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">Note for Teacher</div>
                                    <form:textarea path="withdrawNotes" rows="5" cols="25" />
                                </div>
                            </label>
                        </div>

                        <button type="submit" value="withdraw">Withdraw</button>
                        <button onclick="js.loadURL('volunteerHome.htm');" type="button">Cancel</button>

                    </form:form>
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
