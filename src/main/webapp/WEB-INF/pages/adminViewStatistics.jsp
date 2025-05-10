<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - View Program Statistics</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="adminViewStatistics">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Program Statistics</h1>

                <div>
                    <p>
                        <span>Number of Classes: </span>
                        <span><c:out value="${siteStatistics.numEvents}"/></span>
                    </p>

                    <p>
                        <span>Number of Classes with a Volunteer: </span>
                        <span><c:out value="${siteStatistics.numMatchedEvents}"/></span>
                    </p>

                    <p>
                        <span>Number of Classes with No Volunteer: </span>
                        <span><c:out value="${siteStatistics.numUnmatchedEvents}"/></span>
                    </p>

                    <p>
                        <span>Number of Volunteers Signed Up For a Class: </span>
                        <span><c:out value="${siteStatistics.numVolunteers}"/></span>
                    </p>

                    <p>
                        <span>Number of Teachers With a Volunteer in Their Class: </span>
                        <span><c:out value="${siteStatistics.numParticipatingTeachers}"/></span>
                    </p>

                    <p>
                        <span>Number of Participating Schools: </span>
                        <span><c:out value="${siteStatistics.numParticipatingSchools}"/></span>
                    </p>

                    <p>
                        <div>Number of Classes by Date:</div>
                        <table class="simple-table">
                            <c:forEach items="${siteStatistics.numEventsByEventDate}" var="entry">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </p>

                    <p>
                        <div>Number of Classes by Grade:</div>
                        <table class="simple-table">
                            <c:forEach items="${siteStatistics.numEventsByGrade}" var="entry">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </p>

                    <p>
                        <div>Number of Classes by Delivery Method:</div>
                        <table class="simple-table">
                            <c:forEach items="${siteStatistics.numEventsByDeliveryMethod}" var="entry">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </p>

                    <p>
                    <div>Number of Classes by Time:</div>
                        <table class="simple-table">
                            <c:forEach items="${siteStatistics.numEventsByEventTime}" var="entry">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </p>
                </div>

                <button class="editOrRegister cancel" type="button" onclick="js.loadURL('siteAdminHome.htm')">Back</button>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>