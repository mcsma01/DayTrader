/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.geronimo.axis2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.naming.Context;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;

import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingHelper;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.engine.DependencyManager;
import org.apache.axis2.jaxws.binding.BindingImpl;
import org.apache.axis2.jaxws.description.builder.WsdlComposite;
import org.apache.axis2.jaxws.description.builder.WsdlGenerator;
import org.apache.axis2.jaxws.javaee.HandlerChainsType;
import org.apache.axis2.jaxws.server.JAXWSMessageReceiver;
import org.apache.axis2.transport.OutTransportInfo;
import org.apache.axis2.transport.RequestResponseTransport;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HTTPTransportReceiver;
import org.apache.axis2.transport.http.HTTPTransportUtils;
import org.apache.axis2.util.JavaUtils;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.jaxws.JAXWSAnnotationProcessor;
import org.apache.geronimo.jaxws.JNDIResolver;
import org.apache.geronimo.jaxws.PortInfo;
import org.apache.geronimo.jaxws.ServerJNDIResolver;
import org.apache.geronimo.jaxws.annotations.AnnotationException;
import org.apache.geronimo.webservices.WebServiceContainer;
import org.apache.ws.commons.schema.XmlSchema;


public class Axis2WebServiceContainer implements WebServiceContainer {

    private static final Log log = LogFactory.getLog(Axis2WebServiceContainer.class);

    public static final String REQUEST = Axis2WebServiceContainer.class.getName() + "@Request";
    public static final String RESPONSE = Axis2WebServiceContainer.class.getName() + "@Response";

    private transient final ClassLoader classLoader;
    private final String endpointClassName;
    private final org.apache.geronimo.jaxws.PortInfo portInfo;
    private ConfigurationContext configurationContext;
    private String contextRoot = null;
    private Map servicesMap;
    private JNDIResolver jndiResolver;
    private JAXWSAnnotationProcessor annotationProcessor;
    private Object endpointInstance;
    private List<Handler> chain;
    private AxisService service;
    private URL configurationBaseUrl;

