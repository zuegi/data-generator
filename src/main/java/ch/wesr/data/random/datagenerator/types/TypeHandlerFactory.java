/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.wesr.data.random.datagenerator.types;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 *
 * @author andrewserff
 */
public class TypeHandlerFactory {

    public static final String RANDOM_DATAGENERATOR_TYPES = "ch.wesr.data.random.datagenerator.types";
    private static final Logger log = LoggerFactory.getLogger(TypeHandlerFactory.class);

    private static TypeHandlerFactory instance;
    private Map<String, Class> typeHandlerNameMap;
    private Map<String, TypeHandler> typeHandlerCache;

    private static final ThreadLocal<TypeHandlerFactory> localInstance = new ThreadLocal<TypeHandlerFactory>(){
        protected TypeHandlerFactory initialValue() {
            return new TypeHandlerFactory();
        }
    };
    
    private TypeHandlerFactory() {
        typeHandlerNameMap = new LinkedHashMap<>();
        typeHandlerCache = new LinkedHashMap<>();
        scanForTypeHandlers();
    }

    public static TypeHandlerFactory getInstance() {
        return localInstance.get();
    }

    private void scanForTypeHandlers() {
        Reflections reflections = new Reflections(RANDOM_DATAGENERATOR_TYPES);
        Set<Class<? extends TypeHandler>> subTypes = reflections.getSubTypesOf(TypeHandler.class);
        for (Class type : subTypes) {
            //first, make sure we aren't trying to create an abstract class
            if (Modifier.isAbstract(type.getModifiers())) {
                continue;
            }
            try {
                Object o = type.newInstance();
                Method nameMethod = o.getClass().getMethod("getName");
                nameMethod.setAccessible(true);

                String typeHandlerName = (String) nameMethod.invoke(o);
                typeHandlerNameMap.put(typeHandlerName, type);
                log.debug("Discovered TypeHandler [ " + typeHandlerName + "," + type.getName() + " ]");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                log.warn("Error instantiating TypeHandler class [ " + type.getName() + " ]. It will not be available during processing.", ex);
            }
        }
    }

    public TypeHandler getTypeHandler(String name, Map<String, Object> knownValues, String currentContext) throws IllegalArgumentException {
        if (name.contains("(")) {
            String typeName = name.substring(0, name.indexOf("("));
            String args = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
            String[] helperArgs = {};
            if (!args.isEmpty()) {
                helperArgs = args.split(",");
                helperArgs = prepareStrings(helperArgs);
            }

            List<String> resolvedArgs = new ArrayList<>();
            for (String arg : helperArgs) {
                if (arg.startsWith("this.") || arg.startsWith("cur.")) {
                    String refPropName = null;
                    if (arg.startsWith("this.")) {
                        refPropName = arg.substring("this.".length(), arg.length());
                    } else if (arg.startsWith("cur.")) {
                        refPropName = currentContext + arg.substring("cur.".length(), arg.length());
                    }
                    Object refPropValue = knownValues.get(refPropName);
                    if (refPropValue != null) {
                        if (Date.class.isAssignableFrom(refPropValue.getClass())) {
                            resolvedArgs.add(BaseDateType.INPUT_DATE_FORMAT.get().format((Date)refPropValue));
                        } else {
                            resolvedArgs.add(refPropValue.toString());
                        }
                    } else {
                        log.warn("Sorry, unable to reference property [ " + refPropName + " ]. Maybe it hasn't been generated yet?");
                    }
                } else {
                    resolvedArgs.add(arg);
                }
            }
            TypeHandler handler = typeHandlerCache.get(typeName);
            if (handler == null) {
                Class handlerClass = typeHandlerNameMap.get(typeName);
                if (handlerClass != null) {
                    try {
                        handler = (TypeHandler) handlerClass.newInstance();
                        handler.setLaunchArguments(resolvedArgs.toArray(new String[]{}));

                        typeHandlerCache.put(typeName, handler);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        log.warn("Error instantiating TypeHandler class [ " + handlerClass.getName() + " ]", ex);
                    }

                }
            } else {
                handler.setLaunchArguments(resolvedArgs.toArray(new String[]{}));
            }

            return handler;
        } else {
            //not a type handler
            return null;
        }
    }

    public static String[] prepareStrings(String[] list) {
        List<String> newList = new ArrayList<>();
        for (String item : list) {
            newList.add(item.trim());
        }
        return newList.toArray(new String[]{});
    }

    public static void main(String[] args) {
        Map<String, Object> vals = new LinkedHashMap<>();
        TypeHandler random = TypeHandlerFactory.getInstance().getTypeHandler("random('one', 'two', 'three')", vals, "");
        if (random == null) {
            log.error("error getting handler");
        } else {
            log.info("success! random value: " + random.getNextRandomValue());
        }
    }
}
