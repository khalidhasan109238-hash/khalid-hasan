package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.AppLanguage
import com.example.data.local.SavedWeatherCity
import com.example.data.remote.GeocodingCityDto
import com.example.data.repository.DailyForecastItem
import com.example.data.repository.HourlyForecastItem
import com.example.data.repository.WeatherSnapshot
import com.example.ui.viewmodel.WeatherLoadState
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    weatherState: WeatherLoadState,
    savedCities: List<SavedWeatherCity>,
    useCelsius: Boolean,
    citySearchQuery: String,
    citySearchResults: List<GeocodingCityDto>,
    isSearchingCity: Boolean,
    language: AppLanguage,
    onToggleUnit: () -> Unit,
    onSelectSavedCity: (SavedWeatherCity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectSearchResult: (GeocodingCityDto) -> Unit,
    onUseDeviceLocation: (Double, Double, String, String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BENGALI
    val context = LocalContext.current
    var locationStatusHint by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val coords = readLastKnownLocation(context)
            if (coords != null) {
                locationStatusHint = null
                onUseDeviceLocation(coords.first, coords.second, "My Location", "আমার অবস্থান")
            } else {
                locationStatusHint = if (isBn) {
                    "ডিভাইসের জিপিএস সিগন্যাল পাওয়া যায়নি, ঢাকার আবহাওয়া দেখানো হচ্ছে।"
                } else {
                    "GPS coordinates unavailable on this device; showing selected city."
                }
            }
        } else {
            locationStatusHint = if (isBn) {
                "লোকেশন অনুমতি প্রদান করা হয়নি। নিচের শহর তালিকা থেকে নির্বাচন করুন।"
            } else {
                "Location permission denied. Select any city below."
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("weather_screen_container"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar + GPS & Unit Toggle Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = citySearchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("weather_city_search_input"),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        placeholder = {
                            Text(if (isBn) "যেকোনো শহর খুঁজুন (যেমন: Khulna, Paris)..." else "Search world cities (e.g. Sylhet, Tokyo)...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search_city_desc)
                            )
                        },
                        trailingIcon = {
                            if (citySearchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = if (isBn) "মুছুন" else "Clear"
                                    )
                                }
                            }
                        }
                    )

                    // GPS Button
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    val coords = readLastKnownLocation(context)
                                    if (coords != null) {
                                        locationStatusHint = null
                                        onUseDeviceLocation(
                                            coords.first,
                                            coords.second,
                                            "My Location",
                                            "আমার অবস্থান"
                                        )
                                    } else {
                                        locationStatusHint = if (isBn) {
                                            "জিপিএস সিগন্যাল সক্রিয় নেই; শহর তালিকা ব্যবহার করুন।"
                                        } else {
                                            "No active GPS fix found; use city selector."
                                        }
                                    }
                                } else {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }
                            .testTag("use_gps_button")
                    ) {
                        Box(
                            modifier = Modifier.padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = stringResource(id = R.string.use_location_desc),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // °C / °F Unit Toggle
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(onClick = onToggleUnit)
                            .testTag("toggle_temp_unit_button")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (useCelsius) "°C" else "°F",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                if (locationStatusHint != null) {
                    Text(
                        text = locationStatusHint!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (isSearchingCity) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                // Online Geocoding Search Results Dropdown Card
                if (citySearchResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            citySearchResults.forEach { result ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectSearchResult(result) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Column {
                                            Text(
                                                text = result.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            val sub = listOfNotNull(result.admin1, result.country)
                                                .joinToString(", ")
                                            if (sub.isNotBlank()) {
                                                Text(
                                                    text = sub,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${"%.1f".format(result.latitude)}°, ${"%.1f".format(result.longitude)}°",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Saved Cities Quick Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    savedCities.forEach { city ->
                        FilterChip(
                            selected = city.isSelected,
                            onClick = { onSelectSavedCity(city) },
                            label = {
                                Text(if (isBn) city.nameBn else city.nameEn)
                            },
                            leadingIcon = if (city.isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            modifier = Modifier.testTag("saved_city_chip_${city.nameEn}")
                        )
                    }
                }
            }
        }

        // Main Weather Display
        when (weatherState) {
            is WeatherLoadState.Loading -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = if (isBn) "লাইভ আবহাওয়া আপডেট লোড হচ্ছে..." else "Fetching live Open-Meteo forecast...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            is WeatherLoadState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isBn) weatherState.messageBn else weatherState.messageEn,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Button(
                                onClick = onRefresh,
                                modifier = Modifier.testTag("weather_retry_button")
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBn) "আবার চেষ্টা করুন" else "Retry")
                            }
                        }
                    }
                }
            }

            is WeatherLoadState.Success -> {
                val snap = weatherState.snapshot
                // Hero Weather Banner
                item {
                    WeatherHeroCard(
                        snapshot = snap,
                        useCelsius = useCelsius,
                        isBn = isBn,
                        onRefresh = onRefresh
                    )
                }

                // 4 Atmospheric Metrics Grid
                item {
                    WeatherMetricsGrid(
                        snapshot = snap,
                        isBn = isBn
                    )
                }

                // Hourly Forecast Section
                if (snap.hourlyItems.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = if (isBn) "ঘণ্টাভিত্তিক পূর্বাভাস (পরবর্তী ১২ ঘণ্টা)" else "Hourly Forecast (Next 12 Hours)",
                                style = MaterialTheme.typography.titleLarge
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(snap.hourlyItems) { hourItem ->
                                    HourlyForecastCard(
                                        item = hourItem,
                                        useCelsius = useCelsius
                                    )
                                }
                            }
                        }
                    }
                }

                // 7-Day Forecast Section
                if (snap.dailyItems.isNotEmpty()) {
                    item {
                        Text(
                            text = if (isBn) "৭ দিনের আবহাওয়ার পূর্বাভাস" else "7-Day Weather Outlook",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    items(snap.dailyItems) { dayItem ->
                        DailyForecastRowCard(
                            item = dayItem,
                            useCelsius = useCelsius,
                            isBn = isBn
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherHeroCard(
    snapshot: WeatherSnapshot,
    useCelsius: Boolean,
    isBn: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weather_hero_card"),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_weather),
                contentDescription = stringResource(id = R.string.hero_weather_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC0F172A),
                                Color(0xB31E3A8A),
                                Color(0xE60F172A)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8)
                        )
                        Column {
                            Text(
                                text = if (isBn) snapshot.cityNameBn else snapshot.cityNameEn,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = snapshot.country,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFBAE6FD)
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .background(Color(0x33FFFFFF), CircleShape)
                            .testTag("refresh_weather_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.refresh_weather_desc),
                            tint = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = formatTemp(snapshot.currentTempC, useCelsius),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) {
                                "অনুভূত হচ্ছে ${formatTemp(snapshot.feelsLikeTempC, useCelsius)}"
                            } else {
                                "Feels like ${formatTemp(snapshot.feelsLikeTempC, useCelsius)}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE0F2FE)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = weatherCodeToIcon(snapshot.weatherCode),
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = weatherCodeToLabel(snapshot.weatherCode, isBn),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricsGrid(
    snapshot: WeatherSnapshot,
    isBn: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                icon = Icons.Default.WaterDrop,
                label = if (isBn) "আর্দ্রতা (Humidity)" else "Humidity",
                value = "${snapshot.humidityPercent}%",
                accent = Color(0xFF0EA5E9),
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                icon = Icons.Default.Air,
                label = if (isBn) "বাতাসের বেগ (Wind)" else "Wind Speed",
                value = "${snapshot.windSpeedKmh} km/h",
                accent = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                icon = Icons.Default.WbSunny,
                label = if (isBn) "সর্বোচ্চ ইউভি (UV Index)" else "Max UV Index",
                value = "${snapshot.uvIndexMax}",
                accent = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                icon = Icons.Default.Speed,
                label = if (isBn) "বায়ুচাপ (Pressure)" else "Air Pressure",
                value = "${snapshot.pressureHpa} hPa",
                accent = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = accent.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HourlyForecastCard(
    item: HourlyForecastItem,
    useCelsius: Boolean
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .width(86.dp)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.hourLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = weatherCodeToIcon(item.weatherCode),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = formatTemp(item.temperatureC, useCelsius),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (item.rainProbability > 0) {
                Surface(
                    color = Color(0xFF0EA5E9).copy(alpha = 0.14f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "💧${item.rainProbability}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRowCard(
    item: DailyForecastItem,
    useCelsius: Boolean,
    isBn: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = item.dateString,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = weatherCodeToLabel(item.weatherCode, isBn),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = weatherCodeToIcon(item.weatherCode),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                if (item.rainProbability > 0) {
                    Text(
                        text = "💧${item.rainProbability}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF0284C7)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${formatTemp(item.maxTempC, useCelsius)} / ${formatTemp(item.minTempC, useCelsius)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatTemp(tempC: Double, useCelsius: Boolean): String {
    return if (useCelsius) {
        "${tempC.roundToInt()}°C"
    } else {
        val tempF = (tempC * 9.0 / 5.0) + 32.0
        "${tempF.roundToInt()}°F"
    }
}

private fun weatherCodeToIcon(code: Int): ImageVector {
    return when (code) {
        0, 1 -> Icons.Default.WbSunny
        2, 3, 45, 48 -> Icons.Default.Cloud
        in 51..67, in 80..82 -> Icons.Default.WaterDrop
        in 95..99 -> Icons.Default.Thunderstorm
        else -> Icons.Default.Cloud
    }
}

private fun weatherCodeToLabel(code: Int, isBn: Boolean): String {
    return when (code) {
        0 -> if (isBn) "পরিষ্কার আকাশ" else "Clear Sky"
        1, 2 -> if (isBn) "আংশিক মেঘলা" else "Partly Cloudy"
        3 -> if (isBn) "মেঘাচ্ছন্ন আকাশ" else "Overcast"
        45, 48 -> if (isBn) "কুয়াশাচ্ছন্ন" else "Misty / Foggy"
        in 51..57 -> if (isBn) "গুঁড়ি গুঁড়ি বৃষ্টি" else "Light Drizzle"
        in 61..67 -> if (isBn) "মাঝারি বৃষ্টিপাত" else "Rain Showers"
        in 80..82 -> if (isBn) "দমকা বৃষ্টিপাত" else "Heavy Rain Showers"
        in 95..99 -> if (isBn) "বজ্রসহ বৃষ্টিপাত" else "Thunderstorm"
        else -> if (isBn) "স্বাভাবিক আবহাওয়া" else "Fair Weather"
    }
}

@SuppressLint("MissingPermission")
private fun readLastKnownLocation(context: Context): Pair<Double, Double>? {
    return try {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        for (provider in providers) {
            val loc = runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
            if (loc != null) {
                return loc.latitude to loc.longitude
            }
        }
        null
    } catch (_: Exception) {
        null
    }
}