    public Axis2WebServiceContainer(PortInfo portInfo,
                                    String endpointClassName,
                                    ClassLoader classLoader,
                                    Context context,
                                    URL configurationBaseUrl) {
        this.classLoader = classLoader;
        this.endpointClassName = endpointClassName;
        this.portInfo = portInfo;
        this.configurationBaseUrl = configurationBaseUrl;
        try {
            configurationContext = ConfigurationContextFactory.createDefaultConfigurationContext();
            configurationContext.setServicePath(portInfo.getLocation());
            
            if(portInfo.getWsdlFile() != null && !portInfo.getWsdlFile().equals("")){ //WSDL file Has been provided
                AxisServiceGenerator serviceGen = new AxisServiceGenerator();
                service = serviceGen.getServiceFromWSDL(portInfo, endpointClassName, configurationBaseUrl, classLoader);
                                            
            }else { //No WSDL, Axis2 will handle it. Is it ?
                service = AxisService.createService(endpointClassName, configurationContext.getAxisConfiguration(), JAXWSMessageReceiver.class);
            }

            service.setScope(Constants.SCOPE_APPLICATION);
            configurationContext.getAxisConfiguration().addService(service);

        } catch (AxisFault af) {
            throw new RuntimeException(af);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jndiResolver = new ServerJNDIResolver(context);
    }  

    public void getWsdl(Request request, Response response) throws Exception {
        doService(request, response);
    }

    public void invoke(Request request, Response response) throws Exception {
        doService(request, response);
    }

    protected void doService(final Request request, final Response response)
            throws Exception {
        initContextRoot(request);

        if (log.isDebugEnabled()) {
            log.debug("Target URI: " + request.getURI());
        }

        MessageContext msgContext = new MessageContext();
        msgContext.setIncomingTransportName(Constants.TRANSPORT_HTTP);
        msgContext.setProperty(MessageContext.REMOTE_ADDR, request.getRemoteAddr());
        

        try {
            TransportOutDescription transportOut = this.configurationContext.getAxisConfiguration()
                    .getTransportOut(new QName(Constants.TRANSPORT_HTTP));
            TransportInDescription transportIn = this.configurationContext.getAxisConfiguration()
                    .getTransportIn(new QName(Constants.TRANSPORT_HTTP));
            
            

            msgContext.setConfigurationContext(this.configurationContext);

            //TODO: Port this segment for session support.
//            String sessionKey = (String) this.httpcontext.getAttribute(HTTPConstants.COOKIE_STRING);
//            if (this.configurationContext.getAxisConfiguration().isManageTransportSession()) {
//                SessionContext sessionContext = this.sessionManager.getSessionContext(sessionKey);
//                msgContext.setSessionContext(sessionContext);
//            }
            msgContext.setTransportIn(transportIn);
            msgContext.setTransportOut(transportOut);
            msgContext.setServiceGroupContextId(UUIDGenerator.getUUID());
            msgContext.setServerSide(true);
            

//            // set the transport Headers
//            HashMap headerMap = new HashMap();
//            for (Iterator it = request.headerIterator(); it.hasNext();) {
//                Header header = (Header) it.next();
//                headerMap.put(header.getName(), header.getValue());
//            }
//            msgContext.setProperty(MessageContext.TRANSPORT_HEADERS, headerMap);
//
//            this.httpcontext.setAttribute(AxisParams.MESSAGE_CONTEXT, msgContext);

            doService2(request, response, msgContext);
        } catch (Throwable e) {
            try {
                AxisEngine engine = new AxisEngine(this.configurationContext);

                msgContext.setProperty(MessageContext.TRANSPORT_OUT, response.getOutputStream());
                msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new Axis2TransportInfo(response));

                MessageContext faultContext = MessageContextBuilder.createFaultMessageContext(msgContext, e);
                // If the fault is not going along the back channel we should be 202ing
                if (AddressingHelper.isFaultRedirected(msgContext)) {
                    response.setStatusCode(202);
                } else {
                    response.setStatusCode(500);
                    response.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, "text/plain");
                    PrintWriter pw = new PrintWriter(response.getOutputStream());
                    e.printStackTrace(pw);
                    pw.flush();
                    String msg = "Exception occurred while trying to invoke service method doService()";
                    log.error(msg, e);
                }
                engine.sendFault(faultContext);
            } catch (Exception ex) {
                if (AddressingHelper.isFaultRedirected(msgContext)) {
                    response.setStatusCode(202);
                } else {
                    response.setStatusCode(500);
                    response.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, "text/plain");
                    PrintWriter pw = new PrintWriter(response.getOutputStream());
                    ex.printStackTrace(pw);
                    pw.flush();
                    String msg = "Exception occurred while trying to invoke service method doService()";
                    log.error(msg, ex);
                }
            }
        }

    }

    private void initContextRoot(Request request) {
        if (contextRoot == null || "".equals(contextRoot)) {
            String[] parts = JavaUtils.split(request.getContextPath(), '/');
            if (parts != null) {
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].length() > 0) {
                        contextRoot = parts[i];
                        break;
                    }
                }
            }
            if (contextRoot == null || request.getContextPath().equals("/")) {
                contextRoot = "/";
            }
            configurationContext.setContextRoot(contextRoot);
            
