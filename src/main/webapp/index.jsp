<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save</title>
        <%@include file="WEB-INF/pages/include/commonHead.jsp"%>

        <script type="text/javascript" src="/teachchildrentosave/tcts/js/scripts.js"></script>


    </head>
    <body class="home">

        <a href="#main" class="ada-read">Skip to main content</a>
        
        <div class="decor"></div>

        <%@include file="WEB-INF/pages/include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="WEB-INF/pages/include/navigation.jsp" %>

            <main id="main">
                <h1>
                    2015 Teach Children to Save Day!
                    <br />
                    April 8-9-10
                </h1>

                <h2>
                    3<sup>rd</sup> and 4<sup>th</sup> graders learn critical lessons on personal finance and economics in a 45 minute lesson
                </h2>

                <h3>Teachers</h3>

                <p>Why participate?</p>

                <ul class="program_highlights">
                    <li><span>Most students don't get these financial literacy lessons any other place</span></li>
                    <li><span>Research has shown that when people are taught the basics of money management as children they are more likely to be fiscally fit as adults</span></li>
                    <li><span>It only takes 45 minutes</span></li>
                </ul>

                <button onclick="js.loadURL('registerTeacher.htm');">Sign up my class</button>

                <h3>Volunteers</h3>

                <p>Why volunteer?</p>

                <ul class="program_highlights">
                    <li><span>Research has shown that when people are taught the basics of money management as children they are more likely to be fiscally fit as adults</span></li>
                    <li><span>Provide valuable financial lessons most children don't won't receive any other time</span></li>
                    <li><span>Choose from a variety of locations that are convenient for you</span></li>
                    <li><span>It only takes 45 minutes</span></li>
                    <li><span>You'll receive all the training material you need</span></li>
                </ul>

                <button onclick="js.loadURL('registerVolunteer.htm');">Volunteer</button>

                <h3>Bank Community Affairs Departments</h3>

                <p>Why participate?</p>

                <ul class="program_highlights">
                    <li><span>Your bank may get CRA credits</span></li>
                    <li><span>Over 90% of Delaware banks participate</span></li>
                    <li><span>Note: Only members of the Delaware Banker's Associations can participate</span></li>
                </ul>



                <%--<center>--%>
                <%--<br>--%>
                <%--<a href="event.htm">Project Devs, click Here for Event Registration</a>--%>
                <%--</center>--%>

                <p>
                    If you would like your bank to participate, please contact <span class="b">[NEED NAME AND CONTACT]</span>
                    to begin participating in the program.
                </p>

            </main>

            <aside>
                <img src="tcts/img/logo-tcts.png" alt="" aria-hidden="true">
            </aside>

        </div><%-- .mainCnt --%>

        <%@include file="WEB-INF/pages/include/footer.jsp" %>
    </body>
</html>