<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">

    <head>
        <title>Teach Children To Save - View Documents</title>
        <%@include file="include/commonHead.jsp"%>
        <script type="text/javascript">
            function makeButtonVisible(elementId) {
                var element = document.getElementById(elementId);
                element.disabled = false;
                element.style.visibility = "visible";
            }

            function makeButtonInvisible(elementId) {
                var element = document.getElementById(elementId);
                element.disabled = true;
                element.style.visibility = "hidden";
            }

            function updateMootness(docNumToUpdate) {
                var volChkbxElem = document.getElementById("showVolunteerChkbx" + docNumToUpdate);
                var baChkbxElem = document.getElementById("showBankAdminChkbx" + docNumToUpdate);
                var mootElem = document.getElementById("showBankAdminIsMoot" + docNumToUpdate);
                if (volChkbxElem.checked) {
                    baChkbxElem.style.display = "none";
                    mootElem.style.display = "inline";
                } else {
                    baChkbxElem.style.display = "inline";
                    mootElem.style.display = "none";
                }
            }

            function disableAllCheckboxesExceptForOneDocument(docNumToLeaveAlone) {
                var numDocuments = <c:out value="${documents.size()}"/>;
                for (var i = 0; i < numDocuments; i++) {
                    if (i != docNumToLeaveAlone) {
                        document.getElementById("showTeacherChkbx" + i).disabled = true;
                        document.getElementById("showVolunteerChkbx" + i).disabled = true;
                        document.getElementById("showBankAdminChkbx" + i).disabled = true;
                    }
                }
            }
        </script>

    </head>
    <body class="documents">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1>Available Documents</h1>

                <div>
                    <form action="uploadDocument.htm"
                          enctype="multipart/form-data" method="POST">
                        <p>
                            <h2>Upload new file</h2><br>
                            <input type="file" name="dataFile" size="40" onclick="makeButtonVisible('uploadButton');return true">
                        </p>
                        <div>
                            <input id="uploadButton" type="submit" value="Upload" class="editOrRegister" disabled style="visibility: hidden">
                        </div>
                    </form>
                </div>
                <div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
                        <li class="mb1">
                           <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                        </li>
                    </ul>
                </div>

                <div>

                    <table class="responsive full-page-table">
                        <thead>
                            <tr>
                                <th scope="col">File Name</th>
                                <th>
                                    <table class="same-size-cols">
                                        <tr>
                                            <th scope="col">Teachers</th>
                                            <th scope="col">Volunteers</th>
                                            <th scope="col">BankAdmin</th>
                                            <th scope="col">
                                                <span class="ada-read">Column of Edit buttons</span>
                                            </th>
                                        </tr>
                                    </table>
                                </th>
                                <th scope = "col">
                                    <span class="ada-read">Column of Delete buttons</span>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty documents}">
                                <td colspan="2" class="emptyTableMessage">There are no documents.</td>
                            </c:if>
                            <% int docNum = 0; %>
                            <c:forEach var="aDocument" items="${documents}">
                                <tr>
                                    <form action="editDocument.htm" method="POST">
                                        <td><a href="${s3Util.makeS3URL(aDocument.name)}">${aDocument.name}</a></td>
                                        <td>
                                            <table class="same-size-cols">
                                                <tr>
                                                    <td>
                                                        <input type="hidden" name="name" value="${aDocument.name}">
                                                        <input type="checkbox" name="showToTeacher" id="showTeacherChkbx<%=docNum%>" <c:if test="${aDocument.showToTeacher}">checked</c:if> onclick="makeButtonVisible('updateButton<%=docNum%>');disableAllCheckboxesExceptForOneDocument(<%=docNum%>)">
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="showToVolunteer"  id="showVolunteerChkbx<%=docNum%>" <c:if test="${aDocument.showToVolunteer}">checked</c:if> onclick="makeButtonVisible('updateButton<%=docNum%>');updateMootness(<%=docNum%>);disableAllCheckboxesExceptForOneDocument(<%=docNum%>)">
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="showToBankAdmin"  id="showBankAdminChkbx<%=docNum%>" <c:if test="${aDocument.showToBankAdmin}">checked</c:if> onclick="makeButtonVisible('updateButton<%=docNum%>');disableAllCheckboxesExceptForOneDocument(<%=docNum%>)" <c:if test="${aDocument.showToVolunteer}">style="display: none"</c:if>>
                                                        <input type="checkbox" id="showBankAdminIsMoot<%=docNum%>" checked disabled <c:if test="${not aDocument.showToVolunteer}">style="display: none"</c:if>>
                                                    </td>
                                                    <td class="action">
                                                        <button id="updateButton<%=docNum%>" disabled style="visibility: hidden" type="submit" class="editOrRegister">Update</button>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </form>
                                    <td class="action">
                                        <form method="POST" action="deleteDocument.htm">
                                            <input type="hidden" name="documentName" value="${aDocument.name}">
                                            <button onclick="return confirm('Are you sure you want to delete ${aDocument.name}?' );"
                                                    type="submit" class="editOrRegister delete">Delete</button>
                                        </form>
                                    </td>

                                </tr>
                                <% docNum += 1; %>
                            </c:forEach>
                        </tbody>
                    </table>

                </div>

            </main>

        </div>

        <%@include file="include/footer.jsp"%>

    </body>
</html>
