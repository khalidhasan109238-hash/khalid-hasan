package com.example.data.repository

data class QuizCategoryInfo(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val badgeEmoji: String
)

data class QuizQuestion(
    val id: String,
    val categoryId: String,
    val questionEn: String,
    val questionBn: String,
    val optionsEn: List<String>,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationBn: String
)

object QuizQuestionBank {
    val categories = listOf(
        QuizCategoryInfo(
            id = "BD_WORLD",
            titleEn = "Bangladesh & World",
            titleBn = "বাংলাদেশ ও বিশ্ব",
            subtitleEn = "History, geography, rivers & global heritage",
            subtitleBn = "ইতিহাস, ভূগোল, নদী ও বিশ্ব ঐতিহ্য",
            badgeEmoji = "🌍"
        ),
        QuizCategoryInfo(
            id = "SCIENCE_TECH",
            titleEn = "Science & Tech",
            titleBn = "বিজ্ঞান ও প্রযুক্তি",
            subtitleEn = "Physics, space, biology & modern inventions",
            subtitleBn = "পদার্থবিজ্ঞান, মহাকাশ, জীববিজ্ঞান ও উদ্ভাবন",
            badgeEmoji = "🚀"
        ),
        QuizCategoryInfo(
            id = "PROGRAMMING",
            titleEn = "Programming & CS",
            titleBn = "প্রোগ্রামিং ও কোডিং",
            subtitleEn = "Kotlin, Android, algorithms & data structures",
            subtitleBn = "কটলিন, অ্যান্ড্রয়েড, অ্যালগরিদম ও ডাটা স্ট্রাকচার",
            badgeEmoji = "💻"
        ),
        QuizCategoryInfo(
            id = "GENERAL",
            titleEn = "General IQ & Trivia",
            titleBn = "সাধারণ জ্ঞান ও আইকিউ",
            subtitleEn = "Literature, sports, logic puzzles & records",
            subtitleBn = "সাহিত্য, খেলাধুলা, বুদ্ধিমত্তা ও বিশ্বরেকর্ড",
            badgeEmoji = "🧠"
        )
    )

