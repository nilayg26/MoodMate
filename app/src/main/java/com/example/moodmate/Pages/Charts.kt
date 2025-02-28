package com.example.moodmate.Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.moodmate.MoodMateBottomBar
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.ViewModels.InternalDataBaseViewModel
import com.example.moodmate.createToastMessage
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme
object ChartColors{
    val AT_BEST= Color(0xFF00FF00)
    val HAPPY= Color(0xFF80FF00)
    val NEUTRAL= Color(0xFFFFE000)
    val LOW= Color(0xFFFF8000)
    val VERY_LOW=Color(0xFFFF0000)
}
@Composable
fun Charts(navController: NavHostController, chartViewModel: InternalDataBaseViewModel){
    val map = remember { mutableStateMapOf<Int, List<String>>() }
    val list = getFrequencyList(map)
    val context= LocalContext.current
    LaunchedEffect(Unit){
        if (map.values.isEmpty()) {
            map.clear()
            map.putAll(chartViewModel.getChartData()?.toMutableMap() ?: mutableMapOf())
        }
    }
    val scrollState= rememberScrollState()
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                MoodMateTopAppBar("Stats")
            },
            bottomBar = {
                MoodMateBottomBar(navController,2)
            }) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Colors.Background)) {
                Column(
                    modifier = Modifier.padding(10.dp).fillMaxSize().background(
                        Colors.Background
                    ).verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val donutChartData = PieChartData(
                        slices = listOf(
                            PieChartData.Slice("Very Low", list[0].toFloat(),ChartColors.VERY_LOW ),
                            PieChartData.Slice("Low", list[1].toFloat(),ChartColors.LOW ),
                            PieChartData.Slice("Neutral", list[2].toFloat(), ChartColors.NEUTRAL),
                            PieChartData.Slice("Happy", list[3].toFloat(),ChartColors.HAPPY ),
                            PieChartData.Slice("At Best mood", list[4].toFloat(),ChartColors.AT_BEST )
                        ),
                        plotType = PlotType.Donut,
                    )
                    val donutChartConfig = PieChartConfig(
                        strokeWidth = 80f,
                        activeSliceAlpha = .9f,
                        isAnimationEnable = true,
                        labelVisible = true,
                        showSliceLabels = true,
                        sliceLabelTextColor = Color.Black,
                        sliceLabelTextSize = 79.sp,
                        labelColor = Color.DarkGray,
                        labelFontSize = 50.sp,
                        isEllipsizeEnabled = true
                    )
                    DonutPieChart(
                        modifier = Modifier
                            .fillMaxWidth(),
                        donutChartData,
                        donutChartConfig,
                        onSliceClick = { slice -> context.createToastMessage(text = slice.label,duration = 0)}
                    )
                    QuoteBox(tod= map.let {
                        getMoodReport(map)
                    }, headline = "AI's Analysis",animation = false)
                }
            }
        }
    }
}
fun getMoodReport(map: SnapshotStateMap<Int, List<String>>): String {
    if (map.isEmpty()) return "🔹 Your Mood Tracked by AI 🔹\n\n❌ No Recent Data Available, Go to Track Your Mood ❌"

    val builder = StringBuilder().append("🔹 Your Mood as Tracked by AI 🔹\n\n──────────\n")
    val moodColors = mapOf(
        1 to "🟥 Very Low",
        2 to "🟧 Low",
        3 to "🟨 Neutral",
        4 to "🟩 Happy",
        5 to "💚 At Best"
    )
    val sortedMoods = map.toSortedMap(compareByDescending { it })

    sortedMoods.entries.forEachIndexed { index, (key, value) ->
        val rating = value[0].toIntOrNull() ?: 3 // Default to 3 if conversion fails
        val expression = value[1]
        val tag = if (index == 0) "🏷️ Latest" else ""
        val color = moodColors[rating] ?: "⚫ Unknown"

        builder.append("Mood ${key + 1} $tag\n")
            .append("⭐ Rating: $rating/5\n")
            .append("😊 Expression: $expression\n")
            .append("🎨 Color: $color\n")
            .append("──────────\n")
    }

    return builder.toString()
}

fun getFrequencyList(map: SnapshotStateMap<Int, List<String>>): SnapshotStateList<Int> {
    val frequency = MutableList(5) { 0 }

    for (value in map.values) {
        val rating = value[0].toIntOrNull() ?: continue
        if (rating in 1..5) {
            frequency[rating - 1]++
        }
    }
    return mutableStateListOf(*frequency.toTypedArray())
}


