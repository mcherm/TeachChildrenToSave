package com.tcts.controller;

import com.tcts.S3Bucket.S3Util;
import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Document;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.EditDocumentFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;


/**
 * This is the controller that serves up the page for managing which documents
 * to display to which users.
 */
@Controller
public class DocumentController {
    @Autowired
    private DatabaseFacade database;

    @Autowired
    private S3Util s3Util;

    /**
     * This displays the page on which the site admin can manage the documents available in the "Important Documents" link
     * on the teacher, volunteer, and bank admin homepages.
     * These documents are currently stored in an S3 Bucket on Amazon.  The site admin can set whether the listed documents
     * will be displayed on the teacher, volunteer, or bank admin home page.  Because bank admins can also volunteer as volunteers
     * any documents made available to volunteers will automatically also be displayed on the bank admin page.
     */
    @RequestMapping(value = "documents", method = RequestMethod.GET)
    public String showDocuments(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return showForm(model);
    }

    private String showForm(Model model) throws SQLException {
        Set<String> bucketDocNames = s3Util.getAllDocuments();
        SortedSet<Document> dbDocuments = database.getDocuments();
        //find any documents that are listed in the database but have been deleted from the bucket and delete them
        //from the database
        Set<Document> docsToBeDeleted = new HashSet<Document>();
        for (Document doc : dbDocuments) {
            if (!bucketDocNames.contains(doc.getName())) {
                database.deleteDocument(doc.getName());
                docsToBeDeleted.add(doc);
            }
        }
        dbDocuments.removeAll(docsToBeDeleted);

        //find any documents that have been added to the bucket and add them to the db list with shownTo permissions set to false
        // They will appear on the list shown to the siteAdmin but won't be added in the database until the siteAdmin edits the permissions
        Document dbDocument;
        for (String bucketName : bucketDocNames) {
            dbDocument = new Document(bucketName, false, false, false);
            if (!dbDocuments.contains(dbDocument)) {
                dbDocuments.add(dbDocument);

            }
        }

        model.addAttribute("s3Util", s3Util);
        model.addAttribute("documents", dbDocuments);
        return "documents";
    }

    @RequestMapping(value = "editDocument", method = RequestMethod.POST)
    public String editDocument(
            @ModelAttribute("editDocumentFormData") EditDocumentFormData formData,
            HttpSession session) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        // --- Make the changes ---
        Document document = new Document(formData.getName(), formData.isShowToTeacher(), formData.isShowToVolunteer(), formData.isShowToBankAdmin());
        database.createOrModifyDocument(document);
        return "redirect:documents.htm";
    }

    @RequestMapping(value = "uploadDocument", method = RequestMethod.POST)
    public String uploadDocument(
            @RequestParam("dataFile") MultipartFile file,
            HttpSession session) throws Exception {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        s3Util.upLoadDocument(file);
        return "redirect:documents.htm";
    }

    /**
     *  Deletes the specified document from the Amazon S3 bucket
     * @param documentName  - docoument name ot be deleted
     * @param session
     * @return
     * @throws SQLException
     */
    @RequestMapping(value = "deleteDocument", method = RequestMethod.POST)
    public String deleteDocument(
            @RequestParam String documentName,
            HttpSession session
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        s3Util.deleteDocument(documentName);
        return "redirect:documents.htm";
    }
}
