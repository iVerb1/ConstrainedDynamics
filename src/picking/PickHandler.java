package picking;

/**
 * Created by Jeroen van Wijgerden on 22-5-2015.
 */
public class PickHandler {

    public static PickListener listener;
    private static int pickedId = -1;
    private static boolean isPicking = false;

    public static void startPicking(int pickedId) {
        PickHandler.pickedId = pickedId;
        PickHandler.isPicking = true;

        if (PickHandler.listener != null)
            PickHandler.listener.startPicking(PickHandler.pickedId);
    }

    public static void stopPicking() {
        PickHandler.isPicking = false;

        if (PickHandler.listener != null)
            PickHandler.listener.stopPicking();
    }

    public static int getPickedId() {
        return PickHandler.pickedId;
    }

    public static boolean isPicking() {
        return PickHandler.isPicking;
    }

}
