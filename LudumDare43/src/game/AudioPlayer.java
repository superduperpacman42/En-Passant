package game;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Plays audio files
 */
public class AudioPlayer {

    private String filename = "";
    private Thread thread;
    private boolean paused;
    
    public AudioPlayer(String filename){
        this.filename = "/sounds/"+filename;
    }
    
    public AudioPlayer(){
    }

    /**
     * Plays a given sound file in the project's "sounds" folder
     * @param filename - Name of the sound file
     */
    public void play(String filename) {
        this.filename = "sounds/"+filename;
        play();
    }
    
    /**
     * Plays the last used sound file
     */
    public void play() {
        paused = false;
        thread = new Thread(new SoundThread());
        thread.start();
    }
    
    /**
     * Stops the current sound from playing
     */
    public void stop(){
        paused = true;
    }
    
    // Runs audio on a separate thread
    private class SoundThread implements Runnable {
        @Override
        public void run() {
            AudioInputStream audioInputStream;
            SourceDataLine line;
            try {
                InputStream stream = getClass().getResourceAsStream(filename);
                audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
            } catch (UnsupportedAudioFileException e) {
                System.err.println("File not supported: " + e);
                return;
            } catch (IOException e) {
                System.err.println("File not found: " + e);
                return;
            }
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
            } catch (LineUnavailableException e) {
                System.err.println("Line unavailable");
                return;
            } catch (Exception e) {
                System.err.println("Could not open line: " + e);
                return;
            }
            line.start();
            int nBytesRead = 0;
            byte[] abData = new byte[128000];
            while (nBytesRead != -1) {
                if(paused){
                    line.close();
                    try {
                        audioInputStream.close();
                    } catch (IOException ex) {
                        System.err.println("Could not close file");
                    }
                    return;
                }
                try {
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                } catch (IOException e) {
                    System.out.println("Could not read from file");
                    return;
                }
                if (nBytesRead >= 0) {
                    line.write(abData, 0, nBytesRead);
                }
            }
            line.close();
            try {
                audioInputStream.close();
            } catch (IOException ex) {
                System.err.println("Could not close file");
            }
        }
    }
    
    public static void initTestProgram(){
        final AudioPlayer audio = new AudioPlayer();

        JButton startButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                audio.play("OpenSource.wav");
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                audio.stop();
            }
        });
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(startButton, BorderLayout.NORTH);
        frame.add(stopButton, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
}