package org.jahia.se.modules.headless.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;


/**
 * API Service
 *
 * @author tleclere,hduchesne
 */
@Component(service = ApiService.class, immediate = true)
public class ApiService {
    /**
     * Object mapper for serialization
     */
    private final ObjectMapper objectMapper;

    /**
     * API Service
     */
    public ApiService() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * Give data executing HTTP request deserializing JSON data
     *
     * @param request HTTP request
     * @param clazz   Class to deserialize data
     * @param <T>     Class to deserialize data
     * @return data deserialized
     * @throws IOException
     */
    public <T> T execute(HttpUriRequest request, Class<T> clazz) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
            HttpEntity entity = httpClient.execute(request).getEntity();
            if (entity != null) {
                String data = IOUtils.toString(entity.getContent());
                if (StringUtils.isNotBlank(data)) {
                    return objectMapper.readValue(data, clazz);
                }
            }
        }
        return null;
    }

//    /**
//     * Give data from JSON remote file
//     *
//     * @param fileUrl file URL
//     * @param clazz   Class to deserialize data
//     * @param <T>     Class to deserialize data
//     * @return data deserialized
//     * @throws IOException
//     */
//    public <T> T downloadGzip(String fileUrl, Class<T> clazz) throws IOException {
//        URL url = new URL(fileUrl);
//        try (InputStream is = new GZIPInputStream(url.openConnection().getInputStream())) {
//            String data = IOUtils.toString(is);
//            if (StringUtils.isNotBlank(data)) {
//                return objectMapper.readValue(data, clazz);
//            }
//        }
//        return null;
//    }
}