    val allQuestions = listOf(
        // BANGLADESH & WORLD
        QuizQuestion(
            id = "bw_1",
            categoryId = "BD_WORLD",
            questionEn = "Which is the largest mangrove forest in the world?",
            questionBn = "পৃথিবীর বৃহত্তম ম্যানগ্রোভ বন কোনটি?",
            optionsEn = listOf("Amazon Rainforest", "The Sundarbans", "Congo Basin", "Daintree Forest"),
            optionsBn = listOf("আমাজন বন", "সুন্দরবন", "কঙ্গো বেসিন", "ডেইনট্রি বন"),
            correctIndex = 1,
            explanationEn = "The Sundarbans, spanning Bangladesh and India along the Bay of Bengal delta, is the largest contiguous mangrove forest in the world.",
            explanationBn = "বঙ্গোপসাগরের উপকূলে অবস্থিত সুন্দরবন পৃথিবীর একক বৃহত্তম ম্যানগ্রোভ বা শ্বাসমূলীয় বন।"
        ),
        QuizQuestion(
            id = "bw_2",
            categoryId = "BD_WORLD",
            questionEn = "In which year did UNESCO declare 21st February as International Mother Language Day?",
            questionBn = "ইউনেস্কো কত সালে ২১শে ফেব্রুয়ারিকে আন্তর্জাতিক মাতৃভাষা দিবস হিসেবে ঘোষণা করে?",
            optionsEn = listOf("1997", "1999", "2000", "2002"),
            optionsBn = listOf("১৯৯৭ সালে", "১৯৯৯ সালে", "২০০০ সালে", "২০০২ সালে"),
            correctIndex = 1,
            explanationEn = "UNESCO proclaimed 21 February as International Mother Language Day on 17 November 1999.",
            explanationBn = "১৯৯৯ সালের ১৭ নভেম্বর ইউনেস্কো ২১শে ফেব্রুয়ারিকে আন্তর্জাতিক মাতৃভাষা দিবস হিসেবে স্বীকৃতি দেয়।"
        ),
        QuizQuestion(
            id = "bw_3",
            categoryId = "BD_WORLD",
            questionEn = "What is the total length of the Padma Multipurpose Bridge in Bangladesh?",
            questionBn = "বাংলাদেশের পদ্মা বহুমুখী সেতুর মূল দৈর্ঘ্য কত?",
            optionsEn = listOf("4.80 km", "5.58 km", "6.15 km", "7.20 km"),
            optionsBn = listOf("৪.৮০ কিলোমিটার", "৫.৫৮ কিলোমিটার", "৬.১৫ কিলোমিটার", "৭.২০ কিলোমিটার"),
            correctIndex = 2,
            explanationEn = "The main structure of the Padma Bridge spans 6.15 kilometers across the Padma River with 41 spans.",
            explanationBn = "৪১টি স্প্যান বিশিষ্ট পদ্মা বহুমুখী সেতুর মূল দৈর্ঘ্য ৬.১৫ কিলোমিটার।"
        ),
        QuizQuestion(
            id = "bw_4",
            categoryId = "BD_WORLD",
            questionEn = "Which country is known as the 'Land of the Midnight Sun'?",
            questionBn = "কোন দেশকে 'নিশীথ সূর্যের দেশ' বলা হয়?",
            optionsEn = listOf("Japan", "Norway", "Iceland", "Switzerland"),
            optionsBn = listOf("জাপান", "নরওয়ে", "আইসল্যান্ড", "সুইজারল্যান্ড"),
            correctIndex = 1,
            explanationEn = "Northern Norway experiences continuous daylight during summer months above the Arctic Circle.",
            explanationBn = "উত্তর মেরু বৃত্তের কাছাকাছি হওয়ায় গ্রীষ্মকালে নরওয়েতে মধ্যরাতেও সূর্য দেখা যায়।"
        ),
        QuizQuestion(
            id = "bw_5",
            categoryId = "BD_WORLD",
            questionEn = "Which river enters Bangladesh through Kurigram district from India?",
            questionBn = "কোন নদীটি কুড়িগ্রাম জেলার মধ্য দিয়ে বাংলাদেশে প্রবেশ করেছে?",
            optionsEn = listOf("Padma", "Brahmaputra", "Meghna", "Surma"),
            optionsBn = listOf("পদ্মা", "ব্রহ্মপুত্র", "মেঘনা", "সুরমা"),
            correctIndex = 1,
            explanationEn = "The Brahmaputra River enters Bangladesh through Kurigram district before flowing south as the Jamuna.",
            explanationBn = "ব্রহ্মপুত্র নদ ভারতের আসাম হয়ে কুড়িগ্রাম জেলার মধ্য দিয়ে বাংলাদেশে প্রবেশ করেছে।"
        ),

        // SCIENCE & TECH
        QuizQuestion(
            id = "st_1",
            categoryId = "SCIENCE_TECH",
            questionEn = "Which planet in our Solar System has the highest surface temperature?",
            questionBn = "সৌরজগতের সবচেয়ে উত্তপ্ত গ্রহ কোনটি?",
            optionsEn = listOf("Mercury", "Venus", "Mars", "Jupiter"),
            optionsBn = listOf("বুধ (Mercury)", "শুক্র (Venus)", "মঙ্গল (Mars)", "বৃহস্পতি (Jupiter)"),
            correctIndex = 1,
            explanationEn = "Venus is the hottest planet due to its dense carbon dioxide atmosphere creating a runaway greenhouse effect (~465°C).",
            explanationBn = "ঘন কার্বন ডাই-অক্সাইড বায়ুমণ্ডলের গ্রিনহাউস প্রভাবের কারণে শুক্র গ্রহ সৌরজগতের সবচেয়ে উত্তপ্ত গ্রহ।"
        ),
        QuizQuestion(
            id = "st_2",
            categoryId = "SCIENCE_TECH",
            questionEn = "What is the powerhouse of the eukaryotic cell?",
            questionBn = "জীবকোষের 'পাওয়ার হাউস' বা শক্তিঘর বলা হয় কোনটিকে?",
            optionsEn = listOf("Ribosome", "Mitochondria", "Golgi Body", "Nucleus"),
            optionsBn = listOf("রাইবোজোম", "মাইটোকন্ড্রিয়া", "গলগি বডি", "নিউক্লিয়াস"),
            correctIndex = 1,
            explanationEn = "Mitochondria generate most of the cell's supply of adenosine triphosphate (ATP), used as a source of chemical energy.",
            explanationBn = "মাইটোকন্ড্রিয়া কোষের শ্বসন প্রক্রিয়ায় এটিপি (ATP) শক্তি উৎপাদন করে বলে একে কোষের শক্তিঘর বলা হয়।"
        ),
        QuizQuestion(
            id = "st_3",
            categoryId = "SCIENCE_TECH",
            questionEn = "Who demonstrated the wireless transmission of radio waves before Marconi and studied plant stimuli?",
            questionBn = "উদ্ভিদের প্রাণ ও বেতার তরঙ্গ গবেষণায় পথিকৃৎ বাঙালি বিজ্ঞানী কে?",
            optionsEn = listOf("Satyendra Nath Bose", "Jagadish Chandra Bose", "Meghnad Saha", "Prafulla Chandra Ray"),
            optionsBn = listOf("সত্যেন্দ্রনাথ বসু", "জগদীশচন্দ্র বসু", "মেঘনাদ সাহা", "প্রফুল্লচন্দ্র রায়"),
            correctIndex = 1,
            explanationEn = "Sir Jagadish Chandra Bose pioneered millimeter-wave radio research and invented the Crescograph to measure plant growth.",
            explanationBn = "স্যার জগদীশচন্দ্র বসু ক্রেসকোগ্রাফ যন্ত্রের সাহায্যে উদ্ভিদের সাড়া দেওয়ার ক্ষমতা প্রমাণ করেন এবং বেতার তরঙ্গের সফল পরীক্ষা চালান।"
        ),
        QuizQuestion(
            id = "st_4",
            categoryId = "SCIENCE_TECH",
            questionEn = "Approximately how long does light take to travel from the Sun to the Earth?",
            questionBn = "সূর্য থেকে পৃথিবীতে আলো পৌঁছাতে আনুমানিক কত সময় লাগে?",
            optionsEn = listOf("2 minutes 10 seconds", "8 minutes 20 seconds", "15 minutes", "1 hour"),
            optionsBn = listOf("২ মিনিট ১০ সেকেন্ড", "৮ মিনিট ২০ সেকেন্ড", "১৫ মিনিট", "১ ঘণ্টা"),
            correctIndex = 1,
            explanationEn = "Traveling at ~300,000 km/s across 149.6 million km, sunlight reaches Earth in about 8 minutes and 20 seconds.",
            explanationBn = "আলোর বেগ প্রতি সেকেন্ডে প্রায় ৩ লক্ষ কিলোমিটার হওয়ায় সূর্য থেকে পৃথিবীতে আলো আসতে প্রায় ৮ মিনিট ২০ সেকেন্ড সময় লাগে।"
        ),
        QuizQuestion(
            id = "st_5",
            categoryId = "SCIENCE_TECH",
            questionEn = "Which gas makes up roughly 78% of Earth's atmosphere?",
            questionBn = "পৃথিবীর বায়ুমণ্ডলের প্রায় ৭৮ শতাংশ কোন গ্যাস দ্বারা গঠিত?",
            optionsEn = listOf("Oxygen", "Carbon Dioxide", "Nitrogen", "Argon"),
            optionsBn = listOf("অক্সিজেন", "কার্বন ডাই-অক্সাইড", "নাইট্রোজেন", "আর্গন"),
            correctIndex = 2,
            explanationEn = "Earth's atmosphere consists of about 78% nitrogen, 21% oxygen, 0.9% argon, and trace gases.",
            explanationBn = "পৃথিবীর বায়ুমণ্ডলে প্রায় ৭৮.০৮% নাইট্রোজেন এবং ২০.৯৫% অক্সিজেন রয়েছে।"
        ),

        // PROGRAMMING & CS
        QuizQuestion(
            id = "pr_1",
            categoryId = "PROGRAMMING",
            questionEn = "In Kotlin, which keyword is used to declare an immutable (read-only) variable?",
            questionBn = "কটলিন (Kotlin) ভাষায় অপরিবর্তনীয় (read-only) ভ্যারিয়েবল ঘোষণা করতে কোন কি-ওয়ার্ড ব্যবহৃত হয়?",
            optionsEn = listOf("var", "val", "let", "final"),
            optionsBn = listOf("var", "val", "let", "final"),
            correctIndex = 1,
            explanationEn = "`val` declares a read-only reference in Kotlin, whereas `var` declares a mutable variable.",
            explanationBn = "কটলিনে `val` দিয়ে অপরিবর্তনীয় (immutable) রেফারেন্স এবং `var` দিয়ে পরিবর্তনশীল ভ্যারিয়েবল তৈরি করা হয়।"
        ),
        QuizQuestion(
            id = "pr_2",
            categoryId = "PROGRAMMING",
            questionEn = "What is the time complexity of binary search on a sorted array of N elements?",
            questionBn = "সাজানো অ্যারেতে বাইনারি সার্চ (Binary Search) অ্যালগরিদমের টাইম কমপ্লেক্সিটি কত?",
            optionsEn = listOf("O(N)", "O(log N)", "O(N log N)", "O(1)"),
            optionsBn = listOf("O(N)", "O(log N)", "O(N log N)", "O(1)"),
            correctIndex = 1,
            explanationEn = "Binary search halves the search space on each comparison, resulting in O(log N) logarithmic time complexity.",
            explanationBn = "বাইনারি সার্চে প্রতি ধাপে অনুসন্ধানের পরিসর অর্ধেক হয়ে যায়, তাই এর টাইম কমপ্লেক্সিটি O(log N)।"
        ),
        QuizQuestion(
            id = "pr_3",
            categoryId = "PROGRAMMING",
            questionEn = "In Jetpack Compose, which annotation marks a function that builds UI elements?",
            questionBn = "Jetpack Compose-এ UI তৈরি করার ফাংশনকে কোন অ্যানোটেশন দিয়ে চিহ্নিত করা হয়?",
            optionsEn = listOf("@UIView", "@Composable", "@Widget", "@Component"),
            optionsBn = listOf("@UIView", "@Composable", "@Widget", "@Component"),
            correctIndex = 1,
            explanationEn = "`@Composable` informs the Compose compiler that the function transforms data into a UI hierarchy.",
            explanationBn = "`@Composable` অ্যানোটেশন ব্যবহার করে Jetpack Compose-এ ডিক্লারেটিভ UI ফাংশন লেখা হয়।"
        ),
        QuizQuestion(
            id = "pr_4",
            categoryId = "PROGRAMMING",
            questionEn = "Which data structure operates on a LIFO (Last-In, First-Out) principle?",
            questionBn = "কোন ডাটা স্ট্রাকচারটি LIFO (Last-In, First-Out) নীতিতে কাজ করে?",
            optionsEn = listOf("Queue", "Stack", "Linked List", "Binary Tree"),
            optionsBn = listOf("কিউ (Queue)", "স্ট্যাক (Stack)", "লিংকড লিস্ট", "বাইনারি ট্রি"),
            correctIndex = 1,
            explanationEn = "A Stack pushes and pops elements from the top, so the last element added is the first one removed.",
            explanationBn = "স্ট্যাক (Stack)-এ সবশেষে রাখা উপাদানটি সবার আগে বের হয় (LIFO)।"
        ),
        QuizQuestion(
            id = "pr_5",
            categoryId = "PROGRAMMING",
            questionEn = "Which component in Room Database defines the SQL queries for reading and writing data?",
            questionBn = "Android Room ডাটাবেসে SQL কুয়েরিগুলো কোন অংশে লেখা হয়?",
            optionsEn = listOf("@Entity", "@Dao", "@Database", "@TypeConverter"),
            optionsBn = listOf("@Entity", "@Dao", "@Database", "@TypeConverter"),
            correctIndex = 1,
            explanationEn = "DAO (Data Access Object) annotated with `@Dao` contains methods that map directly to SQL queries.",
            explanationBn = "`@Dao` (Data Access Object) ইন্টারফেসের ভেতরে ডাটাবেসের কুয়েরি, ইনসার্ট ও ডিলিট মেথডগুলো সংজ্ঞায়িত করা হয়।"
        ),

        // GENERAL IQ & TRIVIA
        QuizQuestion(
            id = "gn_1",
            categoryId = "GENERAL",
            questionEn = "Who wrote the national anthem of Bangladesh, 'Amar Shonar Bangla'?",
            questionBn = "বাংলাদেশের জাতীয় সংগীত 'আমার সোনার বাংলা' কে রচনা করেছেন?",
            optionsEn = listOf("Kazi Nazrul Islam", "Rabindranath Tagore", "Jasimuddin", "Michael Madhusudan Dutt"),
            optionsBn = listOf("কাজী নজরুল ইসলাম", "রবীন্দ্রনাথ ঠাকুর", "জসীমউদ্দীন", "মাইকেল মধুসূদন দত্ত"),
            correctIndex = 1,
            explanationEn = "Rabindranath Tagore composed 'Amar Shonar Bangla' in 1905; the first 10 lines were adopted as Bangladesh's national anthem.",
            explanationBn = "১৯০৫ সালে বিশ্বকবি রবীন্দ্রনাথ ঠাকুর 'আমার সোনার বাংলা' গানটি রচনা করেন।"
        ),
        QuizQuestion(
            id = "gn_2",
            categoryId = "GENERAL",
            questionEn = "If a sequence goes 2, 6, 12, 20, 30, what is the next number?",
            questionBn = "২, ৬, ১২, ২০, ৩০ — এই ধারাটির পরবর্তী সংখ্যাটি কত?",
            optionsEn = listOf("38", "40", "42", "44"),
            optionsBn = listOf("৩৮", "৪০", "৪২", "৪৪"),
            correctIndex = 2,
            explanationEn = "The differences between consecutive terms are +4, +6, +8, +10, so the next difference is +12: 30 + 12 = 42.",
            explanationBn = "সংখ্যাগুলোর পার্থক্য যথাক্রমে ৪, ৬, ৮, ১০ করে বাড়ছে। তাই পরের সংখ্যাটি হবে ৩০ + ১২ = ৪২।"
        ),
        QuizQuestion(
            id = "gn_3",
            categoryId = "GENERAL",
            questionEn = "How many squares are there in total on a standard 8x8 chessboard (considering only 1x1 unit squares)?",
            questionBn = "একটি স্ট্যান্ডার্ড দাবা বোর্ডে মোট কয়টি ছোট (১x১) ঘর থাকে?",
            optionsEn = listOf("48", "64", "81", "100"),
            optionsBn = listOf("৪৮টি", "৬৪টি", "৮১টি", "১০০টি"),
            correctIndex = 1,
            explanationEn = "A standard chessboard is an 8 by 8 grid containing 64 alternating light and dark unit squares.",
            explanationBn = "দাবা বোর্ডে ৮টি সারি ও ৮টি কলামে মোট ৮×৮ = ৬৪টি সাদা-কালো ঘর থাকে।"
        ),
        QuizQuestion(
            id = "gn_4",
            categoryId = "GENERAL",
            questionEn = "Which ocean is the deepest and largest on Earth?",
            questionBn = "পৃথিবীর বৃহত্তম ও গভীরতম মহাসাগর কোনটি?",
            optionsEn = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
            optionsBn = listOf("আটলান্টিক মহাসাগর", "ভারত মহাসাগর", "প্রশান্ত মহাসাগর", "উত্তর মহাসাগর"),
            correctIndex = 2,
            explanationEn = "The Pacific Ocean covers more than 30% of Earth's surface and contains the Mariana Trench.",
            explanationBn = "প্রশান্ত মহাসাগর পৃথিবীর বৃহত্তম ও গভীরতম মহাসাগর, যেখানে মারিয়ানা ট্রেঞ্চ অবস্থিত।"
        ),
        QuizQuestion(
            id = "gn_5",
            categoryId = "GENERAL",
            questionEn = "Who is known as the 'Rebel Poet' (Bidrohi Kobi) of Bengali literature?",
            questionBn = "বাংলা সাহিত্যের 'বিদ্রোহী কবি' হিসেবে কে পরিচিত?",
            optionsEn = listOf("Jibanananda Das", "Kazi Nazrul Islam", "Sukanta Bhattacharya", "Shamsur Rahman"),
            optionsBn = listOf("জীবনানন্দ দাশ", "কাজী নজরুল ইসলাম", "সুকান্ত ভট্টাচার্য", "শামসুর রাহমান"),
            correctIndex = 1,
            explanationEn = "National Poet Kazi Nazrul Islam earned the title 'Bidrohi Kobi' following his iconic poem 'Bidrohi' published in 1922.",
            explanationBn = "জাতীয় কবি কাজী নজরুল ইসলাম তাঁর কালজয়ী 'বিদ্রোহী' কবিতার জন্য বিদ্রোহী কবি নামে পরিচিত।"
        )
    )

    fun getQuestionsForCategory(categoryId: String): List<QuizQuestion> {
        return if (categoryId == "ALL") {
            allQuestions.shuffled().take(8)
        } else {
            allQuestions.filter { it.categoryId == categoryId }
        }
    }
}
