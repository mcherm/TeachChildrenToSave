package com.tcts.controller;

import com.tcts.S3Bucket.S3Util;
import com.tcts.common.Configuration;
import com.tcts.common.SessionData;
import com.tcts.common.SitesConfig;
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

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;


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

    @Autowired
    private SitesConfig sitesConfig;

    @Autowired
    private Configuration configuration;


    /**
     * This displays the page on which the site admin can manage the documents available in the "Important Documents" link
     * on the teacher, volunteer, and bank admin homepages.
     * These documents are currently stored in an S3 Bucket on Amazon.  The site admin can set whether the listed documents
     * will be displayed on the teacher, volunteer, or bank admin home page.  Because bank admins can also volunteer as volunteers
     * any documents made available to volunteers will automatically also be displayed on the bank admin page.
     */
    @RequestMapping(value = "documents.htm", method = RequestMethod.GET)
    public String showDocuments(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return showForm(model);
    }

    private String showForm(Model model) throws SQLException {
        final String site = sitesConfig.getSite();
        final String env = configuration.getProperty("dynamoDB.environment");
        String folder = site + "/" + env + "/";

        final Set<String> allDocNamesFromBucket = s3Util.getAllDocuments();
        final Set<String> bucketDocNames = allDocNamesFromBucket.stream()
                .filter(x -> x.startsWith(folder) && x.length() > folder.length()) // in the folder & not the folder itself
                .collect(Collectors.toSet());

        final SortedSet<Document> dbDocuments = database.getDocuments();

        // Any documents that appear in the database but NOT in the bucket should be deleted
        // from the database -- it won't do any good to know about it if the document doesn't
        // exist! Doing this cleanup each time we touch it keeps the two lists in sync.
        final Set<Document> docsToBeDeletedFromDb = new HashSet<Document>();
        for (Document doc : dbDocuments) {
            if (!bucketDocNames.contains(folder + doc.getName())) {
                database.deleteDocument(doc.getName()); // found in DB but not in bucket; delete from DB
                docsToBeDeletedFromDb.add(doc);
            }
        }
        dbDocuments.removeAll(docsToBeDeletedFromDb); // remove it from our copy of the DB contents (AFTER iterating)

        // Any documents that appear in the bucket but not in the database should be added to
        // the database with all the shownTo permissions set to false, so only the DB admin
        // can see them. Doing this cleanup each time we touch it keeps the two lists in sync.
        for (String bucketName : bucketDocNames) {
            final Document dbDocument = new Document(bucketName.substring(folder.length()), false, false, false);
            // Next line is weird but it works. We want to check whether the Document name (with ANY
            // permissions) exists in the DB. Fortunately, the Document object compares for equality
            // based only on the name, and NOT on the permissions, so a simple contains() will test
            // whether it is there by name. If not, then we'll insert the one we built with all of the
            // permissions set to false.
            if (!dbDocuments.contains(dbDocument)) {
                dbDocuments.add(dbDocument);
                database.createOrModifyDocument(dbDocument); // found in bucket & not DB; add to DB
            }
        }

        model.addAttribute("s3Util", s3Util);
        model.addAttribute("documents", dbDocuments);
        return "documents";
    }

    @RequestMapping(value = "editDocument.htm", method = RequestMethod.POST)
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

    @RequestMapping(value = "uploadDocument.htm", method = RequestMethod.POST)
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
    @RequestMapping(value = "deleteDocument.htm", method = RequestMethod.POST)
    public String deleteDocument(
            @RequestParam("documentName") String documentName,
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
