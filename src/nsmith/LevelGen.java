package nsmith;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.CustomizedLevel;
import dk.itu.mario.level.generator.CustomizedLevelGenerator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author nix
 */
public class LevelGen extends CustomizedLevelGenerator {
    
    FileReader file = null;
    HashMap<Long, ArrayList<ArrayList<Integer>>> seedLabels;
    int setLength;
    long seed;
    
    public LevelGen() {
        
        seedLabels = new HashMap<Long, ArrayList<ArrayList<Integer>>>(); 
        
        try {
            this.file = new FileReader("src/nsmith/seedDataSet.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LevelGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public LevelInterface generateLevel(GamePlay playerMetrics) {
        LevelInterface level;
        
        readLabeledSet();
        
        seed = new Random().nextLong();
        
        if (nsLevel.createNsLevel(320, 15, seed, 1, 1, playerMetrics)) {
            nsLevel.LEVEL.creat();
            level = nsLevel.getLEVEL();
        } else {
            nsLevel.clearLEVEL(true);
            nsLevel.createNsLevel(320, 15, seed, 1, 1, playerMetrics);
            nsLevel.LEVEL.creat();
            level = nsLevel.getLEVEL();

        }
        
//        surveyFeedBack();
            
//        writeLabel();
        
        return level;
    }
    
    private void readLabeledSet(){
            
            BufferedReader reader = new BufferedReader(file);
            String line = null;
            String[] lineIn;
            int dataNo = 0;
            ArrayList<ArrayList<Integer>> entryRow = new ArrayList<ArrayList<Integer>>();
            ArrayList<Integer> labels = new ArrayList<Integer>();
            try {
                
                while ((line = reader.readLine()) != null) {
                    if(nsLevel.DEBUG)System.out.println(line);
                    
                    lineIn = line.split(" ");
                    
                    if(seedLabels.containsKey(Long.decode(lineIn[0]))){
                        entryRow = seedLabels.get(Long.decode(lineIn[0]));
                    }
                    
                    for(int i = 1; i < lineIn.length; i++){   
                        labels.add(Integer.decode(lineIn[i]));
                    }
                    entryRow.add(labels);
                    
                    seedLabels.put(Long.decode(lineIn[0]), entryRow );
                        
                }
                
                reader.close();
            } 
            catch (IOException ex) {
                Logger.getLogger(LevelGen.class.getName()).log(Level.SEVERE, null, ex);
            }
           setLength = seedLabels.size();
    }
    
    private void writeLabel(){
        if(seedLabels.size() > setLength){
            try{

                PrintWriter out = new PrintWriter("src/nsmith/seedDataSet.txt");
                
                for(Map.Entry<Long, ArrayList<ArrayList<Integer>>> entry : seedLabels.entrySet()){
                    for(ArrayList<Integer> a_i : entry.getValue()){
                       String line = entry.getKey().toString();
                       for(Integer i_i : a_i){
                           line = line + " " + i_i.toString();
                       }
                       out.println(line); 
                    }
                }
                out.close();
            }
            catch(Exception e){

            }
        }
    }
    
    public void surveyFeedBack() {
        ArrayList<ArrayList<Integer>> preExist = new ArrayList<ArrayList<Integer>>();
        
        if(seedLabels.containsKey(seed)){
            preExist = seedLabels.get(seed);
        }
        
        ArrayList<Integer> val = new ArrayList<Integer>();
        
        Object[] options = {"Yes","No"};
        
        val.add(JOptionPane.showOptionDialog(null,
                "Was this level playable?","Playable",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]));
        
        val.add(JOptionPane.showOptionDialog(null,
                "Was this level enjoyable?","Enjoyable",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]));
        
        val.add(AIUtils.math_geom_avg(nsLevel.getLEVEL().getDifficultyHistory()));
        val.add(AIUtils.std_dev(nsLevel.getLEVEL().getDifficultyHistory()));
        
        preExist.add(val);
        
       seedLabels.put(seed, preExist);
       
       writeLabel();
    }
}
