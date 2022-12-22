package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import java.io.File;
import net.minecraft.world.World;

public class DiskLib {
   public static File getSaveDir(World world) {
      File tr = new File(RedPowerCore.getSaveDir(world), "redpower");
      tr.mkdirs();
      return tr;
   }

   public static String generateSerialNumber(World world) {
      StringBuilder tr = new StringBuilder();

      for(int i = 0; i < 16; ++i) {
         tr.append(String.format("%01x", world.rand.nextInt(16)));
      }

      return tr.toString();
   }

   public static File getDiskFile(File dir, String serno) {
      return new File(dir, String.format("disk_%s.img", serno));
   }
}
