package com.eloraam.redpower.core;

public class MathLib {
   private static Matrix3[] orientMatrixList = new Matrix3[24];
   private static Quat[] orientQuatList = new Quat[24];

   public static void orientMatrix(Matrix3 m, int down, int rot) {
      m.set(orientMatrixList[down * 4 + rot]);
   }

   public static Quat orientQuat(int down, int rot) {
      return new Quat(orientQuatList[down * 4 + rot]);
   }

   static {
      Quat q2 = Quat.aroundAxis(1.0, 0.0, 0.0, Math.PI);

      for(int j = 0; j < 4; ++j) {
         Quat q1 = Quat.aroundAxis(0.0, 1.0, 0.0, -Math.PI / 2 * (double)j);
         orientQuatList[j] = q1;
         q1 = new Quat(q1);
         q1.multiply(q2);
         orientQuatList[j + 4] = q1;
      }

      for(int i = 0; i < 4; ++i) {
         int k = (i >> 1 | i << 1) & 3;
         q2 = Quat.aroundAxis(0.0, 0.0, 1.0, Math.PI / 2);
         q2.multiply(Quat.aroundAxis(0.0, 1.0, 0.0, Math.PI / 2 * (double)(k + 1)));

         for(int var6 = 0; var6 < 4; ++var6) {
            Quat q1 = new Quat(orientQuatList[var6]);
            q1.multiply(q2);
            orientQuatList[8 + 4 * i + var6] = q1;
         }
      }

      for(int var9 = 0; var9 < 24; ++var9) {
         orientMatrixList[var9] = new Matrix3(orientQuatList[var9]);
      }

   }
}
