package com.tjclawson.fft;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Generate {

    public static void main(String[] args) throws Exception {

        // declare constants
        float sampleRate = 44100;
        double frequency1 = 261.626;
        double frequency2 = 329.628;
        double amplitude = 0.5;
        double twoPiFreq1 = 2 * Math.PI * frequency1;
        double twoPiFreq2 = 2 * Math.PI * frequency2;

        // initialize array to store floating point value representing wave form
        double[] buffer = new double[44100 * 2];

        // populate buffer for each sample
        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = amplitude * (Math.sin(twoPiFreq1 * time) + Math.sin(twoPiFreq2 * time)) /2;
        }

        // initialize array to store corresponding byte encoding of each buffer value
        final byte[] byteBuffer = new byte[buffer.length * 2];

        // convert floating point values to signed byte values (-128 to 127)e
        int index = 0;
        for (int i = 0; i < byteBuffer.length; ) {
            int x = (int) (buffer[index++] * 32767);
            byteBuffer[i++] = (byte) x;
            byteBuffer[i++] = (byte) (x >>> 8);
        }

        // declare audio format
        boolean bigEndian = false;
        boolean signed = true;
        int bits = 16;
        int channels = 1;
        AudioFormat format = new AudioFormat(sampleRate, bits, channels, signed, bigEndian);

        // declare source data line to manage transfer of bytes
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

        // open line with audio format
        line.open(format);

        // invoke start method so that it is ready to receive data
        line.start();

        long now = System.currentTimeMillis();

        // write data as single block write
        int written = line.write(byteBuffer, 0, byteBuffer.length);

        // add line.drain() to ensure all bytes are sent to hardware before line is closed
        line.drain();
        // close line
        line.close();

        // output stats
        System.out.println(written + " bytes written");
        long total = System.currentTimeMillis() - now;
        System.out.println(total + " ms.");
    }
}
