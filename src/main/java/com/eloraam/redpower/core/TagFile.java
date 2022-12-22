package com.eloraam.redpower.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class TagFile {
   private TreeMap<String, Object> contents = new TreeMap();
   private TreeMap<String, String> comments = new TreeMap();
   private String filecomment = "";

   public void addTag(String name, Object tag) {
      int idx = 0;
      TreeMap<String, Object> sub = this.contents;

      while(true) {
         int nid = name.indexOf(46, idx);
         if (nid < 0) {
            String p = name.substring(idx);
            if (p.equals("")) {
               throw new IllegalArgumentException("Empty key name");
            } else {
               sub.put(p, tag);
               return;
            }
         }

         String p = name.substring(idx, nid);
         idx = nid + 1;
         if (p.equals("")) {
            throw new IllegalArgumentException("Empty key name");
         }

         Object ob = sub.get(p);
         if (ob == null) {
            TreeMap<String, Object> tmp = new TreeMap();
            sub.put(p, tmp);
            sub = tmp;
         } else {
            if (!(ob instanceof TreeMap)) {
               throw new IllegalArgumentException("Key not a dictionary");
            }

            sub = (TreeMap)ob;
         }
      }
   }

   public Object getTag(String name) {
      int idx = 0;
      TreeMap<String, Object> sub = this.contents;

      while(true) {
         int nid = name.indexOf(46, idx);
         if (nid < 0) {
            String p = name.substring(idx);
            return sub.get(p);
         }

         String p = name.substring(idx, nid);
         idx = nid + 1;
         Object ob = sub.get(p);
         if (!(ob instanceof TreeMap)) {
            return null;
         }

         sub = (TreeMap)ob;
      }
   }

   public Object removeTag(String name) {
      int idx = 0;
      TreeMap<String, Object> sub = this.contents;

      while(true) {
         int nid = name.indexOf(46, idx);
         if (nid < 0) {
            String p = name.substring(idx);
            return sub.remove(p);
         }

         String p = name.substring(idx, nid);
         idx = nid + 1;
         Object ob = sub.get(p);
         if (!(ob instanceof TreeMap)) {
            return null;
         }

         sub = (TreeMap)ob;
      }
   }

   public void commentTag(String k, String v) {
      this.comments.put(k, v);
   }

   public void commentFile(String cmt) {
      this.filecomment = cmt;
   }

   public void addString(String name, String value) {
      this.addTag(name, value);
   }

   public void addInt(String name, int value) {
      this.addTag(name, value);
   }

   public String getString(String name) {
      Object ob = this.getTag(name);
      return !(ob instanceof String) ? null : (String)ob;
   }

   public String getString(String name, String _default) {
      Object ob = this.getTag(name);
      if (ob == null) {
         this.addTag(name, _default);
         return _default;
      } else {
         return !(ob instanceof String) ? _default : (String)ob;
      }
   }

   public int getInt(String name) {
      return this.getInt(name, 0);
   }

   public int getInt(String name, int _default) {
      Object ob = this.getTag(name);
      if (ob == null) {
         this.addTag(name, _default);
         return _default;
      } else {
         return !(ob instanceof Integer) ? 0 : (Integer)ob;
      }
   }

   private void writeComment(PrintStream ps, String indent, String cmt) {
      if (cmt != null) {
         for(String s : cmt.split("\n")) {
            ps.printf("%s# %s\n", indent, s);
         }
      }

   }

   private String collapsedTag(TreeMap<String, Object> tag, String key, String ft) {
      String cn = key;

      String k;
      for(Object ob = tag.get(key); this.comments.get(ft) == null; ft = ft + "." + k) {
         if (ob instanceof String) {
            return cn + "=\"" + ((String)ob).replace("\"", "\\\"") + "\"";
         }

         if (ob instanceof Integer) {
            return cn + "=" + ob;
         }

         tag = (TreeMap)ob;
         if (tag.size() != 1) {
            return null;
         }

         k = (String)tag.firstKey();
         cn = cn + "." + k;
         ob = tag.get(k);
      }

      return null;
   }

   private void saveTag(PrintStream ps, TreeMap<String, Object> tag, String name, String indent) throws IOException {
      for(String k : tag.keySet()) {
         String ft = name != null ? name + "." + k : k;
         this.writeComment(ps, indent, (String)this.comments.get(ft));
         Object ob = tag.get(k);
         if (ob instanceof String) {
            ps.printf("%s%s=\"%s\"\n", indent, k, ((String)ob).replace("\"", "\\\""));
         } else if (ob instanceof Integer) {
            ps.printf("%s%s=%d\n", indent, k, ob);
         } else if (ob instanceof TreeMap) {
            String ct = this.collapsedTag(tag, k, ft);
            if (ct != null) {
               ps.printf("%s%s\n", indent, ct);
            } else {
               ps.printf("%s%s {\n", indent, k);
               this.saveTag(ps, (TreeMap<String, Object>)ob, ft, indent + "    ");
               ps.printf("%s}\n\n", indent);
            }
         }
      }

   }

   public void saveFile(File file) {
      try {
         FileOutputStream os = new FileOutputStream(file);
         PrintStream ps = new PrintStream(os);
         this.writeComment(ps, "", this.filecomment);
         this.saveTag(ps, this.contents, null, "");
         ps.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   private static void readTag(TreeMap<String, Object> tag, StreamTokenizer tok) throws IOException {
      label61:
      while(tok.nextToken() != -1 && tok.ttype != 125) {
         if (tok.ttype != 10) {
            if (tok.ttype != -3) {
               throw new IllegalArgumentException("Parse error");
            }

            String key = tok.sval;
            TreeMap<String, Object> ltag = tag;

            while(true) {
               Object obtag;
               switch(tok.nextToken()) {
                  case 46:
                     obtag = ltag.get(key);
                     if (!(obtag instanceof TreeMap)) {
                        TreeMap<String, Object> ttag = new TreeMap();
                        ltag.put(key, ttag);
                        ltag = ttag;
                     } else {
                        ltag = (TreeMap)obtag;
                     }

                     tok.nextToken();
                     if (tok.ttype != -3) {
                        throw new IllegalArgumentException("Parse error");
                     }

                     key = tok.sval;
                     break;
                  case 61:
                     tok.nextToken();
                     if (tok.ttype == -2) {
                        ltag.put(key, (int)tok.nval);
                     } else {
                        if (tok.ttype != 34) {
                           throw new IllegalArgumentException("Parse error");
                        }

                        ltag.put(key, tok.sval);
                     }

                     tok.nextToken();
                     if (tok.ttype == 10) {
                        continue label61;
                     }

                     throw new IllegalArgumentException("Parse error");
                  case 123:
                     obtag = ltag.get(key);
                     if (!(obtag instanceof TreeMap)) {
                        TreeMap<String, Object> ttag = new TreeMap();
                        ltag.put(key, ttag);
                        ltag = ttag;
                     } else {
                        ltag = (TreeMap)obtag;
                     }

                     readTag(ltag, tok);
                     tok.nextToken();
                     if (tok.ttype == 10) {
                        continue label61;
                     }

                     throw new IllegalArgumentException("Parse error");
                  default:
                     throw new IllegalArgumentException("Parse error");
               }
            }
         }
      }

   }

   public static TagFile loadFile(File file) {
      TagFile tagFile = new TagFile();

      try {
         FileInputStream stream = new FileInputStream(file);
         tagFile.readStream(stream);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return tagFile;
   }

   public void readFile(File file) {
      try {
         FileInputStream stream = new FileInputStream(file);
         this.readStream(stream);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void readStream(InputStream stream) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
         StreamTokenizer tokenizer = new StreamTokenizer(reader);
         tokenizer.commentChar(35);
         tokenizer.eolIsSignificant(true);
         tokenizer.lowerCaseMode(false);
         tokenizer.parseNumbers();
         tokenizer.quoteChar(34);
         tokenizer.ordinaryChar(61);
         tokenizer.ordinaryChar(123);
         tokenizer.ordinaryChar(125);
         tokenizer.ordinaryChar(46);
         readTag(this.contents, tokenizer);
         stream.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   TagFile.Query query(String pattern) {
      return new TagFile.Query(pattern);
   }

   public class Query implements Iterable<Object> {
      String[] pattern;

      private Query(String pat) {
         this.pattern = pat.split("\\.");
      }

      public Iterator<Object> iterator() {
         return new TagFile.Query.QueryIterator();
      }

      public class QueryIterator implements Iterator<Object> {
         ArrayList<TagFile.QueryEntry> path = new ArrayList();
         String lastentry;

         private QueryIterator() {
            if (!this.step0(0, TagFile.this.contents, "")) {
               this.step();
            }

         }

         private void step() {
            while(this.path != null) {
               if (this.step1()) {
                  return;
               }
            }

         }

         private boolean step1() {
            TagFile.QueryEntry qe = (TagFile.QueryEntry)this.path.get(this.path.size() - 1);
            if (!qe.iter.hasNext()) {
               this.path.remove(this.path.size() - 1);
               if (this.path.size() == 0) {
                  this.path = null;
               }

               return false;
            } else {
               String str = (String)qe.iter.next();
               String sp = qe.path.equals("") ? str : qe.path + "." + str;
               if (qe.lvl == Query.this.pattern.length - 1) {
                  this.lastentry = sp;
                  return true;
               } else {
                  Object ob = qe.tag.get(str);
                  return ob instanceof TreeMap && this.step0(qe.lvl + 1, (TreeMap<String, Object>)ob, sp);
               }
            }
         }

         private boolean step0(int lvl0, TreeMap<String, Object> p, String sp) {
            for(int lvl = lvl0; lvl < Query.this.pattern.length; ++lvl) {
               if (Query.this.pattern[lvl].equals("%")) {
                  TagFile.QueryEntry var6 = new TagFile.QueryEntry();
                  var6.path = sp;
                  var6.tag = p;
                  var6.lvl = lvl;
                  var6.iter = p.keySet().iterator();
                  this.path.add(var6);
                  return false;
               }

               Object ob = p.get(Query.this.pattern[lvl]);
               if (sp.equals("")) {
                  sp = Query.this.pattern[lvl];
               } else {
                  sp = sp + "." + Query.this.pattern[lvl];
               }

               if (!(ob instanceof TreeMap)) {
                  if (lvl == Query.this.pattern.length - 1) {
                     this.lastentry = sp;
                     return true;
                  }
                  break;
               }

               p = (TreeMap)ob;
            }

            this.path.remove(this.path.size() - 1);
            if (this.path.size() == 0) {
               this.path = null;
            }

            return false;
         }

         public boolean hasNext() {
            return this.path != null;
         }

         public String next() {
            String tr = this.lastentry;
            this.step();
            return tr;
         }

         public void remove() {
         }
      }
   }

   private static class QueryEntry {
      public TreeMap<String, Object> tag;
      public Iterator<String> iter;
      public String path;
      int lvl;

      private QueryEntry() {
      }
   }
}
