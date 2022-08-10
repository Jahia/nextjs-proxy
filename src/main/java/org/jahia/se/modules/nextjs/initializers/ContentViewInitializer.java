package org.jahia.se.modules.nextjs.initializers;

import org.apache.http.client.utils.URIBuilder;
import org.jahia.se.modules.nextjs.services.Template;
import org.jahia.se.modules.nextjs.services.TemplateService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(service = ModuleChoiceListInitializer.class, immediate = true)
public class ContentViewInitializer implements ModuleChoiceListInitializer{
    private static final Logger logger = LoggerFactory.getLogger(ContentViewInitializer.class);

    private static final String endpointPath = "/api/jahia/views";
    /**
     * Choicelist initializer key
     */
    private static final String INITIALIZER_KEY = "nextjsContentViewInitializer";
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
            JCRNodeWrapper node = (JCRNodeWrapper)
                    ((context.get("contextParent") != null)
                            ? context.get("contextParent")
                            : context.get("contextNode"));

            JCRSiteNode siteNode = node.getResolveSite();
            ExtendedNodeType nodetype = (ExtendedNodeType) context.get("contextType");

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
            typesJSON.put("nodeType",  nodetype.getName());
            typesJSON.put("mixinTypes",  new JSONArray(nodetype.getMixinExtendNames()));
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