//            Parameter servicePath = new Parameter(Constants.PARAM_SERVICE_PATH, new String(""));
            
        }
    }

    public void doService2(
            final Request request,
            final Response response,
            final MessageContext msgContext) throws Exception {

        ConfigurationContext configurationContext = msgContext.getConfigurationContext();
        final String servicePath = configurationContext.getServiceContextPath();
        final String contextPath = (servicePath.startsWith("/") ? servicePath : "/" + servicePath);

        URI uri = request.getURI();
        String path = uri.getPath();
        String soapAction = request.getHeader(HTTPConstants.HEADER_SOAP_ACTION);
        
        AxisService service = findServiceWithEndPointClassName(configurationContext, endpointClassName);
        String serviceName = service.getName();

        // TODO: Port this section
//        // Adjust version and content chunking based on the config
//        boolean chunked = false;
//        TransportOutDescription transportOut = msgContext.getTransportOut();
//        if (transportOut != null) {
//            Parameter p = transportOut.getParameter(HTTPConstants.PROTOCOL_VERSION);
//            if (p != null) {
//                if (HTTPConstants.HEADER_PROTOCOL_10.equals(p.getValue())) {
//                    ver = HttpVersion.HTTP_1_0;
//                }
//            }
//            if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
//                p = transportOut.getParameter(HTTPConstants.HEADER_TRANSFER_ENCODING);
//                if (p != null) {
//                    if (HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED.equals(p.getValue())) {
//                        chunked = true;
//                    }
//                }
//            }
//        }
        

        if (request.getMethod() == Request.GET) {
            if (!path.startsWith(contextPath)) {
                response.setStatusCode(301);
                response.setHeader("Location", contextPath);
                return;
            }
            if (uri.toString().indexOf("?") < 0) {
                if (!path.endsWith(contextPath)) {
                    if (serviceName.indexOf("/") < 0) {
                        String res = HTTPTransportReceiver.printServiceHTML(serviceName, configurationContext);
                        PrintWriter pw = new PrintWriter(response.getOutputStream());
                        pw.write(res);
                        pw.flush();
                        return;
                    }
                }
            }
            
            //TODO: Has to implement 
            if (uri.getQuery().startsWith("wsdl2")) {
                if (service != null) {
                    service.printWSDL2(response.getOutputStream());
                    return;
                }
            }
            if (uri.getQuery().startsWith("wsdl")) {
                if (portInfo.getWsdlFile() != null && !portInfo.getWsdlFile().equals("")) { //wsdl file has been provided
                    Definition wsdlDefinition = new AxisServiceGenerator().getWSDLDefition(portInfo, configurationBaseUrl, classLoader);
                    if(wsdlDefinition != null){
                        WSDLFactory factory = WSDLFactory.newInstance();
                        WSDLWriter writer = factory.newWSDLWriter();                    
                        writer.writeWSDL(wsdlDefinition, response.getOutputStream());
                        return;
                    }
                }else {
                    service.printWSDL(response.getOutputStream());
                    return;
                }
            }
            //TODO: Not working properly and do we need to have these requests ?
            if (uri.getQuery().startsWith("xsd=")) {
                String schemaName = uri.getQuery().substring(uri.getQuery().lastIndexOf("=") + 1);

                if (service != null) {
                    //run the population logic just to be sure
                    service.populateSchemaMappings();
                    //write out the correct schema
                    Map schemaTable = service.getSchemaMappingTable();
                    final XmlSchema schema = (XmlSchema) schemaTable.get(schemaName);
                    //schema found - write it to the stream
                    if (schema != null) {
                        schema.write(response.getOutputStream());
                        return;
                    } else {
                        // no schema available by that name  - send 404
                        response.setStatusCode(404);
                        return;
                    }
                }                
            }
            //cater for named xsds - check for the xsd name
            if (uri.getQuery().startsWith("xsd")) {
                if (service != null) {
                    response.setContentType("text/xml");
                    response.setHeader("Transfer-Encoding", "chunked");
                    service.printSchema(response.getOutputStream());
                    response.getOutputStream().close();
                    return;
                }
            }

            msgContext.setProperty(MessageContext.TRANSPORT_OUT, response.getOutputStream());
            msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new Axis2TransportInfo(response));

            // deal with GET request
//   Temporary hack ... Looks like AXIS2 removed this method.  Commented out for now and 
//   replaced with a simple setting of the processed boolean below
//          boolean processed = HTTPTransportUtils.processHTTPGetRequest(
//                  msgContext,
//                  response.getOutputStream(),
//                  soapAction,
//                  path,
//                  configurationContext,
//                  HTTPTransportReceiver.getGetRequestParameters(path));
            boolean processed = false;

            if (!processed) {
                response.setStatusCode(200);
                String s = HTTPTransportReceiver.getServicesHTML(configurationContext);
                PrintWriter pw = new PrintWriter(response.getOutputStream());
                pw.write(s);
                pw.flush();
            }

        } else if (request.getMethod() == Request.POST) {
            msgContext.setAxisService(service);
            configurationContext.fillServiceContextAndServiceGroupContext(msgContext);
            ServiceGroupContext serviceGroupContext = msgContext.getServiceGroupContext();
            DependencyManager.initService(serviceGroupContext);
            endpointInstance = msgContext.getServiceContext().getProperty(ServiceContext.SERVICE_OBJECT);

            BindingImpl binding = new BindingImpl("GeronimoBinding");
            binding.setHandlerChain(chain);
            msgContext.setProperty(JAXWSMessageReceiver.PARAM_BINDING, binding);
            // deal with POST request
            msgContext.setProperty(MessageContext.TRANSPORT_OUT, response.getOutputStream());
            msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new Axis2TransportInfo(response));
            msgContext.setAxisService(service);
            msgContext.setProperty(RequestResponseTransport.TRANSPORT_CONTROL,
                    new Axis2RequestResponseTransport(response));
            msgContext.setProperty(Constants.Configuration.TRANSPORT_IN_URL, request.getURI().toString());
            msgContext.setIncomingTransportName(Constants.TRANSPORT_HTTP);

            HttpServletRequest servletRequest =
                (HttpServletRequest)request.getAttribute(WebServiceContainer.SERVLET_REQUEST);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, servletRequest);

            HttpServletResponse servletResponse =
                (HttpServletResponse)request.getAttribute(WebServiceContainer.SERVLET_RESPONSE);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, servletResponse);

            ServletContext servletContext =
                (ServletContext)request.getAttribute(WebServiceContainer.SERVLET_CONTEXT);
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT, servletContext);
            
            String contenttype = request.getHeader(HTTPConstants.HEADER_CONTENT_TYPE);

            annotationProcessor = new JAXWSAnnotationProcessor(jndiResolver,
                    new Axis2WebServiceContext(msgContext));

            init();
            try {
                HTTPTransportUtils.processHTTPPostRequest(
                        msgContext,
                        request.getInputStream(),
                        response.getOutputStream(),
                        contenttype,
                        soapAction,
                        path);
            } finally {
                stop();
            }
        } else {
            throw new UnsupportedOperationException("[" + request.getMethod() + " ] method not supported");
        }

        // Finalize response
        OperationContext operationContext = msgContext.getOperationContext();
        Object contextWritten = null;
        Object isTwoChannel = null;
        if (operationContext != null) {
            contextWritten = operationContext.getProperty(Constants.RESPONSE_WRITTEN);
            isTwoChannel = operationContext.getProperty(Constants.DIFFERENT_EPR);
        }


        if ((contextWritten != null) && Constants.VALUE_TRUE.equals(contextWritten)) {
            if ((isTwoChannel != null) && Constants.VALUE_TRUE.equals(isTwoChannel)) {
                response.setStatusCode(202);
                return;
            }
            response.setStatusCode(200);
        } else {
            response.setStatusCode(202);
        }
    }
    
    public void destroy() {
    }
    
    /**
     * Resolves the Axis Service associated with the endPointClassName
     * @param cfgCtx
     * @param endPointClassName
     */
    private AxisService findServiceWithEndPointClassName(ConfigurationContext cfgCtx, String endPointClassName) {

        // Visit all the AxisServiceGroups.
        Iterator svcGrpIter = cfgCtx.getAxisConfiguration().getServiceGroups();
        while (svcGrpIter.hasNext()) {

            // Visit all the AxisServices
            AxisServiceGroup svcGrp = (AxisServiceGroup) svcGrpIter.next();
            Iterator svcIter = svcGrp.getServices();
            while (svcIter.hasNext()) {
                AxisService service = (AxisService) svcIter.next();

                // Grab the Parameter that stores the ServiceClass.
                String epc = (String)service.getParameter("ServiceClass").getValue();
                
                if (epc != null) {
                    // If we have a match, then just return the AxisService now.
                    if (endPointClassName.equals(epc)) {
                        return service;
                    }
                }
            }
        }
        return null;
    }    
    
    public class Axis2TransportInfo implements OutTransportInfo {
        private Response response;

        public Axis2TransportInfo(Response response) {
            this.response = response;
        }

        public void setContentType(String contentType) {
            response.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, contentType);
        }
    }
    
    class Axis2RequestResponseTransport implements RequestResponseTransport
    {
      private Response response;
      private CountDownLatch responseReadySignal = new CountDownLatch(1);
      RequestResponseTransportStatus status = RequestResponseTransportStatus.INITIAL;
      
      Axis2RequestResponseTransport(Response response)
      {
        this.response = response;
}     
      public void acknowledgeMessage(MessageContext msgContext) throws AxisFault
      {
        if (log.isDebugEnabled()) {
            log.debug("acknowledgeMessage");
        }
         
        if (log.isDebugEnabled()) {
            log.debug("Acking one-way request");
        }

        response.setContentType("text/xml; charset="
                                + msgContext.getProperty("message.character-set-encoding"));
        
        response.setStatusCode(202);
        try
        {
          response.flushBuffer();
        }
        catch (IOException e)
        {
          throw new AxisFault("Error sending acknowledgement", e);
        }
        
        signalResponseReady();
      }
      
      public void awaitResponse() throws InterruptedException
      {
        if (log.isDebugEnabled()) {
            log.debug("Blocking servlet thread -- awaiting response");
        }
        status = RequestResponseTransportStatus.WAITING;
        responseReadySignal.await();
      }

      public void signalResponseReady()
      {
        if (log.isDebugEnabled()) {
            log.debug("Signalling response available");
        }
        status = RequestResponseTransportStatus.SIGNALLED;
        responseReadySignal.countDown();
      }

      public RequestResponseTransportStatus getStatus() {
        return status;
      }
    }
    
    class WSDLGeneratorImpl implements WsdlGenerator {

        private Definition def;
        
        public WSDLGeneratorImpl(Definition def) {
            this.def = def;
        }
        
        public WsdlComposite generateWsdl(String implClass, String bindingType) throws WebServiceException {
            // Need WSDL generation code
            WsdlComposite composite = new WsdlComposite();
            composite.setWsdlFileName(implClass);
            HashMap<String, Definition> testMap = new HashMap<String, Definition>();
            testMap.put(composite.getWsdlFileName(), def);
            composite.setWsdlDefinition(testMap);
            return composite;
        }
    }

    protected void init() {
        // configure and inject handlers
        try {
            configureHandlers();
        } catch (Exception e) {
            throw new WebServiceException("Error configuring handlers", e);
        }

        // inject resources into service
        try {
            injectResources(endpointInstance);
        } catch (AnnotationException e) {
            throw new WebServiceException("Service resource injection failed", e);
        }
    }

    /*
     * Gets the right handlers for the port/service/bindings and
     * performs injection.
     */
    protected void configureHandlers() throws Exception {
        HandlerChainsType handlerChains = this.portInfo.getHandlers(HandlerChainsType.class);
        Axis2HandlerResolver handlerResolver =
            new Axis2HandlerResolver(endpointInstance.getClass().getClassLoader(),
                                   endpointInstance.getClass(),
                                   handlerChains,
                                   this.annotationProcessor);


        // TODO: pass non-null PortInfo to get the right handlers
        chain = handlerResolver.getHandlerChain(null);
    }

    public void stop() {
        // call handlers preDestroy
        for (Handler handler : chain) {
            this.annotationProcessor.invokePreDestroy(handler);
        }

        // call service preDestroy
        this.annotationProcessor.invokePreDestroy(endpointInstance);
    }

    private void injectResources(Object instance) throws AnnotationException {
        this.annotationProcessor.processAnnotations(instance);
        this.annotationProcessor.invokePostConstruct(instance);
    }

}
