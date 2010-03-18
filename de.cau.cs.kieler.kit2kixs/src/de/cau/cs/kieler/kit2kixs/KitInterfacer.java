package de.cau.cs.kieler.kit2kixs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import kiel.util.kit.analysis.DepthFirstAdapter;
import kiel.util.kit.node.ABooleanVarEventType;
import kiel.util.kit.node.ACargument;
import kiel.util.kit.node.AChart;
import kiel.util.kit.node.ACollapsedSview;
import kiel.util.kit.node.ACompositeState;
import kiel.util.kit.node.AConditionalTranstype;
import kiel.util.kit.node.ACstate;
import kiel.util.kit.node.ADeclaration;
import kiel.util.kit.node.ADoActionStateargument;
import kiel.util.kit.node.ADoubleVarEventType;
import kiel.util.kit.node.AEntryActionStateargument;
import kiel.util.kit.node.AEvent;
import kiel.util.kit.node.AExitActionStateargument;
import kiel.util.kit.node.AFalseTBoolean;
import kiel.util.kit.node.AFinalPseudo;
import kiel.util.kit.node.AFirstcarguments;
import kiel.util.kit.node.AFirstrarguments;
import kiel.util.kit.node.AFirstsarguments;
import kiel.util.kit.node.AFirsttransarguments;
import kiel.util.kit.node.AFloatVarEventType;
import kiel.util.kit.node.AHeightSview;
import kiel.util.kit.node.AInitialTransition;
import kiel.util.kit.node.AInitialvalue;
import kiel.util.kit.node.AInputDeclTyp;
import kiel.util.kit.node.AIntegerAddVarEventType;
import kiel.util.kit.node.AIntegerMultVarEventType;
import kiel.util.kit.node.AIntegerVarEventType;
import kiel.util.kit.node.AInternalTransitionTranstype;
import kiel.util.kit.node.AIodeclaration;
import kiel.util.kit.node.ALabelRegionargument;
import kiel.util.kit.node.ALabelStateargument;
import kiel.util.kit.node.ALabelTransargument;
import kiel.util.kit.node.ALabelposTview;
import kiel.util.kit.node.ALastcargument;
import kiel.util.kit.node.ALastrargument;
import kiel.util.kit.node.ALastsargument;
import kiel.util.kit.node.ALasttransargument;
import kiel.util.kit.node.AModelChartargument;
import kiel.util.kit.node.ANewEventRegionargument;
import kiel.util.kit.node.ANewEventStateargument;
import kiel.util.kit.node.ANewRegionElement;
import kiel.util.kit.node.ANewVariableRegionargument;
import kiel.util.kit.node.ANewVariableStateargument;
import kiel.util.kit.node.ANormalTranstype;
import kiel.util.kit.node.AOtherTransition;
import kiel.util.kit.node.AOutputDeclTyp;
import kiel.util.kit.node.APathTview;
import kiel.util.kit.node.APchoicePseudo;
import kiel.util.kit.node.APdeephistoryPseudo;
import kiel.util.kit.node.APdynamicPseudo;
import kiel.util.kit.node.APforkPseudo;
import kiel.util.kit.node.APhistoryPseudo;
import kiel.util.kit.node.APinitialPseudo;
import kiel.util.kit.node.APjoinPseudo;
import kiel.util.kit.node.APjunctionPseudo;
import kiel.util.kit.node.APosSview;
import kiel.util.kit.node.APrioposTview;
import kiel.util.kit.node.APriorityTransargument;
import kiel.util.kit.node.APsuspendPseudo;
import kiel.util.kit.node.APsyncPseudo;
import kiel.util.kit.node.ARargument;
import kiel.util.kit.node.ARegion;
import kiel.util.kit.node.ASargument;
import kiel.util.kit.node.ASimpleState;
import kiel.util.kit.node.AStateElement;
import kiel.util.kit.node.AStrongTranstype;
import kiel.util.kit.node.ASuspensionTranstype;
import kiel.util.kit.node.ATargument;
import kiel.util.kit.node.ATransitionElement;
import kiel.util.kit.node.ATrueTBoolean;
import kiel.util.kit.node.ATypeStateargument;
import kiel.util.kit.node.ATypeTransargument;
import kiel.util.kit.node.AVarDeclTyp;
import kiel.util.kit.node.AVariable;
import kiel.util.kit.node.AVersionChartargument;
import kiel.util.kit.node.AViewRegionargument;
import kiel.util.kit.node.AViewStateargument;
import kiel.util.kit.node.AViewTransargument;
import kiel.util.kit.node.AVtype;
import kiel.util.kit.node.AWeakTranstype;
import kiel.util.kit.node.AWidthSview;
import kiel.util.kit.node.Node;
import kiel.util.kit.node.Start;

