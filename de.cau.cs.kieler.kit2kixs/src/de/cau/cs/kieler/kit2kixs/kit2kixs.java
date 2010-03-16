package de.cau.cs.kieler.kit2kixs;




import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.fast.utilities.settings.CommandLineArgs;

import de.cau.cs.kieler.synccharts.Region;
import de.cau.cs.kieler.synccharts.State;
import de.cau.cs.kieler.synccharts.SyncchartsFactory;

public class kit2kixs {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("kit2kix");
        if (args.length == 0) {
            System.out.println("For syntax please refer to the smakc! settings documentation.");

        } else {
            // check inputs
            CommandLineArgs cla = new CommandLineArgs(args);
            if (args.length == 0 || cla.retrieve("help", "h") != null) {
                System.out.println("usage: kit2kix file.kit.");
            }
            String fileName = args[0];

            String outdir = "";

            File input = new File(fileName);
            if (!input.exists()) {
                System.out.println("The file " + fileName + " does not exist.");
            } else if (!input.isFile()) {
                System.out
                        .println("The file "
                                + fileName
                                + " is in fact not a regular file, it's probably a directory, named pipe or system device. Please specify a regular file.");
            } else if (!input.canRead()) {
                System.out.println("The file " + fileName
                        + " is not accessible. Please chekc if you have permission to access it.");

            } else {
              //  StateMachineFactory smf;

                if (fileName.contains(".")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                System.out.println("generate" + fileName  + ".kixs");
                
                //smf = StateMachineFactory.createFactory("kit");

               State root = LoaderImpl.load(fileName + ".kit");
                // machines.add(root);

                ResourceSet resourceSet = new ResourceSetImpl();

                // Register the default resource factory -- only needed for stand-alone!
                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("kixs",
                        new XMIResourceFactoryImpl());

                // Get the URI of the model file.
                URI fileURI = URI.createFileURI(new File(fileName + ".kixs").getAbsolutePath());

                // Create a resource for this file.
                Resource resource = resourceSet.createResource(fileURI);

                SyncchartsFactory sf = SyncchartsFactory.eINSTANCE;

                Region rootRegion = sf.createRegion();

                rootRegion.getInnerStates().add(root);

                resource.getContents().add(rootRegion);

                // Save the contents of the resource to the file system.
                try {
                    resource.save(Collections.EMPTY_MAP); // the map can pass special saving
                    // options to the operation
                } catch (IOException e) {
                    /* error handling */
                }
                System.out.println("done");
            }
        }
    }
}
