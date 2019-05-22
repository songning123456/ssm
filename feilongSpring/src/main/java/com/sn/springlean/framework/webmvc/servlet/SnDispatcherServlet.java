package com.sn.springlean.framework.webmvc.servlet;

import com.sn.springlean.framework.annotation.SnController;
import com.sn.springlean.framework.annotation.SnRequestMapping;
import com.sn.springlean.framework.annotation.SnRequestParam;
import com.sn.springlean.framework.beans.SnBeanWrapper;
import com.sn.springlean.framework.context.SnWebApplicationContext;
import com.sn.springlean.framework.webmvc.SnHandlerAdapter;
import com.sn.springlean.framework.webmvc.SnHandlerMapping;
import com.sn.springlean.framework.webmvc.SnModelAndView;
import com.sn.springlean.framework.webmvc.SnViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author sn
 */
public class SnDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";
    SnWebApplicationContext applicationContext = null;

    private List<SnHandlerMapping> handlerMappings = new ArrayList<>();
    private Map<SnHandlerMapping, SnHandlerAdapter> handlerAdapterMap = new HashMap<>();
    private List<SnViewResolver> viewResolvers = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req, resp);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String strLocation = config.getInitParameter(LOCATION);
        applicationContext = new SnWebApplicationContext(strLocation);
        initStrategies(applicationContext);

    }

    private void initStrategies(SnWebApplicationContext context) {
        //通过HandlerMapping，将请求映射到处理器
        initHandlerMappings(context);
        //通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerAdapters(context);
        //通过viewResolver解析逻辑视图到具体视图实现
        initViewResolvers(context);
    }

    private void initHandlerMappings(SnWebApplicationContext context) {
        String[] beanNames = context.getBeanDefinitions();
        for (String beanName : beanNames) {
            SnBeanWrapper beanWrapper = (SnBeanWrapper) context.getBean(beanName);

            if (!beanWrapper.getOriginalBean().getClass().isAnnotationPresent(SnController.class)) {
                continue;
            }
            Class<?> clazz = beanWrapper.getOriginalBean().getClass();
            String strBaseUrl = "";
            if (clazz.isAnnotationPresent(SnRequestMapping.class)) {
                SnRequestMapping classRM = clazz.getAnnotation(SnRequestMapping.class);
                strBaseUrl = classRM.value().trim();
            }

            // Controller扫描之后,接着扫描其Method
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(SnRequestMapping.class)) {
                    continue;
                }

                SnRequestMapping methodRM = method.getAnnotation(SnRequestMapping.class);
                String methodUrl = methodRM.value().trim();
                String strTotalUrl = ("/" + strBaseUrl + methodUrl.replaceAll("\\*", ".*")).replaceAll("/+", "/");
                this.handlerMappings.add(new SnHandlerMapping(beanWrapper.getOriginalBean(), method, Pattern.compile(strTotalUrl)));
                System.out.println("Mapping: " + strTotalUrl + " , " + method);
            }


        }
    }

    private void initHandlerAdapters(SnWebApplicationContext context) {
        for (SnHandlerMapping handlerMapping : this.handlerMappings) {
            Method method = handlerMapping.getMethod();
            Map<String, Integer> methodParamMapping = new HashMap<>(10);

            Annotation[][] paras = method.getParameterAnnotations();

            // 先处理命名参数
            for (int i = 0; i < paras.length; i++) {
                for (Annotation a : paras[i]) {
                    if (a instanceof SnRequestParam) {
                        String paraName = ((SnRequestParam) a).value().trim();
                        methodParamMapping.put(paraName, i);
                        break;
                    }
                }
            }

            // 再处理request, response参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpServletRequest.class || parameterTypes[i] == HttpServletResponse.class) {
                    // 记录下method形参中request,response两种参数的位置
                    methodParamMapping.put(parameterTypes[i].getName(), i);
                }
            }


            this.handlerAdapterMap.put(handlerMapping, new SnHandlerAdapter(handlerMapping, methodParamMapping));
            System.out.println("initHandlerAdapters: " + methodParamMapping + " , " + method);
        }
    }

    private void initViewResolvers(SnWebApplicationContext context) {
        String fileRootPath = context.getConfig().getProperty("templateRoot");
        String absolutePaths = Objects.requireNonNull(this.getClass().getClassLoader().getResource(fileRootPath)).getFile();

        File viewDirectory = new File(absolutePaths);
        for (File file : Objects.requireNonNull(viewDirectory.listFiles())) {
            this.viewResolvers.add(new SnViewResolver(file.getName(), file));
        }
    }


    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        SnHandlerMapping handlerMapping = getHandler(request, response);
        if (handlerMapping == null) {
            try {
                response.getWriter().write("404 not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        SnHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        SnModelAndView viewAndModel = handlerAdapter.handler(request, response);
        processDispatcherResult(request, response, viewAndModel);
    }

    private void processDispatcherResult(HttpServletRequest request, HttpServletResponse response, SnModelAndView viewAndModel) {

        try {
            if (viewAndModel == null) {
                response.getWriter().write("404 not found, please try again");
                return;
            }
            for (SnViewResolver viewResolver : this.viewResolvers) {
                if (viewAndModel.getViewName().equals(viewResolver.getViewName())) {
                    String strResult = viewResolver.processViews(viewAndModel);
                    strResult = strResult == null ? "" : strResult;
                    response.getWriter().write(strResult);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SnHandlerAdapter getHandlerAdapter(SnHandlerMapping handlerMapping) {
        if (handlerMapping == null) {
            return null;
        }
        return this.handlerAdapterMap.get(handlerMapping);
    }


    private SnHandlerMapping getHandler(HttpServletRequest request, HttpServletResponse response) {
        if (this.handlerMappings.size() < 1) {
            return null;
        }

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replace("/+", "/");
        for (SnHandlerMapping handlerMapping : this.handlerMappings) {
            if (handlerMapping.getUrlPattern().matcher(url).matches()) {
                return handlerMapping;
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }
}
