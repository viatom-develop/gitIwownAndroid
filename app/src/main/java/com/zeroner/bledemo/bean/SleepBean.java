package com.zeroner.bledemo.bean;


public class SleepBean implements Comparable<SleepBean>{
    private int year;
    private int month;
    private int day;

    public SleepBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SleepBean sleepBean = (SleepBean) o;
        return year == sleepBean.year &&
                month == sleepBean.month &&
                day == sleepBean.day;
    }

    @Override
    public int hashCode() {
        return (String.valueOf(year) + String.valueOf(month)+String.valueOf(day)).hashCode();
    }

    @Override
    public String toString() {
        return "SleepBean{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }


    @Override
    public int compareTo( SleepBean sleepBean) {
        int i = sleepBean.getYear() * 380 + sleepBean.getMonth() * 31 + sleepBean.getDay();
        int i2 = this.getYear() * 380 + this.getMonth() * 31 + this.getDay();
        if (i > i2) {
            return -1;
        } else if (i == i2) {
            return 0;
        } else {
            return 1;
        }
    }
}
