package com.example.v2_medtime.screens.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.v2_medtime.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorDataNascimento(
    onDismiss: () -> Unit,
    onSelecionarData: (String) -> Unit
) {
    val hoje = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        yearRange = 1900..hoje.year,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val data = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return !data.isAfter(hoje)
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= hoje.year
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val data = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        val formatador = DateTimeFormatter.ofPattern("ddMMyyyy")

                        onSelecionarData(data.format(formatador))
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = colorResource(R.color.azul_botao),
                todayDateBorderColor = colorResource(R.color.azul_botao)
            )
        )
    }
}
