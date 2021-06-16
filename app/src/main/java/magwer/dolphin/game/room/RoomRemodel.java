package magwer.dolphin.game.room;

import java.util.Random;

/**
 * 房间种类：宝箱房 boss房 普通房
 * 铺设类型：墙壁（不可击碎） 地板 障碍物（可击碎）敌人
 * 敌人类：发射逻辑 仇恨机制
 */
public class RoomRemodel {
    // 房间种类: 宝箱 普通 boss 商店
    public static final int REWARD = 0;
    public static final int NORMAL = 1;
    public static final int BOSS = 2;
    public static final int SHOP = 3;

    // 房间大小类型
    public static final int LARGE = 9;
    public static final int MIDDLE = 7;
    public static final int LITTLE = 5;

    // 网格类型
    public enum SlotType {
        WALL,
        FLOOR,
        BAR,
        TREASURE,
        PORTAL,
        NONE
    }

    // 设置房间记录
    RoomModel room;
    FightingDelegage fig = new FightingDelegage();


    /**
     * @param x 中心x坐标
     * @param y 中心y坐标
     * @param n 以（x,y）为中心的n*n范围
     * @return 是否出现墙体
     * @description 边界检测
     */
    public boolean boarderCheck(int x, int y, int n) {
        int radius = n / 2;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (fig.MapArray[i][j] != SlotType.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description 普通房间设置
     */
    public void remodelNormal(int init_x, int init_y, ChapterModel chapter) {
        fig.makeRoom();

        if (boarderCheck(init_x, init_y, LARGE)) {
            remodelLarge(init_x, init_y);
        } else if (boarderCheck(init_x, init_y, MIDDLE)) {
            remodelMiddle(init_x, init_y);
        } else if (boarderCheck(init_x, init_y, LITTLE)) {
            remodelLittle(init_x, init_y);
        }

        room = new RoomModel(fig.MapArray);
        chapter.getRoom(room);
    }

    /**
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description 宝箱房间设置
     */
    public void remodelTreasure(int init_x, int init_y, ChapterModel chapter) {
        fig.makeRoom();
        fig.MapArray[init_x][init_y] = SlotType.TREASURE;
        room = new RoomModel(fig.MapArray);
        chapter.getRoom(room);
    }

    /**`
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description boss房间设置
     */
    public void remodelBoss(int init_x, int init_y, ChapterModel chapter) {
        fig.makeRoom();

        fig.MapArray[init_x - 5][init_y + 1] = SlotType.BAR;
        fig.MapArray[init_x - 5][init_y + 3] = SlotType.BAR;
        fig.MapArray[init_x - 4][init_y + 2] = SlotType.BAR;
        fig.MapArray[init_x - 6][init_y + 2] = SlotType.BAR;

        fig.MapArray[init_x - 5][init_y - 1] = SlotType.BAR;
        fig.MapArray[init_x - 5][init_y - 3] = SlotType.BAR;
        fig.MapArray[init_x - 4][init_y - 2] = SlotType.BAR;
        fig.MapArray[init_x - 6][init_y - 2] = SlotType.BAR;

        fig.MapArray[init_x + 5][init_y + 1] = SlotType.BAR;
        fig.MapArray[init_x + 5][init_y + 3] = SlotType.BAR;
        fig.MapArray[init_x + 4][init_y + 2] = SlotType.BAR;
        fig.MapArray[init_x + 6][init_y + 2] = SlotType.BAR;

        fig.MapArray[init_x + 5][init_y - 1] = SlotType.BAR;
        fig.MapArray[init_x + 5][init_y - 3] = SlotType.BAR;
        fig.MapArray[init_x + 4][init_y - 2] = SlotType.BAR;
        fig.MapArray[init_x + 6][init_y - 2] = SlotType.BAR;

        room = new RoomModel(fig.MapArray);
        chapter.getRoom(room);
    }


    /**
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description 普通大房间设置
     */
    private void remodelLarge(int init_x, int init_y) {
        int barKind = new Random().nextInt(4);
        switch (barKind) {
            case 0:
                for (int i = init_x - 3; i <= init_x + 3; i++) {
                    fig.MapArray[i][init_y + 4] = SlotType.BAR;
                    fig.MapArray[i][init_y - 4] = SlotType.BAR;
                }
                for (int i = init_y - 3; i <= init_y + 3; i++) {
                    fig.MapArray[init_x - 4][i] = SlotType.BAR;
                    fig.MapArray[init_x + 4][i] = SlotType.BAR;
                }
                break;

            case 1:
                for (int i = init_x - 4; i <= init_x + 4; i++) {
                    fig.MapArray[i][init_y] = SlotType.BAR;
                }
                for (int i = init_y - 4; i <= init_y + 4; i++) {
                    fig.MapArray[init_x][i] = SlotType.BAR;
                }

                fig.MapArray[init_x - 1][init_y] = SlotType.FLOOR;
                fig.MapArray[init_x + 1][init_y] = SlotType.FLOOR;
                fig.MapArray[init_x][init_y + 1] = SlotType.FLOOR;
                fig.MapArray[init_x][init_y + 1] = SlotType.FLOOR;
                break;

            case 2:
                break;
            case 3:
                remodelMiddle(init_x, init_y);
                break;
        }
    }

    /**
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description 普通中房间设置
     */
    private void remodelMiddle(int init_x, int init_y) {
        int barKind = new Random().nextInt(6);
        switch (barKind) {
            case 0:
                fig.MapArray[init_x - 3][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 3] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x + 3][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 3] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x - 3][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 3][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 3] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 3] = SlotType.BAR;
                fig.MapArray[init_x][init_y - 3] = SlotType.BAR;
                break;

            case 1:
                for (int x = init_x - 2; x <= init_x + 2; x = x + 2) {
                    for (int y = init_y - 2; y <= init_y + 2; y = y + 2) {
                        fig.MapArray[x][y] = SlotType.BAR;
                    }
                }
                break;

            case 2:
                for (int y = init_y - 2; y <= init_y + 2; y++) {
                    fig.MapArray[init_x - 2][y] = SlotType.BAR;
                    fig.MapArray[init_x][y] = SlotType.BAR;
                    fig.MapArray[init_x + 2][y] = SlotType.BAR;
                }
                break;

            case 3:
                fig.MapArray[init_x - 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 1] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 1] = SlotType.BAR;

                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 1] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 1] = SlotType.BAR;
                break;

            case 4:
                break;
            case 5:
                remodelLittle(init_x, init_y);
                break;
        }
    }

    /**
     * @param init_x 本房间中心X坐标
     * @param init_y 本房间中心Y坐标
     * @description 普通小房间设置
     */
    private void remodelLittle(int init_x, int init_y) {
        int barKind = new Random().nextInt(6);
        switch (barKind) {
            case 0:
                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 2] = SlotType.BAR;
                break;

            case 1:
                fig.MapArray[init_x - 2][init_y] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y] = SlotType.BAR;
                fig.MapArray[init_x][init_y] = SlotType.BAR;
                fig.MapArray[init_x][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y + 2] = SlotType.BAR;
                break;

            case 2:
                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 2] = SlotType.BAR;


                fig.MapArray[init_x - 1][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 2] = SlotType.BAR;
                break;

            case 3:
                fig.MapArray[init_x - 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x - 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 2] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 2] = SlotType.BAR;
                break;

            case 4:
                fig.MapArray[init_x - 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x - 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x - 1][init_y - 2] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y + 1] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y + 2] = SlotType.BAR;

                fig.MapArray[init_x + 2][init_y - 1] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 1][init_y - 2] = SlotType.BAR;
                break;

            case 5:
                fig.MapArray[init_x - 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y - 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y] = SlotType.BAR;
                fig.MapArray[init_x][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x - 2][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x][init_y + 2] = SlotType.BAR;
                fig.MapArray[init_x + 2][init_y + 2] = SlotType.BAR;
                break;
        }
    }
}
