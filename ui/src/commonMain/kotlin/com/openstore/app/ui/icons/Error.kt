package com.openstore.app.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CommonIcons.Error: ImageVector
    get() {
        if (_error != null) {
            return _error!!
        }
        _error = Builder(name = "Error", defaultWidth = 64.0.dp, defaultHeight = 64.0.dp,
                viewportWidth = 64.0f, viewportHeight = 64.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF1B1B1C)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(32.2257f, 60.7999f)
                    curveTo(46.9208f, 60.7999f, 58.8335f, 56.7022f, 58.8335f, 51.6474f)
                    curveTo(58.8335f, 46.5927f, 46.9208f, 42.495f, 32.2257f, 42.495f)
                    curveTo(17.5306f, 42.495f, 5.6179f, 46.5927f, 5.6179f, 51.6474f)
                    curveTo(5.6179f, 56.7022f, 17.5306f, 60.7999f, 32.2257f, 60.7999f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(47.4226f, 54.4001f)
                    lineTo(47.8377f, 54.2177f)
                    lineTo(47.6552f, 53.8025f)
                    lineTo(47.2401f, 53.985f)
                    lineTo(47.4226f, 54.4001f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(20.6063f, 56.3042f)
                    lineTo(21.1418f, 56.0688f)
                    lineTo(20.9064f, 55.5334f)
                    lineTo(20.371f, 55.7688f)
                    lineTo(20.6063f, 56.3042f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(18.2382f, 45.1088f)
                    lineTo(18.4905f, 45.2545f)
                    lineTo(18.6362f, 45.0022f)
                    lineTo(18.3839f, 44.8565f)
                    lineTo(18.2382f, 45.1088f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(21.8738f, 44.6801f)
                    lineTo(22.0578f, 44.7864f)
                    lineTo(22.164f, 44.6024f)
                    lineTo(21.98f, 44.4961f)
                    lineTo(21.8738f, 44.6801f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(23.5669f, 45.1334f)
                    lineTo(23.7509f, 45.2396f)
                    lineTo(23.8572f, 45.0556f)
                    lineTo(23.6731f, 44.9494f)
                    lineTo(23.5669f, 45.1334f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(13.8952f, 47.259f)
                    lineTo(14.0792f, 47.3652f)
                    lineTo(14.1855f, 47.1812f)
                    lineTo(14.0014f, 47.075f)
                    lineTo(13.8952f, 47.259f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(37.3127f, 58.7579f)
                    lineTo(37.4967f, 58.8641f)
                    lineTo(37.6029f, 58.6801f)
                    lineTo(37.4189f, 58.5739f)
                    lineTo(37.3127f, 58.7579f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(32.718f, 57.8846f)
                    lineTo(32.902f, 57.9908f)
                    lineTo(33.0082f, 57.8068f)
                    lineTo(32.8242f, 57.7006f)
                    lineTo(32.718f, 57.8846f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(41.6484f, 56.8421f)
                    lineTo(41.8325f, 56.9484f)
                    lineTo(41.9387f, 56.7643f)
                    lineTo(41.7547f, 56.6581f)
                    lineTo(41.6484f, 56.8421f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(24.2365f, 57.1706f)
                    lineTo(24.6036f, 57.2808f)
                    lineTo(24.7138f, 56.9136f)
                    lineTo(24.3466f, 56.8034f)
                    lineTo(24.2365f, 57.1706f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(39.3705f, 56.6716f)
                    lineTo(39.7377f, 56.7817f)
                    lineTo(39.8479f, 56.4146f)
                    lineTo(39.4807f, 56.3044f)
                    lineTo(39.3705f, 56.6716f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(25.1227f, 44.9844f)
                    lineTo(25.4899f, 45.0946f)
                    lineTo(25.6f, 44.7274f)
                    lineTo(25.2328f, 44.6173f)
                    lineTo(25.1227f, 44.9844f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(31.0016f, 44.5316f)
                    lineTo(31.3688f, 44.6417f)
                    lineTo(31.4789f, 44.2745f)
                    lineTo(31.1118f, 44.1644f)
                    lineTo(31.0016f, 44.5316f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(8.5856f, 49.3314f)
                    lineTo(8.9528f, 49.4415f)
                    lineTo(9.0629f, 49.0743f)
                    lineTo(8.6957f, 48.9642f)
                    lineTo(8.5856f, 49.3314f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(16.0559f, 48.9642f)
                    lineTo(16.4231f, 49.0743f)
                    lineTo(16.5333f, 48.7072f)
                    lineTo(16.1661f, 48.597f)
                    lineTo(16.0559f, 48.9642f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(46.1455f, 55.6909f)
                    lineTo(46.5127f, 55.801f)
                    lineTo(46.6229f, 55.4338f)
                    lineTo(46.2557f, 55.3237f)
                    lineTo(46.1455f, 55.6909f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(42.4135f, 45.2889f)
                    lineTo(42.7806f, 45.399f)
                    lineTo(42.8908f, 45.0319f)
                    lineTo(42.5236f, 44.9217f)
                    lineTo(42.4135f, 45.2889f)
                    close()
                }
                path(fill = linearGradient(0.0f to Color(0x00CC2121), 0.14f to Color(0x05CC2121),
                        0.36f to Color(0x21CC2121), 0.64f to Color(0x47CC2121), 0.95f to
                        Color(0x70CC2121), 1.0f to Color(0x7FCC2121), start =
                        Offset(32.2211f,14.0657f), end = Offset(32.2211f,49.437f)), stroke = null,
                        strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(44.8608f, 10.5848f)
                    verticalLineTo(50.5564f)
                    curveTo(44.8608f, 53.0975f, 39.2003f, 55.1567f, 32.2189f, 55.1567f)
                    curveTo(25.2375f, 55.1567f, 19.577f, 53.0975f, 19.577f, 50.5564f)
                    verticalLineTo(10.5848f)
                    horizontalLineTo(44.8586f)
                    horizontalLineTo(44.8608f)
                    close()
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(32.2232f, 56.3045f)
                    curveTo(23.4968f, 56.3045f, 16.4225f, 53.73f, 16.4225f, 50.5542f)
                    curveTo(16.4225f, 47.3784f, 23.4968f, 44.804f, 32.2232f, 44.804f)
                    curveTo(40.9497f, 44.804f, 48.0239f, 47.3784f, 48.0239f, 50.5542f)
                    curveTo(48.0239f, 53.73f, 40.9497f, 56.3045f, 32.2232f, 56.3045f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(41.3055f, 47.3584f)
                    curveTo(43.5048f, 48.1864f, 44.8608f, 49.3124f, 44.8608f, 50.5545f)
                    curveTo(44.8608f, 53.0955f, 39.2003f, 55.1547f, 32.2189f, 55.1547f)
                    curveTo(28.806f, 55.1547f, 25.7085f, 54.6618f, 23.4347f, 53.86f)
                    lineTo(41.3033f, 47.3562f)
                    lineTo(41.3055f, 47.3584f)
                    close()
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF4F4F7)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(41.3084f, 47.3584f)
                    lineTo(23.4398f, 53.8622f)
                }
                path(fill = SolidColor(Color(0xFF1B1B1C)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(21.5414f, 25.6853f)
                    curveTo(17.8352f, 21.5792f, 19.6139f, 13.9332f, 25.5144f, 8.6073f)
                    curveTo(31.4148f, 3.2815f, 39.2025f, 2.2927f, 42.9087f, 6.3987f)
                    curveTo(46.6149f, 10.5048f, 44.8361f, 18.1508f, 38.9357f, 23.4767f)
                    curveTo(33.0353f, 28.8025f, 25.2476f, 29.7913f, 21.5414f, 25.6853f)
                    close()
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(24.4741f, 27.6476f)
                    curveTo(24.8049f, 27.3059f, 25.151f, 26.9686f, 25.5125f, 26.6422f)
                    curveTo(30.1302f, 22.4735f, 35.9046f, 20.962f, 39.9725f, 22.4713f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(43.8409f, 16.6307f)
                    curveTo(43.5825f, 16.1663f, 43.2714f, 15.7304f, 42.9099f, 15.3295f)
                    curveTo(39.2035f, 11.2243f, 31.4159f, 12.2123f, 25.5167f, 17.5376f)
                    curveTo(23.2451f, 19.588f, 21.5846f, 21.9823f, 20.6142f, 24.3854f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(43.9227f, 7.8486f)
                    verticalLineTo(16.427f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(41.9073f, 5.4829f)
                    verticalLineTo(20.1445f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(20.7346f, 24.5996f)
                    verticalLineTo(15.1516f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(23.5953f, 27.2596f)
                    verticalLineTo(10.5848f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(36.7697f, 3.8663f)
                    verticalLineTo(25.1851f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(28.9139f, 6.1183f)
                    verticalLineTo(28.1731f)
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(21.5414f, 25.6853f)
                    curveTo(17.8352f, 21.5792f, 19.6139f, 13.9332f, 25.5144f, 8.6073f)
                    curveTo(31.4148f, 3.2815f, 39.2025f, 2.2927f, 42.9087f, 6.3987f)
                    curveTo(46.6149f, 10.5048f, 44.8361f, 18.1508f, 38.9357f, 23.4767f)
                    curveTo(33.0353f, 28.8025f, 25.2476f, 29.7913f, 21.5414f, 25.6853f)
                    close()
                }
            }
            group {
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(30.9711f, 18.9032f)
                    lineTo(34.4868f, 17.9327f)
                    lineTo(29.7369f, 26.8504f)
                    lineTo(26.2212f, 27.8235f)
                    lineTo(30.9711f, 18.9032f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(21.2199f, 6.2417f)
                    lineTo(24.7356f, 5.2685f)
                    lineTo(32.9102f, 11.5492f)
                    lineTo(29.3945f, 12.5223f)
                    lineTo(21.2199f, 6.2417f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(41.0659f, 21.3852f)
                    lineTo(44.5817f, 20.4147f)
                    lineTo(42.6302f, 24.3489f)
                    lineTo(39.0139f, 25.1985f)
                    lineTo(41.0659f, 21.3852f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(32.9156f, 15.0835f)
                    lineTo(36.4314f, 14.1104f)
                    lineTo(44.5039f, 20.412f)
                    lineTo(40.9882f, 21.3825f)
                    lineTo(32.9156f, 15.0835f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(34.1759f, 3.4558f)
                    lineTo(37.5059f, 6.056f)
                    lineTo(32.9139f, 15.0819f)
                    lineTo(40.9968f, 21.3866f)
                    lineTo(39.0122f, 25.1978f)
                    lineTo(30.937f, 18.8988f)
                    lineTo(26.218f, 27.8242f)
                    lineTo(22.888f, 25.2266f)
                    lineTo(27.5102f, 16.317f)
                    lineTo(19.4115f, 9.8976f)
                    lineTo(21.3158f, 6.2223f)
                    lineTo(29.3884f, 12.5213f)
                    lineTo(34.1759f, 3.4558f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(37.5065f, 6.0556f)
                    lineTo(41.0222f, 5.0851f)
                    lineTo(36.4314f, 14.1072f)
                    lineTo(32.9156f, 15.0803f)
                    lineTo(37.5065f, 6.0556f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFCC2121)), stroke = SolidColor(Color(0xFF0500FF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Butt, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(34.1849f, 3.4528f)
                    lineTo(37.698f, 2.4797f)
                    lineTo(41.0003f, 5.0855f)
                    lineTo(37.4846f, 6.056f)
                    lineTo(34.1849f, 3.4528f)
                    close()
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF2ECCFF)),
                        strokeLineWidth = 0.5f, strokeLineCap = Round, strokeLineJoin =
                        StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                        NonZero) {
                    moveTo(42.9042f, 6.3989f)
                    curveTo(46.6107f, 10.5041f, 44.8319f, 18.1514f, 38.9327f, 23.4767f)
                    curveTo(33.0334f, 28.802f, 25.2459f, 29.79f, 21.5394f, 25.6848f)
                }
            }
        }
        .build()
        return _error!!
    }

private var _error: ImageVector? = null
