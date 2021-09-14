import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

class Order{

    public byte id;
    public byte[] ingredients;
    public byte price;
    public byte depthLimit = 50;

    public Order(byte id, byte[] ingredients, byte price){
        this.id = id;
        this.ingredients = ingredients;
        this.price = price;
    }

    public String output(){
        return "BREW " + this.id;
    }
}


class Spell{

    public byte id;
    public byte[] ingredients;
    public boolean castable;
    public boolean repeatable;

    public Spell(byte id, byte[] ingredients, boolean castable, boolean repeatable){
        this.id = id;
        this.ingredients = ingredients;
        this.castable = castable;
        this.repeatable = repeatable;
    }

    public String output(byte times){
        return "CAST " + this.id + " " + times;
    }

    public void show(){
        System.err.println();
        System.err.println("id: " + id);
        System.err.println("ingredients: ");
        for(byte i = 0; i < 4; i++){
            System.err.println();
            System.err.print(this.ingredients[i] + ", ");
        }
        System.err.println();
        System.err.println("castable: " + castable);
        System.err.println("repeatable: " + repeatable);
        System.err.println();
    }
}

class LearnSpell{

    public byte id;
    public byte[] ingredients;
    public byte tomeIndex;
    public byte taxCount;
    public boolean repeatable;

    public LearnSpell(byte id, byte[] ingredients, byte tomeIndex, byte taxCount, boolean repeatable){
        this.id = id;
        this.ingredients = ingredients;
        this.tomeIndex = tomeIndex;
        this.taxCount = taxCount;
        this.repeatable = repeatable;
    }

    public String output(){
        return "LEARN " + this.id;
    }
    public String output(byte times){
        return "CAST " + this.id + " " + times;
    }

    public void show(){
        System.err.println();
        System.err.println("id: " + id);
        System.err.println("ingredients: ");
        for(byte i = 0; i < 4; i++){
            System.err.println();
            System.err.print(this.ingredients[i] + ", ");
        }
        System.err.println();
        System.err.println("repeatable: " + repeatable);
        System.err.println("tomeIndex: " + tomeIndex);
        System.err.println("taxCount: " + taxCount);
        System.err.println();
    }
}


class State{

    public byte score;
    public byte[] bounsPrice;
    public double stateEval; 
    public byte[] ingredients;
    public BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed;
    public byte depth;
    public byte bigBounsUsed;
    public byte littleBounsUsed;
    public byte ordersLeft;
    public String command;

    public State(byte score, double parentEval, byte[] ingredients, BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, byte[] bounsPrice,
    String command, byte depth, byte bigBounsUsed, byte littleBounsUsed, byte ordersLeft){
        this.score = score;
        this.ingredients = ingredients;
        this.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed;
        this.command = command;
        this.depth = depth;
        this.bigBounsUsed = bigBounsUsed;
        this.littleBounsUsed = littleBounsUsed;
        this.ordersLeft = ordersLeft;

        if(Player.opponentSim){
            this.stateEval = this.evaluate2();
        }
        else{
            this.stateEval = this.evaluate();
        }

        this.stateEval += parentEval;

        this.bounsPrice = bounsPrice;
    }


    public double evaluate(){
        double res = this.score + this.ingredients[0] + this.ingredients[1] * 2 + 
        this.ingredients[2] * 3 + this.ingredients[3] * 4;

        if(this.ingredients[3] > Player.maxYellows || this.ingredients[2] > 9){
            res -= 15;
        }

        for (byte i = Player.startOrdersIndex; i < Player.startOrdersIndex + Player.ordersSize; i++) {
            if(this.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)) res += 1.1;
        }

        for (byte i = Player.startSpellsLearndIndex; i < Player.startSpellsLearndIndex + Player.spellsToLearnSize; i++) {
            if(this.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)) res += 0.5;
        }

        return Math.pow(0.95, this.depth) * res;
    }

    public double evaluate2(){
        return (this.score + this.ingredients[1] + this.ingredients[2] + this.ingredients[3]) * Math.pow(0.95, this.depth);
    }

    public void show(){
        System.err.println();
        System.err.println("score: " + score);
        System.err.println("depth: " + depth);
        System.err.println("ingredients: ");
        for(byte i = 0; i < 4; i++){
            System.err.print(this.ingredients[i] + ", ");
        }
        System.err.println();
        System.err.println();
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, Arrays.hashCode(bounsPrice), stateEval, Arrays.hashCode(ingredients), Objects.hash(spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed), 
        depth, bigBounsUsed, littleBounsUsed, ordersLeft);
    }
}


