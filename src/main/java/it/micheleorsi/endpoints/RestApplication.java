/**
 * 
 */
package it.micheleorsi.endpoints;

import it.micheleorsi.endpoints.services.FetchService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author micheleorsi
 *
 */
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(FetchService.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.VoidVoidDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.HttpReqResDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.MultipartFormDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.FormDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.EntityParamDispatchProvider.class);
//        s.add(com.sun.jersey.spi.inject.InjectableProvider.class);
        s.add(com.sun.jersey.core.impl.provider.xml.SAXParserContextProvider.class);
        s.add(com.sun.jersey.core.impl.provider.xml.XMLStreamReaderContextProvider.class);
        s.add(com.sun.jersey.core.impl.provider.xml.DocumentBuilderFactoryProvider.class);
        s.add(com.sun.jersey.core.impl.provider.xml.TransformerFactoryProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.VoidVoidDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.HttpReqResDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.MultipartFormDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.FormDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.model.method.dispatch.EntityParamDispatchProvider.class);
        s.add(com.sun.jersey.server.impl.container.filter.NormalizeFilter.class);
        s.add(com.sun.jersey.core.impl.provider.entity.StringProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.ByteArrayProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.FileProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.InputStreamProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.DataSourceProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.RenderedImageProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.FormProvider.class);
        s.add(com.sun.jersey.core.impl.provider.entity.FormMultivaluedMapProvider.class);
        return s;
    }
}
