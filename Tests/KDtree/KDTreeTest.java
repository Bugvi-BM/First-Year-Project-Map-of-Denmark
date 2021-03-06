package KDtree;

import Helpers.Shapes.PolygonApprox;
import Model.Elements.Building;
import Model.Elements.SuperElement;
import OSM.OSMWay;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * The * on the notes in this test class refer to external notes.
 */
public class KDTreeTest {

    private KDTree tree;

    @Before
    public void setUp() throws Exception {
        /*
         * A Nodegenerator instance is created.
         * 15 Point2D.Floats are made and added to the nodegenerator.
         */
        NodeGenerator nodeGenerator = new NodeGenerator(15, 2);
        tree = new KDTree();

         // Adds the Point2D.Floats with the coordinates (1,1) , (2,2) , ... , (15,15)
        for (int i = 1; i < 16; i++) {
            nodeGenerator.addPoint(new Point2D.Float(i, i));
        }

        //Initialise the nodegenerator (finding medians and the correct nodes for the KDTree).
        nodeGenerator.initialise();

        //Applying the nodegenerators data to the KDTree that is used for this test.
        nodeGenerator.setupTree(tree);

        /*
         * Making a building instance for each Point2D.Floats generated above.
         * A pointer for each building is added to the KDTree such that there is
         * a pointer for each point (1,1) , (2,2) , ... , (15,15)
         */
        for (int i = 1; i < 16; i++) {
            OSMWay way = new OSMWay();
            way.add(new Point2D.Float(i, i));
            PolygonApprox polygonApprox = new PolygonApprox(way);
            Building building = new Building(polygonApprox);
            Pointer p = new Pointer(i,i, building);
            tree.putPointer(p);
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void putNode() throws Exception {
        //Making a new KD tree
        tree = new KDTree();

        //Testing if the field root is null
        Field field = tree.getClass().getDeclaredField("root");
        field.setAccessible(true);
        assertNull(field.get(tree));

        /* 1 - with null as input */
        /* 2 - parent is null and input is null - statement 2 is true */
        //Testing with input as null when the field root is null
        tree.putNode(null);
        assertNull(field.get(tree));

        /* 1 - with a Node as input */
        /* 2 - parent is null and input is a Node - statement 2 is true */
        //Making the first Node to put, this should be the new root and have depth 0
        Node testNode1 = new Node(8,8,0);
        tree.putNode(testNode1);
        assertEquals(testNode1, field.get(tree));

        //Making the second and third Nodes to put. These should have depth 1
        Node testNode2 = new Node(4,4,1);
        Node testNode3 = new Node(12,12,1);
        /* 2 - statement 2 is false */
        /* 3 - statement 3 is true */
        /* 4 - statement 4 true*/
        tree.putNode(testNode2);
        /* 2 - statement 2 is false */
        /* 3 - statement 3 is true */
        /* 4 - statement 4 false*/
        tree.putNode(testNode3);
        Node root = (Node)field.get(tree);
        assertEquals(testNode2, root.getLeft());
        assertEquals(testNode3, root.getRight());

        //Making the fourth and the fifth Nodes to put. These should have depth 2
        Node testNode4 = new Node(2,2,2);
        Node testNode5 = new Node(6,6,2);
        /* 2 - statement 2 is false */
        /* 3 - statement 3 is false */
        /* 4 - statement 5 true */
        tree.putNode(testNode4);
        /* 2 - statement 2 is false */
        /* 3 - statement 3 is false */
        /* 4 - statement 5 false */
        tree.putNode(testNode5);
        root = (Node)field.get(tree);
        assertEquals(testNode4, root.getLeft().getLeft());
        assertEquals(testNode5, root.getLeft().getRight());
    }

    @Test
    public void putPointer() throws Exception {
        //Using the premade KDTree, but clearing it, such that no pointers are stored in the leaves.
        assertEquals(15, tree.getAllSections().size(),0);
        tree.clear();
        assertEquals(0, tree.getAllSections().size(),0);

        /* 6 - with a Pointer as input */
        /* 7 - statement 7 is true, true and then false in the recursion sequence */
        /* 8 - statement 8 is true and then false in the recursion sequence */
        /* 9 - statement 9 is true */
        /* 10 - statement 10 is true */
        //Making a pointer to be put into the root-left-left leaf of the KDTree
        OSMWay way = new OSMWay();
        way.add(new Point2D.Float(1, 1));
        PolygonApprox polygonApprox = new PolygonApprox(way);
        Building building1 = new Building(polygonApprox);
        Pointer p = new Pointer(1,1, building1);
        tree.putPointer(p);
        /*
         * Checking if the pointer, which points at the building1 instance,
         * is to be found in the root-left-left section of the KDTree.
         */
        HashSet set = tree.getManySections(1,1,3,3);
        assertTrue(set.contains(building1));

        /* 6 - with a Pointer as input */
        /* 7 - statement 7 is true, true and then false in the recursion sequence */
        /* 8 - statement 8 is true and then false in the recursion sequence */
        /* 9 - statement 9 is true */
        /* 10 - statement 10 is false */
        //Making a pointer to be put into the root-left-right leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(5, 5));
        polygonApprox = new PolygonApprox(way);
        Building building5 = new Building(polygonApprox);
        p = new Pointer(5,5, building5);
        tree.putPointer(p);
        /*
         * Checking if the pointer, which points at the building5 instance,
         * is to be found in the root-left-right section of the KDTree.
         */
        set = tree.getManySections(5,5,7,7);
        assertTrue(set.contains(building5));

        /* 6 - with a Pointer as input */
        /* 7 - statement 7 is true, true and then false in the recursion sequence */
        /* 8 - statement 8 is true and then false in the recursion sequence */
        /* 9 - statement 9 is false */
        /* 10 - statement 10 is true */
        //Making a pointer to be put into the root-right-left leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(9, 9));
        polygonApprox = new PolygonApprox(way);
        Building building9 = new Building(polygonApprox);
        p = new Pointer(9,9, building9);
        tree.putPointer(p);
        /*
         * Checking if the pointer, which points at the building5 instance,
         * is to be found in the root-left-right section of the KDTree.
         */
        set = tree.getManySections(9,9,11,11);
        assertTrue(set.contains(building9));

        /* 6 - with a Pointer as input */
        /* 7 - statement 7 is true, true and then false in the recursion sequence */
        /* 8 - statement 8 is true and then false in the recursion sequence */
        /* 9 - statement 9 is false */
        /* 10 - statement 10 is false */
        //Making a pointer to be put into the root-right-right leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(15, 15));
        polygonApprox = new PolygonApprox(way);
        Building building15 = new Building(polygonApprox);
        p = new Pointer(15,15, building15);
        tree.putPointer(p);
        /*
         * Checking if the pointer, which points at the building5 instance,
         * is to be found in the root-left-right section of the KDTree.
         */
        set = tree.getManySections(13,13,15,15);
        assertTrue(set.contains(building15));
    }

    @Test
    public void clear() throws Exception {
        //Making sure that the tree contains 15 elements.
        HashSet<SuperElement> set = tree.getAllSections();
        assertEquals(set.size(), 15);

        /* 11 - statement 11 is true */
        /* 12 - statement 12 is true, true and then false in the recursion sequence */
        //Invoking clear method. The tree should now be empty.
        tree.clear();
        set = tree.getAllSections();
        assertEquals(set.size(), 0);

        /*
         * Making a new KDTree, but not using the nodeGenerator to set up the KDTree.
         * The field root should be null.
         */
        tree = new KDTree();

        //Testing if the field root is null
        Field field = tree.getClass().getDeclaredField("root");
        field.setAccessible(true);
        field.get(tree);
        assertNull(field.get(tree));

        /* 11 - statement 11 is false */
        //Invoking clear method. The field root should still be null
        tree.clear();
        assertNull(field.get(tree));
    }

    @Test
    public void getManySections() throws Exception {
        //Using the premade KDTree, but clearing it, such that no pointers are stored in the leaves.
        tree.clear();

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is true */
        /* 17A - statement 17A is true */
        /* 18 - statement 18 The forEach loop is not traversed */
        //There are no pointers in the root-left-left leaf. the returned set should be empty.
        HashSet set = tree.getManySections(1,1,1,1);
        assertEquals(0,set.size(),0);

        //Making a pointer to be put into the root-left-left leaf of the KDTree
        OSMWay way = new OSMWay();
        way.add(new Point2D.Float(1, 1));
        PolygonApprox polygonApprox = new PolygonApprox(way);
        Building building1 = new Building(polygonApprox);
        Pointer p = new Pointer(1,1, building1);
        tree.putPointer(p);

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is true */
        /* 17A - statement 17A is true */
        /* 18 - statement 18 The forEach loop is traversed once */
        //There is one pointer in the root-left-left leaf, and the element in the HashSet return should be building1
        set = tree.getManySections(1,1,1,1);
        assertEquals(1, set.size(), 0);
        assertTrue(set.contains(building1));

        //Making a pointer to be put into the root-right-right leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(15, 15));
        polygonApprox = new PolygonApprox(way);
        Building building15 = new Building(polygonApprox);
        p = new Pointer(15,15, building15);
        tree.putPointer(p);

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is false */
        /* 16B - statement 16B is true */
        /* 17A - statement 17A is false */
        /* 17B - statement 17B is true */
        /* 18 - statement 18 The forEach loop is traversed once */
        //There is one pointer in the root-right-right leaf, and the element in the HashSet return should be building15
        set = tree.getManySections(15,15,15,15);
        assertEquals(1, set.size(), 0);
        assertTrue(set.contains(building15));

        //Making a pointer to be put into the root-left-right leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(6, 6));
        polygonApprox = new PolygonApprox(way);
        Building building6 = new Building(polygonApprox);
        p = new Pointer(6,6, building6);
        tree.putPointer(p);

        //Making a pointer to be put into the root-right-left leaf of the KDTree
        way = new OSMWay();
        way.add(new Point2D.Float(10, 10));
        polygonApprox = new PolygonApprox(way);
        Building building10 = new Building(polygonApprox);
        p = new Pointer(10,10, building10);
        tree.putPointer(p);

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is true */
        /* 17A - statement 17A is false */
        /* 17B - statement 17B is false */
        /* 17C - statement 17C is true (accessed) */
        /* 18 - statement 18 The forEach loop is traversed twice */
        //There is one pointer in the root-left-left leaf, and in the root-left-right leaf.
        //The elements in the HashSet should be building1 and building6
        set = tree.getManySections(1,1,7,7);
        assertEquals(2, set.size(), 0);
        assertTrue(set.contains(building1));
        assertTrue(set.contains(building6));

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is false */
        /* 16B - statement 16B is false */
        /* 16C - statement 16C is true (accessed) */
        /* 17A - statement 17A is false */
        /* 17B - statement 17B is false */
        /* 17C - statement 17C is true (accessed) */
        /* 18 - statement 18 The forEach loop is traversed four times */
        //There is one pointer in each of the four leaves in the KDTree
        //The elements in the HashSet should be building1, building6, building10 and building15
        set = tree.getManySections(1,1,15,15);
        assertEquals(4, set.size(), 0);
        assertTrue(set.contains(building1));
        assertTrue(set.contains(building6));
        assertTrue(set.contains(building10));
        assertTrue(set.contains(building15));

        /* 13 - statement 13 is true */
        /* 14 - statement 14 is true, true and then false in the recursion sequence */
        /* 15 - statement 15 is true and then false in the recursion sequence */
        /* 16A - statement 16A is false */
        /* 16B - statement 16B is false */
        /* 16C - statement 16C is true (accessed) */
        /* 17A - statement 17A is true (in the recursion on the right side - root-right-left) */
        /* 17B - statement 17B is true (in the recursion on the left side - root-left-right) */
        /* 18 - statement 18 The forEach loop is traversed four times */
        //There is one pointer in second and third leaves in the KDTree (the middle leaves)
        //The elements in the HashSet should be building6 and building10
        set = tree.getManySections(5,5,11,11);
        assertEquals(2, set.size(), 0);
        assertTrue(set.contains(building6));
        assertTrue(set.contains(building10));

        //Setting up a new KDTree, with no nodes
        tree = new KDTree();

        //Checking that the field root is null
        Field field = tree.getClass().getDeclaredField("root");
        field.setAccessible(true);
        assertNull(field.get(tree));

        /* 13 - statement is false*/
        //The HashSet should be null because the root is null
        set = tree.getManySections(1,1,15,15);
        assertNull(set);
    }

    @Test
    public void getAllSections() throws Exception {
        /* 19 - is accessed*/
        //There is 15 elements in the KDTree. Alle elements are expected to be returned
        //because they are within the (-1000,-1000) to (1000,1000) range.
        HashSet set = tree.getAllSections();
        assertEquals(15, set.size());

        //Negative test of the getAllSections method:
        //Making a new tree with leaves smaller and greater than -1000 and 1000.
        tree = new KDTree();

        /*
         * A Nodegenerator instance is created.
         * 4096 Point2D.Floats are made and added to the nodegenerator.
         */
        NodeGenerator nodeGenerator = new NodeGenerator(5000, 11);
        tree = new KDTree();

        // Adds the Point2D.Floats with the coordinates (-2500,-2500) , (-2499,-2499) , ... , (2499,2499)
        for (int i = -2500; i < 2500; i++) {
            nodeGenerator.addPoint(new Point2D.Float(i, i));
        }

        //Initialise the nodegenerator (finding medians and the correct nodes for the KDTree).
        nodeGenerator.initialise();

        //Applying the nodegenerators data to the KDTree that is used for this test.
        nodeGenerator.setupTree(tree);

        /*
         * Making a building instance for each Point2D.Floats generated above.
         * A pointer for each building is added to the KDTree such that there is
         * a pointer for each point (-2500,-2500) , (-2499,-2499) , ... , (2499,2499)
         */
        for (int i = -2500; i < 2500; i++) {
            OSMWay way = new OSMWay();
            way.add(new Point2D.Float(i, i));
            PolygonApprox polygonApprox = new PolygonApprox(way);
            Building building = new Building(polygonApprox);
            Pointer p = new Pointer(i,i, building);
            tree.putPointer(p);
        }

        //Testing that not all elements are returned, because the boundry of the KDTree is
        //smaller and greater than -1000 and 1000
        set = tree.getAllSections();
        assertNotEquals(5000, set.size());
        assertTrue(set.size() < 5000);
    }

    @Test(expected=NullPointerException.class)
    //Testing for NullPointerException in the putPointer-method
    public void testNullPointerExceptionInPutPointer() {
        tree.putPointer(null);
    }
}