package listeners;

import objects.Bone;

import java.util.ArrayList;

public interface TableListener {

    void updateAllBones();

    void updateActiveBones(ArrayList<Bone> activeBones);

    void putComputerBone(Bone randomActiveBone);

    void putStartBone(Bone bone);

    void showComputerBones(boolean showComputerBones);

    void startPlayer();

    void start();
}
