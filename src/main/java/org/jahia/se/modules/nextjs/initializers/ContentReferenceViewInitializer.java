package org.jahia.se.modules.nextjs.initializers;

import org.apache.http.client.utils.URIBuilder;
import org.jahia.se.modules.nextjs.services.Template;
import org.jahia.se.modules.nextjs.services.TemplateService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;

import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.initializers.ChoiceListValue;
import org.jahia.services.content.nodetypes.initializers.ModuleChoiceListInitializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(service = ModuleChoiceListInitializer.class, immediate = true)
public class ContentReferenceViewInitializer implements ModuleChoiceListInitializer{
    private static final Logger logger = LoggerFactory.getLogger(ContentReferenceViewInitializer.class);

    private static final String endpointPath = "/api/jahia/views";
    /**
     * Choicelist initializer key
     */
    private static final String INITIALIZER_KEY = "nextjsContentReferenceViewInitializer";
    private String key;


    /**
     * City service API
     */
    private TemplateService templateService;

    @Activate
    public void onActivate() {
        setKey(INITIALIZER_KEY);
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * @param templateService template service API
     */
    @Reference(service = TemplateService.class)
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public List<ChoiceListValue> getChoiceListValues(ExtendedPropertyDefinition epd, String param, List<ChoiceListValue> values, Locale locale, Map<String, Object> context) {
        List<ChoiceListValue> choiceListValues = new ArrayList<>();

        StringBuilder endpoint = new StringBuilder();
        URI uri;
        try {
            JCRNodeWrapper node = (JCRNodeWrapper) context.get("contextNode");
            if(node == null)
                return choiceListValues;

//            String referenceNodeType = node.getProperty("j:node").getNode().getPrimaryNodeType().getName();
            JCRNodeWrapper referenceNode = node.getProperty("j:node").getContextualizedNode();
            List<String> mixinTypes = Arrays.stream(referenceNode.getMixinNodeTypes())
                    .map(extNodeType -> extNodeType.getName())
                    .collect(Collectors.toList());

            JCRSiteNode siteNode = node.getResolveSite();


            String endpointHost  = siteNode.getProperty("j:nextjsHost").getValue().toString();
            String endpointSecret  = siteNode.getProperty("j:nextjsPreviewSecret").getValue().toString();

            if(endpointHost == null || endpointHost.length() == 0){
                logger.error("*** Nextjs frontend server url not configured ***");
                return choiceListValues;
            }

            endpoint.append(endpointHost).append(endpointPath);
            URIBuilder builder = new URIBuilder(endpoint.toString());
            builder.setParameter("secret", endpointSecret);
            JSONObject typesJSON = new JSONObject();
            typesJSON.put("nodeType",  referenceNode.getPrimaryNodeTypeName());
            typesJSON.put("mixinTypes",  new JSONArray(mixinTypes));
            builder.setParameter("nodetypes", typesJSON.toString());

            uri = builder.build();

        } catch (JSONException | RepositoryException | URISyntaxException e){
            logger.error("Error happened", e);
            return choiceListValues;
        }

        Template[] templates;
        templates = templateService.getTemplates(uri);
        return Stream.of(templates).map(template -> new ChoiceListValue(template.getDisplayName(), template.getName())).collect(Collectors.toList());
    }

}
