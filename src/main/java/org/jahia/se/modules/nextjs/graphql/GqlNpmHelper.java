package org.jahia.se.modules.nextjs.graphql;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import org.jahia.bin.Render;
import org.jahia.modules.graphql.provider.dxm.node.GqlJcrNodeInput;
import org.jahia.modules.graphql.provider.dxm.node.GqlJcrPropertyInput;
import org.jahia.modules.graphql.provider.dxm.osgi.annotations.GraphQLOsgiService;
import org.jahia.modules.graphql.provider.dxm.util.ContextUtil;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.*;
import org.jahia.services.uicomponents.bean.editmode.EditConfiguration;
import org.jahia.utils.LanguageCodeConverters;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

public class GqlNpmHelper {
    private JCRSessionFactory jcrSessionFactory;
    private JCRTemplate jcrTemplate;
    private RenderService renderService;

    @Inject
    @GraphQLOsgiService
    public void setJcrSessionFactory(JCRSessionFactory jcrSessionFactory) {
        this.jcrSessionFactory = jcrSessionFactory;
    }

    @Inject
    @GraphQLOsgiService
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Inject
    @GraphQLOsgiService
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }



//
//    @GraphQLField
//    public String renderNode(
//            @GraphQLName("path") String path,
//            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
//            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
//            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
//            DataFetchingEnvironment environment) throws RepositoryException, RenderException {
//        RenderContext renderContext = getRenderContext(environment);
//
//        JCRNodeWrapper node = jcrSessionFactory.getCurrentUserSession(renderContext.getWorkspace(), renderContext.getMainResource().getLocale()).getNode(path);
//        Resource resource = new Resource(node, templateType != null ? templateType : "html", view, contextConfiguration != null ? contextConfiguration : "module");
//        return renderService.render(resource, getRenderContext(environment));
//    }
//
//    @GraphQLField
//    public String renderModule(
//            @GraphQLName("path") String path,
//            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
//            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
//            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
//            DataFetchingEnvironment environment) throws IllegalAccessException, InvocationTargetException, JspException {
//        return renderTag(new ModuleTag(), getAttr(path, view, templateType, contextConfiguration), environment);
//    }
//
//    @GraphQLField
//    public String renderInclude(
//            @GraphQLName("path") String path,
//            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
//            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
//            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
//            DataFetchingEnvironment environment) throws IllegalAccessException, InvocationTargetException, JspException {
//        return renderTag(new IncludeTag(), getAttr(path, view, templateType, contextConfiguration), environment);
//    }
//
//    @GraphQLField
//    public String renderOption(
//            @GraphQLName("path") String path,
//            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
//            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
//            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
//            DataFetchingEnvironment environment) throws IllegalAccessException, InvocationTargetException, JspException {
//        return renderTag(new OptionTag(), getAttr(path, view, templateType, contextConfiguration), environment);
//    }

    @GraphQLField
    public SimpleRenderedNode getRenderedComponent(
            @GraphQLName("mainResourcePath") String mainResourcePath,
            @GraphQLName("path") String path,
            @GraphQLName("node") GqlJcrNodeInput input,
            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
            @GraphQLName("isEditMode") @GraphQLDescription("Edit mode") Boolean isEditMode,
            @GraphQLName("language") @GraphQLDescription("Language") String language,
            DataFetchingEnvironment environment
    ) {
        try {

            String id = "cache" + (Objects.hash(mainResourcePath, path, view, templateType, input.getName(), input.getPrimaryNodeType(), contextConfiguration, isEditMode, language));
            return new SimpleRenderedNode(id, jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), null, LanguageCodeConverters.languageCodeToLocale(language),
                    session -> getRenderedComponent(mainResourcePath, path, input, view, templateType, contextConfiguration, isEditMode, environment, session)
            ));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return null;
    }

