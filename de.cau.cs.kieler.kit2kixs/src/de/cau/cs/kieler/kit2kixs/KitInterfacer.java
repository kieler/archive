package de.cau.cs.kieler.kit2kixs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

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

/*import smakc.compiler.interfaces.ssm.Arc;
import smakc.compiler.interfaces.ssm.CombinationOperator;
import smakc.compiler.interfaces.ssm.ConditionalExpression;
import smakc.compiler.interfaces.ssm.InstantaneousAction;
import smakc.statemachineproviders.kit.wrappers.KitAction;
import smakc.statemachineproviders.kit.wrappers.KitBinaryExpression;
import smakc.statemachineproviders.kit.wrappers.KitExpression;
import smakc.statemachineproviders.kit.wrappers.KitInitialTransition;
import smakc.statemachineproviders.kit.wrappers.KitTransition;*/

public class KitInterfacer extends DepthFirstAdapter {

    private SyncchartsFactory sf = SyncchartsFactory.eINSTANCE;
    private HashMap<String, State> statecache = new HashMap<String, State>();
    // private Stack<State> stack = new Stack<State>();
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

    private void printStates() {
        /*
         * System.out.print("states: {"); Iterator<State> i = currentstate.iterator(); while
         * (i.hasNext()) { System.out.print(i.next().getId() + ", "); } System.out.println(")");
         */
    }