class Sortbyroll implements Comparator<State>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(State a, State b)
    {
        double res = a.stateEval - b.stateEval;
        if(res > 0) return -1;
        if(res < 0) return 1;
        return 0;
    }
}


class TakePlace{
    public TakePlace(){

    }
}

class Player {

    public static Order[] orders = new Order[5];
    public static List<Spell> mySpells;
    public static List<Spell> opSpells;
    public static LearnSpell[] spellsToLearn = new LearnSpell[6];
    public static byte mySpellsSize;
    public static byte opSpellsSize;
    public static byte spellsToLearnSize;
    public static byte ordersSize;

    public static byte startOrdersIndex;
    public static byte startSpellsLearndIndex;
    public static short startSpellsLearndUsedIndex;

    public static byte opStartOrdersIndex;
    public static byte opStartSpellsLearndIndex;
    public static short opStartSpellsLearndUsedIndex;

    public static long startTime;
    public static short moneStates;
    //public static double[] values = {1, 2, 3, 4};

    //public static int mone = 0;

    public static short N = 400;
    public static byte learn = 7;

    public static byte depthLimit = 15;
    public static boolean opponentSim = true;

    public static byte maxYellows = 5;
 
    public static short timeToEnd = 1000;

    public static byte ordersLeftToMe = 6;
    public static byte ordersLeftToOpponent = 6;
    public static byte prevOpponentScore = 0;
    public static byte prevMyScore = 0;

    public static HashSet<Integer> states;

    public static State[] nextLevel;
    public static short indexNextLevel;

    public static byte[] ordersPrevId;
    public static byte bigBounsPrevId = -1;
    public static byte littleBounsPrevId = -1;
    public static byte ordersDeliverdOnBigBouns = 0;
    public static byte ordersDeliverdOnLittleBouns = 0;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            ordersSize = 0;
            mySpellsSize = 0;
            opSpellsSize = 0;
            spellsToLearnSize = 0;
            mySpells = new ArrayList<Spell>();
            opSpells = new ArrayList<Spell>();

            short actionCount = in.nextShort(); // the number of spells and recipes in play
            startTime = System.currentTimeMillis();
            for (byte i = 0; i < actionCount; i++) {
                byte actionId = in.nextByte(); // the unique ID of this spell or recipe
                String actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
                byte delta0 = in.nextByte(); // tier-0 ingredient change
                byte delta1 = in.nextByte(); // tier-1 ingredient change
                byte delta2 = in.nextByte(); // tier-2 ingredient change
                byte delta3 = in.nextByte(); // tier-3 ingredient change
                byte price = in.nextByte(); // the price in rupees if this is a potion
                byte tomeIndex = in.nextByte(); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax; For brews, this is the value of the current urgency bonus
                byte taxCount = in.nextByte(); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell; For brews, this is how many times you can still gain an urgency bonus
                boolean castable = in.nextByte() != 0; // in the first league: always 0; later: 1 if this is a castable player spell
                boolean repeatable = in.nextByte() != 0; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

                byte[] ingredients = {delta0, delta1, delta2, delta3};
                
                if(actionType.equals("BREW")){
                    orders[ordersSize] = new Order(actionId, ingredients, price);
                    ordersSize++;
                }
                else if(actionType.equals("CAST")){
                    if(ingredients[3] < 0){
                        byte tempMaxYellows = (byte) (10 - (ingredients[2] + ingredients[1] + ingredients[0]));
                        if(maxYellows < tempMaxYellows){
                            maxYellows = tempMaxYellows;
                        }
                    }
                    mySpells.add(new Spell(actionId, ingredients, castable, repeatable));
                    mySpellsSize++;
                }
                else if(actionType.equals("OPPONENT_CAST")){
                    opSpells.add(new Spell(actionId, ingredients, castable, repeatable));
                    opSpellsSize++;
                }
                else if(actionType.equals("LEARN")){
                    spellsToLearn[spellsToLearnSize] = new LearnSpell(actionId, ingredients, tomeIndex, taxCount, repeatable);
                    spellsToLearnSize++;
                }
            }

            spellsToLearnSize = 2;

            State me = null, opponent = null;

            BitSet spellsUsedAndOrdersDeliverdAndSpellsLearnd = new BitSet(mySpellsSize + ordersSize + spellsToLearnSize * 2);

            for (short i = 0; i < mySpellsSize; i++) {
                Spell spell = mySpells.get(i);
                spellsUsedAndOrdersDeliverdAndSpellsLearnd.set(i, !spell.castable);
            }

            BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndOpponent = new BitSet(opSpellsSize + ordersSize + spellsToLearnSize * 2);