import org.fast.utilities.exceptions.NotImplementedException;

import de.cau.cs.kieler.synccharts.Region;
import de.cau.cs.kieler.synccharts.Signal;
import de.cau.cs.kieler.synccharts.State;
import de.cau.cs.kieler.synccharts.StateType;
import de.cau.cs.kieler.synccharts.SyncchartsFactory;
import de.cau.cs.kieler.synccharts.Transition;
import de.cau.cs.kieler.synccharts.TransitionType;

public class KitInterfacer extends DepthFirstAdapter {

    private SyncchartsFactory sf = SyncchartsFactory.eINSTANCE;
    private HashMap<String, State> statecache = new HashMap<String, State>();
    public State root;
    private Stack<Region> currentregion = new Stack<Region>();
    private Stack<State> currentstate = new Stack<State>();
    private Transition currenttrans = null;
    private int declarationtype = -1;
    private boolean suspend = false;

    // private long id;
    private static final int INPUT = 0;
    private static final int OUTPUT = 1;

    private int id;

    private int nextId() {
        id++;
        return id - 1;
    }

    private final boolean debug = false;

    private void printRegion() {
        if (debug) {
            System.out.print("regions: {");
            Iterator<Region> i = currentregion.iterator();
            while (i.hasNext()) {
                Region r = i.next();
                if (r.getParentState() != null) {
                    System.out.print(r.getParentState().getId() + ".");
                }
                System.out.print(r.getId() + ", ");
            }
            System.out.println(")");

        }
    }

    private void printStates() {
        if (debug) {
            System.out.print("states: {");
            Iterator<State> i = currentstate.iterator();
            while (i.hasNext()) {
                System.out.print(i.next().getId() + ", ");
            }
            System.out.println(")");

        }
    }

    private State getOrCreateNew(String name, boolean composite) {
        name = name.replace("#", "");
        name = name.replace(" ", "");
        if (!statecache.containsKey(name)) {
            if (debug) {
                System.out.println("create " + name);
            }
            State state = sf.createState();
            state.setId(name);
            state.setLabel(name);
            if (!currentregion.isEmpty()) {
                currentregion.peek().getInnerStates().add(state);
            }
            printStates();
            statecache.put(name, state);
        }
        State res = statecache.get(name);
        if (composite && res.getRegions().isEmpty()) {
            Region region = sf.createRegion();
            region.setId("R0");
            region.setParentState(res);
            res.getRegions().add(region);
            currentregion.push(region);
            printRegion();
        }
        return res;
    }

    @Override
    public void caseABooleanVarEventType(ABooleanVarEventType node) {
        super.caseABooleanVarEventType(node);
    }

    @Override
    public void caseACargument(ACargument node) {
        super.caseACargument(node);
    }

    @Override
    public void caseAChart(AChart node) {
        root = sf.createState();
        root.setId(node.getIdentifier().getText());
        root.setLabel(root.getId());
        Region region = sf.createRegion();
        region.setId("main");
        currentregion.push(region);
        printRegion();
        region.setParentState(root);
        root.getRegions().add(region);
        currentstate.push(root);
        printStates();
        super.caseAChart(node);
    }

    @Override
    public void caseACompositeState(ACompositeState node) {
        super.caseACompositeState(node);
    }

    @Override
    public void caseACstate(ACstate node) {
        // System.out.println(node.getClass().getSimpleName());
        // State s = sf.createState();
        // currentregion.peek().getInnerStates().add(s);
        // s.setId(node.g)
        super.caseACstate(node);
    }

    @Override
    public void caseADeclaration(ADeclaration node) {
        if (!node.getIdentifier().getText().equalsIgnoreCase("tick")) {
            Signal sig = sf.createSignal();
            sig.setName(node.getIdentifier().getText());

            if (declarationtype == INPUT) {
                sig.setIsInput(true);
            } else if (declarationtype == OUTPUT) {
                sig.setIsOutput(true);
            }

            currentstate.peek().getSignals().add(sig);

        }
        super.caseADeclaration(node);
    }

