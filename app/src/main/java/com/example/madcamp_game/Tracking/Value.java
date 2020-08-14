package com.example.madcamp_game.Tracking;

public class Value {

    double X;
    double Y;
    double Z;
    double XZ;
    double YZ;
    String name;

    public Value(String name, double x, double y, double z, double XZ, double YZ) {
        this.name = name;
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.XZ = XZ;
        this.YZ = YZ;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getX() {
        return X;
    }
    public void setX(double x) {
        X = x;
    }
    public double getY() {
        return Y;
    }
    public void setY(double y) {
        Y = y;
    }
    public double getZ() {
        return Z;
    }
    public void setZ(double z) {
        Z = z;
    }
    public double getXZ() {
        return XZ;
    }
    public void setXZ(double XZ) {
        this.XZ = XZ;
    }
    public double getYZ() {
        return YZ;
    }
    public void setYZ(double YZ) {
        this.YZ = YZ;
    }

    public double difference(Value measured_value) {
        return Math.sqrt(Math.pow(measured_value.getX()-this.X,2)
                + Math.pow(measured_value.getY()-this.Y,2)
                + Math.pow(measured_value.getZ()-this.Z,2)
                + Math.pow(measured_value.getXZ()-this.XZ,2)
                + Math.pow(measured_value.getYZ()-this.YZ,2));
    }
}
