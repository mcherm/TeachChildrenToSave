<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Contact Us</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="contact">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp"%>


        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Contact Us</h1>

                <p>
                    If you have any questions, don't hesitate to contact us.
                </p>


                <p>
                    <span class="b">Email</span>
                    <br>
                    <c:out value="${email}"/>
                </p>


                <p>
                    <span class="b">Social Media</span>
                    <br>
                    <div>Facebook: <a href="https://www.facebook.com/CEEEatUD" target="_blank">https://www.facebook.com/CEEEatUD</a></div>
                    <div>Twitter: <a href="https://twitter.com/CEEEatUD" target="_blank">https://twitter.com/CEEEatUD</a></div>
                </p>


                <p>
                    <span class="b">Phone</span>
                    <br>
                    [Need to insert phone]
                </p>


                <h3>Mail</h3>

                <p>
                    Teach Children to Save Day <br>
                    C/O <br>
                    [Need to insert address]
                </p>
                
                <div>
                
                <p style="color:green" >
                    <br>
                    <c:out value="${confirmationMessage}"/>
                </p>

                    <form:form method="POST" action="contact.htm" modelAttribute="formData">

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">Your Email Address</div>
                                    <form:input path="emailAddress"/>
                                </div>
                            </label>
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
                </div>

            </main>

            <aside>
                <img src="tcts/img/iStock_000011666378Small-kids-computers.jpg" alt="" aria-hidden="true">
            </aside>

        </div><%-- .mainCnt --%>
        
        
        <%@include file="include/footer.jsp" %>

    </body>
</html>