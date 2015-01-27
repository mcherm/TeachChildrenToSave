<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - volunteer confirmation</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body>

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">
            <h1>Submitted Student Information</h1>
               <div>
                <div>
                    <div>Name</div>
                    <div>${firstName} &nbsp; ${lastName}</div>
                </div>
                <div>
                    <div>Email</div>
                    <div>${emailAddress}</div>
                </div>
                <div>
                    <div>Work Address</div>
                    <div>${addressLine1}</div>
                    <div>${state}</div>
                    <div>${city} - ${zipcode}</div>
                </div>
                <div>
                    <div>Work Phone</div>
                    <div>${workPhoneNumber}</div>
                </div>
                <div>
                    <div>Mobile Phone</div>
                    <div>${mobilePhoneNumber}</div>
                </div>
                <div>
                    <div>Employer </div>
                    <div>${employerInfo}</div>
                </div>
            </div>

        </main>
    </div>

        <%@include file="include/footer.jsp"%>
    </body>
</html>