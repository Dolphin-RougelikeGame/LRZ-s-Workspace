package magwer.dolphin.game.room;

public class FightingDelegage{

    // 房间宽高、地图宽高
    private final int room_width = 17;
    private final int room_height = 9;
    private final int map_width = 27;
    private final int map_height = 15;


    // 房间到边界的距离
    int border_x = 5;
    int border_y = 3;

    // 地图数组
    RoomRemodel.SlotType[][] MapArray = new RoomRemodel.SlotType[27][15];
    //设置章节记录
    ChapterModel chapter = new ChapterModel();

    /**
     * @description 生成房间
     */
    public void makeRoom() {
        int limit_x = room_width + border_x;
        int limit_y = room_height + border_y;
        for (int i = border_x; i < limit_x; i++) {
            for (int j = border_y; j < limit_y; j++) {
                MapArray[i][j] = RoomRemodel.SlotType.FLOOR;
            }
        }

        for(int i = border_x - 1; i <= limit_x; i++) {
            MapArray[i][border_y - 1] = RoomRemodel.SlotType.WALL;
            MapArray[i][limit_y]      = RoomRemodel.SlotType.WALL;
        }

        for(int j = border_y - 1; j <= limit_y; j++) {
            MapArray[border_x - 1][j] = RoomRemodel.SlotType.WALL;
            MapArray[limit_x][j]      = RoomRemodel.SlotType.WALL;
        }
    }

    /**
     * @description 装饰房间
     * @param init_x     本房间中心X坐标
     * @param init_y     本房间中心Y坐标
     * @param kind       房间类型
     */
    private void roomRemodel(int init_x, int init_y, int kind){
        // 初始化RoomRemodel方法对象
        RoomRemodel remodel = new RoomRemodel();
        switch (kind){
            case RoomRemodel.REWARD:
                remodel.remodelTreasure(init_x, init_y, chapter);
                break;
            case RoomRemodel.NORMAL:
                remodel.remodelNormal(init_x, init_y, chapter);
                break;
            case RoomRemodel.BOSS:
                remodel.remodelBoss(init_x, init_y, chapter);
                break;
        }
    }

    public ChapterModel generate() {
        //设置房间中心位置
        int init_x = border_x + room_width / 2;
        int init_y = border_y + room_height / 2;

        // 确定房间个数
        int numOfRooms = chapter.getSum();

        // 设置普通房
        for (int i = 0; i < numOfRooms - 2; i++){
            makeRoom();
            roomRemodel(init_x, init_y, RoomRemodel.NORMAL);
        }

        // 设置boss房
        makeRoom();
        roomRemodel(init_x, init_y, RoomRemodel.BOSS);

        // 设置宝箱房
        makeRoom();
        roomRemodel(init_x, init_y, RoomRemodel.REWARD);

        return chapter;
    }

    public void test() {
        //设置房间中心位置
        int init_x = border_x + room_width / 2;
        int init_y = border_y + room_height / 2;

        // 确定房间个数
        int numOfRooms = chapter.getSum();

        // 设置普通房
        for (int i = 0; i < numOfRooms - 2; i++){
            roomRemodel(init_x, init_y, RoomRemodel.NORMAL);
        }

        // 设置boss房
        roomRemodel(init_x, init_y, RoomRemodel.BOSS);

        // 设置宝箱房
        roomRemodel(init_x, init_y, RoomRemodel.REWARD);

        // 将存在MapArray中的地图画出来
        for (int n = 0; n < chapter.getSum(); n++){
            for (int i = 0; i < map_width; i++){
                for (int j = 0; j < map_height; j++){
                    int floorSize = 40;
                    RoomModel model = chapter.getRooms().get(n);
                    RoomRemodel.SlotType slot = model.getMatrix()[i][j];

                    //if (Objects.requireNonNull().getMatrix()[i][j] == RoomRemodel.SlotType.FLOOR)
                    //    floors.add(new Floor(floorSize * i, floorSize * j, context));
                    //if (Objects.requireNonNull(chapter.getRooms().get(n)).getMatrix()[i][j] == RoomRemodel.SlotType.BAR)
                    //    bars.add(new Bar(floorSize * i, floorSize * j, context));
                    //if (Objects.requireNonNull(chapter.getRooms().get(n)).getMatrix()[i][j] == RoomRemodel.SlotType.TREASURE)
                    //    treasures.add(new Treasure(floorSize * i, floorSize * j, context));
                }
            }
        }

    }
}
