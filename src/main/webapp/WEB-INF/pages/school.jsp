<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - School </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="event_registration">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>School Information</h1>
            <!--<form:form method="POST" action="addEvent.htm" modelAttribute="event">-->
               <div>
                <div class="row_div">
                    <div class="row_div_left">
                        <div class="caption_div"><form:label path="schoolId">School ID</form:label></div>
                        <div class="field_div row_div_field_left_joint"><c:out value="${school.schoolId}"/></div>
                   </div>
                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="name">School Name</form:label></div>
                        <div class="field_div"><c:out value="${school.name}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="addressLine1">Address</form:label></div>
                        <div class="field_div"><c:out value="${school.addressLine1}"/></div>
                    </div>
                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="addressLine1">City</form:label></div>
                        <div class="field_div"><c:out value="${school.city}"/></div>
                    </div>
                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="state">State</form:label></div>
                        <div class="field_div"><c:out value="${school.state}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="zip">Zip</form:label></div>
                        <div class="field_div"><c:out value="${school.zip}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="county">County</form:label></div>
                        <div class="field_div"><c:out value="${school.county}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="schoolDistrict;">School district</form:label></div>
                        <div class="field_div"><c:out value="${school.schoolDistrict}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="phone;">School phone</form:label></div>
                        <div class="field_div"><c:out value="${school.phone}"/></div>
                    </div>

                    <div class="row_div_right">
                        <div class="caption_div"><form:label path="lmiEligible;;">School lmiEligible;</form:label></div>
                        <div class="field_div"><c:out value="${school.lmiEligible}"/></div>
                    </div>

                </div>
            </div>

        </main>
    </div>

        <!--</form:form>-->
        <%@include file="include/footer.jsp"%>
    </body>
</html>