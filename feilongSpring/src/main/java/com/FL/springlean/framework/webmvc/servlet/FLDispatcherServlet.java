package com.FL.springlean.framework.webmvc.servlet;

import com.FL.springlean.framework.annotation.FLController;
import com.FL.springlean.framework.annotation.FLRequestMapping;
import com.FL.springlean.framework.annotation.FLRequestParam;
import com.FL.springlean.framework.beans.FLBeanWrapper;
import com.FL.springlean.framework.context.FLWebApplicationContext;
import com.FL.springlean.framework.webmvc.FLHandlerAdapter;
import com.FL.springlean.framework.webmvc.FLHandlerMapping;
import com.FL.springlean.framework.webmvc.FLModelAndView;
import com.FL.springlean.framework.webmvc.FLViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FLDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";
    FLWebApplicationContext applicationContext = null;

    private List<FLHandlerMapping> handlerMappings = new ArrayList<>();
    private Map<FLHandlerMapping, FLHandlerAdapter> handlerAdapterMap = new HashMap<>();
    private List<FLViewResolver> viewResolvers = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        resp.getWriter().write("this is a hello world");
        try {
            doDispatch(req, resp);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String strLocation = config.getInitParameter(LOCATION);
        applicationContext = new FLWebApplicationContext(strLocation);
        initStrategies(applicationContext);

    }

    private void initStrategies(FLWebApplicationContext context) {
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现
    }

    private void initHandlerMappings(FLWebApplicationContext context) {
        String[] beanNames = context.getBeanDefinitions();
        for (String beanName : beanNames) {
            FLBeanWrapper beanWrapper = (FLBeanWrapper) context.getBean(beanName);

            if (!beanWrapper.get_originalBean().getClass().isAnnotationPresent(FLController.class)) {
                continue;
            }
            Class<?> clazz = beanWrapper.get_originalBean().getClass();
            String strBaseUrl = "";
            if (clazz.isAnnotationPresent(FLRequestMapping.class)) {
                FLRequestMapping classRM = clazz.getAnnotation(FLRequestMapping.class);
                strBaseUrl = classRM.value().trim();
            }

            //Controller扫描之后,接着扫描其Method
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(FLRequestMapping.class)) {
                    continue;
                }

                FLRequestMapping methodRM = method.getAnnotation(FLRequestMapping.class);
                String methodUrl = methodRM.value().trim();
                String strTotalUrl = ("/" + strBaseUrl + methodUrl.replaceAll("\\*", ".*")).replaceAll("/+", "/");
                this.handlerMappings.add(new FLHandlerMapping(beanWrapper.get_originalBean(), method, Pattern.compile(strTotalUrl)));
                System.out.println("Mapping: " + strTotalUrl + " , " + method);
            }


        }
    }

    private void initHandlerAdapters(FLWebApplicationContext context) {
        for (FLHandlerMapping handlerMapping : this.handlerMappings) {
            Method method = handlerMapping.getMethod();
            Map<String, Integer> methodParamMapping = new HashMap<>();

            Annotation[][] paras = method.getParameterAnnotations();

            //先处理命名参数
            for (int i = 0; i < paras.length; i++) {
                for (Annotation a : paras[i]) {
                    if (a instanceof FLRequestParam) {
                        String paraName = ((FLRequestParam) a).value().trim();
                        methodParamMapping.put(paraName, i);
                        break;
                    }
                }
            }

            //再处理request, response参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpServletRequest.class || parameterTypes[i] == HttpServletResponse.class) {
                    //记录下method形参中request,response两种参数的位置
                    methodParamMapping.put(parameterTypes[i].getName(), i);
                }
            }


            this.handlerAdapterMap.put(handlerMapping, new FLHandlerAdapter(handlerMapping, methodParamMapping));
            System.out.println("initHandlerAdapters: " + methodParamMapping + " , " + method);
        }
    }

    private void initViewResolvers(FLWebApplicationContext context) {
        String fileRootPath = context.getConfig().getProperty("templateRoot");
        String absolutePaths = this.getClass().getClassLoader().getResource(fileRootPath).getFile();

        File viewDirectory = new File(absolutePaths);
        for (File file : viewDirectory.listFiles()) {
            this.viewResolvers.add(new FLViewResolver(file.getName(), file));
        }
    }


    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws InstantiationException, IllegalAccessException {
        FLHandlerMapping handlerMapping = getHandler(request, response);
        if(handlerMapping==null){
            try {
                response.getWriter().write("404 not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        FLHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        FLModelAndView viewAndModel = handlerAdapter.handler(request, response);
        processDispatcherResult(request, response, viewAndModel);
    }

    private void processDispatcherResult(HttpServletRequest request, HttpServletResponse response, FLModelAndView viewAndModel) {

        try {
            if (viewAndModel == null) {
                response.getWriter().write("404 not found, please try again");
                return;
            }
            for (FLViewResolver viewResolver : this.viewResolvers) {
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

    private FLHandlerAdapter getHandlerAdapter(FLHandlerMapping handlerMapping) {
        if (handlerMapping == null) {
            return null;
        }
        return this.handlerAdapterMap.get(handlerMapping);
    }


    private FLHandlerMapping getHandler(HttpServletRequest request, HttpServletResponse response) {
        if (this.handlerMappings.size() < 1) {
            return null;
        }

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replace("/+", "/");
        for (FLHandlerMapping handlerMapping : this.handlerMappings) {
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
