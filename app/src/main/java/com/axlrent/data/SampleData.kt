package com.axlrent.data

import com.axlrent.ui.theme.*

/**
 * Sample data models and mock data for the Axl Rent app.
 */

import com.google.firebase.firestore.Exclude

data class Camera(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val description: String = "",
    val specs: String = "",
    val pricePerDay: Long = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val category: String = "",
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val sensor: String = "",
    val resolution: String = "",
    val lens: String = "",
    val fullDescription: String = "",
    val imageUrl: String = "",
    val stock: Int = 5,
    @get:Exclude val imageRes: Int? = null
) {
    @get:Exclude
    val localImageRes: Int?
        get() = when (id) {
            "CAM001" -> com.axlrent.R.drawable.canon_eos_3000d
            "CAM002" -> com.axlrent.R.drawable.nikon_d3500
            "CAM004" -> com.axlrent.R.drawable.sony_alpha_a6000
            "CAM005" -> com.axlrent.R.drawable.fuji_x_a3
            "CAM006" -> com.axlrent.R.drawable.canonn_eos_m50_mark_ll
            "CAM007" -> com.axlrent.R.drawable.gopro_hero_9_black
            "CAM008" -> com.axlrent.R.drawable.dji_osmo_action_3
            "CAM009" -> com.axlrent.R.drawable.insta360_one_x2
            "CAM010" -> com.axlrent.R.drawable.canon_ef_50mm_f18_stm
            "CAM011" -> com.axlrent.R.drawable.sony_fe_50mmf1_8
            "CAM012" -> com.axlrent.R.drawable.sigma_30mm_f14_dc_dn
            "CAM013" -> com.axlrent.R.drawable.godox_sl60w
            "CAM014" -> com.axlrent.R.drawable.ring_light_18_inch_led
            "CAM015" -> com.axlrent.R.drawable.godox_tt685_ii_flash
            "CAM016" -> com.axlrent.R.drawable.takara_eco_196a
            "CAM017" -> com.axlrent.R.drawable.somita_st_3520
            "CAM018" -> com.axlrent.R.drawable.manfrotto_compact_action
            else -> null
        }
}

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val ktpUrl: String = "",
    val role: String = "User",
    val avatarUrl: String = "",
    val bio: String = ""
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val camera: Camera = Camera(),
    val startDate: String = "",
    val endDate: String = "",
    val status: OrderStatus = OrderStatus.WAITING,
    val totalPrice: Long = 0
)

enum class OrderStatus {
    WAITING, ACTIVE, LATE, COMPLETED, CANCELLED
}

data class Review(
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: String = ""
)

object SampleData {

    val categories = listOf("DSLR", "Mirrorless", "Action Cam", "Lensa", "Lighting", "Tripod")