    @Override
    public void caseADoActionStateargument(ADoActionStateargument node) {
        /*
         * String sigs = node.toString().substring(node.toString().indexOf('"')+
         * 1,node.toString().lastIndexOf('"')); Vector<InstantaneousAction> vec = new
         * Vector<InstantaneousAction>(); Vector<Signal> signals = new Vector<Signal>(); for (String
         * sig : sigs.split(",")){ signals.add(new KitSignal(sig.trim())); } vec.add(new
         * KitAction(signals,InstantaneousAction.IN_STATE));
         */
        // currentstate.peek().addInStateAction(vec);
        super.caseADoActionStateargument(node);
    }

    @Override
    public void caseADoubleVarEventType(ADoubleVarEventType node) {
        super.caseADoubleVarEventType(node);
    }

    @Override
    public void caseAEntryActionStateargument(AEntryActionStateargument node) {

        /*
         * String sigs = node.toString().substring(node.toString().indexOf('"')+
         * 1,node.toString().lastIndexOf('"')); Vector<InstantaneousAction> vec = new
         * Vector<InstantaneousAction>(); Vector<Signal> signals = new Vector<Signal>(); for (String
         * sig : sigs.split(",")){ signals.add(new KitSignal(sig.trim())); } vec.add(new
         * KitAction(signals,InstantaneousAction.ON_ENTRY));
         */
        // currentstate.peek().addStateEntryActions(vec);
        super.caseAEntryActionStateargument(node);
    }

    @Override
    public void caseAEvent(AEvent node) {
        State state = currentstate.peek();
        Signal sig = sf.createSignal();
        sig.setName(node.getString().getText().replace("\"", ""));
        state.getSignals().add(sig);
        super.caseAEvent(node);
    }

    @Override
    public void caseAExitActionStateargument(AExitActionStateargument node) {
        /*
         * String sigs = node.toString().substring(node.toString().indexOf('"')+
         * 1,node.toString().lastIndexOf('"')); Vector<InstantaneousAction> vec = new
         * Vector<InstantaneousAction>(); Vector<Signal> signals = new Vector<Signal>(); for (String
         * sig : sigs.split(",")){ signals.add(new KitSignal(sig.trim())); }
         */
        // vec.add(new KitAction(signals,InstantaneousAction.ON_EXIT));
        // currentstate.peek().addStateExitActions(vec);
        super.caseAExitActionStateargument(node);
    }

    @Override
    public void caseAFalseTBoolean(AFalseTBoolean node) {
        super.caseAFalseTBoolean(node);
    }

    @Override
    public void caseAFinalPseudo(AFinalPseudo node) {
        super.caseAFinalPseudo(node);
    }

    @Override
    public void caseAFirstcarguments(AFirstcarguments node) {
        super.caseAFirstcarguments(node);
    }

    @Override
    public void caseAFirstrarguments(AFirstrarguments node) {
        super.caseAFirstrarguments(node);
    }

    @Override
    public void caseAFirstsarguments(AFirstsarguments node) {
        super.caseAFirstsarguments(node);
    }

    @Override
    public void caseAFirsttransarguments(AFirsttransarguments node) {
        // System.out.println(node.getClass().getSimpleName() + ": " +
        // node.getTransargument().toString());
        super.caseAFirsttransarguments(node);
    }

    @Override
    public void caseAFloatVarEventType(AFloatVarEventType node) {
        super.caseAFloatVarEventType(node);
    }

    @Override
    public void caseAHeightSview(AHeightSview node) {
        super.caseAHeightSview(node);
    }

    @Override
    public void caseAInitialTransition(AInitialTransition node) {
        // System.out.println(node.getClass().getSimpleName() + ": " +
        // node.getTargetState().getText());
        super.caseAInitialTransition(node);
    }

    @Override
    public void caseAInitialvalue(AInitialvalue node) {
        super.caseAInitialvalue(node);
    }

    @Override
    public void caseAInputDeclTyp(AInputDeclTyp node) {
        declarationtype = INPUT;
        super.caseAInputDeclTyp(node);
    }

    @Override
    public void caseAIntegerAddVarEventType(AIntegerAddVarEventType node) {
        super.caseAIntegerAddVarEventType(node);
    }

    @Override
    public void caseAIntegerMultVarEventType(AIntegerMultVarEventType node) {
        super.caseAIntegerMultVarEventType(node);
    }

    @Override
    public void caseAIntegerVarEventType(AIntegerVarEventType node) {
        super.caseAIntegerVarEventType(node);
    }

    @Override
    public void caseAInternalTransitionTranstype(AInternalTransitionTranstype node) {
        // System.out.println(node.getClass().getSimpleName() + ": " +
        // node.getInternal().getText());
        super.caseAInternalTransitionTranstype(node);
    }