//    private String renderTag(TagSupport tag, Map<String, Object> attr, DataFetchingEnvironment environment) throws IllegalAccessException, InvocationTargetException, JspException {
//        RenderContext renderContext = getRenderContext(environment);
//        BeanUtils.populate(tag, attr);
//
//        renderContext.setServletPath(Render.getRenderServletPath());
//        if (attr.containsKey("path")) {
//            try {
//                renderContext.setSite(JCRSessionFactory.getInstance().getCurrentUserSession().getNode((String) attr.get("path")).getResolveSite());
//            } catch (RepositoryException e) {
//                e.printStackTrace();
//            }
//        }
//
//        MockPageContext pageContext = new MockPageContext(renderContext);
//        tag.setPageContext(pageContext);
//        tag.doStartTag();
//        tag.doEndTag();
//        return pageContext.getTargetWriter().getBuffer().toString();
//    }
//
//    private Map<String, Object> getAttr(String path, String view, String templateType, String contextConfiguration) {
//        Map<String,Object> attr = new HashMap<>();
//        attr.put("path", path);
//        attr.put("template", view);
//        attr.put("templateType", templateType);
//        attr.put("contextConfiguration", contextConfiguration);
//        return attr;
//    }

    private String getRenderedComponent(String mainResourcePath, String path, GqlJcrNodeInput input, String view, String templateType, String contextConfiguration, Boolean isEditMode, DataFetchingEnvironment environment, JCRSessionWrapper session) throws RepositoryException {
        JCRNodeWrapper main = session.getNode(mainResourcePath);
        JCRNodeWrapper parent;
        JCRSessionWrapper systemSession = JCRSessionFactory.getInstance().getCurrentSystemSession(session.getWorkspace().getName(), session.getLocale(), session.getFallbackLocale());
        if (path == null) {
            parent = systemSession.getNode("/").addNode("tmp");
        } else {
            parent = systemSession.getNode(path);
        }

        String name = input.getName();
        if (name == null) {
            name = "temp-node";
        }

        JCRNodeWrapper node = parent.addNode(name, input.getPrimaryNodeType());
        Collection<GqlJcrPropertyInput> properties = input.getProperties();
        if (properties != null) {
            for (GqlJcrPropertyInput property : properties) {
                if (property.getValues() != null) {
                    node.setProperty(property.getName(), property.getValues().toArray(new String[0]));
                } else {
                    node.setProperty(property.getName(), property.getValue());
                }
            }
        }

        if (contextConfiguration == null) {
            contextConfiguration = "module";
        }
        if (templateType == null) {
            templateType = "html";
        }

        Resource r = new Resource(node, templateType, view, contextConfiguration);

        RenderContext renderContext = getRenderContext(environment);
        renderContext.setServletPath(Render.getRenderServletPath());
        renderContext.setSite(main.getResolveSite());
        Resource mainResource = new Resource(main, templateType, null, contextConfiguration);
        renderContext.setMainResource(mainResource);

        Template t = renderService.resolveTemplate(mainResource, renderContext);
        renderContext.getRequest().setAttribute("previousTemplate", t);
        renderContext.getRequest().setAttribute("templateSet", Boolean.TRUE);
        renderContext.getRequest().setAttribute("gqlNpm.depthLimit", 1);

        if (isEditMode != null) {
            renderContext.setEditMode(isEditMode);
            if (isEditMode) {
                renderContext.setEditModeConfig((EditConfiguration) SpringContextSingleton.getBean("editmode"));
            }
        }

        try {
            return renderService.render(r, renderContext);
        } catch (RenderException e) {
            throw new RepositoryException(e);
        }
    }

    private RenderContext getRenderContext(DataFetchingEnvironment environment) {
        HttpServletRequest request = ContextUtil.getHttpServletRequest(environment.getContext());
        HttpServletResponse response = ContextUtil.getHttpServletResponse(environment.getContext());
        if (request == null || response == null) {
            throw new RuntimeException("No HttpRequest or HttpResponse");
        }

        if (request instanceof HttpServletRequestWrapper) {
            request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
        }

        return new RenderContext(request, response, JCRSessionFactory.getInstance().getCurrentUser());
    }

    @GraphQLDescription("Rendering result for a node")
    public static class SimpleRenderedNode {
        private String id;
        private String output;

        public SimpleRenderedNode(String id, String output) {
            this.id = id;
            this.output = output;
        }

        @GraphQLField
        @GraphQLDescription("Rendering output")
        public String getId() {
            return id;
        }

        @GraphQLField
        @GraphQLDescription("Rendering output")
        public String getOutput() {
            return output;
        }


    }

}

