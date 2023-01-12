package com.eloraam.redpower.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class RenderModel {
    public Vector3[] vertices;
    public TexVertex[][] texs;
    int[][][] groups;

    public static RenderModel loadModel(String location) {
        try {
            IResource resource
                = Minecraft.getMinecraft().getResourceManager().getResource(
                    new ResourceLocation(location)
                );
            InputStream is = resource.getInputStream();
            RenderModel.ModelReader ml = new RenderModel.ModelReader();
            ml.readModel(is);
            List<TexVertex[]> vtl = new ArrayList();
            int i = 0;

            while (i < ml.faceno.size()) {
                TexVertex[] tr = new TexVertex[4];

                for (int lgs = 0; lgs < 4; ++lgs) {
                    int lgmn = ml.faceno.get(i);
                    ++i;
                    if (lgmn < 0) {
                        throw new IllegalArgumentException("Non-Quad Face");
                    }

                    int lgsn = ml.faceno.get(i);
                    ++i;
                    TexVertex t = ((TexVertex) ml.texvert.get(lgsn - 1)).copy();
                    t.vtx = lgmn - 1;
                    t.v = 1.0 - t.v;
                    tr[lgs] = t;
                }

                int var15 = ml.faceno.get(i);
                ++i;
                if (var15 >= 0) {
                    throw new IllegalArgumentException("Non-Quad Face");
                }

                vtl.add(tr);
            }

            RenderModel model = new RenderModel();
            model.vertices = (Vector3[]) ml.vertex.toArray(new Vector3[0]);
            model.texs = (TexVertex[][]) vtl.toArray(new TexVertex[0][]);
            model.groups = new int[ml.grcnt.size()][][];

            for (int var13 = 0; var13 < ml.grcnt.size(); ++var13) {
                int lgs = ml.grcnt.get(var13);
                model.groups[var13] = new int[lgs][];

                for (int lgmn = 0; lgmn < ml.grcnt.get(var13); ++lgmn) {
                    model.groups[var13][lgmn] = new int[2];
                }
            }

            i = 0;
            int lgs = -1;
            int lgmn = -1;

            int lgsn;
            for (lgsn = -1; i < ml.groups.size(); i += 3) {
                if (lgs >= 0) {
                    model.groups[lgmn][lgsn][0] = lgs;
                    model.groups[lgmn][lgsn][1] = ml.groups.get(i + 2);
                }

                lgmn = ml.groups.get(i);
                lgsn = ml.groups.get(i + 1);
                lgs = ml.groups.get(i + 2);
            }

            if (lgs >= 0) {
                model.groups[lgmn][lgsn][0] = lgs;
                model.groups[lgmn][lgsn][1] = ml.fno;
            }

            return model;
        } catch (IOException var11) {
            var11.printStackTrace();
            return null;
        }
    }

    public RenderModel scale(double factor) {
        for (Vector3 vertex : this.vertices) {
            vertex.multiply(factor);
        }

        return this;
    }

    public static class ModelReader {
        public List<Vector3> vertex = new ArrayList();
        public List<Integer> faceno = new ArrayList();
        public List<TexVertex> texvert = new ArrayList();
        public List<Integer> groups = new ArrayList();
        public List<Integer> grcnt = new ArrayList();
        int fno = 0;

        private void eatLine(StreamTokenizer tok) throws IOException {
            while (tok.nextToken() != -1) {
                if (tok.ttype == 10) {
                    return;
                }
            }
        }

        private void endLine(StreamTokenizer tok) throws IOException {
            if (tok.nextToken() != 10) {
                throw new IllegalArgumentException("Parse error");
            }
        }

        private double getFloat(StreamTokenizer tok) throws IOException {
            if (tok.nextToken() != -2) {
                throw new IllegalArgumentException("Parse error");
            } else {
                return tok.nval;
            }
        }

        private int getInt(StreamTokenizer tok) throws IOException {
            if (tok.nextToken() != -2) {
                throw new IllegalArgumentException("Parse error");
            } else {
                return (int) tok.nval;
            }
        }

        private void parseFace(StreamTokenizer tok) throws IOException {
            while (true) {
                tok.nextToken();
                if (tok.ttype == -1 || tok.ttype == 10) {
                    this.faceno.add(-1);
                    ++this.fno;
                    return;
                }

                if (tok.ttype != -2) {
                    throw new IllegalArgumentException("Parse error");
                }

                int n1 = (int) tok.nval;
                if (tok.nextToken() != 47) {
                    throw new IllegalArgumentException("Parse error");
                }

                int n2 = this.getInt(tok);
                this.faceno.add(n1);
                this.faceno.add(n2);
            }
        }

        private void setGroup(int gr, int sub) {
            this.groups.add(gr);
            this.groups.add(sub);
            this.groups.add(this.fno);
            if (this.grcnt.size() < gr) {
                throw new IllegalArgumentException("Parse error");
            } else {
                if (this.grcnt.size() == gr) {
                    this.grcnt.add(0);
                }

                this.grcnt.set(gr, Math.max(this.grcnt.get(gr), sub + 1));
            }
        }

        private void parseGroup(StreamTokenizer tok) throws IOException {
            int n1 = this.getInt(tok);
            int n2 = 0;
            tok.nextToken();
            if (tok.ttype == 95) {
                n2 = this.getInt(tok);
                tok.nextToken();
            }

            this.setGroup(n1, n2);
            if (tok.ttype != 10) {
                throw new IllegalArgumentException("Parse error");
            }
        }

        public void readModel(InputStream fis) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            StreamTokenizer tok = new StreamTokenizer(r);
            tok.commentChar(35);
            tok.eolIsSignificant(true);
            tok.lowerCaseMode(false);
            tok.parseNumbers();
            tok.quoteChar(34);
            tok.ordinaryChar(47);

            while (tok.nextToken() != -1) {
                if (tok.ttype != 10) {
                    if (tok.ttype != -3) {
                        throw new IllegalArgumentException("Parse error");
                    }

                    String var4 = tok.sval;
                    switch (var4) {
                        case "v":
                            Vector3 f1 = new Vector3();
                            f1.x = this.getFloat(tok);
                            f1.y = this.getFloat(tok);
                            f1.z = this.getFloat(tok);
                            this.vertex.add(f1);
                            this.endLine(tok);
                            break;
                        case "vt": {
                            double f11 = this.getFloat(tok);
                            double f2 = this.getFloat(tok);
                            this.texvert.add(new TexVertex(0, f11, f2));
                            this.endLine(tok);
                            break;
                        }
                        case "vtc": {
                            double f11 = this.getFloat(tok);
                            double f2 = this.getFloat(tok);
                            TexVertex tv = new TexVertex(0, f11, f2);
                            tv.r = (float) this.getFloat(tok);
                            tv.g = (float) this.getFloat(tok);
                            tv.b = (float) this.getFloat(tok);
                            this.texvert.add(tv);
                            this.endLine(tok);
                            break;
                        }
                        case "f":
                            this.parseFace(tok);
                            break;
                        case "g":
                            this.parseGroup(tok);
                            break;
                        default:
                            this.eatLine(tok);
                    }
                }
            }

            fis.close();
        }
    }
}
