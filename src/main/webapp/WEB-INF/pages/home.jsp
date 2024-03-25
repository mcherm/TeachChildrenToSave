<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="home">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">
                <h1>
                    ${currentYear} Teach Children to Save Day!
                    <br />
                    ${eventDatesOnHomepage}
                </h1>

                <h2>
                    Teachers and bank volunteers collaborate to help 3<sup>rd</sup> and 4<sup>th</sup> grade students learn critical lessons in personal finance and economics in a short, 45-minute lesson.
                </h2>

                <h3>Teachers</h3>

                <p>Why participate?</p>

                <ul class="program_highlights">
                    <li><span>Most students are not learning these important financial literacy lessons at home.</span></li>
                    <li><span>Research has shown that when people are taught the basics of money management as children, they are more likely to be fiscally fit as adults.</span></li>
                    <li><span>Volunteer instructors will come right to your classroom and take only 45 minutes of instructional time to present the lesson.</span></li>
                </ul>
                <button onclick="js.loadURL('registerTeacher.htm');">Sign up my class</button>

                <h3>Volunteers</h3>

                <p>Why volunteer?</p>

                <ul class="program_highlights">
                    <li><span>Research has shown that when people are taught the basics of money management as children, they are more likely to be fiscally fit as adults.</span></li>
                    <li><span>Volunteers will be providing valuable financial lessons that most children will not receive at any other time.</span></li>
                    <li><span>You can choose from among a variety of locations a school most convenient for you.</span></li>
                    <li><span>All needed training materials will be provided.</span></li>
                    <li><span>It takes only 45 minutes of your time to make a difference.</span></li>
                </ul>

                <button onclick="js.loadURL('registerVolunteer.htm');">Volunteer</button>

                <h3>Already Registered?</h3>
                <em>   * You must create a new login each year.</em><br><br>
                <button onclick="js.loadURL('login.htm');">Sign In</button>

                <%
                    String faq = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/TCTSD%20FAQs.pdf";
                %>

                <h3><a href="<%=faq%>" target="_blank">FAQs</a></h3>

            </main>

            <aside>
                <img src="tcts/img/TCTSD_Logo_2024.png" alt="" aria-hidden="true">
                <%--<img src="tcts/img/iStock_000019109215Small-happy-kids.jpg" alt="" aria-hidden="true">--%>
            </aside>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>
    </body>
</html>