    @Override
    public void caseAIodeclaration(AIodeclaration node) {
        super.caseAIodeclaration(node);
    }

    @Override
    public void caseALabelposTview(ALabelposTview node) {
        super.caseALabelposTview(node);
    }

    @Override
    public void caseALabelRegionargument(ALabelRegionargument node) {
        super.caseALabelRegionargument(node);
    }

    @Override
    public void caseALabelStateargument(ALabelStateargument node) {
        super.caseALabelStateargument(node);
    }

    @Override
    public void caseALabelTransargument(ALabelTransargument node) {

        String label = node.getString().getText().replace('\"', ' ');
        label = label.replace('[', '(');
        label = label.replace("]", ")");

        label = label.replace("tick", "");

        // TODO: replace by true as soon as simulator can handle this

        if (currenttrans.isIsImmediate() && !label.contains("#")) {
            label = "#" + label;
        }

        currenttrans.setTriggersAndEffects(label);

        super.caseALabelTransargument(node);
    }

    @Override
    public void caseALastcargument(ALastcargument node) {
        super.caseALastcargument(node);
    }

    @Override
    public void caseALastrargument(ALastrargument node) {
        super.caseALastrargument(node);
    }

    @Override
    public void caseALastsargument(ALastsargument node) {
        super.caseALastsargument(node);
    }

    @Override
    public void caseALasttransargument(ALasttransargument node) {
        super.caseALasttransargument(node);
    }

    @Override
    public void caseAModelChartargument(AModelChartargument node) {
        super.caseAModelChartargument(node);
    }

    @Override
    public void caseANewEventRegionargument(ANewEventRegionargument node) {
        super.caseANewEventRegionargument(node);
    }

    @Override
    public void caseANewEventStateargument(ANewEventStateargument node) {
        super.caseANewEventStateargument(node);
    }

    @Override
    public void caseANewRegionElement(ANewRegionElement node) {
        // System.out.println(node.getClass().getSimpleName() + ": " + node.getRegion().toString());
        super.caseANewRegionElement(node);
    }

    @Override
    public void caseANewVariableRegionargument(ANewVariableRegionargument node) {
        // System.out.println(node.getClass().getSimpleName() + ": " +
        // node.getVariable().toString());
        super.caseANewVariableRegionargument(node);
    }

    @Override
    public void caseANewVariableStateargument(ANewVariableStateargument node) {
        super.caseANewVariableStateargument(node);
    }

    @Override
    public void caseANormalTranstype(ANormalTranstype node) {
        currenttrans.setType(TransitionType.NORMALTERMINATION);
        super.caseANormalTranstype(node);
    }

    @Override
    public void caseAOtherTransition(AOtherTransition node) {
        super.caseAOtherTransition(node);
    }

    @Override
    public void caseAOutputDeclTyp(AOutputDeclTyp node) {
        declarationtype = OUTPUT;
        super.caseAOutputDeclTyp(node);
    }

    @Override
    public void caseAPathTview(APathTview node) {
        super.caseAPathTview(node);
    }

    @Override
    public void caseAPchoicePseudo(APchoicePseudo node) {
        currentstate.peek().setType(StateType.CONDITIONAL);
        super.caseAPchoicePseudo(node);
    }

    @Override
    public void caseAPdeephistoryPseudo(APdeephistoryPseudo node) {
        super.caseAPdeephistoryPseudo(node);
    }

    @Override
    public void caseAPdynamicPseudo(APdynamicPseudo node) {
        currentstate.peek().setType(StateType.CONDITIONAL);
        super.caseAPdynamicPseudo(node);
    }

    @Override
    public void caseAPforkPseudo(APforkPseudo node) {
        // System.out.println(node.getClass().getSimpleName() + ": " + node.getFork().getText());
        super.caseAPforkPseudo(node);
    }

    @Override
    public void caseAPhistoryPseudo(APhistoryPseudo node) {
        super.caseAPhistoryPseudo(node);
    }

    @Override
    public void caseAPinitialPseudo(APinitialPseudo node) {
        // System.out.println(node.getClass().getSimpleName() + ": " + node.getInitial().getText());
        super.caseAPinitialPseudo(node);
    }

    @Override
    public void caseAPjoinPseudo(APjoinPseudo node) {
        super.caseAPjoinPseudo(node);
    }

