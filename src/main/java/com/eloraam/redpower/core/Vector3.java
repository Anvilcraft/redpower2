package com.eloraam.redpower.core;

import java.util.Formatter;
import java.util.Locale;

public class Vector3 {
   public double x;
   public double y;
   public double z;

   public Vector3() {
   }

   public Vector3(double xi, double yi, double zi) {
      this.x = xi;
      this.y = yi;
      this.z = zi;
   }

   public Vector3(Vector3 v) {
      this.x = v.x;
      this.y = v.y;
      this.z = v.z;
   }

   public Object clone() {
      return new Vector3(this);
   }

   public void set(double xi, double yi, double zi) {
      this.x = xi;
      this.y = yi;
      this.z = zi;
   }

   public void set(Vector3 v) {
      this.x = v.x;
      this.y = v.y;
      this.z = v.z;
   }

   public double dotProduct(Vector3 v) {
      return v.x * this.x + v.y * this.y + v.z * this.z;
   }

   public double dotProduct(double xi, double yi, double zi) {
      return xi * this.x + yi * this.y + zi * this.z;
   }

   public void crossProduct(Vector3 v) {
      double tx = this.y * v.z - this.z * v.y;
      double ty = this.z * v.x - this.x * v.z;
      double tz = this.x * v.y - this.y * v.x;
      this.x = tx;
      this.y = ty;
      this.z = tz;
   }

   public void add(double xi, double yi, double zi) {
      this.x += xi;
      this.y += yi;
      this.z += zi;
   }

   public void add(Vector3 v) {
      this.x += v.x;
      this.y += v.y;
      this.z += v.z;
   }

   public void subtract(Vector3 v) {
      this.x -= v.x;
      this.y -= v.y;
      this.z -= v.z;
   }

   public void multiply(double d) {
      this.x *= d;
      this.y *= d;
      this.z *= d;
   }

   public double mag() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double magSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public void normalize() {
      double d = this.mag();
      if (d != 0.0) {
         this.multiply(1.0 / d);
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      Formatter fmt = new Formatter(sb, Locale.US);
      fmt.format("Vector:\n");
      fmt.format("  < %f %f %f >\n", this.x, this.y, this.z);
      return sb.toString();
   }
}
