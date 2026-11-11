# BÁO CÁO CHI TIẾT KẾT QUẢ TRIỂN KHAI PHÂN HỆ THÀNH VIÊN 2
## DỰ ÁN: HAPPY KID LEARN — HỆ THỐNG TRÒ CHƠI HỌC TẬP THÔNG MINH CHO TRẺ EM

---

* **Dự án:** `Happy-Kids-Learn`
* **Công nghệ:** Android Native, Java 17, Android SDK (API 24 - 34), XML Layouts, Material Components
* **Nhánh phát triển:** `feature/ui-home-and-alphabet`
* **Vai trò:** Thành viên 2 — Phụ trách Home Dashboard & Phân hệ Ngôn ngữ (4 Mini-game)
* **Phong cách thiết kế chủ đạo:** Cuốn Sách Kỳ Diệu (*The Magic Book*)
* **Linh vật đồng hành:** Sao Bông (*Little Star*)
---

## MỤC LỤC

1. [Giới thiệu tổng quan](#1-giới-thiệu-tổng-quan)
   * [1.1. Vai trò và trách nhiệm của Thành viên 2](#11-vai-trò-và-trách-nhiệm-của-thành-viên-2)
   * [1.2. Mục tiêu phát triển & Phạm vi chức năng](#12-mục-tiêu-phát-triển--phạm-vi-chức-năng)
   * [1.3. Ý tưởng thiết kế cốt lõi: Cuốn Sách Kỳ Diệu & Linh vật Sao Bông](#13-ý-tưởng-thiết-kế-cốt-lõi-cuốn-sách-kỳ-diệu--linh-vật-sao-bông)
   * [1.4. Định hướng trải nghiệm học tập cho trẻ 4–8 tuổi](#14-định-hướng-trải-nghiệm-học-tập-cho-trẻ-48-tuổi)
2. [Home Dashboard — Trang chủ Cuốn Sách Kỳ Diệu (`Member2HomeActivity`)](#2-home-dashboard--trang-chủ-cuốn-sách-kỳ-diệu-member2homeactivity)
   * [2.1. Cấu trúc giao diện Cuốn Sách Kỳ Diệu](#21-cấu-trúc-giao-diện-cuốn-sách-kỳ-diệu)
   * [2.2. Khu vực Chào mừng & Linh vật tương tác Sao Bông](#22-khu-vực-chào-mừng--linh-vật-tương-tác-sao-bông)
   * [2.3. Hero Section & Huy hiệu Tổng quan Thành tích](#23-hero-section--huy-hiệu-tổng-quan-thành-tích)
   * [2.4. Danh mục học tập đa sắc màu (Dynamic Category Chips)](#24-danh-mục-học-tập-đa-sắc-màu-dynamic-category-chips)
   * [2.5. Thẻ Gợi ý "Học Tiếp Nào!" (Smart Continue Card)](#25-thẻ-gợi-ý-học-tiếp-nào-smart-continue-card)
   * [2.6. Danh sách trò chơi dạng Bìa sách 2.5D & Responsive Layout](#26-danh-sách-trò-chơi-dạng-bìa-sách-25d--responsive-layout)
   * [2.7. Tích hợp Điều hướng Thông minh (`Member2GameRouter`) & Empty State](#27-tích-hợp-điều-hướng-thông-minh-member2gamerouter--empty-state)
3. [Mini-game Alphabet — Học Bảng Chữ Cái Tiếng Việt (`AlphabetActivity`)](#3-mini-game-alphabet--học-bảng-chữ-cái-tiếng-việt-alphabetactivity)
   * [3.1. Mục tiêu học tập & Bộ nội dung 29 chữ cái](#31-mục-tiêu-học-tập--bộ-nội-dung-29-chữ-cái)
   * [3.2. Chế độ Thẻ chữ Flashcard 2 mặt (3D Flip Animation)](#32-chế-độ-thẻ-chữ-flashcard-2-mặt-3d-flip-animation)
   * [3.3. Chế độ Thử thách Nghe chọn chữ (Listening Challenge)](#33-chế-độ-thử-thách-nghe-chọn-chữ-listening-challenge)
   * [3.4. Thanh cuộn chọn chữ nhanh & Mốc hạt giống đã gieo](#34-thanh-cuộn-chọn-chữ-nhanh--mốc-hạt-giống-đã-gieo)
   * [3.5. Hệ thống âm thanh, phát âm TTS & Phản hồi khích lệ](#35-hệ-thống-âm-thanh-phát-âm-tts--phản-hồi-khích-lệ)
4. [Mini-game Spelling — Xưởng Ghép Vần Tiếng Việt (`SpellingActivity`)](#4-mini-game-spelling--xưởng-ghép-vần-tiếng-việt-spellingactivity)
   * [4.1. Mục tiêu học tập & Khung kiến thức 16 bài ghép vần](#41-mục-tiêu-học-tập--khung-kiến-thức-16-bài-ghép-vần)
   * [4.2. Cơ chế tương tác kép: Kéo - Thả (Drag & Drop) và Chạm nhanh (Tap to Place)](#42-cơ-chế-tương-tác-kép-kéo---thả-drag--drop-và-chạm-nhanh-tap-to-place)
   * [4.3. Bàn làm việc (Workbench), Các ô Slot & Khay thẻ chữ (Tray Pool)](#43-bàn-làm-việc-workbench-các-ô-slot--khay-thẻ-chữ-tray-pool)
   * [4.4. Hệ thống trợ giúp thông minh (Gợi ý chữ & Thu hồi ô)](#44-hệ-thống-trợ-giúp-thông-minh-gợi-ý-chữ--thu-hồi-ô)
   * [4.5. Kiểm tra đáp án, đọc từ hoàn chỉnh & Phản hồi giọng nói](#45-kiểm-tra-đáp-án-đọc-từ-hoàn-chỉnh--phản-hồi-giọng-nói)
5. [Mini-game English — Tiếng Anh Cho Bé (`EnglishActivity`)](#5-mini-game-english--tiếng-anh-cho-bé-englishactivity)
   * [5.1. Mục tiêu học tập & 4 chủ đề từ vựng cốt lõi](#51-mục-tiêu-học-tập--4-chủ-đề-từ-vựng-cốt-lõi)
   * [5.2. Chế độ Flashcard trực quan đa phương tiện](#52-chế-độ-flashcard-trực-quan-đa-phương-tiện)
   * [5.3. Chế độ Luyện nghe chọn hình (Listening Mode)](#53-chế-độ-luyện-nghe-chọn-hình-listening-mode)
   * [5.4. Chế độ Trắc nghiệm tương tác (Interactive Quiz Stage)](#54-chế-độ-trắc-nghiệm-tương-tác-interactive-quiz-stage)
   * [5.5. Cơ chế phát âm đa ngôn ngữ an toàn: Tiếng Anh kết hợp Giọng khen Tiếng Việt](#55-cơ-chế-phát-âm-đa-ngôn-ngữ-an-toàn-tiếng-anh-kết-hợp-giọng-khen-tiếng-việt)
6. [Mini-game Storybook — Đọc Truyện Cổ Tích & Ngụ Ngôn (`StorybookActivity`)](#6-mini-game-storybook--đọc-truyện-cổ-tích--ngụ-ngôn-storybookactivity)
   * [6.1. Mục tiêu học tập & 3 câu chuyện ngụ ngôn nguyên bản giàu tính giáo dục](#61-mục-tiêu-học-tập--3-câu-chuyện-ngụ-ngôn-nguyên-bản-giàu-tính-giáo-dục)
   * [6.2. Kiến trúc 2 màn hình: Thư viện truyện (Library) & Trình đọc sách (Reader)](#62-kiến-trúc-2-màn-hình-thư-viện-truyện-library--trình-đọc-sách-reader)
   * [6.3. Cơ chế lật trang sách chân thực (ViewPager2 PageTransformer) & Minh họa Vector](#63-cơ-chế-lật-trang-sách-chân-thực-viewpager2-pagetransformer--minh-họa-vector)
   * [6.4. Trình kể chuyện tự động có tô màu câu đọc (`NarrationController`)](#64-trình-kể-chuyện-tự-động-có-tô-màu-câu-đọc-narrationcontroller)
   * [6.5. Tổng kết bài học đạo đức (Moral Lesson) & Chúc mừng hoàn thành](#65-tổng-kết-bài-học-đạo-đức-moral-lesson--chúc-mừng-hoàn-thành)
7. [Hệ thống Âm thanh & Giọng nói Động viên (`Member2SoundEffects` & `Member2VoiceFeedback`)](#7-hệ-thống-âm-thanh--giọng-nói-động-viên-member2soundeffects--member2voicefeedback)
   * [7.1. Kiến trúc tích hợp không xâm lấn với `SoundManager` của Core](#71-kiến-trúc-tích-hợp-không-xâm-lấn-với-soundmanager-của-core)
   * [7.2. Bản nhạc nền phong cách "Magical Storybook — Gentle Music Box & Soft Piano" (72 BPM)](#72-bản-nhạc-nền-phong-cách-magical-storybook--gentle-music-box--soft-piano-72-bpm)
   * [7.3. Bộ hiệu ứng âm thanh xúc giác (SFX Tap, Open Book, Correct, Gentle Try, Celebrate)](#73-bộ-hiệu-ứng-âm-thanh-xúc-giác-sfx-tap-open-book-correct-gentle-try-celebrate)
   * [7.4. Hệ thống giọng nói khích lệ tiếng Việt ấm áp (`Member2VoicePhrases`)](#74-hệ-thống-giọng-nói-khích-lệ-tiếng-việt-ấm-áp-member2voicephrases)
   * [7.5. Cơ chế Audio Ducking tự động & Quản lý vòng đời âm thanh an toàn](#75-cơ-chế-audio-ducking-tự-động--quản-lý-vòng-đời-âm-thanh-an-toàn)
   * [7.6. Bảng đối chiếu hiện trạng âm thanh: Đã cài đặt, Đã kiểm thử & Hạn chế thực tế](#76-bảng-đối-chiếu-hiện-trạng-âm-thanh-đã-cài-đặt-đã-kiểm-thử--hạn-chế-thực-tế)
8. [Thiết kế Giao diện & Tài nguyên Đồ họa (UI/UX Design System)](#8-thiết-kế-giao-diện--tài-nguyên-đồ-họa-uiux-design-system)
   * [8.1. Phong cách thiết kế Cuốn Sách Kỳ Diệu (The Magic Book)](#81-phong-cách-thiết-kế-cuốn-sách-kỳ-diệu-the-magic-book)
   * [8.2. Bảng màu Pastel thân thiện với trẻ em (`m2_colors.xml`)](#82-bảng-màu-pastel-thân-thiện-với-trẻ-em-m2_colorsxml)
   * [8.3. Thiết kế bìa sách và đồ họa minh họa 2.5D](#83-thiết-kế-bìa-sách-và-đồ-họa-minh-họa-25d)
   * [8.4. Linh vật Sao Bông & Các biểu cảm trạng thái](#84-linh-vật-sao-bông--các-biểu-cảm-trạng-thái)
   * [8.5. Tối ưu tương tác công thái học cho trẻ 4–8 tuổi](#85-tối-ưu-tương-tác-công-thái-học-cho-trẻ-48-tuổi)
9. [Quản lý Tiến độ & Đồng bộ Dữ liệu (`Member2ProgressRepository`)](#9-quản-lý-tiến-độ--đồng-bộ-dữ-liệu-member2progressrepository)
   * [9.1. Kiến trúc lưu trữ 2 lớp (SharedPreferences chi tiết & Room Database)](#91-kiến-trúc-lưu-trữ-2-lớp-sharedpreferences-chi-tiết--room-database)
   * [9.2. Luồng thực thi bất đồng bộ nền (Background Executor & Main Looper Post)](#92-luồng-thực-thi-bất-đồng-bộ-nền-background-executor--main-looper-post)
   * [9.3. Cơ chế tính điểm và cấp sao lũy tiến](#93-cơ-chế-tính-điểm-và-cấp-sao-lũy-tiến)
   * [9.4. Đồng bộ 2 chiều an toàn khi Phụ huynh đặt lại dữ liệu](#94-đồng-bộ-2-chiều-an-toàn-khi-phụ-huynh-đặt-lại-dữ-liệu)
10. [Cấu trúc Mã nguồn & Danh mục Thành phần](#10-cấu-trúc-mã-nguồn--danh-mục-thành-phần)
    * [10.1. Sơ đồ cây thư mục các Package thuộc Thành viên 2](#101-sơ-đồ-cây-thư-mục-các-package-thuộc-thành-viên-2)
    * [10.2. Bảng mô tả chi tiết trách nhiệm các lớp Java](#102-bảng-mô-tả-chi-tiết-trách-nhiệm-các-lớp-java)
    * [10.3. Bảng tổng hợp Layouts và Tài nguyên thuộc Thành viên 2](#103-bảng-tổng-hợp-layouts-và-tài-nguyên-thuộc-thành-viên-2)
11. [Báo cáo Kiểm thử Thực tế (Testing & Verification Report)](#11-báo-cáo-kiểm-thử-thực-tế-testing--verification-report)
    * [11.1. Kết quả kiểm thử Unit Test (7 test classes, 26 test cases)](#111-kết-quả-kiểm-thử-unit-test-7-test-classes-26-test-cases)
    * [11.2. Kết quả kiểm thử tích hợp trên Android Emulator (Pixel 6, Android 14)](#112-kết-quả-kiểm-thử-tích-hợp-trên-android-emulator-pixel-6-android-14)
    * [11.3. Bảng kiểm tra 10 kịch bản âm thanh và giọng nói bắt buộc](#113-bảng-kiểm-tra-10-kịch-bản-âm-thanh-và-giọng-nói-bắt-buộc)
    * [11.4. Các lỗi kỹ thuật đã phát hiện và cách khắc phục trong quá trình phát triển](#114-các-lỗi-kỹ-thuật-đã-phát-hiện-và-cách-khắc-phục-trong-quá-trình-phát-triển)
    * [11.5. Những hạn chế kỹ thuật còn tồn tại và đề xuất mở rộng](#115-những-hạn-chế-kỹ-thuật-còn-tồn-tại-và-đề-xuất-mở-rộng)
12. [Kết quả Thực hiện & Hướng dẫn Sử dụng](#12-kết-quả-thực-hiện--hướng-dẫn-sử-dụng)
    * [12.1. Tổng kết khối lượng công việc hoàn thành](#121-tổng-kết-khối-lượng-công-việc-hoàn-thành)
    * [12.2. Điểm nổi bật về trải nghiệm người dùng](#122-điểm-nổi-bật-về-trải-nghiệm-người-dùng)
    * [12.3. Hướng dẫn sử dụng và kiểm thử dành cho Người chấm điểm / Trưởng nhóm](#123-hướng-dẫn-sử-dụng-và-kiểm-thử-dành-cho-người-chấm-điểm--trưởng-nhóm)

---

## 1. GIỚI THIỆU TỔNG QUAN

### 1.1. Vai trò và trách nhiệm của Thành viên 2
Trong đồ án xây dựng ứng dụng giáo dục mầm non **Happy Kid Learn**, Thành viên 2 chịu trách nhiệm thiết kế kiến trúc trải nghiệm người dùng, xây dựng màn hình trung tâm **Home Dashboard** và phát triển trọn vẹn **Phân hệ Ngôn ngữ (Language Module)** gồm 4 trò chơi học tập tương tác cốt lõi:
1. **Alphabet (Chữ Cái Tiếng Việt):** Làm quen bảng chữ cái tiếng Việt với thẻ lật 3D và thử thách nghe chọn chữ.
2. **Spelling (Ghép Vần Tiếng Việt):** Xưởng ghép vần tương tác hỗ trợ kéo thả và chạm thẻ chữ cái.
3. **English (Tiếng Anh Cho Bé):** Học từ vựng tiếng Anh theo chủ đề qua thẻ Flashcard, nghe chọn hình và trắc nghiệm tương tác.
4. **Storybook (Đọc Truyện Tranh):** Tủ truyện cổ tích và ngụ ngôn tương tác với giọng đọc diễn cảm, bắt nét tô chữ và bài học đạo đức.

Bên cạnh đó, Thành viên 2 đảm nhiệm việc phát triển hệ thống điều phối âm thanh xúc giác, nhạc nền thư giãn chuẩn sư phạm và động cơ giọng nói tiếng Việt khích lệ kết quả học tập cho toàn bộ phân hệ của mình.

### 1.2. Mục tiêu phát triển & Phạm vi chức năng
* **Mục tiêu giáo dục:** Khơi dậy niềm đam mê học hỏi tự nhiên, phát triển tư duy ngôn ngữ và phản xạ nghe - nhìn - phát âm chuẩn cho trẻ trong độ tuổi 4–8 tuổi.
* **Mục tiêu kỹ thuật:**
  * Áp dụng mô hình hướng module độc lập (*modular architecture*). Toàn bộ mã nguồn, dữ liệu và tài nguyên của Thành viên 2 nằm gọn gàng trong package `com.example.happykidlearn.member2.*` và tiền tố tài nguyên `m2_*`.
  * **Tuân thủ quy ước nhóm tuyệt đối:** Không can thiệp, không ghi đè vào các tệp tin dùng chung của nhóm trưởng (Thành viên 1) hay các thành viên khác (`MainActivity`, `GameRegistry`, `SoundManager`, Room Database, Gradle files...).
  * Khai thác triệt để các thành phần giao diện hiện đại: `ViewPager2`, `RecyclerView`, `ConstraintLayout`, `MaterialCardView`, `ValueAnimator`, `TextToSpeech` đa ngôn ngữ.

### 1.3. Ý tưởng thiết kế cốt lõi: Cuốn Sách Kỳ Diệu & Linh vật Sao Bông
* **Ý tưởng "Cuốn Sách Kỳ Diệu" (*The Magic Book*):** Thay vì các giao diện lưới ứng dụng công nghệ khô cứng, Happy Kid Learn được định hình như một cuốn bách khoa toàn thư cổ tích gập mở 2.5D. Toàn bộ nền tảng hiển thị dưới hình tượng các trang sách xếp lớp, gáy sách da mềm, các tấm thẻ bài học đóng vai trò như các bìa sách minh họa nổi bật trên kệ sách gỗ tuổi thơ.
* **Linh vật "Sao Bông" (*Little Star*):** Một nhân vật hoạt hình ngôi sao vàng ấm áp với đôi mắt tròn xoe và má hồng. Sao Bông đóng vai trò như người bạn đồng hành (*learning buddy*), luôn có mặt ở đầu mỗi màn hình để đón chào, nháy mắt khích lệ, gợi ý bước tiếp theo và cùng bé hân hoan ăn mừng khi đạt thành tích mới.

### 1.4. Định hướng trải nghiệm học tập cho trẻ 4–8 tuổi
Trẻ em mầm non và đầu tiểu học có đặc thù: khả năng tập trung có giới hạn, chưa thành thạo kỹ năng đọc hiểu văn bản phức tạp và rất nhạy cảm với các phản hồi tiêu cực. Do đó, thiết kế của Thành viên 2 tuân thủ các nguyên tắc sư phạm:
* **Tương tác trực quan — Đa giác quan:** Mỗi khái niệm chữ cái/từ vựng đều đi liền với hình ảnh minh họa sinh động, âm thanh phát âm mẫu và hiệu ứng chuyển động xúc giác.
* **Phản hồi mang tính xây dựng (*Positive Reinforcement*):** Tuyệt đối không dùng còi báo lỗi chói tai hay lời chê bai. Khi trẻ làm đúng, ứng dụng khen ngợi nồng nhiệt; khi trẻ chọn sai, ứng dụng nhẹ nhàng phát hiệu ứng âm thanh ấm áp và lời động viên: *"Không sao đâu, bé thử lần nữa nào!"*.
* **Dung sai thao tác rộng (*Touch-friendly ergonomics*):** Tất cả các nút bấm, ô chữ và thẻ bài học đều có kích thước tối thiểu từ 48dp đến 64dp, hỗ trợ cả thao tác chạm đơn giản lẫn kéo thả nâng cao.

---

## 2. HOME DASHBOARD — TRANG CHỦ CUỐN SÁCH KỲ DIỆU (`Member2HomeActivity`)

### 2.1. Cấu trúc giao diện Cuốn Sách Kỳ Diệu
Giao diện `Member2HomeActivity` được định nghĩa trong layout `m2_activity_home.xml` với bố cục phân tầng mô phỏng một cuốn sách thần tiên:
* **Gáy sách (*Book Spine*):** Sử dụng drawable `m2_bg_book_spine.xml` tạo dải màu ấm bao quanh cạnh viền.
* **Mép trang sách xếp lớp (*Book Pages Bottom*):** Drawable `m2_bg_book_pages_bottom.xml` tạo hiệu ứng các lớp giấy xếp chồng nhau ở chân màn hình.
* **Biển hiệu tựa đề (*Book Title Plaque*):** `m2_bg_book_title_plaque.xml` đóng khung tên thương hiệu "Happy Kid Learn" mềm mại như biển khắc gỗ bọc mây trắng.

### 2.2. Khu vực Chào mừng & Linh vật tương tác Sao Bông
* **Thanh Header trên cùng:**
  * Bên trái: Nút bật/tắt âm thanh dạng tròn (`btn_m2_home_sound`) đồng bộ trạng thái trực tiếp với hệ thống âm thanh và SharedPreferences.
  * Ở giữa: Logo thương hiệu bồng bềnh trên đám mây trắng (`m2_bg_logo_cloud.xml`).
  * Bên phải: Nút Phụ huynh (`btn_m2_home_parent`) màu san hô, được bảo vệ bằng hộp thoại cổng phụ huynh (`ParentalGateDialog`) trước khi mở `ParentalSettingsActivity`.
* **Linh vật Sao Bông tương tác:**
  * Đặt tại `ll_m2_mascot_badge`. Được cài đặt hoạt ảnh chuyển động nhịp thở nhẹ nhàng (`mascotPulseAnimator`: tịnh tiến trục Y -4dp kết hợp phóng to 103% theo chu kỳ 1.6 giây).
  * Kiểm tra và tôn trọng cài đặt trợ năng của hệ điều hành: tự động vô hiệu hóa hoạt ảnh khi thiết bị kích hoạt chế độ giảm chuyển động (`isReducedMotionEnabled()`).
  * Khi trẻ chạm vào Sao Bông: phát âm thanh ăn mừng `playCelebrate()` và chuyển biểu cảm sang khuôn mặt hớn hở `m2_mascot_saobong_happy`.

### 2.3. Hero Section & Huy hiệu Tổng quan Thành tích
* **Hero Section:** Đặt tấm ảnh bìa Pop-up 2.5D `m2_illust_hero_popup_book.jpg` (kích thước tối ưu 960x540 px, chỉ 97 KB) với nút bấm kêu gọi hành động lớn *"Khám phá ngay!"* (`btn_m2_hero_explore`).
* **Huy hiệu Thành tích Tổng quan:**
  * Hiển thị tổng số bài học bé đã hoàn thành (`tv_m2_total_completed`) và tổng số sao thu thập được (`tv_m2_total_stars`).
  * Hệ thống 3 ngôi sao động (`iv_m2_hero_star1..3`): Tự động thắp sáng tương ứng theo ngưỡng thành tích tích lũy của trẻ (đạt 1 bài thắp 1 sao, đạt 3 bài thắp 2 sao, đạt 10 bài thắp đủ 3 sao rực rỡ).
  * Biểu cảm của linh vật trên Hero banner tự động thay đổi theo mốc sao đạt được (`m2_mascot_saobong_welcome` $\rightarrow$ `m2_mascot_saobong_happy` $\rightarrow$ `m2_mascot_saobong_celebrate`).

### 2.4. Danh mục học tập đa sắc màu (Dynamic Category Chips)
Thanh danh mục cuộn ngang (`ll_m2_category_chips`) không bị gán cứng (*hardcode*) mà được `Member2HomeActivity` tự động tổng hợp động từ `GameRegistry.getInstance().getGames()`:
* Trích xuất toàn bộ các danh mục hiện có trong hệ sinh thái trò chơi (Ngôn ngữ, Toán học, Logic, Sáng tạo...).
* Bổ sung mục chọn mặc định *"Tất cả (N)"* với số lượng trò chơi hiển thị tức thì.
* Mỗi chip danh mục sở hữu màu nền viên thuốc pastel riêng biệt (`m2_bg_cat_pill_*.xml`), icon đặc trưng (`m2_ic_cat_*.xml`) và hiệu ứng phản hồi khi chạm: phóng to 104%, nổi bóng đổ 3dp và làm nổi bật danh mục đang chọn.

### 2.5. Thẻ Gợi ý "Học Tiếp Nào!" (Smart Continue Card)
Khu vực gợi ý học tập thông minh (`ll_m2_continue_card`) áp dụng thuật toán phân tích tiến độ:
1. Quét danh sách trò chơi trong hệ thống để tìm trò chơi mà bé đang học dở dang (số bài đã học $> 0$ và chưa hoàn thành $100\%$).
2. Nếu tìm thấy: Hiển thị tựa đề trò chơi đang dở, chi tiết tiến độ (ví dụ: *"Đã học 8/29 bài — Đạt 24 sao"*) cùng nút bấm *"Học tiếp ngay $\rightarrow$"*.
3. Nếu bé chưa bắt đầu hoặc đã hoàn thành toàn bộ: Thẻ tự động chuyển sang chế độ Khởi động bài học nhập môn với tựa đề *"Bắt đầu học chữ cái cùng Sao Bông!"*.
4. Khi bé chạm vào thẻ: Tự động điều hướng trực tiếp vào màn hình của mini-game tương ứng kèm âm thanh mở sách kì diệu `playOpenBook()`.

### 2.6. Danh sách trò chơi dạng Bìa sách 2.5D & Responsive Layout
* **Khả năng hiển thị thích ứng (*Responsive Span Count*):** `calculateResponsiveSpanCount()` tự động tính toán cấu hình màn hình. Trên điện thoại hiển thị lưới 2 cột cân đối; trên máy tính bảng ($\ge 600\text{dp}$) hoặc khi xoay ngang màn hình, lưới tự động mở rộng thành 3 cột giúp tối ưu hóa không gian hiển thị.
* **Thiết kế Thẻ trò chơi dạng bìa sách (`GameGridAdapter` & `HomeGameUiModel`):**
  * Mỗi trò chơi được thể hiện như một cuốn sách thu nhỏ với gáy sách dập nổi, góc bo tròn mềm mại và dải màu bìa sách đặc trưng.
  * Hiển thị thanh tiến độ nằm ngang (`ProgressBar`) bọc bo tròn viền.
  * Hiển thị số sao đạt được kèm icon sao vàng.
  * **Quy tắc sắp xếp ưu tiên (*Featured Book Order*):** 4 trò chơi của Thành viên 2 đã hoàn thiện được đẩy lên đầu trang theo đúng lộ trình sư phạm:
    $$\text{Chữ Cái (Alphabet)} \longrightarrow \text{Ghép Vần (Spelling)} \longrightarrow \text{Tiếng Anh (English)} \longrightarrow \text{Đọc Truyện (Storybook)}$$
    Tiếp nối phía sau là 11 thẻ trò chơi mở rộng với nhãn huy hiệu trang trọng *"Sắp ra mắt"* (`badge_coming_soon`).

### 2.7. Tích hợp Điều hướng Thông minh (`Member2GameRouter`) & Empty State
* **Điều hướng an toàn:** Lớp `Member2GameRouter` tự động kiểm tra xem Activity đích có tồn tại trong hệ thống hay không bằng cơ chế Reflection và PackageManager an toàn. Khi người dùng chạm vào trò chơi chưa phát triển, ứng dụng hiển thị thông báo Toast nhẹ nhàng *"Trò chơi [Tên] đang được hoàn thiện, bé chờ nhé!"* mà không gây crash ứng dụng.
* **Trạng thái lọc rỗng (*Empty State*):** Khi người dùng chọn một danh mục chưa có trò chơi nào, layout rỗng (`layout_m2_empty_state`) xuất hiện với hình minh họa hộp quà rỗng dễ thương và nút bấm *"Xem tất cả trò chơi"*, cho phép khôi phục về danh mục "Tất cả" chỉ bằng một chạm.

---

## 3. MINI-GAME ALPHABET — HỌC BẢNG CHỮ CÁI TIẾNG VIỆT (`AlphabetActivity`)

### 3.1. Mục tiêu học tập & Bộ nội dung 29 chữ cái
Lớp `AlphabetContent` cung cấp cấu trúc dữ liệu chuẩn mực cho toàn bộ 29 chữ cái tiếng Việt theo chương trình giáo dục mầm non của Bộ GD&ĐT:
$$\text{A, Ă, Â, B, C, D, Đ, E, Ê, G, H, I, K, L, M, N, O, Ô, Ơ, P, Q, R, S, T, U, Ư, V, X, Y}$$
Mỗi chữ cái là một đối tượng `AlphabetCardItem` bao gồm:
* Ký tự in hoa và in thường tương ứng.
* Tên chữ cái chuẩn phiên âm sư phạm (ví dụ: A đọc là "a", Ă đọc là "á", Â đọc là "ớ", Đ đọc là "đê").
* Từ vựng ví dụ gần gũi với trẻ nhỏ (A — Quả Cam, B — Con Bướm, C — Con Cá...).
* Resource ID của hình vẽ minh họa vector sắc nét tương ứng.
* Hướng dẫn nét viết từng bước (ví dụ: *"Nét xiên trái $\rightarrow$ Nét xiên phải $\rightarrow$ Nét ngang giữa"*).
* Bộ từ ngữ mở rộng giúp bé phát triển vốn từ.

### 3.2. Chế độ Thẻ chữ Flashcard 2 mặt (3D Flip Animation)
* Triển khai bằng `ViewPager2` kết hợp `AlphabetCardAdapter` và layout `m2_item_alphabet_flashcard.xml`.
* **Hiệu ứng lật thẻ 3D (*Card Flip 3D*):**
  * Tận dụng thuộc tính `setCameraDistance()` để thiết lập chiều sâu không gian quang học, loại bỏ hiện tượng méo góc khi xoay.
  * Hoạt ảnh xoay trục Y mềm mại (180 độ): Khi người dùng chạm vào nút lật thẻ hoặc vuốt nhẹ, mặt trước phẳng mượt mà ẩn đi và mặt sau hiện lên liền mạch.
* **Mặt trước (Front View):** Hiển thị khối chữ cái in hoa cỡ lớn trên biển khắc nổi (`m2_bg_alphabet_letter_plaque.xml`), hình minh họa vector phong cách ấm áp, từ vựng ví dụ và nút loa phát âm.
* **Mặt sau (Back View):** Hiển thị sơ đồ hướng dẫn thứ tự các nét viết chữ cái, bảng từ vựng mở rộng và khung tổng kết giúp phụ huynh dễ dàng hướng dẫn con em tại nhà.

### 3.3. Chế độ Thử thách Nghe chọn chữ (Listening Challenge)
Khi chuyển sang tab *"Thử Thách"* (`btn_m2_alpha_mode_challenge`), giao diện chuyển sang sân khấu luyện nghe tương tác:
1. Động cơ tự động chọn ngẫu nhiên một chữ cái mục tiêu và phát âm qua Text-to-Speech (ví dụ: *"Bé hãy tìm chữ: B"*).
2. Nút phát lại hình loa to tròn (`btn_m2_alpha_challenge_replay`) cho phép bé nghe lại nhiều lần tùy ý.
3. Hiển thị 3 nút lựa chọn chữ cái cỡ lớn (`btnChallengeChoices[0..2]`), trong đó có 1 đáp án đúng và 2 đáp án gây nhiễu được xáo trộn ngẫu nhiên.
4. **Phản hồi tức thì:**
   * Chọn đúng: Nút lựa chọn chuyển sang màu xanh ngọc bích rực rỡ, phát hiệu ứng âm thanh chuông ngân `playCorrect()`, linh vật Sao Bông nhảy mừng và giọng nói tiếng Việt cất lên ngẫu nhiên lời khen ngợi.
   * Chọn sai: Nút lựa chọn rung nhẹ chuyển màu cam mềm mại, phát âm thanh thử lại `playGentleTry()` và giọng đọc dịu dàng động viên: *"Chưa đúng rồi, mình thử lại nhé!"*.

### 3.4. Thanh cuộn chọn chữ nhanh & Mốc hạt giống đã gieo
* Nằm ở cạnh dưới màn hình (`hsv_m2_alphabet_selector`), chứa 29 nút tròn đại diện cho 29 chữ cái.
* Mỗi nút tròn hiển thị ký tự chữ cái tương ứng. Khi chữ cái đã được bé học và ghi nhớ, nút tròn tự động chuyển sang trạng thái "Hạt giống đã gieo mầm" màu xanh lá tươi sáng (`m2_bg_alphabet_seed_learned.xml`).
* Trẻ có thể cuộn ngang và chạm vào bất kỳ chữ cái nào để nhảy ngay đến thẻ chữ đó mà không cần vuốt qua nhiều trang.
* Cột mốc hoàn thành (*Milestones*): Banner động trên cùng (`tvMilestoneBanner`) tự động chúc mừng khi bé đạt các mốc 5 chữ cái, 10 chữ cái, 20 chữ cái và trọn vẹn 29 chữ cái.

### 3.5. Hệ thống âm thanh, phát âm TTS & Phản hồi khích lệ
* Tích hợp bộ phát âm `Member2SpeechHelper` chuẩn `vi-VN` với tốc độ phát âm được hiệu chỉnh chậm rãi ($0.92\times$) và cao độ trong sáng ($1.04\times$), đặc biệt thích hợp cho khả năng tiếp thu thính giác của trẻ mầm non.
* Hệ thống `Member2VoiceFeedback` kết nối trực tiếp vào các mốc xác thực đáp án đúng/sai, đảm bảo không bao giờ phát tiếng chồng lấn lên giọng đọc mẫu của chữ cái.

---

## 4. MINI-GAME SPELLING — XƯỞNG GHÉP VẦN TIẾNG VIỆT (`SpellingActivity`)

### 4.1. Mục tiêu học tập & Khung kiến thức 16 bài ghép vần
Mini-game được xây dựng theo mô hình "Xưởng Ghép Chữ Thông Minh", cung cấp 16 bài học ghép vần tiếng Việt chuẩn Unicode, chia làm các cấp độ từ đơn giản đến nâng cao:
* **Cấp độ 1 — Từ đơn âm vị cơ bản (2 chữ cái):** `CÁ`, `MẸ`, `BA`, `BÒ`, `GÀ`.
* **Cấp độ 2 — Phụ âm ghép & Vần đôi (3 chữ cái):** `THỎ`, `MÈO`, `HOA`, `NẤM`, `CÂY`.
* **Cấp độ 3 — Cấu trúc âm tiết đầy đủ có âm đệm, âm cuối (4–5 chữ cái):** `SÁCH`, `QUẢ`, `CHIM`, `BÓNG`, `MÂY`, `TRĂNG`.

Lớp `SpellingEngine` quản lý việc phân tách từ ghép thành các âm vị đơn lẻ, xử lý chuẩn hóa tiếng Việt có dấu theo quy chuẩn Unicode NFC/NFD, đảm bảo việc so khớp ký tự chính xác tuyệt đối ngay cả với các nguyên âm có dấu thanh phức tạp (`ơ`, `ê`, `ă`, `ả`, `ã`, `ắ`...).

### 4.2. Cơ chế tương tác kép: Kéo - Thả (Drag & Drop) và Chạm nhanh (Tap to Place)
Nhận thấy một số trẻ 4 tuổi ngón tay còn vụng về khi thực hiện thao tác kéo - thả liên tục trên màn hình cảm ứng, Thành viên 2 đã thiết kế giải pháp tương tác kép thông minh:
* **Thao tác Kéo - Thả (*Drag & Drop*):**
  * Trẻ nhấn giữ vào thẻ chữ ở khay dưới (`ll_m2_spelling_pool`), sự kiện `View.startDragAndDrop()` được kích hoạt với `ClipData` chứa chỉ số của thẻ chữ.
  * Trong quá trình kéo, các ô slot (`ll_m2_spelling_slots`) lắng nghe sự kiện `DragEvent.ACTION_DRAG_ENTERED` và `ACTION_DRAG_EXITED`, tự động sáng viền đứt nét (`m2_bg_spelling_slot_hover.xml`) để dẫn hướng ngón tay cho bé.
  * Khi thả thẻ vào ô, thẻ chữ đáp gọn gàng vào vị trí kèm âm thanh lách cách xúc giác `playTap()`.
* **Thao tác Chạm nhanh (*Tap to Place*):**
  * Trẻ chỉ cần chạm 1 lần vào thẻ chữ dưới khay: Thẻ chữ tự động tìm và bay vào ô slot còn trống đầu tiên từ trái sang phải.
  * Ngược lại, khi chạm vào một thẻ chữ đang nằm trên ô slot: Thẻ chữ lập tức được thu hồi trả về khay bên dưới.

### 4.3. Bàn làm việc (Workbench), Các ô Slot & Khay thẻ chữ (Tray Pool)
* **Bàn làm việc Xưởng Chữ (`m2_bg_spelling_workbench.xml`):** Đóng khung bằng họa tiết gỗ mộc bọc đệm ấm, đặt ở vị trí trung tâm màn hình.
* **Các ô chứa chữ (*Slots*):** Kích thước chuẩn lớn ($64\times 64\text{dp}$), thể hiện 3 trạng thái rõ ràng:
  * *Trạng thái rỗng:* Nền màu be nhạt, viền đứt nét màu xanh lơ (`m2_bg_spelling_slot_empty.xml`).
  * *Trạng thái đang điền chữ:* Nền nổi khối 2.5D, ký tự in đậm to rõ (`m2_bg_spelling_slot_filled.xml`).
  * *Trạng thái trả lời đúng:* Chuyển sang viền xanh ngọc bích phát sáng (`m2_bg_spelling_slot_correct.xml`).
* **Khay chứa thẻ chữ (*Tray Pool*):** Khay gỗ chứa các thẻ chữ cái cần thiết cho từ hiện tại kèm theo các chữ cái gây nhiễu được thuật toán xáo trộn ngẫu nhiên. Khi một thẻ chữ đã được đặt vào ô slot, thẻ trên khay tự động mờ đi ($\alpha = 0.28$) và vô hiệu hóa tương tác tạm thời để chống trùng lặp.

### 4.4. Hệ thống trợ giúp thông minh (Gợi ý chữ & Thu hồi ô)
* **Nút Gợi ý bóng đèn (`btn_m2_spelling_hint`):** Khi bé lúng túng, chạm vào nút bóng đèn sẽ kích hoạt hàm `findFirstSlotNeedingHelp()`. Ứng dụng tự động phát hiện ô slot sai hoặc trống đầu tiên, chớp sáng nhẹ thẻ chữ cái tương ứng trên khay và phát giọng đọc dẫn hướng nhẹ nhàng.
* **Nút Thu hồi làm lại (`btn_m2_spelling_clear`):** Thu hồi toàn bộ các chữ cái đang nằm trên ô slot về lại khay chứa trong một lần chạm, giúp bé dễ dàng sắp xếp lại từ đầu.
* **Nút Loa nghe phát âm từ (`btn_m2_spelling_listen`):** Cho phép bé nghe giọng đọc mẫu của từ ngữ và ngữ cảnh minh họa bất kỳ lúc nào.

### 4.5. Kiểm tra đáp án, đọc từ hoàn chỉnh & Phản hồi giọng nói
Khi bé nhấn nút *"Kiểm Tra"* (`btn_m2_spelling_check`):
* **Trường hợp ghép đúng:**
  * Toàn bộ các ô chữ chuyển sang sắc xanh ngọc bích rực rỡ, phát âm thanh leng keng `playCorrect()`.
  * **Chuỗi giọng nói sư phạm tuần tự:** Bộ đọc `Member2VoiceFeedback.speakTextThenCorrect()` phát âm từ vựng hoàn chỉnh bằng tiếng Việt trước (ví dụ: *"Con Cá"*), sau đó tự động nối tiếp bằng một lời khen ngợi nồng nhiệt (ví dụ: *"Tuyệt vời! Bé làm đúng rồi!"*).
  * Ghi nhận điểm số, lưu tiến độ bài học vào Room Database và mở khóa bài tiếp theo.
* **Trường hợp ghép sai:**
  * Các ô chữ chưa đúng rung lắc nhẹ (Shake Animation), phát âm thanh nhẹ nhàng `playGentleTry()`.
  * Giọng nói tiếng Việt ấm áp cất lên: *"Chưa đúng rồi, mình thử lại nhé!"*. Không trừ điểm, không phạt thời gian, bảo vệ tối đa tâm lý tự tin của trẻ.

---

## 5. MINI-GAME ENGLISH — TIẾNG ANH CHO BÉ (`EnglishActivity`)

### 5.1. Mục tiêu học tập & 4 chủ đề từ vựng cốt lõi
Module Tiếng Anh được thiết kế nhằm xây dựng phản xạ song ngữ tự nhiên cho trẻ với 24 từ vựng nền tảng phân bổ đều trong 4 chủ đề thân thuộc nhất:
1. **Animals (Động vật):** Dog (Con chó), Cat (Con mèo), Bird (Con chim), Duck (Con vịt), Rabbit (Con thỏ), Fish (Con cá).
2. **Colors (Màu sắc):** Red (Màu đỏ), Blue (Màu xanh dương), Green (Màu xanh lá), Yellow (Màu vàng), Orange (Màu cam), Purple (Màu tím).
3. **Numbers (Chữ số):** One (Số 1), Two (Số 2), Three (Số 3), Four (Số 4), Five (Số 5), Six (Số 6).
4. **Family (Gia đình):** Father (Bố), Mother (Mẹ), Brother (Anh/Em trai), Sister (Chị/Em gái), Baby (Em bé), Grandfather (Ông).

### 5.2. Chế độ Flashcard trực quan đa phương tiện
* Giao diện thẻ Flashcard lớn bọc bóng nổi 3D (`card_m2_english_flashcard`):
  * Hình minh họa vector sắc nét tương ứng với từ vựng.
  * Từ tiếng Anh in hoa đậm nét, cỡ chữ lớn ($30\text{sp}$).
  * Phiên âm quốc tế chuẩn IPA đặt trong ngoặc vuông (ví dụ: `/kæt/`, `/dɔːɡ/`, `/red/`).
  * Nghĩa tiếng Việt giải thích rõ ràng kèm câu ví dụ song ngữ sinh động (ví dụ: *"The cat is cute — Chú mèo thật đáng yêu"*).
  * Nút loa phát âm to rõ: Kích hoạt giọng đọc tiếng Anh bản xứ chuẩn Hoa Kỳ (`Locale.US`).

### 5.3. Chế độ Luyện nghe chọn hình (Listening Mode)
* Chuyển đổi linh hoạt qua nút chế độ *"Luyện Nghe"* (`btn_m2_english_mode_listen`).
* Ứng dụng ẩn từ vựng chữ viết, chỉ giữ lại nút loa phát âm to ở trung tâm màn hình.
* Khi loa phát âm (ví dụ: *"Blue"*), bé lắng nghe và quan sát 4 thẻ hình ảnh lựa chọn (`optionCards[0..3]`).
* Rèn luyện thói quen phản xạ liên tưởng trực tiếp giữa âm thanh ngôn ngữ và hình ảnh trực quan mà không cần bước dịch trung gian.

### 5.4. Chế độ Trắc nghiệm tương tác (Interactive Quiz Stage)
* Được vận hành bởi động cơ trắc nghiệm `EnglishQuizEngine`: Tự động sinh ra 20 câu hỏi trắc nghiệm tương tác với cấu trúc chuẩn: câu hỏi dẫn dắt, 1 đáp án chính xác và 3 đáp án nhiễu cùng chủ đề.
* Thanh tiến trình trắc nghiệm hiển thị số câu đã vượt qua (ví dụ: *"Câu 3/20"*).
* Tính điểm và tặng sao minh bạch: Trẻ trả lời đúng trên 80% số câu hỏi sẽ được trao tặng trọn vẹn 3 ngôi sao danh dự.
* Bảng tổng kết kết quả thi đua xuất hiện ở câu cuối cùng với tràng pháo tay ăn mừng `playCelebrate()` và lời tuyên dương từ linh vật Sao Bông.

### 5.5. Cơ chế phát âm đa ngôn ngữ an toàn: Tiếng Anh kết hợp Giọng khen Tiếng Việt
* **Thách thức kỹ thuật:** Trong mini-game Tiếng Anh, từ vựng và câu hỏi bắt buộc phải phát âm bằng tiếng Anh chuẩn (`Locale.US`), trong khi lời khen thưởng và động viên kết quả bắt buộc phải nói bằng tiếng Việt (`vi-VN`) để trẻ 4–8 tuổi hiểu được. Nếu gọi đồng thời, hai giọng đọc sẽ phát đè lên nhau gây chói tai và méo tiếng.
* **Giải pháp của Thành viên 2:** Sử dụng cơ chế hàng đợi bất đồng bộ tuần tự (*Sequential Voice Queue*):
  1. Khi bé chạm đáp án trắc nghiệm: Ứng dụng kích hoạt phát âm từ tiếng Anh vừa chọn bằng TextToSpeech tiếng Anh.
  2. Lắng nghe callback `onSpeechDone` từ `UtteranceProgressListener`.
  3. Chỉ khi giọng đọc tiếng Anh đã hoàn tất trọn vẹn, ứng dụng mới chuyển Locale sang `vi-VN` và cất lời khen tiếng Việt (*"Xuất sắc! Bé làm đúng rồi!"*).
  4. Nếu có lỗi mạng hoặc ngắt quãng, Watchdog timer tự động giải phóng tài nguyên sau 2.5 giây, đảm bảo trải nghiệm luôn liền mạch, tuyệt đối không bị treo hay nghẽn âm.

---

## 6. MINI-GAME STORYBOOK — ĐỌC TRUYỆN TRANH (`StorybookActivity`)

### 6.1. Mục tiêu học tập & 3 câu chuyện ngụ ngôn nguyên bản giàu tính giáo dục
Lớp `StoryContent` biên soạn trọn vẹn 3 câu chuyện ngụ ngôn kinh điển có giá trị giáo dục nhân cách sâu sắc cho trẻ em:
1. **Rùa và Thỏ (`story_tortoise_hare` — 5 trang):** Bài học về tính kiên trì, nỗ lực bền bỉ và không bao giờ chủ quan kiêu ngạo.
2. **Quạ Thông Minh (`story_crow_pitcher` — 4 trang):** Bài học ca ngợi trí thông minh, tính kiên nhẫn tìm tòi giải pháp khi đối mặt khó khăn.
3. **Kiến và Châu Chấu (`story_ant_grasshopper` — 4 trang):** Bài học về tinh thần chăm chỉ lao động, biết lo xa và chuẩn bị chu đáo cho tương lai.

Mỗi câu chuyện được thiết kế từ 4 đến 5 trang sách. Từng trang sách đều có hình ảnh minh họa vector toàn cảnh, đoạn văn bản tiếng Việt trong sáng, đoạn dịch tiếng Anh phụ đề và từ khóa đạo đức nổi bật.

### 6.2. Kiến trúc 2 màn hình: Thư viện truyện (Library) & Trình đọc sách (Reader)
* **Màn hình Thư viện Truyện (*Story Library View*):**
  * Hiển thị danh sách các cuốn truyện dưới dạng các tập sách bìa cứng 2.5D nằm trên kệ sách gỗ.
  * Mỗi cuốn truyện có nhãn số trang, mô tả bài học đạo đức ngắn và huy hiệu đánh dấu trạng thái *"Đã đọc xong"* hoặc *"Đang đọc"*.
* **Màn hình Trình đọc Sách (*Story Reader View*):**
  * Tự động ẩn thanh danh sách, mở ra không gian đọc truyện toàn màn hình ấm áp.
  * Hỗ trợ nút quay lại thư viện nhanh (`btn_m2_story_back_library`) và xử lý nút Back vật lý của Android qua `OnBackPressedDispatcher`, luôn đảm bảo quay về Thư viện an toàn trước khi thoát game.

### 6.3. Cơ chế lật trang sách chân thực (ViewPager2 PageTransformer) & Minh họa Vector
* Sử dụng `ViewPager2` kết hợp `StoryPageAdapter` và layout `m2_item_story_page.xml`.
* Tích hợp hiệu ứng lật trang sách chân thực: Khi trẻ vuốt màn hình, trang sách lướt qua với độ dốc bóng mờ (*Page Elevation Gradient*) và góc nghiêng trang giấy, tạo cảm giác như đang lật giở từng trang của một cuốn truyện tranh in màu cao cấp ngoài đời thực.
* Mỗi trang sách chứa khung tranh minh họa khổ lớn sắc nét cùng hộp chữ viền tròn mềm mại với kích thước chữ đọc thoải mái ($18\text{sp}$), chống mỏi mắt cho trẻ.

### 6.4. Trình kể chuyện tự động có tô màu câu đọc (`NarrationController`)
* Bộ điều khiển đọc truyện `NarrationController` hoạt động như một cô giáo mầm non kể chuyện:
  * Nút Play/Pause lớn (`btn_m2_story_play_pause`): Bật hoặc tạm dừng giọng đọc bất kỳ lúc nào.
  * Nút Phát lại (`btn_m2_story_replay`): Đọc lại trang hiện tại.
* **Tính năng bắt nét tô chữ thời gian thực (*Sentence Range Highlighting*):**
  * Lắng nghe sự kiện `onRangeStart(utteranceId, start, end, frame)` từ engine TextToSpeech.
  * Tự động bôi màu nền vàng nghệ ấm áp (*SpannableString Highlight*) cho chính xác từng câu hoặc từng cụm từ mà giọng đọc đang cất lên.
  * Giúp trẻ vừa nghe kể chuyện, vừa dõi mắt theo từng mặt chữ, hình thành khả năng nhận diện mặt chữ tự nhiên.
* Khi giọng đọc hoàn thành trang hiện tại, bộ điều khiển tự động lật sang trang kế tiếp một cách mượt mà.

### 6.5. Tổng kết bài học đạo đức (Moral Lesson) & Chúc mừng hoàn thành
* Tại trang cuối cùng của mỗi câu chuyện, layout hiển thị tấm thẻ bài học vàng óng (*Moral Lesson Plaque*) đúc kết ý nghĩa nhân văn của câu chuyện bằng ngôn từ ngắn gọn, dễ nhớ.
* Nút *"Hoàn Thành Câu Chuyện"* xuất hiện trang trọng: Khi trẻ bấm vào, hiệu ứng âm thanh ăn mừng `playCelebrate()` vang lên rộn rã, giọng nói tiếng Việt cất lên lời khen ngợi truyền cảm: *"Tuyệt vời! Bé đã vượt qua thử thách!"*, đồng thời ghi nhận huy hiệu đã đọc vào cơ sở dữ liệu.

---

## 7. HỆ THỐNG ÂM THANH & GIỌNG NÓI ĐỘNG VIÊN (`Member2SoundEffects` & `Member2VoiceFeedback`)

### 7.1. Kiến trúc tích hợp không xâm lấn với `SoundManager` của Core
* Để đảm bảo không gây xung đột với mã nguồn của Thành viên 1, toàn bộ hệ thống âm thanh của Thành viên 2 được bao bọc trong lớp điều phối tập trung `Member2SoundEffects`.
* Lớp này **chỉ giao tiếp thông qua các API công khai của `SoundManager`** (`getInstance()`, `preloadSound()`, `playSfx()`, `playBgm()`, `setBgmVolume()`, `isSfxMuted()`, `isBgmMuted()`...), tuyệt đối không sửa đổi một dòng code nào trong `SoundManager.java`.

### 7.2. Bản nhạc nền phong cách "Magical Storybook — Gentle Music Box & Soft Piano" (72 BPM)
* **File nhạc gốc chất lượng cao:** `m2_bgm_magic_book.wav` (44.1 kHz, 16-bit Stereo, thời lượng vòng lặp 53.33 giây mượt mà không ngắt quãng).
* **Đặc trưng âm học:**
  * Kết hợp giữa tiếng đàn Hộp Nhạc (*Music Box*) trong trẻo và tiếng Dương Cầm (*Soft Felt Piano*) ấm áp, thư thái.
  * Nhịp độ 72 BPM chuẩn thư giãn, giai điệu ngọt ngào phù hợp tuyệt đối với tâm lý trẻ 4–8 tuổi.
  * Không sử dụng tiếng trống dồn dập, không có âm cao chói tai, không có giọng hát (*vocal*), không có sự thay đổi âm lượng đột ngột.
* **Cân bằng tỷ lệ âm lượng khoa học:**
  * Nhạc nền luôn đóng vai trò phụ êm dịu làm nền.
  * Âm lượng tại Home Dashboard: giữ ở mức **18%** (`HOME_BGM_VOLUME = 0.18f`).
  * Âm lượng khi vào các Mini-game: tự động giảm xuống mức **10%** (`MINIGAME_BGM_VOLUME = 0.10f`) để bé tập trung nghe âm vần.

### 7.3. Bộ hiệu ứng âm thanh xúc giác (SFX)
Bao gồm 5 file hiệu ứng âm thanh chuẩn được lưu trong `res/raw/` với tiền tố `m2_`:
1. `m2_sfx_tap.wav`: Tiếng gõ chạm nhẹ như hạt bong bóng nảy, phát khi chạm các nút điều hướng.
2. `m2_sfx_open_book.wav`: Tiếng sột soạt lật mở trang sách ma thuật, phát khi mở bài học.
3. `m2_sfx_correct.wav`: Tiếng chuông gió ngân nga trong trẻo khi trả lời chính xác.
4. `m2_sfx_gentle_try.wav`: Âm thanh mộc ấm áp, dịu dàng khi chọn chưa đúng, không gây cảm giác thất bại hay khó chịu.
5. `m2_sfx_celebrate.wav`: Hợp xướng chuông vàng rộn rã khi hoàn thành bài học hoặc đạt mốc sao.

Tất cả các SFX đều được bảo vệ bởi cơ chế lọc chống bấm lặp (*Debounce Cooldown*) với khoảng thời gian tối thiểu 70ms (`MIN_SFX_INTERVAL_MS`), ngăn ngừa triệt để hiện tượng méo tiếng khi trẻ bấm liên hồi vào màn hình.

### 7.4. Hệ thống giọng nói khích lệ tiếng Việt ấm áp (`Member2VoicePhrases`)
Lớp `Member2VoicePhrases` và `Member2VoiceFeedback` quản lý bộ danh mục các câu thoại tiếng Việt sư phạm được chọn lọc kỹ lưỡng, chia làm 3 nhóm:
* **Nhóm 1 — Khen ngợi khi trả lời đúng (CORRECT):**
  * *"Chính xác rồi! Bé giỏi lắm!"*
  * *"Tuyệt vời! Bé làm đúng rồi!"*
  * *"Xuất sắc! Tiếp tục nào!"*
  * *"Hay quá! Bé thật thông minh!"*
* **Nhóm 2 — Động viên nhẹ nhàng khi trả lời sai (WRONG):**
  * *"Chưa đúng rồi, mình thử lại nhé!"*
  * *"Không sao đâu, bé thử lần nữa nào!"*
  * *"Cố lên! Bé sắp làm được rồi!"*
  * *"Thử lại một lần nữa nhé!"*
* **Nhóm 3 — Chúc mừng khi hoàn thành bài học (COMPLETE):**
  * *"Chúc mừng! Bé đã hoàn thành bài học!"*
  * *"Tuyệt vời! Bé đã vượt qua thử thách!"*
  * *"Giỏi lắm! Cùng khám phá bài tiếp theo nhé!"*

**Cơ chế phát thông minh:**
* Thuật toán xáo trộn ngẫu nhiên có bộ nhớ: Đảm bảo không bao giờ phát lặp lại một câu thoại 2 lần liên tiếp.
* Áp dụng khoảng cách giãn cách (*Cooldown Interval*) giữa các câu trong cùng một nhóm (tối thiểu 1.2 giây), ngăn chặn tình trạng giọng nói đọc đè lên nhau.

### 7.5. Cơ chế Audio Ducking tự động & Quản lý vòng đời âm thanh an toàn
* **Tự động giảm âm nền (*Audio Ducking*):**
  * Ngay khi bắt đầu có giọng đọc chữ cái, từ vựng hoặc giọng khen ngợi (`onStart`), nhạc nền được `ValueAnimator` tự động hạ nhanh xuống mức thì thầm cực nhỏ **2.5%** (`SPEECH_DUCKED_BGM_VOLUME = 0.025f`) trong vòng 120ms.
  * Sau khi giọng đọc dứt lời (`onDone`), bộ điều khiển chờ một nhịp debounce 120ms (phòng trường hợp có câu khen nối tiếp), rồi mới tăng dần âm lượng trở lại mức ban đầu qua hiệu ứng Fade In mềm mại kéo dài 500ms.
* **Quản lý vòng đời an toàn (*Lifecycle-aware*):**
  * Khi người dùng rời khỏi màn hình (`onPause` / `onStop`): Toàn bộ giọng đọc TTS và BGM lập tức tạm dừng hoặc giải phóng, tuyệt đối không có âm thanh rò rỉ chạy ngầm ngoài màn hình.
  * Khi người dùng nhấn nút Tắt tiếng (Mute): Cả BGM, SFX và giọng nói TTS đều lập tức dừng phát ngay trong micro giây.

### 7.6. Bảng đối chiếu hiện trạng âm thanh: Đã cài đặt, Đã kiểm thử & Hạn chế thực tế

| Thành phần âm thanh | Hiện trạng trong Code | Đã kiểm thử xác minh | Đánh giá & Hạn chế thực tế |
|---|---|---|---|
| **Nhạc nền BGM (`m2_bgm_magic_book`)** | Đầy đủ trong `res/raw/`, điều phối qua `Member2SoundEffects` | Xác minh vòng lặp, âm lượng 18% và 10% trên Emulator | Nhạc nền êm dịu, vòng lặp mượt mà không ngắt quãng |
| **Hiệu ứng xúc giác (SFX 5 loại)** | Đầy đủ 5 file WAV trong `res/raw/` | Xác minh qua Logcat và phát trên Emulator | Hoạt động ổn định, có cooldown 70ms chống bấm dồn dập |
| **Audio Ducking tự động** | Thuật toán ValueAnimator trong `Member2SoundEffects` | Kiểm tra luồng `duckBgmForSpeech(true/false)` | Âm lượng hạ xuống 2.5% và khôi phục mượt mà |
| **Phát âm Tiếng Việt (Bảng chữ cái, Ghép vần)** | Tích hợp qua `Member2SpeechHelper` (Locale `vi-VN`) | Xác minh khởi tạo thành công engine TTS trên Emulator | Chất lượng giọng đọc phụ thuộc vào gói giọng `vi-VN` đã cài đặt trên thiết bị Android của người dùng |
| **Phát âm Tiếng Anh (Module English)** | Tích hợp qua `Member2SpeechHelper` (Locale `en-US`) | Xác minh phát âm từ vựng tiếng Anh trên Emulator | Giọng đọc tiếng Anh chuẩn US rõ ràng, tự nhiên |
| **Giọng nói khen/động viên tiếng Việt** | Quản lý qua `Member2VoiceFeedback` | Xác minh chuỗi hàng đợi không chồng lấn trên Emulator | Đảm bảo an toàn không crash; thiết bị thiếu gói tiếng Việt sẽ bỏ qua câu khen tiếng Việt an toàn |

---

## 8. THIẾT KẾ GIAO DIỆN & TÀI NGUYÊN ĐỒ HỌA (UI/UX DESIGN SYSTEM)

### 8.1. Phong cách thiết kế Cuốn Sách Kỳ Diệu (The Magic Book)
Toàn bộ hệ thống giao diện của Thành viên 2 được xây dựng theo ngôn ngữ thiết kế nhất quán:
* Các đường nét bo tròn lớn (Radius từ 16dp đến 24dp), mang lại cảm giác an toàn, mềm mại, gần gũi với trẻ nhỏ.
* Hiệu ứng chiều sâu 2.5D được tạo dựng qua các lớp đổ bóng đa tầng (*Multi-layered elevation shadows*), gáy sách da và các đường viền nổi bật.

### 8.2. Bảng màu Pastel thân thiện với trẻ em (`m2_colors.xml`)
Hệ thống màu sắc sử dụng tông màu Pastel tươi sáng, độ bão hòa được cân chỉnh vừa phải để tạo sự hào hứng cho trẻ mà không gây chói hay mỏi mắt:

| Tên màu trong tài nguyên | Mã màu HEX | Ý nghĩa sử dụng |
|---|---|---|
| `m2_cream_bg` | `#FFFDF7` | Màu nền trang giấy ấm áp, chống mỏi mắt |
| `m2_book_cover_coral` | `#FF6B6B` | Màu bìa sách San Hô tươi tắn (Chữ Cái) |
| `m2_book_cover_teal` | `#2EC4B6` | Màu bìa sách Xanh Mòng Két thông minh (Ghép Vần) |
| `m2_book_cover_purple` | `#79529CF` | Màu bìa sách Tím Mộng Mơ quốc tế (Tiếng Anh) |
| `m2_book_cover_pink` | `#FF70A6` | Màu bìa sách Hồng Ngọt Ngào cổ tích (Đọc Truyện) |
| `m2_color_gold_star` | `#FFB703` | Màu vàng ánh sao thành tích và huy hiệu sao |
| `m2_text_primary` | `#2D3142` | Màu chữ than đậm dễ đọc, độ tương phản cao |
| `m2_text_secondary` | `#6C757D` | Màu chữ phụ đề và hướng dẫn |

### 8.3. Thiết kế bìa sách và đồ họa minh họa 2.5D
* **Bìa sách trò chơi:** Toàn bộ 15 bìa sách trò chơi trong `res/drawable-nodpi/m2_cover_*.jpg` được thiết kế đồng bộ với tỷ lệ $4:3$ chuẩn (kích thước $560\times 420\text{px}$, dung lượng tối ưu chỉ từ 30 KB đến 50 KB mỗi ảnh). Khi nạp toàn bộ vào bộ nhớ, tổng dung lượng RAM chỉ chiếm khoảng 16 MB.
* **Đồ họa Vector nội dung:** Toàn bộ các hình minh họa chữ cái, con vật, màu sắc và đồ vật trong các mini-game đều sử dụng chuẩn Android Vector Drawable (`.xml`). Điều này giúp hình ảnh luôn sắc nét ở mọi mật độ điểm ảnh màn hình (từ mdpi đến xxxhdpi) mà kích thước tệp chỉ chiếm 2–3 KB.

### 8.4. Linh vật Sao Bông & Các biểu cảm trạng thái
Linh vật Sao Bông được thiết kế thành 4 trạng thái biểu cảm sống động lưu trong `res/drawable/`:
1. `m2_mascot_saobong_welcome.xml`: Sao Bông tươi cười vẫy tay đón chào bé khi mới mở ứng dụng.
2. `m2_mascot_saobong_happy.xml`: Sao Bông híp mắt cười vui vẻ khi bé hoàn thành một bài học nhỏ.
3. `m2_mascot_saobong_hint.xml`: Sao Bông chớp mắt chỉ tay hướng dẫn khi bé cần sự trợ giúp.
4. `m2_mascot_saobong_celebrate.xml`: Sao Bông đội vương miện sao, tung hoa ăn mừng khi bé đạt mốc xuất sắc.

### 8.5. Tối ưu tương tác công thái học cho trẻ 4–8 tuổi
* **Kích thước vùng chạm an toàn (*Touch Target Size*):** Mọi nút bấm tương tác đều có kích thước tối thiểu từ $48\times 48\text{dp}$ đến $64\times 64\text{dp}$, khoảng cách lề tối thiểu 8dp để trẻ không bị bấm nhầm.
* **Hiệu ứng xúc giác chạm (*Touch Feedback*):** Nút bấm tự động co nhỏ 95% khi chạm và nảy lại 100% khi nhả tay (`animateButtonPress`), mang lại phản hồi trực quan chân thực.
* **Bảo vệ màn hình xoay và khôi phục trạng thái:** Tất cả các Activity đều cài đặt đầy đủ `onSaveInstanceState` và khôi phục biến trạng thái, đảm bảo không bị mất tiến trình học khi người dùng xoay ngang màn hình hoặc ứng dụng bị hệ thống giải phóng tạm thời.

---

## 9. QUẢN LÝ TIẾN ĐỘ & ĐỒNG BỘ DỮ LIỆU (`Member2ProgressRepository`)

### 9.1. Kiến trúc lưu trữ 2 lớp (SharedPreferences chi tiết & Room Database)
Để quản lý tiến độ học tập một cách tối ưu và không xâm lấn kiến trúc chung của nhóm, Thành viên 2 đã áp dụng mô hình lưu trữ 2 tầng:
* **Tầng 1 — Chi tiết từng bài học (SharedPreferences `m2_member2_item_progress`):** Lưu trữ danh sách tập hợp (`Set<String>`) các mã định danh chi tiết của từng chữ cái, bài ghép vần, từ vựng hoặc cuốn truyện mà trẻ đã thực sự học xong (ví dụ: thẻ chữ `A`, `B`, bài ghép `sp_01`, từ tiếng Anh `animals_dog`, truyện `story_tortoise_hare`).
* **Tầng 2 — Tổng hợp hệ thống (Room Database `GameProgress`):** Tự động đồng bộ số lượng bài đã hoàn thành (`completedLevels`), tổng số bài (`totalLevels`) và số sao đạt được (`starsEarned`) vào bảng cơ sở dữ liệu dùng chung của ứng dụng.

### 9.2. Luồng thực thi bất đồng bộ nền (Background Executor & Main Looper Post)
* Mọi thao tác truy vấn và ghi dữ liệu Room Database đều được đóng gói và thực thi trên luồng nền thông qua `Executors.newSingleThreadExecutor()`.
* Khi có kết quả, `Handler(Looper.getMainLooper()).post()` sẽ chuyển giao dữ liệu an toàn về Main Thread để cập nhật giao diện, tuân thủ nguyên tắc tuyệt đối không chặn UI Thread của Android.

### 9.3. Cơ chế tính điểm và cấp sao lũy tiến
* Tính chất lũy tiến an toàn (*Idempotent*): Khi trẻ chơi lại một bài đã hoàn thành trước đó, hệ thống ghi nhận kết quả ôn tập nhưng không cộng dồn trùng lặp số bài đã hoàn thành, đảm bảo chỉ số tiến độ luôn chính xác.
* Tự động tính toán số sao tích lũy tương ứng theo tỷ lệ hoàn thành của từng trò chơi.

### 9.4. Đồng bộ 2 chiều an toàn khi Phụ huynh đặt lại dữ liệu
* Lớp `Member2ProgressRepository` tích hợp hàm `syncLocalPrefsWithRoomReset()`: Mỗi khi màn hình Home Dashboard được mở lại (`onResume`), repository sẽ đối chiếu số lượng trong Room Database.
* Nếu phụ huynh đã bấm nút "Đặt lại tiến độ" từ màn hình Cài Đặt Phụ Huynh (`ParentalSettingsActivity`), repository sẽ phát hiện sự chênh lệch và tự động dọn sạch bộ nhớ chi tiết tương ứng, đảm bảo dữ liệu hiển thị trên toàn bộ 4 mini-game luôn đồng bộ tuyệt đối với hệ thống của nhóm.

---

## 10. CẤU TRÚC MÃ NGUỒN & DANH MỤC THÀNH PHẦN

### 10.1. Sơ đồ cây thư mục các Package thuộc Thành viên 2

```
app/src/main/java/com/example/happykidlearn/member2/
├── alphabet/                       # Mini-game 1: Học Chữ Cái Tiếng Việt
│   ├── AlphabetActivity.java       # Activity chính (Flashcard 3D & Thử thách nghe)
│   ├── AlphabetCardAdapter.java    # Adapter hiển thị thẻ chữ cái lật 3D
│   └── AlphabetContent.java        # Danh mục dữ liệu chuẩn 29 chữ cái tiếng Việt
├── spelling/                       # Mini-game 2: Ghép Vần Tiếng Việt
│   ├── SpellingActivity.java       # Activity chính (Xưởng ghép chữ, Drag & Drop, Tap)
│   ├── SpellingEngine.java         # Động cơ ghép vần, chuẩn hóa Unicode NFD/NFC
│   └── SpellingExercise.java       # Model định nghĩa bài tập ghép vần
├── english/                        # Mini-game 3: Tiếng Anh Cho Bé
│   ├── EnglishActivity.java        # Activity chính (Flashcard, Listen, Quiz)
│   ├── EnglishContent.java         # Danh mục dữ liệu 4 chủ đề từ vựng tiếng Anh
│   ├── EnglishQuestion.java        # Model câu hỏi trắc nghiệm tiếng Anh
│   └── EnglishQuizEngine.java      # Động cơ sinh đề và chấm điểm trắc nghiệm
├── storybook/                      # Mini-game 4: Đọc Truyện Cổ Tích & Ngụ Ngôn
│   ├── StorybookActivity.java      # Activity chính (Thư viện truyện & Trình đọc)
│   ├── StoryContent.java           # Dữ liệu nội dung 3 câu chuyện ngụ ngôn
│   ├── StoryPage.java              # Model dữ liệu trang truyện
│   ├── StoryPageAdapter.java       # Adapter lật trang sách ViewPager2
│   └── NarrationController.java    # Trình đọc truyện tự động & bôi màu câu chữ
├── dashboard/                      # Màn hình Trung tâm Home Dashboard
│   ├── Member2HomeActivity.java    # Activity Trang chủ Cuốn Sách Kỳ Diệu
│   ├── GameGridAdapter.java        # Adapter hiển thị danh sách bìa sách trò chơi 2.5D
│   ├── HomeGameUiModel.java        # Model hiển thị thẻ trò chơi trên Dashboard
│   └── Member2GameRouter.java      # Bộ định tuyến điều hướng an toàn giữa các game
└── common/                         # Các thành phần dùng chung phân hệ Thành viên 2
    ├── Member2ContentCatalog.java  # Danh mục ánh xạ icon, bìa sách và màu sắc
    ├── Member2ProgressRepository.java # Quản lý tiến độ 2 lớp (SharedPreferences & Room)
    ├── Member2SoundEffects.java    # Bộ điều phối SFX và BGM Gentle Music Box 72 BPM
    ├── Member2SpeechHelper.java    # Trình điều khiển TextToSpeech dùng chung & Ducking
    ├── Member2VoiceFeedback.java   # Phản hồi giọng nói tiếng Việt khích lệ kết quả
    └── Member2VoicePhrases.java    # Danh mục các câu thoại khen thưởng / động viên
```

### 10.2. Bảng mô tả chi tiết trách nhiệm các lớp Java (25 Lớp)

| STT | Tên Lớp Java | Thuộc Package | Trách nhiệm chính trong hệ thống |
|:---:|---|---|---|
| 1 | `Member2HomeActivity` | `member2.dashboard` | Điều hành màn hình Trang chủ Cuốn Sách Kỳ Diệu, hiển thị danh mục, thẻ gợi ý, tổng quan sao |
| 2 | `GameGridAdapter` | `member2.dashboard` | Quản lý hiển thị danh sách các thẻ trò chơi dạng bìa sách 2.5D trên RecyclerView |
| 3 | `HomeGameUiModel` | `member2.dashboard` | Đóng gói dữ liệu hiển thị cho từng thẻ game (tiến độ, số sao, màu bìa, trạng thái sẵn sàng) |
| 4 | `Member2GameRouter` | `member2.dashboard` | Định tuyến an toàn sang 4 mini-game của Thành viên 2 và hiển thị thông báo chờ cho 11 game khác |
| 5 | `AlphabetActivity` | `member2.alphabet` | Màn hình học 29 chữ cái tiếng Việt với 2 chế độ Flashcard lật 3D và Thử thách nghe chọn chữ |
| 6 | `AlphabetCardAdapter` | `member2.alphabet` | Điều khiển hoạt ảnh lật 3D hai mặt thẻ chữ cái trong ViewPager2 |
| 7 | `AlphabetContent` | `member2.alphabet` | Định nghĩa kho nội dung chuẩn 29 chữ cái, từ vựng ví dụ, nét viết và hình vẽ minh họa |
| 8 | `SpellingActivity` | `member2.spelling` | Màn hình Xưởng Ghép Chữ, điều phối cơ chế tương tác Kéo - Thả, Chạm thẻ, gợi ý và kiểm tra |
| 9 | `SpellingEngine` | `member2.spelling` | Xử lý logic ghép vần tiếng Việt, chuẩn hóa Unicode NFD/NFC, xáo trộn khay chữ |
| 10 | `SpellingExercise` | `member2.spelling` | Cấu trúc dữ liệu bài tập ghép vần (mã bài, từ mục tiêu, gợi ý, ngữ cảnh minh họa) |
| 11 | `EnglishActivity` | `member2.english` | Màn hình học Tiếng Anh với 3 chế độ: Flashcard từ vựng, Luyện nghe và Trắc nghiệm Quiz |
| 12 | `EnglishContent` | `member2.english` | Lưu trữ dữ liệu từ vựng 4 chủ đề (Động vật, Màu sắc, Chữ số, Gia đình) kèm phiên âm IPA |
| 13 | `EnglishQuestion` | `member2.english` | Cấu trúc dữ liệu một câu hỏi trắc nghiệm tiếng Anh tương tác |
| 14 | `EnglishQuizEngine` | `member2.english` | Sinh đề thi trắc nghiệm ngẫu nhiên, xáo trộn đáp án nhiễu và tính toán số sao tích lũy |
| 15 | `StorybookActivity` | `member2.storybook` | Màn hình Đọc Truyện Tranh 2 chế độ: Thư viện truyện và Trình đọc sách lật trang |
| 16 | `StoryContent` | `member2.storybook` | Chứa nội dung 3 câu chuyện ngụ ngôn nguyên bản (Rùa & Thỏ, Quạ thông minh, Kiến & Châu chấu) |
| 17 | `StoryPage` | `member2.storybook` | Cấu trúc dữ liệu một trang truyện (minh họa, văn bản tiếng Việt, phụ đề tiếng Anh, bài học) |
| 18 | `StoryPageAdapter` | `member2.storybook` | Adapter hiển thị nội dung từng trang truyện tranh trên ViewPager2 |
| 19 | `NarrationController` | `member2.storybook` | Trình điều khiển đọc truyện tự động, bắt sự kiện TTS để bôi màu câu chữ đang đọc |
| 20 | `Member2ContentCatalog` | `member2.common` | Danh bạ tài nguyên trung tâm: ánh xạ icon, ảnh bìa sách 2.5D, màu sắc theo từng game |
| 21 | `Member2ProgressRepository` | `member2.common` | Quản lý lưu trữ tiến độ 2 tầng, đồng bộ SharedPreferences với Room Database nền |
| 22 | `Member2SoundEffects` | `member2.common` | Điều phối SFX xúc giác, phát nhạc nền BGM 72 BPM và thực hiện Audio Ducking mượt mà |
| 23 | `Member2SpeechHelper` | `member2.common` | Trình điều khiển TextToSpeech dùng chung, khởi tạo sớm (warm-up), quản lý hàng đợi âm đọc |
| 24 | `Member2VoiceFeedback` | `member2.common` | Điều hành việc phát giọng nói tiếng Việt khích lệ khi bé làm đúng/sai/hoàn thành bài học |
| 25 | `Member2VoicePhrases` | `member2.common` | Kho ngữ liệu các câu thoại tiếng Việt sư phạm khen thưởng và động viên, kiểm soát cooldown |

### 10.3. Bảng tổng hợp Layouts và Tài nguyên thuộc Thành viên 2

* **Giao diện Layouts (`res/layout/`):**
  * `m2_activity_home.xml`: Toàn bộ giao diện Home Dashboard Cuốn Sách Kỳ Diệu.
  * `m2_item_home_game_card.xml`: Layout một thẻ trò chơi bìa sách 2.5D.
  * `m2_activity_alphabet.xml`: Giao diện học chữ cái (Flashcard & Thử thách nghe).
  * `m2_item_alphabet_flashcard.xml`: Layout hai mặt thẻ chữ cái lật 3D.
  * `m2_activity_spelling.xml`: Giao diện Xưởng Ghép Chữ (Bàn làm việc, Slots, Khay chữ).
  * `m2_activity_english.xml`: Giao diện học tiếng Anh (Flashcard & Sân khấu Quiz).
  * `m2_activity_storybook.xml`: Giao diện Đọc truyện tranh (Thư viện & Trình đọc).
  * `m2_item_story_page.xml`: Layout một trang truyện tranh lật giở.
  * `m2_layout_game_header.xml`: Thanh tiêu đề trò chơi dùng chung chuẩn thiết kế Thành viên 2.
* **Tài nguyên Hình ảnh & Bìa sách (`res/drawable-nodpi/`):**
  * 15 ảnh bìa sách 2.5D (`m2_cover_*.jpg`), chuẩn kích thước $560\times 420\text{px}$.
  * 1 ảnh bìa Hero Pop-up Book (`m2_illust_hero_popup_book.jpg`), kích thước $960\times 540\text{px}$.
* **Tài nguyên Đồ họa Vector (`res/drawable/`):**
  * Hơn 70 file vector drawable (`m2_illust_*`, `m2_bg_*`, `m2_ic_*`, `m2_mascot_*`) cho các hình minh họa chữ cái, động vật, màu sắc, chữ số, hình nền nút bấm và 4 sắc thái linh vật Sao Bông.
* **Tài nguyên Âm thanh (`res/raw/`):**
  * 1 file nhạc nền: `m2_bgm_magic_book.wav` (BGM Gentle Music Box & Soft Piano, 72 BPM).
  * 5 file hiệu ứng xúc giác: `m2_sfx_tap.wav`, `m2_sfx_open_book.wav`, `m2_sfx_correct.wav`, `m2_sfx_gentle_try.wav`, `m2_sfx_celebrate.wav`.
* **Tài nguyên Giá trị (`res/values/`):**
  * `m2_colors.xml`: Bảng màu Pastel chuẩn sư phạm cho phân hệ Thành viên 2.
  * `m2_dimens.xml`: Bảng quy chuẩn kích thước lề, padding, bán kính bo góc và cỡ chữ.
  * `m2_strings.xml`: Toàn bộ các chuỗi văn bản giao diện tiếng Việt rõ ràng, dễ hiểu.
  * `m2_styles.xml`: Định nghĩa kiểu dáng nút bấm, thẻ bài và thanh công cụ.

---

## 11. BÁO CÁO KIỂM THỬ THỰC TẾ (TESTING & VERIFICATION REPORT)

### 11.1. Kết quả kiểm thử Unit Test (7 test classes, 26 test cases)
Toàn bộ logic nghiệp vụ, tính toán điểm số, định tuyến router và thuật toán âm thanh được bảo vệ bởi 7 lớp Unit Test tự động trong `app/src/test/java/com/example/happykidlearn/member2/`:

| Tên Test Class | Số Test Cases | Nội dung kiểm thử cốt lõi | Kết quả thực thi |
|---|:---:|---|:---:|
| `AlphabetContentTest` | 4 | Kiểm tra đủ 29 chữ cái tiếng Việt, thứ tự A–Y, hình minh họa hợp lệ, sinh đáp án trắc nghiệm nghe | **PASS (100%)** |
| `SpellingEngineTest` | 4 | Kiểm tra 16 bài ghép vần tiếng Việt, chuẩn hóa Unicode NFD/NFC, thuật toán hỗ trợ gợi ý ô chữ | **PASS (100%)** |
| `EnglishQuizEngineTest` | 4 | Kiểm tra 4 chủ đề x 6 từ vựng, thuật toán sinh 20 câu hỏi trắc nghiệm, tính sao minh bạch | **PASS (100%)** |
| `StoryContentTest` | 3 | Kiểm tra 3 câu chuyện ngụ ngôn, cấu trúc trang sách, thuật toán tách câu cho NarrationController | **PASS (100%)** |
| `HomeDashboardModelTest` | 3 | Kiểm tra ánh xạ 15 bìa sách, xử lý an toàn phép chia mẫu số 0, router chặn game chưa sẵn sàng | **PASS (100%)** |
| `Member2SoundEffectsTest` | 1 | Kiểm tra các hằng số âm lượng BGM thỏa mãn tỷ lệ phụ trợ 10–20% so với giọng đọc | **PASS (100%)** |
| `Member2VoicePhrasesTest` | 7 | Kiểm tra đủ các nhóm câu thoại, chống phát lặp liên tiếp, cơ chế giãn cách cooldown trong cùng nhóm | **PASS (100%)** |
| **TỔNG CỘNG** | **26** | **Toàn bộ 26/26 kiểm thử đơn vị thành công tuyệt đối** | **BUILD SUCCESSFUL** |

### 11.2. Kết quả kiểm thử tích hợp trên Android Emulator (Pixel 6, Android 14)
* **Môi trường kiểm thử:** Android Emulator thiết bị Google Pixel 6, hệ điều hành Android 14 (API 34), mật độ điểm ảnh 420dpi, độ phân giải $1080\times 2400$.
* **Đo lường hiệu năng thực tế (`dumpsys meminfo`):**
  * Tổng dung lượng RAM chiếm dụng (Total PSS): **chỉ 27.8 MB** (mức cực kỳ nhẹ đối với một ứng dụng Android đa phương tiện).
  * Bộ nhớ Java Heap: **6.2 MB**.
  * Bộ nhớ Native Heap: **2.6 MB**.
  * Bộ nhớ Graphics: **xấp xỉ 0 KB** (nhờ tối ưu hóa toàn bộ ảnh vẽ vector và nén ảnh bìa $560\times 420\text{px}$).
* **Kiểm tra độ mượt mà giao diện:**
  * Thao tác vuốt lướt trên Home Dashboard đạt tốc độ ổn định 60 khung hình/giây (FPS).
  * Hoạt ảnh lật thẻ 3D của bảng chữ cái và hoạt ảnh lật trang truyện tranh diễn ra trơn tru, không có hiện tượng giật cục (*jank*).

### 11.3. Bảng kiểm tra 10 kịch bản âm thanh và giọng nói bắt buộc

| STT | Kịch bản kiểm thử âm thanh | Hành vi quan sát được trên thiết bị / Logcat | Đánh giá |
|:---:|---|---|:---:|
| 1 | **Trả lời đúng** | Phát tiếng chuông `m2_sfx_correct`, sau đó giọng nữ cất lời khen ngợi tiếng Việt (ví dụ: *"Tuyệt vời! Bé làm đúng rồi!"*) | Đạt yêu cầu |
| 2 | **Trả lời sai** | Phát âm dịu `m2_sfx_gentle_try`, sau đó giọng đọc nhẹ nhàng động viên (ví dụ: *"Chưa đúng rồi, mình thử lại nhé!"*) | Đạt yêu cầu |
| 3 | **Hoàn thành bài học** | Phát chuông rộn rã `m2_sfx_celebrate`, giọng nói cất lên lời chúc mừng (ví dụ: *"Chúc mừng! Bé đã hoàn thành bài học!"*) | Đạt yêu cầu |
| 4 | **Tắt tiếng (Mute)** | Chạm nút loa trên Home Dashboard: BGM dừng ngay, chạm các nút không phát SFX, không phát giọng nói | Đạt yêu cầu |
| 5 | **Bật lại tiếng** | Chạm lại nút loa: Nhạc nền BGM fade-in êm ái trở lại, các âm thanh xúc giác và giọng đọc hoạt động bình thường | Đạt yêu cầu |
| 6 | **Bấm liên tục dồn dập** | Bé chạm liên tiếp vào màn hình: Bộ đệm cooldown 70ms hoạt động, âm thanh không bị phát chồng hay vỡ tiếng | Đạt yêu cầu |
| 7 | **Rời khỏi Activity** | Nhấn Home hoặc Back: Âm thanh BGM và giọng đọc dừng ngay trong `onPause`, không có âm thanh rò rỉ ngầm | Đạt yêu cầu |
| 8 | **Thiết bị thiếu giọng vi-VN** | Trình đọc bắt sự kiện `LANG_MISSING_DATA` an toàn, hiển thị Toast cảnh báo, ứng dụng không bị crash | Đạt yêu cầu |
| 9 | **Không hỏng tiếng Anh** | Trong Quiz tiếng Anh: Đọc từ vựng bằng tiếng Anh trước, xong mới khen tiếng Việt; tuyệt đối không chồng tiếng | Đạt yêu cầu |
| 10 | **Đồng bộ tiến độ Room DB** | Chơi xong bài: Điểm số và số sao cập nhật ngay vào Room Database, không làm sai lệch logic chấm điểm | Đạt yêu cầu |

### 11.4. Các lỗi kỹ thuật đã phát hiện và cách khắc phục trong quá trình phát triển
1. **Lỗi khởi tạo lặp TextToSpeech gây trễ 3.7 giây:**
   * *Nguyên nhân:* Việc tạo mới `TextToSpeech` trong phương thức `onCreate()` của từng Activity khiến hệ thống tốn 3.7 giây khởi tạo lại engine mỗi khi mở game.
   * *Khắc phục:* Chuyển đổi `Member2SpeechHelper` thành kiến trúc dùng chung ở cấp Application Context, thực hiện khởi động sớm (*warm-up*) ngay từ `Member2HomeActivity`, đồng thời bổ sung hàng đợi đệm (*Pending Utterance Queue*) cho phép bé chạm phát âm ngay cả khi engine đang khởi động.
2. **Lỗi chồng tiếng giữa Tiếng Anh và Lời khen Tiếng Việt:**
   * *Nguyên nhân:* Tiếng Anh phát âm qua Locale `en-US` và lời khen qua Locale `vi-VN` nếu gọi tuần tự thông thường trong code Java sẽ bị TextToSpeech ngắt nhau (*QUEUE_FLUSH*) hoặc đọc từ tiếng Anh bằng giọng phát âm tiếng Việt.
   * *Khắc phục:* Thiết kế lớp `Member2VoiceFeedback` với hàng đợi bước phát âm (*ArrayDeque<Step>*), lắng nghe chính xác sự kiện `onSpeechDone` của câu thứ nhất rồi mới nạp câu thứ hai kèm Watchdog Timer 2.5 giây bảo vệ chống kẹt.
3. **Lỗi tràn bộ nhớ Android Studio do máy ảo nhúng Tool Window:**
   * *Nguyên nhân:* Tính năng *"Launch in a tool window"* buộc Android Studio phải giải mã luồng video độ phân giải cao của máy ảo trực tiếp trong tiến trình IDE, kết hợp cùng card đồ họa tích hợp Intel UHD gây tràn bộ nhớ Heap của Android Studio.
   * *Khắc phục:* Thiết lập máy ảo chạy ở cửa sổ độc lập (*Standalone Window*), nâng mức Maximum Heap Size của IDE lên 4096 MB và dừng các tiến trình Gradle daemon chạy ngầm bằng `.\gradlew.bat --stop`.

### 11.5. Những hạn chế kỹ thuật còn tồn tại và đề xuất mở rộng
* **Phụ thuộc vào Engine TTS của thiết bị:** Mặc dù mã nguồn xử lý an toàn mọi ngoại lệ, chất lượng ngữ điệu của giọng đọc tiếng Việt vẫn phụ thuộc vào chất lượng gói dữ liệu TextToSpeech do nhà sản xuất hoặc người dùng cài đặt trên thiết bị (khuyến nghị thiết bị cài đặt Google Speech Services để có chất lượng tự nhiên nhất).
* **Đề xuất nâng cấp tương lai:** Có thể thu âm trực tiếp các đoạn audio giọng đọc của giáo viên mầm non thực thụ đóng gói thành các tệp âm thanh nén chuẩn (AAC/OGG) lưu sẵn trong `res/raw/` để chất lượng âm điệu ấm áp và tự nhiên tuyệt đối 100% trên mọi dòng máy mà không cần phụ thuộc vào động cơ TextToSpeech của bên thứ ba.

---

## 12. KẾT QUẢ THỰC HIỆN & HƯỚNG DẪN SỬ DỤNG

### 12.1. Tổng kết khối lượng công việc hoàn thành
Thành viên 2 đã hoàn thành toàn diện và vượt tiến độ các hạng mục được giao trong phân hệ:
* Xây dựng trọn vẹn màn hình trung tâm **Home Dashboard** đạt chuẩn mỹ thuật cao, giàu tính tương tác và tràn đầy cảm hứng học tập với chủ đề "Cuốn Sách Kỳ Diệu" và linh vật Sao Bông.
* Hoàn thành xuất sắc **4 Mini-game Phân hệ Ngôn ngữ**:
  1. *Chữ Cái (Alphabet):* 29 chữ cái, lật thẻ 3D, thử thách nghe chọn chữ.
  2. *Ghép Vần (Spelling):* 16 bài ghép vần tiếng Việt, hỗ trợ kéo thả và chạm nhanh.
  3. *Tiếng Anh (English):* 4 chủ đề x 6 từ vựng, flashcard song ngữ, luyện nghe và 20 câu hỏi quiz tương tác.
  4. *Đọc Truyện (Storybook):* 3 truyện ngụ ngôn ý nghĩa, lật trang sách chân thực, đọc truyện bôi màu chữ và đúc kết bài học đạo đức.
* Xây dựng hệ sinh thái âm thanh hoàn chỉnh: Nhạc nền thư giãn nhẹ nhàng 72 BPM phong cách Music Box & Piano, bộ 5 hiệu ứng xúc giác, hệ thống giọng nói khích lệ tiếng Việt và cơ chế tự động hạ nhạc nền (Audio Ducking) chuyên nghiệp.
* Đảm bảo tính độc lập module tuyệt đối: 100% mã nguồn nằm gọn trong phân hệ Thành viên 2, bảo vệ nguyên vẹn các tệp tin dùng chung của nhóm.

### 12.2. Điểm nổi bật về trải nghiệm người dùng
1. **Tính sư phạm tích cực:** Không trừng phạt khi làm sai, luôn khích lệ bằng giọng nói dịu dàng, giúp trẻ xây dựng sự tự tin và lòng ham học hỏi.
2. **Hình ảnh sinh động, tải nhanh:** Khai thác tối đa đồ họa vector sắc nét và tối ưu kích thước ảnh bìa sách, giúp ứng dụng chạy cực kỳ nhẹ nhàng, tiêu tốn chưa đến 28 MB RAM.
3. **Thao tác công thái học:** Hỗ trợ tương tác kép (kéo thả hoặc chạm), các nút bấm lớn, màu sắc tương phản rõ ràng, hoàn toàn phù hợp với tầm với và kỹ năng vận động của trẻ nhỏ.

### 12.3. Hướng dẫn sử dụng và kiểm thử dành cho Người chấm điểm / Trưởng nhóm

#### Bước 1: Mở màn hình Home Dashboard
* Khởi chạy ứng dụng `Happy Kid Learn` từ Android Studio hoặc Android Emulator.
* Nếu ứng dụng đang đặt `Member2HomeActivity` làm Launcher Activity trong `AndroidManifest.xml`, ứng dụng sẽ mở ngay vào Home Dashboard Cuốn Sách Kỳ Diệu.

#### Bước 2: Trải nghiệm Trang chủ Home Dashboard
* Quan sát linh vật Sao Bông ở góc trên đang nhịp thở nhẹ nhàng. Chạm vào Sao Bông để nghe âm thanh reo vui.
* Chạm vào các chip danh mục (Ngôn ngữ, Toán học, Logic, Sáng tạo, Tất cả) để trải nghiệm bộ lọc danh mục động.
* Chạm vào nút loa góc trên bên trái để thử nghiệm tính năng tắt/bật âm thanh.

#### Bước 3: Kiểm thử 4 Mini-game Phân hệ Ngôn ngữ
1. **Chữ Cái:**
   * Chạm vào thẻ sách *"Chữ Cái"* trên lưới trò chơi hoặc bấm nút *"Khám phá ngay!"*.
   * Vuốt ngang để lật qua các chữ cái, chạm vào thẻ chữ để xem hoạt ảnh lật 3D ra mặt sau.
   * Chuyển sang tab *"Thử Thách"* để chơi phần Nghe chọn chữ: chạm đáp án đúng để nghe lời khen ngợi bằng giọng nói tiếng Việt.
2. **Ghép Vần:**
   * Mở trò chơi *"Ghép Vần"*.
   * Thử nghiệm kéo thả thẻ chữ cái từ khay dưới vào ô slot, hoặc chạm trực tiếp vào thẻ chữ cái để thẻ tự động nhảy vào ô trống.
   * Nhấn nút *"Kiểm Tra"*: lắng nghe giọng đọc phát âm từ vựng tiếng Việt hoàn chỉnh trước, sau đó phát lời khen tiếng Việt.
3. **Tiếng Anh:**
   * Mở trò chơi *"Tiếng Anh"*.
   * Chuyển qua lại giữa các chủ đề: Động vật, Màu sắc, Chữ số, Gia đình.
   * Chuyển sang chế độ *"Luyện Nghe"* hoặc *"Trắc Nghiệm"* để trả lời các câu hỏi: quan sát hệ thống phát âm tiếng Anh từ vựng chuẩn xác và cất lời khen tiếng Việt khi trả lời đúng.
4. **Đọc Truyện:**
   * Mở trò chơi *"Đọc Truyện"*.
   * Chọn một câu chuyện (ví dụ: *"Rùa và Thỏ"*).
   * Bấm nút *"Đọc Nghe"* để trải nghiệm tính năng kể chuyện tự động: quan sát từng câu văn được tự động bôi màu vàng nghệ đồng bộ theo giọng đọc.
   * Lật đến trang cuối cùng và bấm nút *"Hoàn Thành Câu Chuyện"* để xem bài học đạo đức và nghe lời chúc mừng.

---