    @Override
    public void caseAPjunctionPseudo(APjunctionPseudo node) {
        super.caseAPjunctionPseudo(node);
    }

    @Override
    public void caseAPosSview(APosSview node) {
        super.caseAPosSview(node);
    }

    @Override
    public void caseAPrioposTview(APrioposTview node) {
        super.caseAPrioposTview(node);
    }

    @Override
    public void caseAPriorityTransargument(APriorityTransargument node) {
        int prio = Integer.parseInt(node.getInt().getText().substring(1,
                node.getInt().getText().length() - 1));
        if (currenttrans != null)
            currenttrans.setPriority(prio - 1);
        super.caseAPriorityTransargument(node);
    }

    @Override
    public void caseAPsuspendPseudo(APsuspendPseudo node) {
        // next transition will be a suspend transition
        suspend = true;
        super.caseAPsuspendPseudo(node);
    }

    @Override
    public void caseAPsyncPseudo(APsyncPseudo node) {
        super.caseAPsyncPseudo(node);
    }

    @Override
    public void caseARargument(ARargument node) {
        super.caseARargument(node);
    }

    @Override
    public void caseARegion(ARegion node) {
        /*
         * Region region = sf.createRegion(); region.setId(node.getIdentifier().getText());
         * currentregion.add(region); currentstate.peek().getRegions().add(region);
         */
        super.caseARegion(node);
    }

    @Override
    public void caseASargument(ASargument node) {
        super.caseASargument(node);
    }

    @Override
    public void caseASimpleState(ASimpleState node) {
        // State state getOrCreateNew(node.getIdentifier().getText());
        super.caseASimpleState(node);
    }

    @Override
    public void caseAStateElement(AStateElement node) {
        // System.out.println(node.getClass().getSimpleName() + ": " + node.getState().toString());
        super.caseAStateElement(node);
    }

    @Override
    public void caseAStrongTranstype(AStrongTranstype node) {
        currenttrans.setType(TransitionType.STRONGABORT);
        super.caseAStrongTranstype(node);
    }

    @Override
    public void caseASuspensionTranstype(ASuspensionTranstype node) {
        super.caseASuspensionTranstype(node);
    }

    @Override
    public void caseATargument(ATargument node) {
        super.caseATargument(node);
    }

    @Override
    public void caseATransitionElement(ATransitionElement node) {
        super.caseATransitionElement(node);
    }

    @Override
    public void caseATrueTBoolean(ATrueTBoolean node) {
        super.caseATrueTBoolean(node);
    }

    @Override
    public void caseATypeStateargument(ATypeStateargument node) {
        super.caseATypeStateargument(node);
    }

    @Override
    public void caseATypeTransargument(ATypeTransargument node) {
        super.caseATypeTransargument(node);
    }

    @Override
    public void caseAVarDeclTyp(AVarDeclTyp node) {
        super.caseAVarDeclTyp(node);
    }

    @Override
    public void caseAVariable(AVariable node) {
        super.caseAVariable(node);
    }

    @Override
    public void caseAVersionChartargument(AVersionChartargument node) {
        super.caseAVersionChartargument(node);
    }

    @Override
    public void caseAViewRegionargument(AViewRegionargument node) {
        super.caseAViewRegionargument(node);
    }

    @Override
    public void caseAViewStateargument(AViewStateargument node) {
        super.caseAViewStateargument(node);
    }

    @Override
    public void caseAViewTransargument(AViewTransargument node) {
        super.caseAViewTransargument(node);
    }

    @Override
    public void caseAVtype(AVtype node) {
        super.caseAVtype(node);
    }

    @Override
    public void caseAWeakTranstype(AWeakTranstype node) {

        currenttrans.setType(TransitionType.WEAKABORT);
        super.caseAWeakTranstype(node);
    }

    @Override
    public void caseAWidthSview(AWidthSview node) {
        super.caseAWidthSview(node);
    }

    @Override
    public void caseStart(Start node) {
        super.caseStart(node);
    }

    @Override
    public void defaultIn(Node node) {
        // System.err.println(node.getClass().getSimpleName() + ": " + node.toString());
        super.defaultIn(node);
    }

    @Override
    public void defaultOut(Node node) {
        super.defaultOut(node);
    }

    @Override
    public void inABooleanVarEventType(ABooleanVarEventType node) {
        super.inABooleanVarEventType(node);
    }

    @Override
    public void inACargument(ACargument node) {
        super.inACargument(node);
    }

    @Override
    public void inAChart(AChart node) {
      super.inAChart(node);
    }

