package odd.views;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;


public class OddLink {

    Long fromId;
    Long toId;
    OddObject from;
    OddObject to;
    String name;

    public OddLink(Long fromId, Long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public synchronized void drawOn(javanimation g) {

        OddObject from = g.diagram.getById(fromId);
//        from.getTypeFieldValueList().forEach(stringStringFieldValueTupple4 -> {
//            Field f = stringStringFieldValueTupple4.getC();
//            Value v = stringStringFieldValueTupple4.getD();
//            if(v instanceof ObjectReference){
//                ObjectReference objectReference = (ObjectReference) v;
//                System.out.println(f.name());
//                if(objectReference.uniqueID() == to.getObjectReferenceType().uniqueID()){
//                    name = f.name();
//                }
//            }
//        });
        OddObject to = g.diagram.getById(toId);
//        System.out.println("FROM" + from);
        if (from == null) return;
        try {
            if (name == null) {
                from.getObjectReferenceType().referenceType().allFields().forEach(field -> {
                    Value v = from.getObjectReferenceType().getValue(field);
                    if (v instanceof ObjectReference) {
                        ObjectReference objectReference = (ObjectReference) v;
//                System.out.println(objectReference.uniqueID() + "@" +field.name());
//                System.out.println("to: " + to.getObjectReferenceType().uniqueID());
                        if (objectReference.uniqueID() == to.getObjectReferenceType().uniqueID()) {
//                    System.out.println("set name");
                            name = field.name();
                        }
                    }
                });
            }
        } catch (VMDisconnectedException e) {
            g.init();
        }

//        if (from.getObjectReferenceType().referenceType().name().equals("java.util.ArrayList")) {
//            g.ellipse(0, 0, from.centerX(), from.centerY());
//            return;
//        }

//        if (from != null && to != null) {
//            g.line(from.centerX(), from.centerY(), to.centerX(), to.centerY());

//            double pt1DirX = to.centerX() - from.centerX();
//            double pt1DirY = to.centerY() - from.centerY();
//            double dx = pt1DirX / 100;
//            double dy = pt1DirY / 100;
//            double x = to.centerX() - dx;
//            double y = to.centerY() - dy;
//            while(to.containtsPoint((float)x, (float)y)){
//                x -= dx;
//                y -= dy;
//            }
//            g.ellipse((float)x, (float)y, 10, 10);
//            //Float[][] pts = {{to.x, to.y}, {to.x + to.width, to.y}, {to.x + to.width, to.y + to.height}, {to.x , to.y + to.height}, {to.x, to.y}};
//
//        }
        if (from != null && to != null) {
            float[] fromXY = crossPoint(from, from.centerX(), from.centerY(), to.centerX(), to.centerY());
            float[] toXY = crossPoint(to, to.centerX(), to.centerY(), from.centerX(), from.centerY());

            if (fromXY == null || toXY == null) return;
//            if (name != null)
//                g.text(name, (fromXY[0] + toXY[0]) / 2, (fromXY[1] + toXY[1]) / 2);

            double len = Math.sqrt((from.centerX() - to.centerX()) * (from.centerX() - to.centerX()) + (from.centerY() - to.centerY()) * (from.centerY() - to.centerY()));
            double unitX = (from.centerX() - to.centerX()) / len;
            double unitY = (from.centerY() - to.centerY()) / len;
            double a1X = unitX * Math.cos(Math.PI * 20 / 180) - unitY * Math.sin(Math.PI * 20 / 180);
            double a1Y = unitX * Math.sin(Math.PI * 20 / 180) + unitY * Math.cos(Math.PI * 20 / 180);
            double a2X = unitX * Math.cos(Math.PI * -20 / 180) - unitY * Math.sin(Math.PI * -20 / 180);
            double a2Y = unitX * Math.sin(Math.PI * -20 / 180) + unitY * Math.cos(Math.PI * -20 / 180);
//            g.line(fromXY[0], fromXY[1], toXY[0], toXY[1]);
//            g.line(toXY[0], toXY[1], (float) (a1X * 15 + toXY[0]), (float) (a1Y * 15 + toXY[1]));
//            g.line(toXY[0], toXY[1], (float) (a2X * 15 + toXY[0]), (float) (a2Y * 15 + toXY[1]));
//            g.line(from.centerX(), from.centerY(), to.centerX(), to.centerY());
//            double pt1DirX = to.centerX() - from.centerX();
//            double pt1DirY = to.centerY() - from.centerY();
//            Float[][] pts = {{to.x, to.y}, {to.x + to.width, to.y}, {to.x + to.width, to.y + to.height}, {to.x, to.y + to.height}, {to.x, to.y}};
//            for (int i = 0; i < pts.length - 1; i++) {
//                Float[] pt1 = pts[i];
//                Float[] pt2 = pts[i + 1];
//                double pt2DirX = pt2[0] - pt1[0];
//                double pt2DirY = pt2[1] - pt1[1];
//                double det = pt1DirX * pt2DirY - pt1DirY * pt2DirX;
//                double deltaPtX = pt1[0] - from.centerX();
//                double deltaPtY = pt1[1] - from.centerY();
//                double alpha = deltaPtX * pt2DirY - deltaPtY * pt2DirX;
//                double beta = deltaPtX * pt1DirY - deltaPtY * pt1DirX;
//                if (det == 0) continue;
//                if (alpha * det < 0) continue;
//                if (beta * det < 0) continue;
//                if (det > 0) {
//                    if (alpha > det || beta > det) continue;
//                } else {
//                    if (alpha < det || beta < det) continue;
//                }
//                double crossX = from.centerX() + (alpha * pt1DirX) / det;
//                double crossY = from.centerY() + (alpha * pt1DirY / det);
//                double len = Math.sqrt((from.centerX() - to.centerX()) * (from.centerX() - to.centerX()) + (from.centerY() - to.centerY()) * (from.centerY() - to.centerY()));
//                double unitX = (from.centerX() - to.centerX()) / len;
//                double unitY = (from.centerY() - to.centerY()) / len;
//                double a1X = unitX * Math.cos(Math.PI * 20 / 180) - unitY * Math.sin(Math.PI * 20 / 180);
//                double a1Y = unitX * Math.sin(Math.PI * 20 / 180) + unitY * Math.cos(Math.PI * 20 / 180);
//                double a2X = unitX * Math.cos(Math.PI * -20 / 180) - unitY * Math.sin(Math.PI * -20 / 180);
//                double a2Y = unitX * Math.sin(Math.PI * -20 / 180) + unitY * Math.cos(Math.PI * -20 / 180);
//                g.line((float) crossX, (float) crossY, (float) (a1X * 15 + crossX), (float) (a1Y * 15 + crossY));
//                g.line((float) crossX, (float) crossY, (float) (a2X * 15 + crossX), (float) (a2Y * 15 + crossY));
//
//            }
        }
    }

    public float[] crossPoint(OddObject oddObject, float fromX, float fromY, float toX, float toY) {
        double pt1DirX = toX - fromX;
        double pt1DirY = toY - fromY;
        Float[][] pts = {{oddObject.x, oddObject.y}, {oddObject.x + oddObject.width, oddObject.y},
                {oddObject.x + oddObject.width, oddObject.y + oddObject.height},
                {oddObject.x, oddObject.y + oddObject.height}, {oddObject.x, oddObject.y}};
        for (int i = 0; i < pts.length - 1; i++) {
            Float[] pt1 = pts[i];
            Float[] pt2 = pts[i + 1];
            double pt2DirX = pt2[0] - pt1[0];
            double pt2DirY = pt2[1] - pt1[1];
            double det = pt1DirX * pt2DirY - pt1DirY * pt2DirX;
            double deltaPtX = pt1[0] - oddObject.centerX();
            double deltaPtY = pt1[1] - oddObject.centerY();
            double alpha = deltaPtX * pt2DirY - deltaPtY * pt2DirX;
            double beta = deltaPtX * pt1DirY - deltaPtY * pt1DirX;
            if (det == 0) continue;
            if (alpha * det < 0) continue;
            if (beta * det < 0) continue;
            if (det > 0) {
                if (alpha > det || beta > det) continue;
            } else {
                if (alpha < det || beta < det) continue;
            }
            double crossX = oddObject.centerX() + (alpha * pt1DirX) / det;
            double crossY = oddObject.centerY() + (alpha * pt1DirY / det);
            return new float[]{(float) crossX, (float) crossY};

        }
        return null;
    }

    public String getLinkString(){
    	return this.fromId.toString() + "->" +this.toId.toString();
    }
}