            for (short i = 0; i < opSpellsSize; i++) {
                Spell spell = opSpells.get(i);
                spellsUsedAndOrdersDeliverdAndSpellsLearndOpponent.set(i, !spell.castable);
            }

            byte moneChanged = 0;
            if(ordersPrevId != null){
                for(byte i = 0; i < 5; i++){
                    if(ordersPrevId[i] != orders[i - moneChanged].id){
                        moneChanged++;
                    }
                }
            }
            else{
                ordersPrevId = new byte[5];
            }

            for(byte i = 0; i < 5; i++){
                ordersPrevId[i] = orders[i].id;
            }


            boolean meDelivered = false;
            boolean opDelivered = false;

            for (short i = 0; i < 2; i++) {
                byte inv0 = in.nextByte(); // tier-0 ingredients in inventory
                byte inv1 = in.nextByte();
                byte inv2 = in.nextByte();
                byte inv3 = in.nextByte();
                byte[] ingredients = {inv0, inv1, inv2, inv3};
                byte score = in.nextByte(); // amount of rupees
                if(i == 0){
                    if(score != prevMyScore){
                        prevMyScore = score;
                        ordersLeftToMe--;
                        meDelivered = true;
                    }
                    me = new State(score, 0, ingredients, spellsUsedAndOrdersDeliverdAndSpellsLearnd, null, "", (byte) 0, (byte) 0, (byte) 0, ordersLeftToMe);
                }
                else{
                    if(score != prevOpponentScore){
                        prevOpponentScore = score;
                        ordersLeftToOpponent--;
                        opDelivered = true;
                    }
                    opponent = new State(score, 0, ingredients, spellsUsedAndOrdersDeliverdAndSpellsLearndOpponent, null, "", (byte) 0, (byte) 0, (byte) 0, ordersLeftToOpponent);
                }
            }

            if(bigBounsPrevId != -1){
                if(ordersDeliverdOnBigBouns < 4 && bigBounsPrevId != orders[0].id){
                    ordersDeliverdOnBigBouns++;

                    if(moneChanged == 1 && opDelivered && meDelivered){
                        ordersDeliverdOnBigBouns++;
                    }
                    else if(moneChanged == 2){
                        if(littleBounsPrevId != -1){
                            if(littleBounsPrevId != orders[0].id)
                                ordersDeliverdOnLittleBouns++;
                        }
                    }
                }
                else if(littleBounsPrevId != -1 && ordersDeliverdOnLittleBouns < 4){
                    if(ordersDeliverdOnBigBouns < 4 && littleBounsPrevId != orders[1].id){
                        ordersDeliverdOnLittleBouns++;
                        if(moneChanged == 1 && opDelivered && meDelivered)
                            ordersDeliverdOnLittleBouns++;
                    }
                    else if(ordersDeliverdOnBigBouns >= 4 && littleBounsPrevId != orders[0].id){
                        ordersDeliverdOnLittleBouns++;
                        if(moneChanged == 1 && opDelivered && meDelivered)
                                ordersDeliverdOnLittleBouns++;
                    }
                } 
            }

            byte[] bounsPrice = null;

            if(ordersDeliverdOnBigBouns >= 4 && ordersDeliverdOnLittleBouns < 4){
                littleBounsPrevId = orders[0].id;
                orders[0].price -= 1;
                //System.err.println("1, 0, 0, 0, 0");
                bounsPrice = new byte[]{1, 0, 0, 0, 0};
            }
            else if(ordersDeliverdOnBigBouns < 4 && ordersDeliverdOnLittleBouns >= 4){
                bigBounsPrevId = orders[0].id;
                orders[0].price -= 3;
                //System.err.println("3, 0, 0, 0, 0");
                bounsPrice = new byte[]{3, 0, 0, 0, 0};
            }
            else if(ordersDeliverdOnBigBouns < 4 && ordersDeliverdOnLittleBouns < 4){
                bigBounsPrevId = orders[0].id;
                littleBounsPrevId = orders[1].id;
                orders[0].price -= 3;
                orders[1].price -= 1;
                //System.err.println("3, 1, 0, 0, 0");
                bounsPrice = new byte[]{3, 1, 0, 0, 0};
            }
            else{
                //System.err.println("0, 0, 0, 0, 0");
                bounsPrice = new byte[]{0, 0, 0, 0, 0};
            }

            me.bigBounsUsed = ordersDeliverdOnBigBouns;
            me.littleBounsUsed = ordersDeliverdOnLittleBouns;
            me.bounsPrice = bounsPrice;