    @Override
    public void inACollapsedSview(ACollapsedSview node) {
        super.inACollapsedSview(node);
    }

    @Override
    public void inACompositeState(ACompositeState node) {
        State sta = getOrCreateNew(node.getIdentifier().getText(), true);
        currentstate.push(sta);
        super.inACompositeState(node);
    }

    @Override
    public void inAConditionalTranstype(AConditionalTranstype node) {
        super.inAConditionalTranstype(node);
    }

    @Override
    public void inACstate(ACstate node) {
        super.inACstate(node);
    }

    @Override
    public void inADeclaration(ADeclaration node) {
        super.inADeclaration(node);
    }

    @Override
    public void inADoActionStateargument(ADoActionStateargument node) {
        super.inADoActionStateargument(node);
    }

    @Override
    public void inADoubleVarEventType(ADoubleVarEventType node) {
        super.inADoubleVarEventType(node);
    }

    @Override
    public void inAEntryActionStateargument(AEntryActionStateargument node) {
        super.inAEntryActionStateargument(node);
    }

    @Override
    public void inAEvent(AEvent node) {
        super.inAEvent(node);
    }

    @Override
    public void inAExitActionStateargument(AExitActionStateargument node) {
        super.inAExitActionStateargument(node);
    }

    @Override
    public void inAFalseTBoolean(AFalseTBoolean node) {
        super.inAFalseTBoolean(node);
    }

    @Override
    public void inAFinalPseudo(AFinalPseudo node) {
        currentstate.peek().setIsFinal(true);
        super.inAFinalPseudo(node);
    }

    @Override
    public void inAFirstcarguments(AFirstcarguments node) {
        super.inAFirstcarguments(node);
    }

    @Override
    public void inAInitialTransition(AInitialTransition node) {

        State initial = this.getOrCreateNew("init" + nextId(), false);
        
        initial.setLabel("I");
        initial.setIsInitial(true);
        // currentregion.peek().getInnerStates().add(initial);
        Transition transition = sf.createTransition();
        currenttrans = transition;
        transition.setIsImmediate(true);
        transition.setTriggersAndEffects("# /");
        // transition.setTrigger(sf.createExpression());
        transition.setSourceState(initial);

        State state = getOrCreateNew(node.getTargetState().getText(), false);
        transition.setTargetState(state);

        initial.getOutgoingTransitions().add(transition);
        state.getParentRegion().getInnerStates().add(initial);


        super.inAInitialTransition(node);
    }

    @Override
    public void inANewRegionElement(ANewRegionElement node) {

        Region region = sf.createRegion();
        State s = currentregion.peek().getParentState();
        region.setId("R" + s.getRegions().size());
        region.setParentState(s);
        s.getRegions().add(region);
        currentregion.push(region);
        printRegion();
        super.inANewRegionElement(node);
    }

    @Override
    public void inAOtherTransition(AOtherTransition node) {
        State sta = getOrCreateNew(node.getSourceState().getText(), false);
        State stb = getOrCreateNew(node.getTargetState().getText(), false);
        Transition trans = sf.createTransition();
        trans.setSourceState(sta);
        trans.setTargetState(stb);
        sta.getOutgoingTransitions().add(trans);
        currenttrans = trans;

        super.inAOtherTransition(node);
    }

    @Override
    public void inASimpleState(ASimpleState node) {
        State sta = getOrCreateNew(node.getIdentifier().getText(), false);
        currentstate.push(sta);
        super.inASimpleState(node);
    }

    @Override
    public void outAChart(AChart node) {
        // containerstack.pop();
        // System.out.println("pop " + node.getIdentifier().getText());
        currentstate.pop();
        printStates();

        super.outAChart(node);
    }

    @Override
    public void outACollapsedSview(ACollapsedSview node) {
        super.outACollapsedSview(node);
    }

    @Override
    public void outACompositeState(ACompositeState node) {
        // containerstack.pop();
        State s = currentstate.pop();
        for (int i = 0; i < s.getRegions().size(); i++) {
            currentregion.pop();
        }
        printRegion();
        super.outACompositeState(node);
    }

    @Override
    public void outANewRegionElement(ANewRegionElement node) {
        // currentregion.pop();
        // printRegion();
        super.outANewRegionElement(node);
    }

    @Override
    public void outASimpleState(ASimpleState node) {
        currentstate.pop();
        // System.out.println("pop " + node.getIdentifier().getText());
        printStates();
        super.outASimpleState(node);
    }
}
