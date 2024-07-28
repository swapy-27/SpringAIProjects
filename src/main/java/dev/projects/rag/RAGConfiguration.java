
package dev.projects.rag;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * RAGConfiguration
 */

// Generating a vector databse
@Configuration
public class RAGConfiguration {

    private Logger log = LoggerFactory.getLogger(RAGConfiguration.class);
    

    @Value("classpath:/static/docs/olympic-data.txt")
    private  Resource faqResource;

    @Value("vectorStor.json")
    private String vectorStoreName;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {

        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        
        File vectorStoreFile= getVectorStoreFile();

        if(vectorStoreFile.exists()){

            log.info("Vector DB exists");
            simpleVectorStore.load(vectorStoreFile);
        }else{
            log.info("Vector DB not exists");

            TextReader textReader = new TextReader(faqResource);
            List<Document> document = textReader.get();
            
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = tokenTextSplitter.split(document);

            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
    
        }

        return simpleVectorStore;

    }

    File getVectorStoreFile() {

        java.nio.file.Path path = Paths.get("src", "main", "resources", "static","data");
        String absolutePath = path.toString() + '/' + vectorStoreName;
        return new File(absolutePath);

    }

}