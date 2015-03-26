package com.funkdefino.common.util.reflect;

import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * <code>$Id: $</code>
 * Loads objects & invokes methods using reflection.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class Loader {

    //** ---------------------------------------------------------- Construction

    /**
     * Constructor (private).
     */
    private Loader() {
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Loads a class by reflection, where the class is defined by an XML element
     * in the form  <element class = "com.funkdefino.XXXX"/>, and whose constructor
     * has a single XmlElement argument.
     * @param element the element.
     * @return a class instance (or null on error).
     */
    public static Object getTarget(XmlElement element) {
      Loader.ArgumentList argList = new Loader.ArgumentList(XmlElement.class,element);
      return getTarget(element, argList);
    }

    /**
     * Loads a class by reflection, where the class is defined by an XML element
     * in the form  <element class = "com.funkdefino.XXXX"/>
     * @param element the element.
     * @param argList an argument list.
     * @return a class instance (or null on error).
     */
    public static Object getTarget(XmlElement element, Loader.ArgumentList argList)
    {
      Object obj = null;
      String sClass;

      if(element != null) {
         try  {XmlValidate.getAttribute(element, ICConstants.ATTR_CLASS);}
         catch(UtilException excp) {excp.printStackTrace();}
         sClass = element.getAttribute(ICConstants.ATTR_CLASS);
         obj = !sClass.equals("") ? Loader.getTarget(sClass, argList) : null;
      }

      return obj;

    } // loadClass()

    /**
     * Loads a class by reflection.
     * @param className the fully-qualified class name.
     * @param argList an argument list.
     * @return a class instance (or null on error).
     */
    public static Object getTarget(String className, ArgumentList argList)
    {
        Object target = null;
        Class[]  clzParams;
        Object[] objArgs;

        if(argList != null) {
            List list = argList.getList();
            int nSize = list.size();                        // Allocate local parameter
            clzParams = new Class [nSize];                  // and object arrays.
            objArgs   = new Object[nSize];

            for(int i = 0;i < nSize; i++)                   // Extract the argument list
            {                                               // (class and object), and
                Object[] arrObj = (Object[])list.get(i);    // initialise the local
                clzParams[i] = (Class)arrObj[0];            // parameter and object
                objArgs  [i] = arrObj[1];                   // arrays.
            }
        }
        else {
            clzParams = new Class[0];                       // No ctor arguments
            objArgs  = new Object[0];
        }

        try {
            ClassLoader  cl = Thread.currentThread().getContextClassLoader();
            Class clsTarget = Class.forName(className, true, cl );  // Use reflection
            Constructor ctor = clsTarget.getConstructor(clzParams); // to instantiate
            target = ctor.newInstance(objArgs);                     // the target.
        }
        catch(Exception excp) {                             // Catch all exceptions
            excp.printStackTrace();                         // and dump.
        }

        return target;                                      // Return to the caller

    } // getTarget()

    /**
     * Loads a class by reflection.
     * @param className the fully-qualified class name.
     * @return a class instance (or null on error).
     */
    public static Object getTarget(String className) {
        return getTarget(className, null);
    }

    /**
     * Invokes a method on an object instance.
     * @param obj the object.
     * @param sMethod the method name.
     * @param objArgs an argument list.
     * @return the method return.
     * @throws InvocationTargetException on error.
     * @throws IllegalAccessException on error.
     * @throws NoSuchMethodException on error.
     */
    public static Object invokeMethod(Object obj, String sMethod, Object... objArgs)
           throws InvocationTargetException, IllegalAccessException,
                  NoSuchMethodException
    {
      Class clsTarget = obj.getClass();
      if( objArgs == null ) objArgs = new Object[0];
      Class[] clsParams = new Class[objArgs.length];
      for(int i = 0; i < objArgs.length; i++ )
          clsParams[i] = objArgs[i].getClass();
      Method method = clsTarget.getMethod(sMethod, clsParams);
      return method.invoke(obj, objArgs);
    }

    /**
     * Invokes a method on an object instance.
     * @param obj the object.
     * @param sMethod the method name.
     * @param argList an argument list.
     * @return the method return.
     * @throws InvocationTargetException on error.
     * @throws IllegalAccessException on error.
     * @throws NoSuchMethodException on error.
     */
    public static Object invokeMethod(Object obj, String sMethod, ArgumentList argList)
           throws InvocationTargetException, IllegalAccessException,
                  NoSuchMethodException
    {

        Class [] clzParams;
        Object[] objArgs;

        if(argList  == null) {
            clzParams = new Class[0];
            objArgs   = new Object[0];
        }
        else {
            int nSize;
            List list = argList.getList();
            clzParams = new Class[(nSize = list.size())];
            objArgs   = new Object[nSize];
            for(int i = 0; i < nSize; i++) {
                clzParams[i] = (Class)((Object[])list.get(i))[0];
                objArgs[i]   = ((Object[])list.get(i) )[1];
            }
        }

        Method method = obj.getClass().getMethod(sMethod, clzParams);
        return method.invoke(obj, objArgs);

    } // invokeMethod()

    /**
     * Invokes a method on an object instance.
     * @param obj the object.
     * @param sMethod the method name.
     * @return the method return.
     * @throws InvocationTargetException on error.
     * @throws IllegalAccessException on error.
     * @throws NoSuchMethodException on error.
     */
    public static Object invokeMethod(Object obj, String sMethod)
           throws InvocationTargetException, IllegalAccessException,
                  NoSuchMethodException
    {
        return invokeMethod(obj, sMethod, new Object[0]);
    }

    /**
     * Returns a list of method names associated with the incoming object.
     * @param obj the object.
     * @return a list of method names.
     */
    public static List<String> getMethods(Object obj)
    {
        List<String> list = new ArrayList<String>();
        Method[] methods = obj.getClass().getMethods();
        for(Method method : methods) {
            list.add(method.getName());
        }

        return list;

    } // getMethods()

    //** ---------------------------------------------------------------- Nested

    public final static class ArgumentList  {
        private final List<Object> m_list = new ArrayList<Object>();
        public  ArgumentList()                                                    {}
        public  ArgumentList(ArgumentList  argList) {m_list.addAll(argList.m_list);}
        public  ArgumentList(Class cls, Object obj)                  {add(cls,obj);}
        public  void add(Class cls, Object obj) {m_list.add(new Object[]{cls,obj});}
        private List getList()                                      {return m_list;}
    }

} // class Loader
