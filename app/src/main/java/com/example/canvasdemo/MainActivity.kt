package com.example.canvasdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.res.imageResource
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.tooling.preview.Preview
import com.example.canvasdemo.ui.theme.CanvasDemoTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

 lateinit var gameState: GameState

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
   // DrawText()
    //DrawPoints()
   // DrawPath()
   // ShadowCircle()
   // DrawImage()
    gameState = remember { GameState() }

    Column(modifier = modifier.fillMaxSize()) {
        Row() {
            Button(onClick = {
                gameState.direction = DIRECTION.LEFT
            }) { Text(text = "Left") }
            Button(onClick = {
                gameState.direction = DIRECTION.RIGTH
            }) { Text(text = "Right") }

            Button(onClick = {
                gameState.gameRunning = !gameState.gameRunning
            }) { Text(text = if (gameState.gameRunning) "Pause" else "Run") }
        }
        PacmanAnimation(gameState = gameState)
    }
}

@Composable
fun DrawText() {

    val colorList: List<Color> = listOf(Color.Black,
        Color.Blue, Color.Yellow, Color.Red, Color.Green, Color.Magenta)

    val textMeasurer = rememberTextMeasurer()

    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = Brush.verticalGradient(colors = colorList)
            )
        ) {
            append("Text Drawing")
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {

        val dimensions = textMeasurer.measure(annotatedText)

        drawRect(
            brush = Brush.horizontalGradient(colors = colorList),
            size = dimensions.size.toSize()
        )
        
        drawText(textMeasurer, annotatedText)
    }
}

@Composable
fun DrawImage() {

    val image = ImageBitmap.imageResource(id = R.drawable.vacation)

    Canvas(
        modifier = Modifier
            .size(360.dp, 270.dp)
    ) {
        drawImage(
            image = image,
            topLeft = Offset(x = 0f, y = 0f),
            colorFilter = ColorFilter.tint(
                color = Color(0xADFFAA2E),
                blendMode = BlendMode.ColorBurn
            )
        )
    }
}

@Composable
fun DrawPoints() {
    Canvas(modifier = Modifier.size(300.dp)) {

        val height = size.height
        val width = size.width
        val points = mutableListOf<Offset>()

        val frequency = (2 * kotlin.math.PI) / width

        for (x in 0..size.width.toInt()) {
            val y = (kotlin.math.sin(x * frequency).toFloat()) * (height / 4) + (height / 2)
            points.add(Offset(x.toFloat(), y))
        }

        drawPoints(
            points = points,
            strokeWidth = 3f,
            pointMode = PointMode.Points,
            color = Color.Blue
        )
    }
}


