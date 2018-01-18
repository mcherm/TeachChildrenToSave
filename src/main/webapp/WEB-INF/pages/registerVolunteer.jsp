<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Register New Volunteer</title>
        <%@include file="include/commonHead.jsp"%>

        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
        <script type="text/javascript">
            var bankSpecificDataLabelMap = {
                <c:forEach items="${banks}" var="bank" varStatus="loopStatus">
                    '<c:out value="${bank.bankId}"/>': '${bank.bankSpecificDataLabel}'<c:if test="${!loopStatus.last}">,</c:if>
                </c:forEach>
            };

            function onBankChoiceChanged(newBankId) {
                var bankSpecificLabel = bankSpecificDataLabelMap[newBankId];
                if (bankSpecificLabel) {
                    $('#bankSpecificDataPlaceholder').html(
                            '<div class="formElementCnt">' +
                            '    <label>' +
                            '        <div class="inputCnt">' +
                            '            <div class="info">' + bankSpecificLabel + '</div>' +
                            '            <input type="text" value="${formData.bankSpecificData}" name="bankSpecificData" id="bankSpecificData">' +
                            '        </div>' +
                            '    </label>' +
                            '</div>'
                    );
                } else {
                    $('#bankSpecificDataPlaceholder').empty();
                }
            }

            $(document).ready(function() {
                $('#bankId').change(function() {
                    onBankChoiceChanged($(this).val());
                });
                onBankChoiceChanged($('#bankId').val());
            })
        </script>
    </head>
    <body class="registerVolunteer">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">


            <h1>Register a New Volunteer</h1>

            <%@include file="include/errors.jsp"%>

            <form:form method="POST" action="registerVolunteer.htm" modelAttribute="formData">

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                First Name (*)
                            </div>
                            <form:input path="firstName"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Last Name (*)
                            </div>
                            <form:input path="lastName"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Email (*)
                            </div>
                            <form:input path="email"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Retype Email (*)
                            </div>
                            <form:input path="emailMatch"/>
                        </div>
                    </label>
                </div>


                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Password (*)
                            </div>
                            <form:input path="password" type="password"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Retype Password (*)
                            </div>
                            <form:input path="passwordMatch" type="password"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Street Address (*)
                            </div>
                            <form:input path="streetAddress"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Suite / Floor Number
                            </div>
                            <form:input path="suiteOrFloorNumber"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Mail Code
                            </div>
                            <form:input path="mailCode"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                City (*)
                            </div>
                            <form:input path="city"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                State (*)
                            </div>
                            <form:input path="state"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Zip Code (*)
                            </div>
                            <form:input path="zip"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Phone Number
                            </div>
                            <form:input path="phoneNumber"/>
                        </div>
                    </label>
                </div>

                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                Select your bank (*)
                            </div>

                            <form:select path="bankId">
                                <form:option value="0" label="- Select Bank -" />
                                <form:options items="${banks}" itemValue="bankId" itemLabel="bankName" />
                            </form:select>
                        </div>
                    </label>
                </div>

                <div id="bankSpecificDataPlaceholder"></div>


                <button type="submit">Register</button>

            </form:form>

            <div>* - Required field</div>

            <p>If you do not see your bank listed please <a href="contact.htm">contact us</a> and we will add
                you bank to the list.</p>

        </main>
    </div><%-- mainCnt --%>

    <%@include file="include/footer.jsp"%>
    </body>
</html>
