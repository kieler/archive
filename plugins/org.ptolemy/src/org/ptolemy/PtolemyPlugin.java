package org.ptolemy;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class PtolemyPlugin extends AbstractUIPlugin {

    public PtolemyPlugin() {
        System.out.println("=================================================================");
        System.out.println("=================================================================");
        // TODO Auto-generated constructor stub
    }



////get the available interfaces and initialize them
//IConfigurationElement[] jsonComponents = Platform.getExtensionRegistry()
//       .getConfigurationElementsFor(Messages.extensionPointIDjsoncomponent);
//
//IConfigurationElement[] stringComponents = Platform.getExtensionRegistry()
//       .getConfigurationElementsFor(Messages.extensionPointIDstringcomponent);
//
//dataComponentList = new ArrayList<AbstractDataComponent>(jsonComponents.length
//       + stringComponents.length);
//
//for (int i = 0; i < jsonComponents.length; i++) {
//   try {
//       System.out.println("KIEM loading component: "
//               + jsonComponents[i].getContributor().getName());
//       JSONObjectDataComponent dataComponent = (JSONObjectDataComponent) jsonComponents[i]
//               .createExecutableExtension("class");
//       dataComponent.setConfigurationElemenet(jsonComponents[i]);
//       dataComponentList.add(dataComponent);
//   } catch (Exception e) {
//       // throw new RuntimeException
//       // ("Error at loading a KIEM data component plugin");
//       this.showWarning(Messages.mWarningLoadingDataComponent.replace("%COMPONENTNAME",
//               jsonComponents[i].getContributor().getName()), null, e, false);
//   }
//}

//http://blog.dhananjaynene.com/2008/01/dynamically-extend-classpath-during-unit-testing/

//1. public class TestUtils  
//2. {  
//3.     public static void addURL(Class clazz)  
//4.     {  
//5.         try  
//6.         {  
//7.   
//8.             URLClassLoader sysloader = (URLClassLoader) ClassLoader  
//9.                     .getSystemClassLoader();  
//10.             Class<urlclassloader> sysclass = URLClassLoader.class;  
//11.   
//12.             String path = sysloader.getResource(  
//13.                     clazz.getCanonicalName().replace('.', '/') + ".class")  
//14.                     .getPath();  
//15.             int lastSlash = path.lastIndexOf('/');  
//16.             path = path.substring(0, lastSlash+1);  
//17.             URL url = new URL("file://" + path);  
//18.   
//19.             Method method = sysclass.getDeclaredMethod("addURL", URL.class);  
//20.             method.setAccessible(true);  
//21.             method.invoke(sysloader, new Object[] { url });  
//22.         }  
//23.         catch (Throwable t)  
//24.         {  
//25.             t.printStackTrace();  
//26.         }  
//27.     }  
//28. }  
//29. </urlclassloader>  

//
//1. public class MyTest  
//2. {  
//3.     @BeforeClass  
//4.     public static void initialiseClass()  
//5.     {  
//6.         addURL(MyTest.class);  
//7.     }  
//8. }  

}
