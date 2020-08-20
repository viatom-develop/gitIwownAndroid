package com.zeroner.bledemo.bean.sql;

import com.alibaba.json.annotation.JSONField;

public class File_protobuf_80data {

    private int Q;
    private int[] T;
    private Sleep E;
    private Pedo P;
    private HeartRate H;
    private HRV V;

    public int[] parseTime(int hour,int minute) {
        int[] dat = new int[2];
        dat[0] = hour;
        dat[1] = minute;
        return dat;
    }

    public static class Sleep {
        private int[] a;
        private int c;
        private int s;

        public int[] getA() {
            return a;
        }

        public void setA(int[] a) {
            this.a = a;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }
    }

    public static class Pedo {
        private int t;
        private int a;
        private float c;
        private int s;
        private int d;

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public float getC() {
            return c;
        }

        public void setC(float c) {
            this.c = c;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }
    }

    public static class HeartRate {
        private int n;
        private int x;
        private int a;

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    public static class HRV {
        private float s;
        private float r;
        private float p;
        private float m;
        private float f;

        public float getS() {
            return s;
        }

        public void setS(float s) {
            this.s = s;
        }

        public float getR() {
            return r;
        }

        public void setR(float r) {
            this.r = r;
        }

        public float getP() {
            return p;
        }

        public void setP(float p) {
            this.p = p;
        }

        public float getM() {
            return m;
        }

        public void setM(float m) {
            this.m = m;
        }

        public float getF() {
            return f;
        }

        public void setF(float f) {
            this.f = f;
        }
    }

    @JSONField(name = "Q")
    public int getQ() {
        return Q;
    }

    public void setQ(int q) {
        Q = q;
    }

    @JSONField(name = "T")
    public int[] getT() {
        return T;
    }

    public void setT(int[] t) {
        T = t;
    }

    @JSONField(name = "E")
    public Sleep getE() {
        return E;
    }

    public void setE(Sleep e) {
        E = e;
    }

    @JSONField(name = "P")
    public Pedo getP() {
        return P;
    }

    public void setP(Pedo p) {
        P = p;
    }

    @JSONField(name = "H")
    public HeartRate getH() {
        return H;
    }

    public void setH(HeartRate h) {
        H = h;
    }
    @JSONField(name = "V")
    public HRV getV() {
        return V;
    }

    public void setV(HRV v) {
        V = v;
    }
}
