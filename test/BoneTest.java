import objects.Bone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoneTest {

    final Bone firstBone = new Bone(0, 0, 1, 2, Bone.VERTICAL);
    final Bone secondBone = new Bone(20, 10, 2, 3, Bone.HORIZONTAL);

    @Test
    public void Bone() {
        try {
            new Bone(0, 0, -2, -3, Bone.VERTICAL);
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }
        try {
            new Bone(0, 20, 0, 4, 53);
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }
        try {
            new Bone(30, 15, -2, -3, -1);
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    public void setPosition() {
        firstBone.setPosition(20, 10);
        assertEquals(firstBone.getX(), secondBone.getX());
        assertEquals(firstBone.getY(), 10);
    }

    @Test
    public void setOrientation() {
        firstBone.setOrientation(Bone.HORIZONTAL);
        assertEquals(firstBone.getOrientation(), Bone.HORIZONTAL);
    }

    @Test
    public void reverse() {
        firstBone.reverse();
        assertEquals(firstBone, new Bone(12, 15, 2, 1, Bone.VERTICAL));
    }

    @Test
    public void isDuplicate() {
        final Bone testDoubleBone = new Bone(0, 0, 2, 2, Bone.HORIZONTAL);
        assertEquals(firstBone.isDuplicate(), firstBone.getFirstPart() == firstBone.getSecondPart());
        assertEquals(testDoubleBone.isDuplicate(), testDoubleBone.getFirstPart() == testDoubleBone.getSecondPart());
    }

    @Test
    public void equals() {
        assertEquals(firstBone, new Bone(4, 13, 1, 2, Bone.VERTICAL));
        assertEquals(secondBone, new Bone(123, 10, 2, 3, Bone.HORIZONTAL));
    }
}
