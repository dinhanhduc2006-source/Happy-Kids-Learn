package com.example.happykidlearn.registry;

import com.example.happykidlearn.MainActivity;
import com.example.happykidlearn.R;
import com.example.happykidlearn.models.GameModel;

import java.util.ArrayList;
import java.util.List;

public class GameRegistry {

    private static GameRegistry instance;
    private final List<GameModel> games = new ArrayList<>();

    private GameRegistry() {
        registerDefaultGames();
    }

    public static synchronized GameRegistry getInstance() {
        if (instance == null) {
            instance = new GameRegistry();
        }
        return instance;
    }

    private void registerDefaultGames() {
        games.add(new GameModel("alphabet", "Chữ Cái", "Ngôn ngữ", "Học 29 chữ cái tiếng Việt", R.drawable.ic_launcher_foreground, R.color.purple_500, MainActivity.class));
        games.add(new GameModel("spelling", "Ghép Vần", "Ngôn ngữ", "Kéo thả ghép vần từ đơn giản", R.drawable.ic_launcher_foreground, R.color.accent_orange, MainActivity.class));
        games.add(new GameModel("handwriting", "Tập Viết", "Ngôn ngữ", "Vẽ nét chữ theo hướng dẫn", R.drawable.ic_launcher_foreground, R.color.accent_green, MainActivity.class));
        games.add(new GameModel("storybook", "Đọc Truyện", "Ngôn ngữ", "Sách tranh đọc truyện theo giọng đọc", R.drawable.ic_launcher_foreground, R.color.accent_pink, MainActivity.class));
        games.add(new GameModel("english", "Tiếng Anh", "Ngôn ngữ", "Từ vựng tiếng Anh cơ bản", R.drawable.ic_launcher_foreground, R.color.purple_700, MainActivity.class));

        games.add(new GameModel("counting", "Số Đếm", "Toán học", "Học đếm từ 1 đến 20 trực quan", R.drawable.ic_launcher_foreground, R.color.accent_yellow, MainActivity.class));
        games.add(new GameModel("fun_math", "Toán Vui", "Toán học", "Phép tính cộng trừ bằng hình ảnh", R.drawable.ic_launcher_foreground, R.color.accent_orange, MainActivity.class));
        games.add(new GameModel("life_math", "Toán Đời Sống", "Toán học", "Mô phỏng đi siêu thị và chia phần", R.drawable.ic_launcher_foreground, R.color.accent_green, MainActivity.class));

        games.add(new GameModel("memory", "Trí Nhớ", "Tư duy", "Lật hình tìm cặp giống nhau", R.drawable.ic_launcher_foreground, R.color.purple_500, MainActivity.class));
        games.add(new GameModel("logic", "Tư Duy", "Tư duy", "Giải câu đố tìm quy luật hình", R.drawable.ic_launcher_foreground, R.color.accent_pink, MainActivity.class));
        games.add(new GameModel("coding", "Lập Trình", "Tư duy", "Hướng dẫn robot di chuyển bằng khối lệnh", R.drawable.ic_launcher_foreground, R.color.purple_700, MainActivity.class));
        games.add(new GameModel("reflexes", "Phản Xạ Nhanh", "Tư duy", "Bấm nổ bong bóng và hứng quà", R.drawable.ic_launcher_foreground, R.color.accent_orange, MainActivity.class));

        games.add(new GameModel("music", "Âm Nhạc", "Nghệ thuật", "Đàn Xylophone 7 nốt sinh động", R.drawable.ic_launcher_foreground, R.color.accent_yellow, MainActivity.class));
        games.add(new GameModel("coloring", "Tô Màu", "Nghệ thuật", "Tranh tô màu nét vẽ đen trắng", R.drawable.ic_launcher_foreground, R.color.accent_green, MainActivity.class));
        games.add(new GameModel("versus", "Bố Con Thi Tài", "Nghệ thuật", "Màn hình chia đôi thi tốc độ", R.drawable.ic_launcher_foreground, R.color.accent_pink, MainActivity.class));
    }

    public List<GameModel> getGames() {
        return new ArrayList<>(games);
    }

    public List<GameModel> getGamesByCategory(String category) {
        List<GameModel> filtered = new ArrayList<>();
        for (GameModel game : games) {
            if (game.getCategory().equalsIgnoreCase(category)) {
                filtered.add(game);
            }
        }
        return filtered;
    }

    public GameModel getGameById(String id) {
        for (GameModel game : games) {
            if (game.getId().equalsIgnoreCase(id)) {
                return game;
            }
        }
        return null;
    }

    public void registerGame(GameModel game) {
        if (game != null) {
            games.add(game);
        }
    }
}