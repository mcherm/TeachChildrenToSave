<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - Add Volunteer</title>
</head>
<body>

<h2>Volunteer Information</h2>
<form:form method="POST" action="addVolunteer.htm" modelAttribute="volunteer">
   <table>
    <tr>
        <td><form:label path="firstName">First Name</form:label></td>
        <td><form:input path="firstName" /></td>
        
        <td><form:label path="lastName">Last Name</form:label></td>
        <td><form:input path="lastName" /></td>
    </tr>
    <tr>
        <td><form:label path="emailAddress">Email Address</form:label></td>
        <td><form:input path="emailAddress" /></td>
    </tr>
    <tr>
        <td><form:label path="confirmEmailAddress">Confirm Email Address</form:label></td>
        <td><form:input path="confirmEmailAddress" /></td>
    </tr>
    
    <tr>
        <td><form:label path="password">password</form:label></td>
        <td><form:input type="password" path="password" /></td>
    </tr>
    
    <tr>
        <td><form:label path="confirmPassword">Re-Enter Password</form:label></td>
        <td><form:input path="confirmPassword" /></td>
    </tr>
    
    <tr>
        <td><form:label path="addressLine1">Work Address Line1</form:label></td>
        <td><form:input path="addressLine1" /></td>
    </tr>
    
    <tr>
        <td><form:label path="addressLine2">Work Address Line2</form:label></td>
        <td><form:input path="addressLine2" /></td>
    </tr>
    
    <tr>
        <td><form:label path="city">City</form:label></td>
        <td><form:input path="city" /></td>
    </tr>
    
    <tr>
        <td><form:label path="state">State</form:label></td>
        <td><form:input path="state" /></td>
        
        <td><form:label path="zipcode">zip</form:label></td>
        <td><form:input path="zipcode" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
</table>  
</form:form>
</body>
</html>