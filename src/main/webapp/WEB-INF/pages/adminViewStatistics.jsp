<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - View Program Statistics</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Program Statistics</h1>

                <div>
                    <label>
                        <div class="info">Number of Classes</div>
                        <div><c:out value="${siteStatistics.numEvents}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of Classes with a Volunteer</div>
                        <div><c:out value="${siteStatistics.numMatchedEvents}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of Classes with No Volunteer</div>
                        <div><c:out value="${siteStatistics.numUnmatchedEvents}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of 3rd Grade Classes</div>
                        <div><c:out value="${siteStatistics.num3rdGradeEvents}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of 4th Grade Classes</div>
                        <div><c:out value="${siteStatistics.num4thGradeEvents}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of Volunteers</div>
                        <div><c:out value="${siteStatistics.numVolunteers}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of Participating Teachers</div>
                        <div><c:out value="${siteStatistics.numParticipatingTeachers}"/></div>
                    </label>

                    <label>
                        <div class="info">Number of Participating Schools</div>
                        <div><c:out value="${siteStatistics.numParticipatingSchools}"/></div>
                    </label>
                </div>

                <button class="editOrRegister cancel" type="button" onclick="js.loadURL('siteAdminHome.htm')">Back</button>

            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>