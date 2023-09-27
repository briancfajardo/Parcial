package edu.eci.arsw.math;

import java.util.concurrent.atomic.AtomicInteger;

public class PiDigitsThread extends Thread{

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;
    private int count;
    private int start;
    private Object lock;
    private AtomicInteger counter;
    private byte[] answer;
    public PiDigitsThread (int count, int start, Object lock, AtomicInteger counter){
        this.count = count;
        this.start = start;
        this.lock = lock;
        this.counter = counter;
    }

    public byte[] getAnswer() {
        return answer;
    }

    public void run(){
        answer = calculate(count, start);

    }
    public void pauseThread(){
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private byte[] calculate(int count, int start){
        byte[] digits = new byte[count];

        double sum = 0;

        long startTime = System.currentTimeMillis();
        long elapsedTime;

        for (int i = 0; i < count; i++) {
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);

                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;

            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 5000) {
                pauseThread();
                startTime = System.currentTimeMillis();
            }
            synchronized (lock){
                counter.incrementAndGet();
            }
        }
        return digits;
    }

    private static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }

}