    private State getOrCreateNew(String name, boolean composite) {
        name = name.replace("#", "");
        name = name.replace(" ", "");
        if (!statecache.containsKey(name)) {
            // System.out.println("create " + name);
            State state = sf.createState();
            state.setId(name);
            state.setLabel(name);
            if (!currentregion.isEmpty()) {
                currentregion.peek().getInnerStates().add(state);
            }
            currentstate.push(state);
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
        currentregion.push(region);
        region.setParentState(root);
        root.getRegions().add(region);
        currentstate.push(root);
        super.caseAChart(node);
    }

    @Override
    public void caseACollapsedSview(ACollapsedSview node) {
        super.caseACollapsedSview(node);
    }

    @Override
    public void caseACompositeState(ACompositeState node) {
        // getOrCreateNew(node.getIdentifier().getText(), true);
        super.caseACompositeState(node);
    }

    @Override
    public void caseAConditionalTranstype(AConditionalTranstype node) {
        super.caseAConditionalTranstype(node);
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
        /*
         * State sta = getOrCreateNew(node.getIdentifier().getText(), true);
         * containerstack.push(sta); currentstate.push(sta); super.inAChart(node);
         */
    }

    @Override
    public void inACollapsedSview(ACollapsedSview node) {
        super.inACollapsedSview(node);
    }

    @Override
    public void inACompositeState(ACompositeState node) {
        State sta = getOrCreateNew(node.getIdentifier().getText(), true);
        // containerstack.push(sta);
        // currentstate.push(sta);
        // currentregion.push(sta.getRegions().get(0));
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
    public void inAFirstrarguments(AFirstrarguments node) {
        super.inAFirstrarguments(node);
    }

    @Override
    public void inAFirstsarguments(AFirstsarguments node) {
        super.inAFirstsarguments(node);
    }

    @Override
    public void inAFirsttransarguments(AFirsttransarguments node) {
        super.inAFirsttransarguments(node);
    }

    @Override
    public void inAFloatVarEventType(AFloatVarEventType node) {
        super.inAFloatVarEventType(node);
    }

    @Override
    public void inAHeightSview(AHeightSview node) {
        super.inAHeightSview(node);
    }

    @Override
    public void inAInitialTransition(AInitialTransition node) {

        State initial = this.getOrCreateNew("init" + nextId(), false);
        initial.setLabel("I");
        initial.setIsInitial(true);
        currentregion.peek().getInnerStates().add(initial);
        Transition transition = sf.createTransition();
        transition.setIsImmediate(true);
        transition.setSourceState(initial);

        State state = getOrCreateNew(node.getTargetState().getText(), false);
        transition.setTargetState(state);
        initial.getOutgoingTransitions().add(transition);

        super.inAInitialTransition(node);
    }

    @Override
    public void inAInitialvalue(AInitialvalue node) {
        super.inAInitialvalue(node);
    }

    @Override
    public void inAInputDeclTyp(AInputDeclTyp node) {
        super.inAInputDeclTyp(node);
    }

    @Override
    public void inAIntegerAddVarEventType(AIntegerAddVarEventType node) {
        super.inAIntegerAddVarEventType(node);
    }

    @Override
    public void inAIntegerMultVarEventType(AIntegerMultVarEventType node) {
        super.inAIntegerMultVarEventType(node);
    }

    @Override
    public void inAIntegerVarEventType(AIntegerVarEventType node) {
        super.inAIntegerVarEventType(node);
    }

    @Override
    public void inAInternalTransitionTranstype(AInternalTransitionTranstype node) {
        super.inAInternalTransitionTranstype(node);
    }

    @Override
    public void inAIodeclaration(AIodeclaration node) {
        super.inAIodeclaration(node);
    }

    @Override
    public void inALabelposTview(ALabelposTview node) {
        super.inALabelposTview(node);
    }

    @Override
    public void inALabelRegionargument(ALabelRegionargument node) {
        super.inALabelRegionargument(node);
    }

    @Override
    public void inALabelStateargument(ALabelStateargument node) {
        super.inALabelStateargument(node);
    }

    @Override
    public void inALabelTransargument(ALabelTransargument node) {
        super.inALabelTransargument(node);
    }

    @Override
    public void inALastcargument(ALastcargument node) {
        super.inALastcargument(node);
    }

    @Override
    public void inALastrargument(ALastrargument node) {
        super.inALastrargument(node);
    }

    @Override
    public void inALastsargument(ALastsargument node) {
        super.inALastsargument(node);
    }

    @Override
    public void inALasttransargument(ALasttransargument node) {
        super.inALasttransargument(node);
    }

    @Override
    public void inAModelChartargument(AModelChartargument node) {
        super.inAModelChartargument(node);
    }

    @Override
    public void inANewEventRegionargument(ANewEventRegionargument node) {
        super.inANewEventRegionargument(node);
    }

    @Override
    public void inANewEventStateargument(ANewEventStateargument node) {
        super.inANewEventStateargument(node);
    }

    @Override
    public void inANewRegionElement(ANewRegionElement node) {

        Region region = sf.createRegion();
        State s = currentstate.peek();
        region.setId("R" + s.getRegions().size());
        region.setParentState(s);
        s.getRegions().add(region);
        currentregion.push(region);
        super.inANewRegionElement(node);
    }

    @Override
    public void inANewVariableRegionargument(ANewVariableRegionargument node) {
        super.inANewVariableRegionargument(node);
    }

    @Override
    public void inANewVariableStateargument(ANewVariableStateargument node) {
        super.inANewVariableStateargument(node);
    }

    @Override
    public void inANormalTranstype(ANormalTranstype node) {
        super.inANormalTranstype(node);
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
    public void inAOutputDeclTyp(AOutputDeclTyp node) {
        super.inAOutputDeclTyp(node);
    }

    @Override
    public void inAPathTview(APathTview node) {
        super.inAPathTview(node);
    }

    @Override
    public void inAPchoicePseudo(APchoicePseudo node) {
        super.inAPchoicePseudo(node);
    }

    @Override
    public void inAPdeephistoryPseudo(APdeephistoryPseudo node) {
        super.inAPdeephistoryPseudo(node);
    }

    @Override
    public void inAPdynamicPseudo(APdynamicPseudo node) {
        super.inAPdynamicPseudo(node);
    }

    @Override
    public void inAPforkPseudo(APforkPseudo node) {
        super.inAPforkPseudo(node);
    }

    @Override
    public void inAPhistoryPseudo(APhistoryPseudo node) {
        super.inAPhistoryPseudo(node);
    }

    @Override
    public void inAPinitialPseudo(APinitialPseudo node) {
        super.inAPinitialPseudo(node);
    }

    @Override
    public void inAPjoinPseudo(APjoinPseudo node) {
        super.inAPjoinPseudo(node);
    }

    @Override
    public void inAPjunctionPseudo(APjunctionPseudo node) {
        super.inAPjunctionPseudo(node);
    }

    @Override
    public void inAPosSview(APosSview node) {
        super.inAPosSview(node);
    }

    @Override
    public void inAPrioposTview(APrioposTview node) {
        super.inAPrioposTview(node);
    }

    @Override
    public void inAPriorityTransargument(APriorityTransargument node) {
        super.inAPriorityTransargument(node);
    }

    @Override
    public void inAPsuspendPseudo(APsuspendPseudo node) {
        super.inAPsuspendPseudo(node);
    }

    @Override
    public void inAPsyncPseudo(APsyncPseudo node) {
        super.inAPsyncPseudo(node);
    }

    @Override
    public void inARargument(ARargument node) {
        super.inARargument(node);
    }

    @Override
    public void inARegion(ARegion node) {

        super.inARegion(node);
    }

    @Override
    public void inASargument(ASargument node) {
        super.inASargument(node);
    }

    @Override
    public void inASimpleState(ASimpleState node) {
        State sta = getOrCreateNew(node.getIdentifier().getText(), false);
        // currentstate.push(sta);
        super.inASimpleState(node);
    }

    @Override
    public void inAStateElement(AStateElement node) {
        super.inAStateElement(node);
    }

    @Override
    public void inAStrongTranstype(AStrongTranstype node) {
        super.inAStrongTranstype(node);
    }

    @Override
    public void inASuspensionTranstype(ASuspensionTranstype node) {
        super.inASuspensionTranstype(node);
    }

    @Override
    public void inATargument(ATargument node) {
        super.inATargument(node);
    }

    @Override
    public void inATransitionElement(ATransitionElement node) {
        super.inATransitionElement(node);
    }

    @Override
    public void inATrueTBoolean(ATrueTBoolean node) {
        super.inATrueTBoolean(node);
    }

    @Override
    public void inATypeStateargument(ATypeStateargument node) {
        super.inATypeStateargument(node);
    }

    @Override
    public void inATypeTransargument(ATypeTransargument node) {
        super.inATypeTransargument(node);
    }

    @Override
    public void inAVarDeclTyp(AVarDeclTyp node) {
        super.inAVarDeclTyp(node);
    }

    @Override
    public void inAVariable(AVariable node) {
        super.inAVariable(node);
    }

    @Override
    public void inAVersionChartargument(AVersionChartargument node) {
        super.inAVersionChartargument(node);
    }

    @Override
    public void inAViewRegionargument(AViewRegionargument node) {
        super.inAViewRegionargument(node);
    }

    @Override
    public void inAViewStateargument(AViewStateargument node) {
        super.inAViewStateargument(node);
    }

    @Override
    public void inAViewTransargument(AViewTransargument node) {
        super.inAViewTransargument(node);
    }

    @Override
    public void inAVtype(AVtype node) {
        super.inAVtype(node);
    }

    @Override
    public void inAWeakTranstype(AWeakTranstype node) {
        super.inAWeakTranstype(node);
    }

    @Override
    public void inAWidthSview(AWidthSview node) {
        super.inAWidthSview(node);
    }

    @Override
    public void inStart(Start node) {
        super.inStart(node);
    }

    @Override
    public void outABooleanVarEventType(ABooleanVarEventType node) {
        super.outABooleanVarEventType(node);
    }

    @Override
    public void outACargument(ACargument node) {
        super.outACargument(node);
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
        super.outACompositeState(node);
    }

    @Override
    public void outAConditionalTranstype(AConditionalTranstype node) {
        super.outAConditionalTranstype(node);
    }

    @Override
    public void outACstate(ACstate node) {
        super.outACstate(node);
    }

    @Override
    public void outADeclaration(ADeclaration node) {
        super.outADeclaration(node);
    }

    @Override
    public void outADoActionStateargument(ADoActionStateargument node) {
        super.outADoActionStateargument(node);
    }

    @Override
    public void outADoubleVarEventType(ADoubleVarEventType node) {
        super.outADoubleVarEventType(node);
    }

    @Override
    public void outAEntryActionStateargument(AEntryActionStateargument node) {
        super.outAEntryActionStateargument(node);
    }

    @Override
    public void outAEvent(AEvent node) {
        super.outAEvent(node);
    }

    @Override
    public void outAExitActionStateargument(AExitActionStateargument node) {
        super.outAExitActionStateargument(node);
    }

    @Override
    public void outAFalseTBoolean(AFalseTBoolean node) {
        super.outAFalseTBoolean(node);
    }

    @Override
    public void outAFinalPseudo(AFinalPseudo node) {
        super.outAFinalPseudo(node);
    }

    @Override
    public void outAFirstcarguments(AFirstcarguments node) {
        super.outAFirstcarguments(node);
    }

    @Override
    public void outAFirstrarguments(AFirstrarguments node) {
        super.outAFirstrarguments(node);
    }

    @Override
    public void outAFirstsarguments(AFirstsarguments node) {
        super.outAFirstsarguments(node);
    }

    @Override
    public void outAFirsttransarguments(AFirsttransarguments node) {
        super.outAFirsttransarguments(node);
    }

    @Override
    public void outAFloatVarEventType(AFloatVarEventType node) {
        super.outAFloatVarEventType(node);
    }

    @Override
    public void outAHeightSview(AHeightSview node) {
        super.outAHeightSview(node);
    }

    @Override
    public void outAInitialTransition(AInitialTransition node) {
        super.outAInitialTransition(node);
    }

    @Override
    public void outAInitialvalue(AInitialvalue node) {
        super.outAInitialvalue(node);
    }

    @Override
    public void outAInputDeclTyp(AInputDeclTyp node) {
        super.outAInputDeclTyp(node);
    }

    @Override
    public void outAIntegerAddVarEventType(AIntegerAddVarEventType node) {
        super.outAIntegerAddVarEventType(node);
    }

    @Override
    public void outAIntegerMultVarEventType(AIntegerMultVarEventType node) {
        super.outAIntegerMultVarEventType(node);
    }

    @Override
    public void outAIntegerVarEventType(AIntegerVarEventType node) {
        super.outAIntegerVarEventType(node);
    }

    @Override
    public void outAInternalTransitionTranstype(AInternalTransitionTranstype node) {
        super.outAInternalTransitionTranstype(node);
    }

    @Override
    public void outAIodeclaration(AIodeclaration node) {
        super.outAIodeclaration(node);
    }

    @Override
    public void outALabelposTview(ALabelposTview node) {
        super.outALabelposTview(node);
    }

    @Override
    public void outALabelRegionargument(ALabelRegionargument node) {
        super.outALabelRegionargument(node);
    }

    @Override
    public void outALabelStateargument(ALabelStateargument node) {
        super.outALabelStateargument(node);
    }

    @Override
    public void outALabelTransargument(ALabelTransargument node) {
        super.outALabelTransargument(node);
    }

    @Override
    public void outALastcargument(ALastcargument node) {
        super.outALastcargument(node);
    }

    @Override
    public void outALastrargument(ALastrargument node) {
        super.outALastrargument(node);
    }

    @Override
    public void outALastsargument(ALastsargument node) {
        super.outALastsargument(node);
    }

    @Override
    public void outALasttransargument(ALasttransargument node) {
        super.outALasttransargument(node);
    }

    @Override
    public void outAModelChartargument(AModelChartargument node) {
        super.outAModelChartargument(node);
    }

    @Override
    public void outANewEventRegionargument(ANewEventRegionargument node) {
        super.outANewEventRegionargument(node);
    }

    @Override
    public void outANewEventStateargument(ANewEventStateargument node) {
        super.outANewEventStateargument(node);
    }

    @Override
    public void outANewRegionElement(ANewRegionElement node) {
        super.outANewRegionElement(node);
    }

    @Override
    public void outANewVariableRegionargument(ANewVariableRegionargument node) {
        super.outANewVariableRegionargument(node);
    }

    @Override
    public void outANewVariableStateargument(ANewVariableStateargument node) {
        super.outANewVariableStateargument(node);
    }

    @Override
    public void outANormalTranstype(ANormalTranstype node) {
        super.outANormalTranstype(node);
    }

    @Override
    public void outAOtherTransition(AOtherTransition node) {
        super.outAOtherTransition(node);
    }

    @Override
    public void outAOutputDeclTyp(AOutputDeclTyp node) {
        super.outAOutputDeclTyp(node);
    }

    @Override
    public void outAPathTview(APathTview node) {
        super.outAPathTview(node);
    }

    @Override
    public void outAPchoicePseudo(APchoicePseudo node) {
        super.outAPchoicePseudo(node);
    }

    @Override
    public void outAPdeephistoryPseudo(APdeephistoryPseudo node) {
        super.outAPdeephistoryPseudo(node);
    }

    @Override
    public void outAPdynamicPseudo(APdynamicPseudo node) {
        super.outAPdynamicPseudo(node);
    }

    @Override
    public void outAPforkPseudo(APforkPseudo node) {
        super.outAPforkPseudo(node);
    }

    @Override
    public void outAPhistoryPseudo(APhistoryPseudo node) {
        super.outAPhistoryPseudo(node);
    }

    @Override
    public void outAPinitialPseudo(APinitialPseudo node) {
        super.outAPinitialPseudo(node);
    }

    @Override
    public void outAPjoinPseudo(APjoinPseudo node) {
        super.outAPjoinPseudo(node);
    }

    @Override
    public void outAPjunctionPseudo(APjunctionPseudo node) {
        super.outAPjunctionPseudo(node);
    }

    @Override
    public void outAPosSview(APosSview node) {
        super.outAPosSview(node);
    }

    @Override
    public void outAPrioposTview(APrioposTview node) {
        super.outAPrioposTview(node);
    }

    @Override
    public void outAPriorityTransargument(APriorityTransargument node) {
        super.outAPriorityTransargument(node);
    }

    @Override
    public void outAPsuspendPseudo(APsuspendPseudo node) {
        super.outAPsuspendPseudo(node);
    }

    @Override
    public void outAPsyncPseudo(APsyncPseudo node) {
        super.outAPsyncPseudo(node);
    }

    @Override
    public void outARargument(ARargument node) {
        super.outARargument(node);
    }

    @Override
    public void outARegion(ARegion node) {
        super.outARegion(node);
    }

    @Override
    public void outASargument(ASargument node) {
        super.outASargument(node);
    }

    @Override
    public void outASimpleState(ASimpleState node) {
        currentstate.pop();
        // System.out.println("pop " + node.getIdentifier().getText());
        printStates();
        super.outASimpleState(node);
    }

    @Override
    public void outAStateElement(AStateElement node) {
        super.outAStateElement(node);
    }

    @Override
    public void outAStrongTranstype(AStrongTranstype node) {
        super.outAStrongTranstype(node);
    }

    @Override
    public void outASuspensionTranstype(ASuspensionTranstype node) {
        super.outASuspensionTranstype(node);
    }

    @Override
    public void outATargument(ATargument node) {
        super.outATargument(node);
    }

    @Override
    public void outATransitionElement(ATransitionElement node) {
        super.outATransitionElement(node);
    }

    @Override
    public void outATrueTBoolean(ATrueTBoolean node) {
        super.outATrueTBoolean(node);
    }

    @Override
    public void outATypeStateargument(ATypeStateargument node) {
        super.outATypeStateargument(node);
    }

    @Override
    public void outATypeTransargument(ATypeTransargument node) {
        super.outATypeTransargument(node);
    }

    @Override
    public void outAVarDeclTyp(AVarDeclTyp node) {
        super.outAVarDeclTyp(node);
    }

    @Override
    public void outAVariable(AVariable node) {
        super.outAVariable(node);
    }

    @Override
    public void outAVersionChartargument(AVersionChartargument node) {
        super.outAVersionChartargument(node);
    }

    @Override
    public void outAViewRegionargument(AViewRegionargument node) {
        super.outAViewRegionargument(node);
    }

    @Override
    public void outAViewStateargument(AViewStateargument node) {
        super.outAViewStateargument(node);
    }

    @Override
    public void outAViewTransargument(AViewTransargument node) {
        super.outAViewTransargument(node);
    }

    @Override
    public void outAVtype(AVtype node) {
        super.outAVtype(node);
    }

    @Override
    public void outAWeakTranstype(AWeakTranstype node) {
        super.outAWeakTranstype(node);
    }

    @Override
    public void outAWidthSview(AWidthSview node) {
        super.outAWidthSview(node);
    }

    @Override
    public void outStart(Start node) {
        super.outStart(node);
    }

}
