package com.tvandinther.reps.di

import com.tvandinther.reps.data.db.AppDatabase
import com.tvandinther.reps.domain.SessionAssembler
import com.tvandinther.reps.ui.exercises.ExercisesViewModel
import com.tvandinther.reps.ui.history.HistoryViewModel
import com.tvandinther.reps.ui.logging.LoggingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().unitDao() }
    single { get<AppDatabase>().exerciseDao() }
    single { get<AppDatabase>().setDao() }
    single { SessionAssembler() }

    viewModel { ExercisesViewModel(get(), get(), get()) }
    viewModel { params -> LoggingViewModel(params.get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}