@Composable
fun PacmanAnimation(gameState: GameState) {
    // Pac-Man position and mouth angle state
    var pacmanX by remember { mutableStateOf(0f) }
    var pacmanY by remember { mutableStateOf(300f) }
    var mouthAngle by remember { mutableStateOf(30f) }
    var mouthOpening by remember { mutableStateOf(true) }

    // Animation loop: ~30 FPS
    LaunchedEffect(Unit) {
        while (true) {
            if (gameState.gameRunning) {

                // Move Pac-Man horizontally
                if (gameState.direction== DIRECTION.RIGTH)
                    pacmanX += 5f
                else if (gameState.direction == DIRECTION.LEFT)
                    pacmanX -= 5f
                if (pacmanX > 1000f) pacmanX = -99f // wrap around
                if (pacmanX < -100) pacmanX = 950f

                // Animate mouth opening/closing
                if (mouthOpening) {
                    mouthAngle += 2f
                    if (mouthAngle >= 45f) mouthOpening = false
                } else {
                    mouthAngle -= 2f
                    if (mouthAngle <= 0f) mouthOpening = true
                }

            }
            delay(20.milliseconds) // 50 FPS

        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPacman(this, pacmanX, pacmanY, mouthAngle)
    }
}

// Draw Pac-Man at given coordinates
fun drawPacman(drawScope: DrawScope, x: Float, y: Float, mouthAngle: Float) {
    with(drawScope) {
        val radius = min(size.width, size.height) / 10
        val pacmanColor = Color.Yellow

        translate(left = x, top = y) {
            rotate(0f) {
                // Draw Pac-Man body with mouth cutout
                drawArc(
                    color = pacmanColor,
                    startAngle = mouthAngle,
                    sweepAngle = 360f - mouthAngle * 2,
                    useCenter = true,
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
            }
        }
    }
}

@Composable
fun DrawPath() {
    Canvas(modifier = Modifier.size(300.dp)) {

        val path = Path().apply {
            moveTo(0f, 0f)
            quadraticTo(
                50.dp.toPx(), 200.dp.toPx(),
                300.dp.toPx(), 300.dp.toPx()
            )
            lineTo(270.dp.toPx(), 100.dp.toPx())
            quadraticTo(60.dp.toPx(), 80.dp.toPx(), 0f, 0f)
            close()
        }

        drawPath(
            path = path,
            Color.Blue,
        )
    }
}

@Composable
fun DrawArc() {
    Canvas(modifier = Modifier.size(300.dp)) {
        drawArc(
            Color.Blue,
            startAngle = 20f,
            sweepAngle = 90f,
            useCenter = true,
            size = Size(250.dp.toPx(), 250.dp.toPx())
        )
    }
}

@Composable
fun ShadowCircle() {
    Canvas(modifier = Modifier.size(300.dp)) {
        val radius = 150.dp.toPx()
        val colorList: List<Color> =
            listOf(Color.Blue, Color.Black)

        val brush = Brush.horizontalGradient(
            colors = colorList,
            startX = 0f,
            endX = 300.dp.toPx(),
            tileMode = TileMode.Repeated
        )

        drawCircle(
            brush = brush,
            radius = radius
        )
    }
}

@Composable
fun RadialFill() {
    Canvas(modifier = Modifier.size(300.dp)) {

        val radius = 150.dp.toPx()
        val colorList: List<Color> = listOf(Color.Red, Color.Blue,
            Color.Magenta, Color.Yellow, Color.Green, Color.Cyan)

        val brush = Brush.radialGradient(
            colors = colorList,
            center = center,
            radius = radius,
            tileMode = TileMode.Repeated
        )

        drawCircle(
            brush = brush,
            center = center,
            radius = radius
        )
    }
}

@Composable
fun GradientFill() {

    Canvas(modifier = Modifier.size(300.dp)) {
        val canvasSize = size
        val colorList: List<Color> = listOf(Color.Red, Color.Blue,
            Color.Magenta, Color.Yellow, Color.Green, Color.Cyan)

        val brush = Brush.horizontalGradient(
            colors = colorList,
            startX = 0f,
            endX = 300.dp.toPx(),
            tileMode = TileMode.Repeated
        )

        drawRect(
            brush = brush,
            size = canvasSize
        )
    }
}

@Composable
fun DrawOval() {
    Canvas(modifier = Modifier.size(300.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawOval(
            color = Color.Blue,
            topLeft = Offset(x = 25.dp.toPx(), y = 90.dp.toPx()),
            size = Size(
                width = canvasWidth - 50.dp.toPx(),
                height = canvasHeight / 2 - 50.dp.toPx()
            ),
            style = Stroke(width = 12.dp.toPx())
        )
    }
}

@Composable
fun DrawCircle() {
    Canvas(modifier = Modifier.size(300.dp)) {
        drawCircle(
            color = Color.Blue,
            center = center,
            radius = 120.dp.toPx()
        )
    }
}

@Composable
fun DrawRect() {
    Canvas(modifier = Modifier.size(300.dp)) {
        rotate(45f) {
            drawRect(
                color = Color.Blue,
                topLeft = Offset(200f, 200f),
                size = size / 2f
            )
        }
    }
}

@Composable
fun DrawLine() {
    Canvas(modifier = Modifier.size(300.dp)) {
        val height = size.height
        val width = size.width

        drawLine(
            start = Offset(x= 0f, y = 0f),
            end = Offset(x = width, y = height),
            color = Color.Blue,
            strokeWidth = 16.0f,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(30f, 10f, 10f, 10f), phase = 0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanvasDemoTheme {
        MainScreen()
    }
}