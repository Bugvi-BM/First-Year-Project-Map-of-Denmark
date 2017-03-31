package KDtree;

import Model.Element;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;

/**
 * Created by Jakob on 30-03-2017.
 */
public class Pointer extends Point {
    private Element element;

    public Pointer(float x, float y, Element element){
        super(x, y);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}