            opponent.bigBounsUsed = ordersDeliverdOnBigBouns;
            opponent.littleBounsUsed = ordersDeliverdOnLittleBouns;
            opponent.bounsPrice = bounsPrice;

            startOrdersIndex = mySpellsSize;
            startSpellsLearndIndex = (byte) (startOrdersIndex + ordersSize);
            startSpellsLearndUsedIndex = (short) (startSpellsLearndIndex + spellsToLearnSize);

            opStartOrdersIndex = opSpellsSize;
            opStartSpellsLearndIndex = (byte) (opStartOrdersIndex + ordersSize);
            opStartSpellsLearndUsedIndex = (short) (opStartSpellsLearndIndex + spellsToLearnSize);

            System.err.println(Runtime.getRuntime().freeMemory());

            if(timeToEnd == 1000){
                timeToEnd = 50;
                TakePlace[] aLotOfStates = new TakePlace[5000000];
                for(int i = 0; i < 5000000; i++){
                   aLotOfStates[i] = new TakePlace();
                }
                System.err.println(Runtime.getRuntime().freeMemory());
                aLotOfStates = null;
                System.gc();
                System.err.println(Runtime.getRuntime().freeMemory());
                byte ID = spellsToLearn[0].id;
                nextLevel = new State[4000];
                learn--;
                System.out.println("LEARN " + ID + " Hmm...");
                continue;
            }

            //System.err.println("me: ");
            //me.show();

            //System.err.println("opponent: ");
            //opponent.show();

            if(learn > 0){
                indexNextLevel = 0;
                moneStates = 0;
                //states = new HashSet<Integer>(2000);
                String output = SimWhatLearn(me);
                learn--;
                System.out.println(output + " learn > 0");
                continue;
            }

            
            //states = new HashSet<Integer>(2000);
            indexNextLevel = 0;
            moneStates = 0;
            opponentSim = true;
            State outputOpponent = SimOpponent(opponent);
            //System.err.println("outputOpponent.command: " + outputOpponent.command);

            String[] actions = outputOpponent.command.split("  ");
            for(byte depth = 0; depth < actions.length; depth++){
                String action = actions[depth];
                if(action.contains("BREW")){
                    int id = Integer.parseInt(action.split(" ")[1]);
                    for(byte i = 0; i < ordersSize; i++){
                        if(orders[i].id == id){
                            orders[i].depthLimit = depth;
                        }
                    }
                }
            }

            indexNextLevel = 0;
            moneStates = 0;
            opponentSim = false;
            String output = Sim(me);

