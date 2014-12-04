<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Spring MVC Form Handling</title>
</head>
<body>

<h2>Submitted Student Information</h2>
   <table>
    <tr>
        <td>FirstName</td>
        <td>${firstName}</td>
    </tr>
    <tr>
        <td>LastName</td>
        <td>${lastName}</td>
    </tr>
    <tr>
        <td>Email Address</td>
        <td>${emailAddress}</td>
    </tr>
</table>  
</body>
</html>