    val cameras = listOf(
        // ===== DSLR =====
        Camera(
            id = "CAM001",
            name = "Canon EOS 3000D",
            brand = "CANON",
            description = "18MP APS-C, cocok untuk pemula",
            specs = "18MP, Full HD, 3fps",
            pricePerDay = 75000,
            rating = 4.5f,
            reviewCount = 156,
            category = "DSLR",
            sensor = "18MP APS-C CMOS",
            resolution = "Full HD 1080p",
            lens = "EF-S Mount",
            fullDescription = "Kamera DSLR entry-level Canon yang sempurna untuk belajar fotografi. Dilengkapi sensor APS-C 18MP dan panduan fitur built-in untuk memudahkan pemula.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.canon_eos_3000d
        ),
        Camera(
            id = "CAM002",
            name = "Nikon D3500",
            brand = "NIKON",
            description = "24.2MP, Guide Mode, Ringan",
            specs = "24.2MP, Full HD, 5fps",
            pricePerDay = 85000,
            rating = 4.6f,
            reviewCount = 132,
            category = "DSLR",
            sensor = "24.2MP APS-C CMOS",
            resolution = "Full HD 1080/60p",
            lens = "F-Mount DX",
            fullDescription = "Nikon D3500 menawarkan kualitas gambar luar biasa dengan bobot hanya 365 gram. Guide Mode membantu pemula memahami pengaturan kamera dengan mudah.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.nikon_d3500
        ),

        // ===== MIRRORLESS =====
        Camera(
            id = "CAM004",
            name = "Sony Alpha A6000",
            brand = "SONY",
            description = "24.3MP, Fast AF, Compact",
            specs = "24.3MP, Full HD, 11fps",
            pricePerDay = 120000,
            rating = 4.7f,
            reviewCount = 245,
            category = "Mirrorless",
            sensor = "24.3MP APS-C CMOS",
            resolution = "Full HD 1080/60p",
            lens = "E-Mount",
            fullDescription = "Sony a6000 adalah mirrorless legendaris yang masih menjadi favorit berkat autofokus 179 titik yang super cepat dan ukuran bodi yang ringkas.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.sony_alpha_a6000
        ),
        Camera(
            id = "CAM005",
            name = "Fujifilm X-A3",
            brand = "FUJIFILM",
            description = "24.2MP, Tilting LCD, Retro",
            specs = "24.2MP, Full HD, Film Sim",
            pricePerDay = 110000,
            rating = 4.5f,
            reviewCount = 178,
            category = "Mirrorless",
            isFavorite = true,
            sensor = "24.2MP APS-C CMOS",
            resolution = "Full HD 1080p",
            lens = "X-Mount",
            fullDescription = "Fujifilm X-A3 mengusung desain retro yang cantik dengan layar sentuh flip 180°. Film simulation Fujifilm menghasilkan warna yang khas dan memikat.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.fuji_x_a3
        ),
        Camera(
            id = "CAM006",
            name = "Canon EOS M50 Mark II",
            brand = "CANON",
            description = "24.1MP, 4K, Eye Detection AF",
            specs = "24.1MP, 4K, DPAF",
            pricePerDay = 130000,
            rating = 4.8f,
            reviewCount = 189,
            category = "Mirrorless",
            sensor = "24.1MP APS-C CMOS",
            resolution = "4K 24fps",
            lens = "EF-M Mount",
            fullDescription = "Canon EOS M50 Mark II adalah pilihan terbaik untuk kreator konten berkat kemampuan live streaming, video 4K, dan Eye Detection AF yang presisi.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.canonn_eos_m50_mark_ll
        ),

        // ===== ACTION CAM =====
        Camera(
            id = "CAM007",
            name = "GoPro Hero 9 Black",
            brand = "GOPRO",
            description = "5K30, HyperSmooth 3.0",
            specs = "5K30, 20MP, Waterproof 10m",
            pricePerDay = 90000,
            rating = 4.6f,
            reviewCount = 312,
            category = "Action Cam",
            sensor = "23.6MP CMOS",
            resolution = "5K/30fps",
            lens = "Ultra Wide",
            fullDescription = "GoPro HERO 9 dilengkapi layar depan untuk selfie/vlogging, perekaman 5K, dan stabilisasi HyperSmooth 3.0 untuk hasil video super halus.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.gopro_hero_9_black
        ),
        Camera(
            id = "CAM008",
            name = "DJI Osmo Action 3",
            brand = "DJI",
            description = "4K/120fps, Dual Touchscreen",
            specs = "4K/120fps, 12MP, Waterproof 16m",
            pricePerDay = 80000,
            rating = 4.5f,
            reviewCount = 145,
            category = "Action Cam",
            sensor = "1/1.7\" CMOS",
            resolution = "4K/120fps",
            lens = "Ultra Wide 155°",
            fullDescription = "DJI Osmo Action 3 menawarkan stabilisasi RockSteady 3.0, perekaman 4K/120fps, dan daya tahan baterai lebih lama dengan magnetic quick mount.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.dji_osmo_action_3
        ),
        Camera(
            id = "CAM009",
            name = "Insta360 One X2",
            brand = "INSTA360",
            description = "5.7K 360°, FlowState Stabilization",
            specs = "5.7K 360°, Waterproof 10m",
            pricePerDay = 100000,
            rating = 4.7f,
            reviewCount = 87,
            category = "Action Cam",
            sensor = "1/2.3\" Dual Sensor",
            resolution = "5.7K/30fps 360°",
            lens = "Dual Fisheye",
            fullDescription = "Insta360 ONE X2 merekam video 360° 5.7K yang imersif dengan stabilisasi FlowState. Cocok untuk konten travel, olahraga, dan real estate virtual.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.insta360_one_x2
        ),

        // ===== LENSA =====
        Camera(
            id = "CAM010",
            name = "Canon EF 50mm f/1.8 STM",
            brand = "CANON",
            description = "Nifty Fifty, Bokeh Cantik",
            specs = "50mm, f/1.8, STM Motor",
            pricePerDay = 40000,
            rating = 4.8f,
            reviewCount = 298,
            category = "Lensa",
            sensor = "-",
            resolution = "-",
            lens = "Canon EF Mount",
            fullDescription = "Lensa 50mm f/1.8 STM Canon, dikenal sebagai 'nifty fifty'. Lensa wajib untuk portrait dengan bokeh lembut dan harga sangat terjangkau.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.canon_ef_50mm_f18_stm
        ),
        Camera(
            id = "CAM011",
            name = "Sony FE 50mm f/1.8",
            brand = "SONY",
            description = "Full-Frame, Ringan & Tajam",
            specs = "50mm, f/1.8, Full-Frame",
            pricePerDay = 50000,
            rating = 4.6f,
            reviewCount = 165,
            category = "Lensa",
            sensor = "-",
            resolution = "-",
            lens = "Sony E-Mount (FE)",
            fullDescription = "Sony FE 50mm f/1.8 adalah lensa prime full-frame yang ringan dan tajam. Pilihan ekonomis untuk pemotretan portrait and daily photography.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.sony_fe_50mmf1_8
        ),
        Camera(
            id = "CAM012",
            name = "Sigma 30mm f/1.4 DC DN",
            brand = "SIGMA",
            description = "APS-C, Art Line, Super Tajam",
            specs = "30mm, f/1.4, Contemporary",
            pricePerDay = 55000,
            rating = 4.9f,
            reviewCount = 210,
            category = "Lensa",
            sensor = "-",
            resolution = "-",
            lens = "Multi-mount (E/X/L)",
            fullDescription = "Sigma 30mm f/1.4 DC DN adalah lensa APS-C tercepat di kelasnya. Ideal untuk street photography dan portrait dengan depth of field yang sangat dangkal.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.sigma_30mm_f14_dc_dn
        ),

        // ===== LIGHTING =====
        Camera(
            id = "CAM013",
            name = "Godox SL60W",
            brand = "GODOX",
            description = "LED Video Light, 60W, 5600K",
            specs = "60W, 5600K, CRI 95+",
            pricePerDay = 60000,
            rating = 4.7f,
            reviewCount = 134,
            category = "Lighting",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Godox SL60W adalah LED video light daylight dengan output stabil 60W. Mount Bowens memungkinkan penggunaan berbagai modifier seperti softbox dan umbrella.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.godox_sl60w
        ),
        Camera(
            id = "CAM014",
            name = "Ring Light 18 Inch LED",
            brand = "GENERIC",
            description = "Bi-Color, Dimmable, + Stand",
            specs = "18 inch, 3200K-5600K, 55W",
            pricePerDay = 50000,
            rating = 4.4f,
            reviewCount = 278,
            category = "Lighting",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Ring light 18 inch LED bi-color dengan dimmer. Termasuk light stand dan phone holder. Wajib punya untuk makeup artist, beauty vlogger, dan live streamer.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.ring_light_18_inch_led
        ),
        Camera(
            id = "CAM015",
            name = "Godox TT685 II Flash",
            brand = "GODOX",
            description = "TTL Speedlite, HSS, 2.4G",
            specs = "GN60, HSS 1/8000s, TTL",
            pricePerDay = 45000,
            rating = 4.6f,
            reviewCount = 98,
            category = "Lighting",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Godox TT685 II adalah speedlite TTL yang powerful dengan GN60, High-Speed Sync, dan sistem wireless 2.4G X. Cocok untuk wedding, event, dan portrait.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.godox_tt685_ii_flash
        ),

        // ===== TRIPOD =====
        Camera(
            id = "CAM016",
            name = "Takara ECO-196A",
            brand = "TAKARA",
            description = "Aluminium, 1.4m, Fluid Head",
            specs = "Max 1.4m, Load 3kg, 580g",
            pricePerDay = 25000,
            rating = 4.3f,
            reviewCount = 456,
            category = "Tripod",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Takara ECO-196A adalah tripod aluminium entry-level yang ringan dan portable. Cocok untuk kamera mirrorless, DSLR ringan, dan smartphone.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.takara_eco_196a
        ),
        Camera(
            id = "CAM017",
            name = "Somita ST-3520",
            brand = "SOMITA",
            description = "Professional Tripod, 170cm",
            specs = "Max 1.7m, Load 5kg, Pan Head",
            pricePerDay = 20000,
            rating = 4.5f,
            reviewCount = 189,
            category = "Tripod",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Somita ST-3520 menawarkan stabilitas luar biasa dengan tinggi maksimal 170cm dan kapasitas beban hingga 5kg. Dilengkapi pan head untuk gerakan halus.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.somita_st_3520
        ),
        Camera(
            id = "CAM018",
            name = "Manfrotto Compact Action",
            brand = "MANFROTTO",
            description = "5-Section, Hybrid Head, Premium",
            specs = "Max 1.55m, Load 1.5kg, 1.2kg",
            pricePerDay = 30000,
            rating = 4.7f,
            reviewCount = 123,
            category = "Tripod",
            sensor = "-",
            resolution = "-",
            lens = "-",
            fullDescription = "Manfrotto Compact Action adalah tripod premium Italia dengan hybrid head untuk foto dan video. Build quality luar biasa and super kompak saat dilipat.",
            imageUrl = "",
            imageRes = com.axlrent.R.drawable.manfrotto_compact_action
        )
    )