            int startBrew = output.indexOf("BREW");
            if(startBrew != -1 && startBrew != 0){
                int endBrew = output.indexOf(" ", startBrew + 5);
                if(endBrew == -1) endBrew = output.length();
                String firstBrew = output.substring(startBrew, endBrew);

                int idFirstBrew = Integer.parseInt(firstBrew.split(" ")[1]);

                boolean brewEarlier = false;

                for(byte i = 0; i < ordersSize; i++){
                    if(orders[i].id == idFirstBrew){
                        byte[] newIngr = addIngredients(me.ingredients, orders[i].ingredients);
                        if(newIngr != null){
                            System.out.println(orders[i].output() + " Fasten Brew");
                            brewEarlier = true;
                            break;
                        }
                    }
                }

                if(brewEarlier) continue;
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            //System.out.println(output);
            // later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            System.out.println(output);
        }
    }

    public static String SimWhatLearn(State state){

        State[] que = new State[N];
        short queIndex = 0;
        //states[indexStates] = state;
        
        double bestPrice = 0;
        State bestState = null;

        int moneStates = 0;
        int moneDepth = 0;

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            //Try to learn this spell
            if(spell.tomeIndex > state.ingredients[0]){
                //I don't have enough blues to pay for it
                continue;
            }

            byte[] newIngr = state.ingredients.clone();
            newIngr[0] -= spell.tomeIndex;
            if(newIngr[0] >= 0){
                newIngr[0] += spell.taxCount;
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startSpellsLearndIndex);
                    

                State newState = new State(state.score, state.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                state.bounsPrice.clone(), spell.output(), (byte) (state.depth + 1), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                //moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    nextLevel[indexNextLevel] = newState;
                    indexNextLevel++;
                }
            }
        }

        Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

        for(short i = 0; i < Math.min(N, indexNextLevel); i++){
            que[queIndex] = nextLevel[i];
            queIndex++;
        }

        queIndex--;
        indexNextLevel = 0;
        moneDepth++;


        while(queIndex > -1){
            State currState = que[queIndex];
            queIndex--;
            

            boolean hasNextGen = nextStatesWhatToLearn(currState);
            
            if(System.currentTimeMillis() - startTime >= timeToEnd) break;

            if(hasNextGen){

                if(currState.stateEval > bestPrice){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                }
            }


            if(queIndex == -1){

                Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

                queIndex++;

                for(short i = 0; i < Math.min(N, indexNextLevel); i++){
                    que[queIndex] = nextLevel[i];
                    queIndex++;
                }

                queIndex--;
                indexNextLevel = 0;
                moneDepth++;
            }
        }

        /*System.err.println();
        System.err.println("moneStates: " + moneStates);
        System.err.println("moneDepth: " + moneDepth);
        System.err.println("bestPrice: " + bestPrice);
        System.err.println();*/

        return bestState.command;
    }

    public static boolean nextStatesWhatToLearn(State currState){

        String command = currState.command;

        boolean hasAdd = false;

        for (byte i = 0; i < ordersSize; i++) {
            Order order = orders[i];
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + startOrdersIndex)
            || order.depthLimit < currState.depth) continue;
            byte[] newIngr = addIngredients(currState.ingredients, order.ingredients);
            if(newIngr != null){
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startOrdersIndex);

                byte [] newBounsPrice = currState.bounsPrice.clone();

                byte price = (byte) (order.price + currState.bounsPrice[i]);

                byte toBigBouns = 0;
                byte toLittleBouns = 0;
                if(currState.bounsPrice[i] != 0){
                    for(byte j = 4; j > i; j--){
                        newBounsPrice[j] = newBounsPrice[j - 1];
                    }

                    if(currState.bounsPrice[i] == 3){
                        if(currState.bigBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toBigBouns = 1;
                        }
                    }
                    else if(currState.littleBounsUsed == 1){
                        if(currState.littleBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toLittleBouns = 1;
                        }
                    }
                }

                State newState = new State((byte) (currState.score + price), currState.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                newBounsPrice, command, (byte) (currState.depth + 1), (byte) (currState.bigBounsUsed + toBigBouns), (byte) (currState.littleBounsUsed + toLittleBouns), (byte) (currState.ordersLeft - 1));
                //moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    hasAdd = true;
                    nextLevel[indexNextLevel] = newState;
                    indexNextLevel++;
                }
            }
        }

        //CAST
        for (byte i = 0; i < mySpellsSize; i++) {
            Spell spell = mySpells.get(i);
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, mySpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(startSpellsLearndUsedIndex, startSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command, (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    //moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command, (byte) (currState.depth + 1), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    //moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            if(!currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + startSpellsLearndIndex)) continue;
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + startSpellsLearndUsedIndex)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, mySpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(startSpellsLearndUsedIndex, startSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startSpellsLearndUsedIndex);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command, (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    //moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startSpellsLearndUsedIndex);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command, (byte) (currState.depth + 1), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    //moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        return hasAdd;
    }

    public static String Sim(State state){

        State[] que = new State[N];
        short queIndex = 0;
        //states[indexStates] = state;
        
        double bestPrice = 0;
        State bestState = null;

        int moneDepth = 0;

        for (byte i = 0; i < ordersSize; i++) {
            Order order = orders[i];
            byte[] newIngr = addIngredients(state.ingredients, order.ingredients);
            if(newIngr != null){
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startOrdersIndex);

                byte [] newBounsPrice = state.bounsPrice.clone();

                byte price = (byte) (order.price + state.bounsPrice[i]);
                byte toBigBouns = 0;
                byte toLittleBouns = 0;
                if(state.bounsPrice[i] != 0){
                    for(byte j = 4; j > i; j--){
                        newBounsPrice[j] = newBounsPrice[j - 1];
                    }

                    if(state.bounsPrice[i] == 3){
                        if(state.bigBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toBigBouns = 1;
                        }
                    }
                    else if(state.littleBounsUsed == 1){
                        if(state.littleBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toLittleBouns = 1;
                        }
                    }
                }

                State newState = new State((byte) (state.score + price), state.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                newBounsPrice, order.output(), (byte) (state.depth + 1), (byte) (state.bigBounsUsed + toBigBouns), (byte) (state.littleBounsUsed + toLittleBouns), (byte) (state.ordersLeft - 1));
                moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    if(ordersLeftToMe == 1){
                        if(newState.score > prevOpponentScore && newState.score > bestPrice){
                            bestPrice = newState.score;
                            bestState = newState;
                        }
                    }
                    else{
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }
                }
            }
        }

        if(bestPrice != 0){
            System.err.println();
            System.err.println("moneStates: " + moneStates);
            System.err.println("moneDepth: " + moneDepth);
            System.err.println("bestPrice: " + bestPrice);
            System.err.println();
            return bestState.command;
        }

        //CAST
        for (byte i = 0; i < mySpellsSize; i++) {
            Spell spell = mySpells.get(i);
            if(state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)){
                byte[] newIngr = addIngredients(state.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, mySpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(state.score, state.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), "REST " + spell.output(countTimes), (byte) (state.depth + 2), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte times = 1;
                byte[] newIngr = addIngredients(state.ingredients, spell.ingredients);
                double addTimes = 0;
                while(newIngr != null){

                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(state.score, state.stateEval + addTimes, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), spell.output(times), (byte) (state.depth + 1), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times++;

                    addTimes = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            //Try to learn this spell
            if(spell.tomeIndex > state.ingredients[0]){// || !spell.repeatable){
                //I don't have enough blues to pay for it
                continue;
            }

            byte[] newIngr = state.ingredients.clone();
            newIngr[0] -= spell.tomeIndex;
            if(newIngr[0] >= 0){
                newIngr[0] += spell.taxCount;
                if(newIngr[0] + newIngr[1] + newIngr[2] + newIngr[3] > 10){
                    newIngr[0] -= newIngr[0] + newIngr[1] + newIngr[2] + newIngr[3] - 10;
                }
                newIngr = addIngredients(newIngr, spell.ingredients);
                double addTimes = 0;
                byte times = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startSpellsLearndIndex);
                    
                    State newState = new State(state.score, state.stateEval + addTimes, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), spell.output() + " " + spell.output(times), (byte) (state.depth + 2), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    addTimes = 0.1;
                    times++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }


        Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

        for(short i = 0; i < Math.min(N, indexNextLevel); i++){
            que[queIndex] = nextLevel[i];
            queIndex++;
        }

        queIndex--;
        indexNextLevel = 0;
        moneDepth++;


        while(queIndex > -1){
            State currState = que[queIndex];
            queIndex--;
            if(currState.ordersLeft == 0 || currState.depth >= depthLimit){
                if(currState.stateEval > bestPrice){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                }

                if(currState.depth <= 3 && currState.score > prevOpponentScore + 4){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                    break;
                }
                continue;
            }

            
            boolean hasNextGen = nextStates(currState);

            if(hasNextGen){

                if(currState.stateEval > bestPrice){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                }

                if(System.currentTimeMillis() - startTime >= timeToEnd){
                    break;
                }
            }


            if(queIndex == -1){

                Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

                queIndex++;

                for(short i = 0; i < Math.min(N, indexNextLevel); i++){
                    que[queIndex] = nextLevel[i];
                    queIndex++;
                }

                queIndex--;
                indexNextLevel = 0;
                moneDepth++;
            }
        }

        System.err.println();
        System.err.println("moneStates: " + moneStates);
        System.err.println("moneDepth: " + moneDepth);
        System.err.println("bestPrice: " + bestPrice);
        System.err.println();

        return bestState.command;
    }

    public static boolean nextStates(State currState){

        String command = currState.command;

        boolean hasAdd = false;

        for (byte i = 0; i < ordersSize; i++) {
            Order order = orders[i];
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + startOrdersIndex)
            || order.depthLimit < currState.depth) continue;
            byte[] newIngr = addIngredients(currState.ingredients, order.ingredients);
            if(newIngr != null){
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startOrdersIndex);

                byte [] newBounsPrice = currState.bounsPrice.clone();

                byte price = (byte) (order.price + currState.bounsPrice[i]);
                byte toBigBouns = 0;
                byte toLittleBouns = 0;
                if(currState.bounsPrice[i] != 0){
                    for(byte j = 4; j > i; j--){
                        newBounsPrice[j] = newBounsPrice[j - 1];
                    }

                    if(currState.bounsPrice[i] == 3){
                        if(currState.bigBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toBigBouns = 1;
                        }
                    }
                    else if(currState.littleBounsUsed == 1){
                        if(currState.littleBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toLittleBouns = 1;
                        }
                    }
                }

                State newState = new State((byte) (currState.score + price), currState.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                newBounsPrice, command + " " + order.output(), (byte) (currState.depth + 1), (byte) (currState.bigBounsUsed + toBigBouns), (byte) (currState.littleBounsUsed + toLittleBouns), (byte) (currState.ordersLeft - 1));
                moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    hasAdd = true;
                    nextLevel[indexNextLevel] = newState;
                    indexNextLevel++;
                }
            }
        }

        //CAST
        for (byte i = 0; i < mySpellsSize; i++) {
            Spell spell = mySpells.get(i);
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, mySpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(startSpellsLearndUsedIndex, startSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + " REST " + spell.output(countTimes), (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + " " + spell.output(countTimes), (byte) (currState.depth + 1), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + startSpellsLearndIndex)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, mySpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(startSpellsLearndUsedIndex, startSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + startSpellsLearndUsedIndex);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + " " + spell.output(countTimes), (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        return hasAdd;
    }


    public static byte[] addIngredients(byte[] ingredients1, byte[] ingredients2){
        byte[] resIngr = new byte[4];
        byte mone = 0;

        for (byte i = 0; i < 4; i++) {
            resIngr[i] = ingredients1[i];
            resIngr[i] += ingredients2[i];
            if(resIngr[i] < 0) return null;
            mone += resIngr[i];
        }

        if(mone > 10) return null;
        return resIngr;
    }


    public static boolean addToStates(State state){
        return true;
    }




    public static State SimOpponent(State state){

        State[] que = new State[N];
        short queIndex = 0;
        //states[indexStates] = state;
        
        double bestPrice = 0;
        State bestState = null;

        int moneDepth = 0;

        for (byte i = 0; i < ordersSize; i++) {
            Order order = orders[i];
            byte[] newIngr = addIngredients(state.ingredients, order.ingredients);
            if(newIngr != null){
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + opStartOrdersIndex);

                byte [] newBounsPrice = state.bounsPrice.clone();

                byte price = (byte) (order.price + state.bounsPrice[i]);
                byte toBigBouns = 0;
                byte toLittleBouns = 0;
                if(state.bounsPrice[i] != 0){
                    for(byte j = 4; j > i; j--){
                        newBounsPrice[j] = newBounsPrice[j - 1];
                    }

                    if(state.bounsPrice[i] == 3){
                        if(state.bigBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toBigBouns = 1;
                        }
                    }
                    else if(state.littleBounsUsed == 1){
                        if(state.littleBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toLittleBouns = 1;
                        }
                    }
                }

                State newState = new State((byte) (state.score + price), state.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                newBounsPrice, order.output(), (byte) (state.depth + 1), (byte) (state.bigBounsUsed + toBigBouns), (byte) (state.littleBounsUsed + toLittleBouns), (byte) (state.ordersLeft - 1));
                moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    if(ordersLeftToOpponent == 1){
                        if(newState.score > prevMyScore && newState.stateEval > bestPrice){
                            bestPrice = newState.stateEval;
                            bestState = newState;
                        }
                    }
                    else{
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }
                }
            }
        }

        if(bestPrice != 0){
            return bestState;
        }

        //CAST
        for (byte i = 0; i < opSpellsSize; i++) {
            Spell spell = opSpells.get(i);
            if(state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)){
                byte[] newIngr = addIngredients(state.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, opSpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(state.score, state.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), "REST  " + spell.output(countTimes), (byte) (state.depth + 2), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte times = 1;
                byte[] newIngr = addIngredients(state.ingredients, spell.ingredients);
                double addTimes = 0;
                while(newIngr != null){

                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(state.score, state.stateEval + addTimes, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), spell.output(times), (byte) (state.depth + 1), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times++;

                    addTimes = 0.1;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            //Try to learn this spell
            if(spell.tomeIndex > state.ingredients[0]){// || !spell.repeatable){
                //I don't have enough blues to pay for it
                continue;
            }

            byte[] newIngr = state.ingredients.clone();
            newIngr[0] -= spell.tomeIndex;
            if(newIngr[0] >= 0){
                newIngr[0] += spell.taxCount;
                if(newIngr[0] + newIngr[1] + newIngr[2] + newIngr[3] > 10){
                    newIngr[0] -= newIngr[0] + newIngr[1] + newIngr[2] + newIngr[3] - 10;
                }
                newIngr = addIngredients(newIngr, spell.ingredients);
                double addTimes = 0;
                byte timesCount = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) state.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + opStartSpellsLearndIndex);
                    
                    State newState = new State(state.score, state.stateEval + addTimes, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    state.bounsPrice.clone(), spell.output() + "  " + spell.output(timesCount), (byte) (state.depth + 2), state.bigBounsUsed, state.littleBounsUsed, state.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    addTimes = 0.1;
                    timesCount++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }


        Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

        for(short i = 0; i < Math.min(N, indexNextLevel); i++){
            que[queIndex] = nextLevel[i];
            queIndex++;
        }

        queIndex--;
        indexNextLevel = 0;
        moneDepth++;


        while(queIndex > -1){
            State currState = que[queIndex];
            queIndex--;
            if(currState.ordersLeft == 0 || currState.depth >= 5){
                if(currState.stateEval > bestPrice){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                }

                if(currState.depth < 5 && currState.score > prevMyScore + 4){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                    break;
                }
                continue;
            }

            
            boolean hasNextGen = nextStatesOpponent(currState);

            if(hasNextGen){

                if(currState.stateEval >= bestPrice){
                    bestPrice = currState.stateEval;
                    bestState = currState;
                }

                if(System.currentTimeMillis() - startTime >= timeToEnd){
                    break;
                }
            }


            if(queIndex == -1){

                Arrays.sort(nextLevel, 0, indexNextLevel, new Sortbyroll());

                queIndex++;

                for(short i = 0; i < Math.min(N, indexNextLevel); i++){
                    que[queIndex] = nextLevel[i];
                    queIndex++;
                }

                queIndex--;
                indexNextLevel = 0;
                moneDepth++;
            }
        }

        /*System.err.println();
        System.err.println("moneStates op: " + moneStates);
        System.err.println("moneDepth: " + moneDepth);
        System.err.println("bestPrice: " + bestPrice);
        System.err.println();*/

        return bestState;
    }


    public static boolean nextStatesOpponent(State currState){

        String command = currState.command;

        boolean hasAdd = false;

        for (byte i = 0; i < ordersSize; i++) {
            Order order = orders[i];
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + opStartOrdersIndex)) continue;
            byte[] newIngr = addIngredients(currState.ingredients, order.ingredients);
            if(newIngr != null){
                BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + opStartOrdersIndex);

                byte [] newBounsPrice = currState.bounsPrice.clone();

                byte price = (byte) (order.price + currState.bounsPrice[i]);
                byte toBigBouns = 0;
                byte toLittleBouns = 0;
                if(currState.bounsPrice[i] != 0){
                    for(byte j = 4; j > i; j--){
                        newBounsPrice[j] = newBounsPrice[j - 1];
                    }

                    if(currState.bounsPrice[i] == 3){
                        if(currState.bigBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toBigBouns = 1;
                        }
                    }
                    else if(currState.littleBounsUsed == 1){
                        if(currState.littleBounsUsed >= 4){
                            if(i + 1 < 5) newBounsPrice[i + 1] = 0;
                        }
                        else{
                            toLittleBouns = 1;
                        }
                    }
                }

                State newState = new State((byte) (currState.score + price), currState.stateEval, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                newBounsPrice, command + "  " + order.output(), (byte) (currState.depth + 1), (byte) (currState.bigBounsUsed + toBigBouns), (byte) (currState.littleBounsUsed + toLittleBouns), (byte) (currState.ordersLeft - 1));
                moneStates++;

                boolean isAdded = addToStates(newState);
                if(isAdded){
                    hasAdd = true;
                    nextLevel[indexNextLevel] = newState;
                    indexNextLevel++;
                }
            }
        }

        //CAST
        for (byte i = 0; i < opSpellsSize; i++) {
            Spell spell = opSpells.get(i);
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, opSpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(opStartSpellsLearndUsedIndex, opStartSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + "  REST  " + spell.output(countTimes), (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
            else{
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + "  " + spell.output(countTimes), (byte) (currState.depth + 1), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates++;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        //LEARN
        for (byte i = 0; i < spellsToLearnSize; i++) {
            LearnSpell spell = spellsToLearn[i];
            if(currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.get(i + opStartSpellsLearndIndex)){
                byte[] newIngr = addIngredients(currState.ingredients, spell.ingredients);
                double times = 0;
                byte countTimes = 1;
                while(newIngr != null){
                    BitSet spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed = (BitSet) currState.spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clone();
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(0, opSpellsSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.clear(opStartSpellsLearndUsedIndex, opStartSpellsLearndUsedIndex + spellsToLearnSize);
                    spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed.set(i + opStartSpellsLearndUsedIndex);

                    State newState = new State(currState.score, currState.stateEval + times, newIngr, spellsUsedAndOrdersDeliverdAndSpellsLearndAndSpellsLearndUsed, 
                    currState.bounsPrice.clone(), command + "  " + spell.output(countTimes), (byte) (currState.depth + 2), currState.bigBounsUsed, currState.littleBounsUsed, currState.ordersLeft);
                    moneStates += 1;

                    boolean isAdded = addToStates(newState);
                    if(isAdded){
                        hasAdd = true;
                        nextLevel[indexNextLevel] = newState;
                        indexNextLevel++;
                    }

                    if(!spell.repeatable) break;

                    times = 0.1;
                    countTimes++;

                    newIngr = addIngredients(newIngr, spell.ingredients);
                }
            }
        }

        return hasAdd;
    }


}
