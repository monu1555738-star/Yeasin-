package com.example.model

data class Country(
    val code: String,        // e.g. "BD"
    val name: String,        // e.g. "Bangladesh"
    val dialCode: String,    // e.g. "+880"
    val flag: String,        // e.g. "🇧🇩"
    val minLength: Int = 8,
    val maxLength: Int = 11,
    val sampleFormat: String = "1712-345678"
) {
    fun isValid(number: String): Boolean {
        val digitsOnly = number.filter { it.isDigit() }
        return digitsOnly.length in minLength..maxLength
    }
}

object CountryData {
    val countries: List<Country> = listOf(
        Country("BD", "Bangladesh", "+880", "🇧🇩", 10, 10, "1712-345678"),
        Country("IN", "India", "+91", "🇮🇳", 10, 10, "98765-43210"),
        Country("AE", "United Arab Emirates", "+971", "🇦🇪", 9, 9, "50-1234567"),
        Country("SA", "Saudi Arabia", "+966", "🇸🇦", 9, 9, "50-1234567"),
        Country("QA", "Qatar", "+974", "🇶🇦", 8, 8, "3312-3456"),
        Country("KW", "Kuwait", "+965", "🇰🇼", 8, 8, "9912-3456"),
        Country("OM", "Oman", "+968", "🇴🇲", 8, 8, "9123-4567"),
        Country("PK", "Pakistan", "+92", "🇵🇰", 10, 10, "300-1234567"),
        Country("GB", "United Kingdom", "+44", "🇬🇧", 10, 11, "7911-123456"),
        Country("US", "United States", "+1", "🇺🇸", 10, 10, "202-555-0143"),
        Country("CA", "Canada", "+1", "🇨🇦", 10, 10, "416-555-0199"),
        Country("AU", "Australia", "+61", "🇦🇺", 9, 9, "412-345-678"),
        Country("DE", "Germany", "+49", "🇩🇪", 10, 11, "151-2345678"),
        Country("FR", "France", "+33", "🇫🇷", 9, 9, "6-12345678"),
        Country("JP", "Japan", "+81", "🇯🇵", 10, 10, "90-1234-5678"),
        Country("KR", "South Korea", "+82", "🇰🇷", 9, 10, "10-1234-5678"),
        Country("SG", "Singapore", "+65", "🇸🇬", 8, 8, "8123-4567"),
        Country("MY", "Malaysia", "+60", "🇲🇾", 9, 10, "12-345-6789"),
        Country("ID", "Indonesia", "+62", "🇮🇩", 9, 12, "812-3456-7890"),
        Country("EG", "Egypt", "+20", "🇪🇬", 10, 10, "10-1234-5678"),
        Country("TR", "Turkey", "+90", "🇹🇷", 10, 10, "532-123-4567"),
        Country("BR", "Brazil", "+55", "🇧🇷", 10, 11, "11-98765-4321"),
        Country("ZA", "South Africa", "+27", "🇿🇦", 9, 9, "82-123-4567"),
        Country("NG", "Nigeria", "+234", "🇳🇬", 10, 10, "802-123-4567"),
        Country("BH", "Bahrain", "+973", "🇧🇭", 8, 8, "3612-3456"),
        Country("JO", "Jordan", "+962", "🇯🇴", 9, 9, "79-123-4567"),
        Country("LB", "Lebanon", "+961", "🇱🇧", 7, 8, "70-123456"),
        Country("IT", "Italy", "+39", "🇮🇹", 9, 10, "333-1234567"),
        Country("ES", "Spain", "+34", "🇪🇸", 9, 9, "612-345678"),
        Country("NL", "Netherlands", "+31", "🇳🇱", 9, 9, "6-12345678"),
        Country("SE", "Sweden", "+46", "🇸🇪", 9, 9, "70-1234567"),
        Country("CH", "Switzerland", "+41", "🇨🇭", 9, 9, "78-123-4567"),
        Country("NZ", "New Zealand", "+64", "🇳🇿", 8, 10, "21-123-4567"),
        Country("PH", "Philippines", "+63", "🇵🇭", 10, 10, "917-123-4567"),
        Country("TH", "Thailand", "+66", "🇹🇭", 9, 9, "81-234-5678"),
        Country("VN", "Vietnam", "+84", "🇻🇳", 9, 10, "91-234-5678")
    )

    val defaultCountry = countries[0] // Bangladesh +880 default as requested in prompt examples
}
