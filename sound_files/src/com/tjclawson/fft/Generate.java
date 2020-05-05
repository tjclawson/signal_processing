package com.tjclawson.fft;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Generate {

    public static void main(String[] args) throws Exception {

        // declare constants
        float sampleRate = 44100;
        double frequency = 261.626;
        double amplitude = 0.5;
        double twoPiF = 2 * Math.PI * frequency;

        // initialize array to store floating point value representing wave form
        double[] buffer = new double[44100];

        // populate buffer for each sample
        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = amplitude * Math.sin(twoPiF * time);
        }

        // initialize array to store corresponding byte encoding of each buffer value
        final byte[] byteBuffer = new byte[buffer.length];

        // convert floating point values to signed byte values (-128 to 127)e
        int index = 0;
        for (int i = 0; i < byteBuffer.length; ) {
            int x = (int) (buffer[index++] * 127);
            byteBuffer[i++] = (byte) x;
        }

        // declare audio format
        boolean bigEndian = false;
        boolean signed = true;
        int bits = 8;
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
