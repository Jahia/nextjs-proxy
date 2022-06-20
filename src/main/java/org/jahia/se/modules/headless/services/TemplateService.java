package org.jahia.se.modules.headless.services;

import org.apache.http.client.methods.HttpGet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

@Component(service = TemplateService.class, immediate = true)
public class TemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);
    private ApiService apiService;

    @Reference(service = ApiService.class)
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public Template[] getTemplates(URI uri) {
        if (apiService != null) {
            try {
                Template[] data = apiService.execute(new HttpGet(uri), Template[].class);
                if (data != null) {
                    return data;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return new Template[0];
    }
}