    val orders = listOf(
        Order(
            id = "AXL-9844",
            camera = cameras[4], // Canon EOS M50 Mark II
            startDate = "Nov 02",
            endDate = "Nov 05, 2023",
            status = OrderStatus.WAITING,
            totalPrice = 450000
        ),
        Order(
            id = "AXL-9821",
            camera = cameras[2], // Sony Alpha A6000
            startDate = "Oct 24",
            endDate = "Oct 27, 2023",
            status = OrderStatus.ACTIVE,
            totalPrice = 390000
        ),
        Order(
            id = "AXL-9799",
            camera = cameras[1], // Fujifilm X-T30 II
            startDate = "Oct 10",
            endDate = "Oct 13, 2023",
            status = OrderStatus.LATE,
            totalPrice = 540000
        ),
        Order(
            id = "AXL-9710",
            camera = cameras[0], // Canon EOS 3000D
            startDate = "Sep 12",
            endDate = "Sep 15, 2023",
            status = OrderStatus.COMPLETED,
            totalPrice = 255000
        ),
        Order(
            id = "AXL-9610",
            camera = cameras[3], // GoPro Hero 10
            startDate = "Aug 05",
            endDate = "Aug 08, 2023",
            status = OrderStatus.CANCELLED,
            totalPrice = 360000
        )
    )

    val reviews = listOf(
        Review(
            userName = "Dimas Pratama",
            rating = 5.0f,
            comment = "Kondisi kamera sangat prima, sensor bersih, dan kelengkapan baterai sangat memuaskan.",
            date = "2 days ago"
        ),
        Review(
            userName = "Sarah Wijaya",
            rating = 4.5f,
            comment = "Kamera bagus dan berfungsi sempurna. Pengiriman cepat dan pelayanan ramah.",
            date = "1 week ago"
        ),
        Review(
            userName = "Andi Saputra",
            rating = 5.0f,
            comment = "Sudah 3 kali sewa di Axl Rent, selalu puas dengan kondisi gear-nya!",
            date = "2 weeks ago"
        )
    )
}
