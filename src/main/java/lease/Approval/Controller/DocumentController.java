package lease.Approval.Controller;

import lease.Approval.Model.Document;
import lease.Approval.Service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/leases/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // Upload multiple documents for a lease
    @PostMapping("/{leaseId}/upload")
    public ResponseEntity<String> uploadDocuments(@PathVariable Long leaseId, @RequestParam("files") List<MultipartFile> files) {
        try {
            documentService.uploadDocuments(leaseId, files);
            return ResponseEntity.status(HttpStatus.OK).body("Documents uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading documents.");
        }
    }

    // Get all documents for a lease
    @GetMapping("/{leaseId}")
    public ResponseEntity<List<Document>> getDocumentsByLeaseId(@PathVariable Long leaseId) {
        List<Document> documents = documentService.getDocumentsByLeaseId(leaseId);
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    // Download a document by ID
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        Document document = documentService.getDocumentById(documentId);
        if (document != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getFileName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.parseMediaType(document.getFileType()))
                    .body(document.getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a document by ID
    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.status(HttpStatus.OK).body("Document deleted successfully.");
    }
}
