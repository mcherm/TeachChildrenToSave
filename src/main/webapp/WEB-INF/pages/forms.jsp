<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Form elements example page</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="contact">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp"%>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Contact Us</h1>

                <form action="">

                    <!-- please ignore/omit inline styles and html line breaks ! -->

                    <p style="margin:10px 0;">
                        Examples of form input markup and rendering errors, please ignore the HTML line breaks.
                    </p>


                    <form action="">

                        <div class="formElementCnt">
                            <label class="error">
                                <div class="inputCnt">
                                    <div class="info">
                                <span class="errorText">
                                    Error:
                                </span>
                                        First Name
                                    </div>
                                    <input type="text" />
                                </div>
                            </label>
                        </div>


                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        First Name
                                    </div>
                                    <input type="text" />
                                </div>
                            </label>
                        </div>

                        <br><br>

                        <div class="formElementCnt">
                            <label class="error">
                                <div class="inputCnt">
                                    <div class="info">
                                <span class="errorText">
                                    Error:
                                </span>
                                        Password
                                    </div>
                                    <input type="password" />
                                </div>
                            </label>
                            <div class="addlInfo">
                                Must contain an Uppercase Letter and a Number
                            </div>
                        </div>


                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Password
                                    </div>
                                    <input type="password" />
                                </div>
                            </label>
                            <div class="addlInfo">
                                Must contain an Uppercase Letter and a Number
                            </div>
                        </div>

                        <br><br>

                        <div class="formElementCnt">
                            <label class="error">
                                <div class="inputCnt">
                                    <div class="info">
	                                <span class="errorText">
	                                    Error:
	                                </span>
                                        State
                                    </div>
                                    <select name="state" id="stateExampleError">
                                        <option>Please Select</option>
                                        <option value="Delaware">Delaware</option>
                                        <option value="Maryland">Maryland</option>
                                        <option value="NewJersey">New Jersey</option>
                                        <option value="Pennsylvania">Penssylvania</option>
                                        <option value="Virginia">Virginia</option>
                                    </select>
                                </div>
                            </label>
                        </div>

                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        State
                                    </div>
                                    <select name="state" id="stateExample">
                                        <option>Please Select</option>
                                        <option value="Delaware">Delaware</option>
                                        <option value="Maryland">Maryland</option>
                                        <option value="NewJersey">New Jersey</option>
                                        <option value="Pennsylvania">Penssylvania</option>
                                        <option value="Virginia">Virginia</option>
                                    </select>
                                </div>
                            </label>
                        </div>

                        <br><br>


                        <div class="formElementCnt">
                            <label class="error">
                                <div class="inputCnt radio">
                                    <div class="info">
	                                <span class="errorText">
	                                    Error:
	                                </span>
                                        Preferred contact time
                                    </div>

                                    <div>
                                        <label>
                                            <input type="radio" name="contactTime" value="daytime" checked>
                                            <span class="txt">Daytime</span>
                                        </label>
                                    </div>

                                    <div>
                                        <label>
                                            <input type="radio" name="contactTime" value="evening">
                                            <span class="txt">Evening</span>
                                        </label>
                                    </div>
                                </div>
                            </label>
                        </div>


                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt radio">
                                    <div class="info">
                                        Preferred contact time
                                    </div>

                                    <div>
                                        <label>
                                            <input type="radio" name="contactTime" value="daytime" checked>
                                            <span class="txt">Daytime</span>
                                        </label>
                                    </div>

                                    <div>
                                        <label>
                                            <input type="radio" name="contactTime" value="evening">
                                            <span class="txt">Evening</span>
                                        </label>
                                    </div>
                                </div>
                            </label>
                        </div>

                        <br><br>


                        <div class="formElementCnt">
                            <label class="error">
                                <div class="inputCnt radio">
                                    <div class="info">
	                                <span class="errorText">
	                                    Error:
	                                </span>
                                        Grade
                                    </div>

                                    <div>
                                        <label>
                                            <input type="checkbox" name="grade" value="grade3" checked>
                                            <span class="txt">3rd Grade (15)</span>
                                        </label>
                                    </div>

                                    <div>
                                        <label>
                                            <input type="checkbox" name="grade" value="grade4">
                                            <span class="txt">4th Grade (15)</span>
                                        </label>
                                    </div>
                                </div>
                            </label>
                        </div>


                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt radio">
                                    <div class="info">
                                        Grade
                                    </div>

                                    <div>
                                        <label>
                                            <input type="checkbox" name="grade" value="grade3" checked>
                                            <span class="txt">3rd Grade (15)</span>
                                        </label>
                                    </div>

                                    <div>
                                        <label>
                                            <input type="checkbox" name="grade" value="grade4">
                                            <span class="txt">4th Grade (15)</span>
                                        </label>
                                    </div>
                            </label>
                        </div>

                        <br><br>


                        <div class="formElementCnt">
                            <label>
                                <div class="inputCnt">
                                    <div class="info">
                                        Additional notes
                                    </div>
                                    <textarea rows="4" cols="40"></textarea>
                                </div>
                            </label>
                        </div>


                </form>

            </main>

        </div><%-- .mainCnt --%>

        <%@include file="include/footer.jsp" %>

    </body>
</html>