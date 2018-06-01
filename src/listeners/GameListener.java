package listeners;

import objects.Bone;

import java.util.ArrayList;

public interface GameListener {

    ArrayList<Bone> getTableBones();

    ArrayList<Bone> getBazaarBones();

    ArrayList<Bone> getPlayerBones();

    ArrayList<Bone> getComputerBones();

    void setLeftMove(int leftMove);

    void setRightMove(int rightMove);

    void putPlayerBone(Bone puttingBone);

    void putComputerBone(Bone randomActiveBone);

    int getMove();
}
