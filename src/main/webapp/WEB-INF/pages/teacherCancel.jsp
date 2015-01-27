<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Teacher Cancel</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Cancel the Request for a Volunteer for My Class</h1>

                <div>Are you sure you wish to cancel the request?</div>

                <c:if test="${not empty errorMessage}">
                    <div class="errorMessage">
                        <c:out value="${errorMessage}" default=""  />
                    </div>
                </c:if>

                <div>
                    <form:form method="POST" action="teacherCancel.htm" modelAttribute="formData">

                        <form:hidden path="eventId"/>

                        <c:if test="${hasVolunteer}">
                            <div class="formElementCnt">
                                <label>
                                    <div class="inputCnt">
                                        <div class="info">Note for Volunteer</div>
                                        <form:textarea path="withdrawNotes" rows="5" cols="25" />
                                    </div>
                                </label>
                            </div>
                        </c:if>

                        <button type="submit" value="cancel">Cancel Class</button>
                        <button onclick="js.loadURL('teacherHome.htm');" type="button">Do Not Cancel Class</button>

                    </form:form>
                </div>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
