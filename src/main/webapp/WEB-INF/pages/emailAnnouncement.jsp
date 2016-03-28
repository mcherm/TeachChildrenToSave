<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Email Announcement</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="registerVolunteer">

    <a href="#main" class="ada-read">Skip to main content</a>
    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">


            <h1>Email Announcement</h1>

            <%@include file="include/errors.jsp"%>

            <form:form method="POST" action="emailAnnouncement.htm" modelAttribute="formData">

                        <div class="formElementCnt">
                            <div class="inputCnt">
                                <p><div class="info">Select User types to send email</div></p>
                                <label><form:checkbox path="matchedTeachers" value="yes"/>Matched Teachers</br></label>
                                <label><form:checkbox path="unmatchedTeachers" value="yes"/>Un-Matched Teachers</br></label>
                                <label><form:checkbox path="matchedVolunteer" value="yes"/>Matched Volunteers</br></label>
                                <label><form:checkbox path="unmatchedVolunteers" value="yes"/>Un-Matched Volunteers</br></label>
                                <label><form:checkbox path="bankAdmins" value="yes"/>Bank Admins</br></label>
                            </div>
                        </div>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Message
                                    </div>
                                    <form:textarea path="message" />
                                </div>
                            </label>
                        </div>

                        <button type="submit" value="SendMessage">Send Message</button>

                    </form:form>

        </main>
    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
    </body>
</html>
