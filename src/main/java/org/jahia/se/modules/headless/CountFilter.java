package org.jahia.se.modules.headless;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component(service = RenderFilter.class)
public class CountFilter extends AbstractFilter {

    @Activate
    public void activate() {
        setPriority(3);
        setApplyOnConfigurations("module");
    }


    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (renderContext.getRequest().getAttribute("gqlNpm.depthLimit") != null) {
            Deque<String> current = (Deque<String>) renderContext.getRequest().getAttribute("renderStack");
            if (current == null) {
                current = new ArrayDeque<>();
                renderContext.getRequest().setAttribute("renderStack", current);
            }
            current.push(resource.getPath());

            if (current.size() > (int) renderContext.getRequest().getAttribute("gqlNpm.depthLimit")) {
                return "";
            }
        }
        return super.prepare(renderContext, resource, chain);
    }

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (renderContext.getRequest().getAttribute("gqlNpm.depthLimit") != null) {
            Deque<String> current = (Deque<String>) renderContext.getRequest().getAttribute("renderStack");
            current.pop();
        }
        return super.execute(previousOut, renderContext, resource, chain);
    }
}
