//package smakc.statemachineproviders;
//
//
//public class StateMachineFactory {
//	
//	private StateMachineLoader sml;
//	
//	protected StateMachineFactory(String provider) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
//		String pack = this.getClass().getPackage().getName();
//		pack += "." + provider;
//		sml = (StateMachineLoader)Class.forName(pack + ".LoaderImpl").newInstance();
//	}
//	
//	public static StateMachineFactory createFactory(String provider){
//		try{
//			return new StateMachineFactory(provider);
//		} catch (Exception e){
//			throw new InstantiationError("The specified State Machine provider does not exist. Are you sure it is in the correct pakcage (smakc.statemachineproviders." + provider + ")? Did you add it to the classpath?");
//		}
//	}
//	
//	public de.cau.cs.kieler.synccharts.State load(String source){
//		return sml.load(source);
//	}
//
//}
