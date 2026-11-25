package com.example.v2_medtime.screens.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = rememberPullToRefreshState()
    var refreshingMinimo by remember { mutableStateOf(false) }

    LaunchedEffect(refreshingMinimo) {
        if (refreshingMinimo) {
            delay(400L)
            refreshingMinimo = false
        }
    }

    val refreshingEfetivo = isRefreshing || refreshingMinimo

    LaunchedEffect(refreshingEfetivo) {
        if (!refreshingEfetivo && state.distanceFraction > 0f) {
            delay(50L)
            if (!refreshingEfetivo && state.distanceFraction > 0f) {
                state.animateToHidden()
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = refreshingEfetivo,
        onRefresh = {
            refreshingMinimo = true
            onRefresh()
        },
        state = state,
        modifier = modifier,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = refreshingEfetivo,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = Color.White,
                color = Color(0xFF0B8DF2)
            )
        },
        content = content
    )
}