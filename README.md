# 🌟 HAPPY KID LEARN - ỨNG DỤNG HỌC TẬP & MINI-GAME CHO BÉ

> **Dự án xây dựng ứng dụng giáo dục tương tác đa phương thức mô phỏng nền tảng DatPilot Kids, phát triển bằng Android Studio (Java).**

---

## 📋 MỤC LỤC
1. [Kiến Trúc & Các Phân Hệ Chức Năng](#1-kiến-trúc--các-phân-hệ-chức-năng)
2. [Công Nghệ & Thư Viện (Tech Stack)](#2-công-nghệ--thư-viện-tech-stack)
3. [Phân Công Nhiệm Vụ 4 Thành Viên](#3-phân-công-nhiệm-vụ-4-thành-viên)
4. [Quy Trình & Chiến Lược Sử Dụng Git](#4-quy-trình--chiến-lược-sử-dụng-git)
5. [Quy Ước Code & Mở Rộng Hệ Thống](#5-quy-ước-code--mở-rộng-hệ-thống)

---

## 1. KIẾN TRÚC & CÁC PHÂN HỆ CHỨC NĂNG

### I. Phân hệ 1: Giao diện Trung tâm & Điều hướng (Dashboard Hub)
* **Màn hình Map/Lưới chủ đề (Home Grid):**
  * Hiển thị dạng lưới thẻ CardView bo góc lớn, màu sắc đa dạng theo từng môn học (Tím, Cam, Hồng, Xanh lá, Vàng...) tương tự web mẫu.
  * Mỗi ô hiển thị: Icon 3D/Vector tròn, Tên môn học/Mini-game, và Thanh tiến độ (`ProgressBar`) kèm số bài đã hoàn thành (ví dụ: `0/42`, `1/9`).
* **Thanh công cụ Header & Âm thanh:**
  * Icon Bật/Tắt nhạc nền (BGM) và hiệu ứng tiếng (SFX).
  * Icon cài đặt mở Parental Gate (vùng phụ huynh).
* **Cơ chế mở rộng (Dynamic Registry):** 
  * Màn hình chính sử dụng `RecyclerView` lấy danh sách từ `GameRegistry`. 
  * Khi bổ sung mini-game mới, hệ thống tự động render thêm thẻ mà không cần viết lại giao diện màn hình chính.

---

### II. Phân hệ 2: Mini-Game Ngôn ngữ & Đọc viết
1. **Chữ cái (Alphabet):**
   * Thẻ flashcard tương tác 29 chữ cái tiếng Việt.
   * Chạm vào chữ để nghe phát âm chuẩn, hiển thị từ vựng và hình minh họa liên quan (A - Con cá).
2. **Ghép vần (Spelling):**
   * Kéo thả các âm tiết/chữ cái rời vào ô đích để ghép thành từ hoàn chỉnh ($b + a = ba$).
   * Kiểm tra đúng/sai kèm âm thanh phát âm từ vừa ghép.
3. **Tập viết (Handwriting):**
   * Canvas vẽ nét tương tác trên màn hình cảm ứng (`CustomView` với `Path`, `Paint`).
   * Có nét mờ hướng dẫn theo thứ tự nét viết, nút tẩy/xóa, đổi màu bút và đánh giá tỷ lệ vẽ trùng khớp.
4. **Đọc truyện (Storybook):**
   * Sách tranh điện tử lật trang mượt mà (`ViewPager2`).
   * Chữ hiển thị chạy hiệu ứng karaoke theo giọng đọc thu âm sẵn; chạm vào nhân vật trong tranh để phát âm thanh vui nhộn.
5. **Tiếng Anh (English for Kids):**
   * Học từ vựng cơ bản theo chủ đề (Màu sắc, Con vật, Gia đình, Số đếm).
   * Chế độ chạm nghe phát âm bản xứ kèm mini-quiz trắc nghiệm chọn hình đúng.

---

### III. Phân hệ 3: Mini-Game Toán học & Đời sống
6. **Số đếm (Counting):**
   * Màn chơi đếm đồ vật tương tác trực quan (chạm vào từng quả táo/con thỏ trên màn hình để đếm 1, 2, 3...).
   * Đồ vật chạm vào sẽ nhảy lên kèm hiệu ứng âm thanh số đếm.
7. **Toán vui (Fun Math):**
   * Các phép tính cộng/trừ trong phạm vi 10, 20 trực quan bằng hình ảnh (ví dụ: 2 chiếc xe + 1 chiếc xe).
   * Trẻ chọn đáp án bằng cách bấm vỡ bong bóng hoặc chọn giỏ hoa quả đúng.
8. **Toán đời sống (Life Math):**
   * Mô phỏng tình huống thực tế: Đi siêu thị chọn đúng số lượng hàng bỏ vào giỏ, trả tiền bằng các mệnh giá xu đơn giản, chia đều bánh kẹo cho các bạn.

---

### IV. Phân hệ 4: Mini-Game Tư duy, Logic & Công nghệ
9. **Trí nhớ (Memory Match):**
   * Trò chơi lật thẻ tìm cặp giống nhau (mảng 2x3, 3x4 hoặc 4x4 thẻ bài úp).
   * Lật 2 thẻ trùng khớp sẽ biến mất hoặc phát sáng và cộng điểm; lật sai sẽ tự động úp lại sau 1 giây.
10. **Tư duy (Logic & Puzzle):**
    * Các câu đố tìm quy luật chuỗi hình (Tam giác - Vuông - Tròn - ?), phân loại đồ vật (vật bay được vs vật dưới nước), ghép bóng (Shadow matching).
11. **Lập trình (Kids Coding):**
    * Trò chơi tư duy thuật toán dạng khối lệnh cơ bản (kéo mũi tên: Tiến, Lùi, Rẽ trái, Rẽ phải).
    * Mục tiêu: Lập trình đường đi đưa chú Robot hoặc bạn động vật về đích/nhặt vật phẩm mà không đâm vào chướng ngại vật.
12. **Trò chơi nhanh (Quick Games / Reflexes):**
    * Rèn luyện phản xạ tay - mắt: Bấm nổ bong bóng bay, đập chuột chũi xuất hiện ngẫu nhiên, hứng hoa quả rơi vào giỏ.

---

### V. Phân hệ 5: Nghệ thuật & Tương tác gia đình
13. **Âm nhạc (Music & Piano):**
    * Bàn phím đàn Xylophone / Piano mini đa sắc màu với 7 nốt nhạc cơ bản (Đồ, Rê, Mi, Pha, Son, La, Si).
    * Chế độ gõ nốt tự do hoặc gõ theo đèn nhấp nháy để chơi một bài hát thiếu nhi quen thuộc.
14. **Tô màu (Coloring Book):**
    * Cung cấp các khung tranh nét đen trắng về động vật, xe cộ, thiên nhiên.
    * Hộp màu sáp tương tác; hỗ trợ chạm để đổ màu vào vùng kín (Flood Fill algorithm) hoặc vẽ tự do bằng cọ nét.
15. **Bố con thi tài (Versus Mode / 2 Players):**
    * Màn hình chia đôi đối xứng (Split-screen) cho 2 người chơi cùng lúc trên một màn hình máy tính bảng hoặc điện thoại.
    * Các thử thách thi tốc độ: Ai tìm hình nhanh hơn, ai tính nhẩm nhanh hơn, ai bấm nút đúng nhiều hơn để giành cúp chiến thắng.

---

### VI. Phân hệ 6: Quản trị Phụ huynh & Lưu trữ Dữ liệu (Core System)
* **Parental Gate:** Phép tính xác thực bảo vệ (ví dụ: hiển thị chữ *"Nhập kết quả: Chín trừ ba bằng mấy?"*) để chỉ người lớn mới vào được cài đặt.
* **Lưu trữ tiến độ (Room Database / SQLite):**
  * Bảng `GameProgress`: Lưu lại cấp độ hiện tại, số sao đạt được, danh sách bài đã hoàn thành để cập nhật thanh tiến độ trên Home Grid.
  * Bảng `UserSettings`: Lưu trạng thái Bật/Tắt âm thanh, cài đặt giới hạn thời gian sử dụng app trong ngày (Screen Time Limit).
* **Sound Engine Tập trung (SoundManager):** Quản lý toàn bộ `SoundPool` cho 15 mini-game và `MediaPlayer` cho nhạc nền, tự động dừng âm thanh khi thoát màn hình để giải phóng RAM.

---

## 🛠️ 2. CÔNG NGHỆ & THƯ VIỆN (TECH STACK)

- **Ngôn ngữ lập trình:** Java (JDK 11)
- **Hệ điều hành tối thiểu (Min SDK):** Android 7.0 (API 24)
- **Target SDK:** API 37
- **Giao diện:** Android XML Layouts, Material Design 3, ConstraintLayout
- **Kiến trúc đề xuất:** MVC / MVVM
- **Quản lý phụ thuộc:** Gradle (Version Catalog `gradle/libs.versions.toml`)

---

## 2. PHÂN CÔNG NHIỆM VỤ 4 THÀNH VIÊN

### Thành viên 1: Trưởng nhóm – Core Engine, Quản trị Phụ huynh & Lưu trữ Dữ liệu (Phân hệ 1 & 6)
* **Vai trò:** Phụ trách kiến trúc hệ thống, cơ sở dữ liệu và quản lý tiến trình.
* **Chi tiết công việc cần code:**
  * Khởi tạo dự án Android Studio, thiết lập Git Repository (`.gitignore`, quy ước đặt tên nhánh).
  * Viết **`BaseGameActivity`**: Tạo khung Activity chuẩn cho tất cả mini-game (bao gồm: Header dùng chung với nút Back, nút Sound, thanh tiến độ, hàm gọi Popup chiến thắng/bắn confetti ăn mừng).
  * Viết **`SoundManager`** (Singleton): Quản lý tập trung `SoundPool` (hiệu ứng click, vỗ tay, đúng/sai) và `MediaPlayer` (nhạc nền lặp vô tận, tự động dừng ở `onPause`).
  * Viết **`GameRegistry`**: Quản lý danh sách mini-game động bằng Factory/Registry Pattern, cung cấp dữ liệu cho màn hình chính.
  * Xây dựng **Room Database**:
    * `Entity/DAO GameProgress`: Lưu ID game, số bài hoàn thành, số sao, trạng thái mở khóa.
    * `Entity/DAO AppSettings`: Lưu cấu hình bật/tắt âm lượng, giới hạn thời gian chơi.
  * Lập trình **Parental Gate & Settings**:
    * Dialog câu đố phép tính bảo vệ người lớn.
    * Màn hình cài đặt thời gian chơi (Screen Time Limit) và bảng tổng kết tiến độ học tập.

---

### Thành viên 2: Frontend Lead & Phân hệ Ngôn ngữ (Phân hệ 1 & 2)
* **Vai trò:** Phụ trách bộ khung giao diện chính (UI Hub) và nhóm game chữ viết/ngôn ngữ.
* **Chi tiết công việc cần code:**
  * Thiết kế Theme toàn app: Màu sắc, font chữ tròn `Nunito`/`Quicksand`, shape XML bo góc cho các thẻ card.
  * Lập trình màn hình chính **Home Dashboard Hub**:
    * Sử dụng `RecyclerView` dạng lưới (`GridLayoutManager` 2 cột hoặc 3 cột tùy kích thước màn hình).
    * Thiết kế custom `CardView` chuẩn theo mẫu: Icon tròn, tiêu đề, thanh ProgressBar và text tiến độ (ví dụ `0/42`, `1/9`).
    * Tự động lấy danh sách từ `GameRegistry` của Thành viên 1 để hiển thị.
  * Lập trình **4 Mini-game Ngôn ngữ:**
    1. **Chữ cái (Alphabet):** Lướt thẻ Flashcard 29 chữ cái, chạm để phát âm thanh phát âm và từ vựng.
    2. **Ghép vần (Spelling):** Kéo thả chữ cái vào ô trống (`OnTouchListener` + `OnDragListener`), ghép đúng từ thì phát âm vần.
    3. **Đọc truyện (Storybook):** Sử dụng `ViewPager2` lật trang truyện tranh, chạy chữ đồng bộ theo giọng đọc.
    4. **Tiếng Anh (English for Kids):** Flashcard song ngữ theo chủ đề + mini-quiz trắc nghiệm nghe âm thanh chọn hình.

---

### Thành viên 3: Graphic/Canvas Specialist & Nghệ thuật – Tương tác (Phân hệ 4 & 5)
* **Vai trò:** Phụ trách các mini-game đòi hỏi tương tác cảm ứng trực tiếp trên màn hình, xử lý đồ họa và chế độ 2 người chơi.
* **Chi tiết công việc cần code:**
  * Tạo custom view tái sử dụng **`BaseCanvasView`** hỗ trợ vẽ đường nét (`Path`, `Paint`, `Canvas`).
  * Lập trình **5 Mini-game Tương tác & Nghệ thuật:**
    1. **Tập viết (Handwriting):** Khung chữ mẫu mờ, bắt sự kiện vuốt ngón tay tô theo nét chữ, nút xóa/tẩy, đổi màu nét vẽ.
    2. **Tô màu (Coloring Book):** Khung tranh đường viền đen trắng, bảng màu sáp tương tác để chạm tô màu vào vùng vẽ hoặc tô tự do.
    3. **Âm nhạc (Music):** Bàn phím gõ đàn Xylophone / Piano 7 nốt màu sắc, nhấn phím nào nốt đó kêu và nảy hiệu ứng.
    4. **Trò chơi nhanh (Quick Games):** Trò chơi phản xạ bấm vỡ bong bóng bay hoặc đập đồ vật xuất hiện ngẫu nhiên theo thời gian đếm ngược.
    5. **Bố con thi tài (Versus Mode):** Màn hình chia đôi đối xứng (Split-screen) cho 2 người cùng bấm thi tốc độ xem ai nhanh hơn.

---

### Thành viên 4: Logic Developer & Toán học – Tư duy (Phân hệ 3 & 4)
* **Vai trò:** Phụ trách các mini-game thuần về giải thuật, tính toán, rèn luyện trí não và tư duy logic.
* **Chi tiết công việc cần code:**
  * Lập trình **3 Mini-game Toán học:**
    1. **Số đếm (Counting):** Màn chơi đếm số lượng hoa quả/đồ vật hiển thị ngẫu nhiên, chạm vào đồ vật thì số thứ tự bay lên.
    2. **Toán vui (Fun Math):** Phép tính trực quan bằng hình vẽ quả/con vật + chọn kết quả đúng bằng bong bóng.
    3. **Toán đời sống (Life Math):** Mô phỏng giỏ hàng đi siêu thị hoặc chia đều kẹo cho các nhân vật, kéo thả kiểm tra đủ số lượng.
  * Lập trình **3 Mini-game Trí tuệ & Tư duy:**
    4. **Trí nhớ (Memory Match):** Mảng thẻ bài úp, lật thẻ với hiệu ứng xoay 3D, kiểm tra cặp trùng khớp và xử lý úp lại khi sai.
    5. **Tư duy (Logic & Puzzle):** Mini-game tìm quy luật chuỗi hình hoặc kéo thả ghép bóng (Shadow Matching).
    6. **Lập trình (Kids Coding):** Kéo thả các khối lệnh mũi tên (Tiến, Lùi, Rẽ) vào hàng đợi rồi bấm nút "Chạy" để điều khiển nhân vật di chuyển trên ma trận lưới đến ô đích.

---

## 3. QUY TRÌNH & CHIẾN LƯỢC SỬ DỤNG GIT

### 1. Chiến lược phân nhánh (Git Branching Model)
Dự án áp dụng mô hình chuẩn **Git Flow rút gọn**:
* **`main`**: Nhánh chứa mã nguồn ổn định nhất, chỉ merge vào khi hoàn thành đồ án hoặc đóng gói file APK nộp bài. **Tuyệt đối không ai commit trực tiếp vào nhánh này.**
* **`dev`**: Nhánh tích hợp chính của cả nhóm. Mọi tính năng sau khi code xong và test chạy được sẽ merge vào đây.
* **`feature/<ten-tinh-nang>`**: Nhánh riêng của từng thành viên để code từng mini-game hoặc module cụ thể.

```text
main ───────────────● (V1.0 Nộp bài)
                      ▲
dev  ───●─────────────● (Tích hợp xong cả 15 game)
        ▲             ▲
        │             │
        ├─ feature/spelling-game (Thành viên 2)
        ├─ feature/handwriting (Thành viên 3)
        └─ feature/math-counting (Thành viên 4)
```

---

### 2. Quy ước file `.gitignore`
Đặt file `.gitignore` chuẩn ở thư mục gốc của repository để không đẩy file rác hoặc cấu hình máy cá nhân lên Git:

```gitignore
*.iml
.gradle
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
```

---

### 3. Quy ước viết Commit Message (Conventional Commits)
Cú pháp thống nhất: `<loại>: <mô tả ngắn bằng tiếng Việt>`
* **`feat`**: Thêm chức năng hoặc mini-game mới (Ví dụ: `feat: them mini-game ghep van`)
* **`fix`**: Sửa lỗi crash, bug logic (Ví dụ: `fix: sua loi khong phat am thanh khi bam nut`)
* **`ui`**: Chỉnh sửa giao diện XML, màu sắc (Ví dụ: `ui: cap nhat cardview bo goc man hinh home`)
* **`refactor`**: Tối ưu hóa code nhưng không đổi chức năng (Ví dụ: `refactor: chuyen soundpool sang soundmanager singleton`)

---

### 4. Quy trình làm việc hàng ngày của từng thành viên

#### Bước 1: Lấy mã nguồn mới nhất trước khi bắt đầu code
```bash
git checkout dev
git pull origin dev
```

#### Bước 2: Tạo nhánh mới cho chức năng được phân công
```bash
# Ví dụ: Thành viên 3 code game Tập viết
git checkout -b feature/handwriting-game

# Ví dụ: Thành viên 4 code game Đếm số
git checkout -b feature/counting-game
```

#### Bước 3: Code và commit theo từng giai đoạn nhỏ
```bash
git status
git add .
git commit -m "feat: tao layout canvas va net ve co ban cho game tap viet"
```

#### Bước 4: Đẩy nhánh lên GitHub / GitLab
```bash
git push -u origin feature/handwriting-game
```

#### Bước 5: Cập nhật code mới từ `dev` trước khi nộp bài
```bash
git pull origin dev
```
* **Nếu có Conflict:** Android Studio sẽ hiện giao diện trực quan gồm 3 cột (*Yours - Result - Theirs*). Thành viên chọn giữ code chuẩn, bấm **Apply**, rồi commit lại:
  ```bash
  git commit -m "fix: giai quyet xung dot voi nhanh dev"
  git push origin feature/handwriting-game
  ```

#### Bước 6: Tạo Pull Request (PR)
1. Lên giao diện GitHub/GitLab, chọn tạo **New Pull Request**.
2. Thiết lập: `base: dev` $\leftarrow$ `compare: feature/handwriting-game`.
3. Gửi link PR vào nhóm để **Thành viên 1 (Trưởng nhóm)** review code và nhấn **Merge**.

---

## 4. QUY ƯỚC CODE & MỞ RỘNG HỆ THỐNG

1. **Kế thừa bắt buộc:** Mọi Activity của mini-game phải kế thừa `BaseGameActivity` và gọi `setupHeader()` trong `onCreate()`.
2. **Âm thanh tập trung:** Không tạo instance `MediaPlayer`/`SoundPool` riêng lẻ trong từng màn hình; gọi qua `soundManager.playSound(...)`.
3. **Mở rộng mini-game mới:** Khi thêm game mới, chỉ cần tạo Activity kế thừa `BaseGameActivity` và khai báo vào `GameRegistry.registerGame(...)`.
