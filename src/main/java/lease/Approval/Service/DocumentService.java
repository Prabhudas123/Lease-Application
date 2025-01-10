package lease.Approval.Service;

import lease.Approval.Model.Document;
import lease.Approval.Repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    // Upload multiple documents
    public List<Document> uploadDocuments(Long leaseId, List<MultipartFile> files) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (MultipartFile file : files) {
            Document document = new Document();
            document.setLeaseId(leaseId);
            document.setFileName(file.getOriginalFilename());
            document.setFileType(file.getContentType());
            document.setData(file.getBytes());
            documents.add(documentRepository.save(document));
        }
        return documents;
    }

    // Get documents by lease ID
    public List<Document> getDocumentsByLeaseId(Long leaseId) {
        return documentRepository.findByLeaseId(leaseId);
    }

    // Download document by ID
    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId).orElse(null);
    }

    // Delete document by ID
    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }
}

